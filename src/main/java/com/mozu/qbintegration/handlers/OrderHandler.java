package com.mozu.qbintegration.handlers;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.tz.NameProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.MozuApiContext;
import com.mozu.api.cache.CacheManager;
import com.mozu.api.cache.CacheManagerFactory;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.contracts.reference.TimeZone;
import com.mozu.api.contracts.reference.TimeZoneCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.settings.GeneralSettingsResource;
import com.mozu.api.resources.platform.ReferenceDataResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.DataMapping;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuOrderItem;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.QBResponse;
import com.mozu.qbintegration.model.QuickBooksOrder;
import com.mozu.qbintegration.model.QuickBooksSavedOrderLine;
import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerSalesTaxCodeRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemSalesTaxRef;
import com.mozu.qbintegration.model.qbmodel.allgen.PaymentMethodRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptLineAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptLineMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptLineMod.TxnLineID;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptLineRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptMod.TxnID;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptModRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ShipAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRsType;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.service.XMLService;
import com.mozu.qbintegration.utils.QBDataValidationUtil;

@Component
public class OrderHandler {

	private static final Log logger = LogFactory.getLog(OrderHandler.class);
	
	private static ObjectMapper mapper = JsonUtils.initObjectMapper();

	@Autowired
	CustomerHandler customerHandler;
	
	@Autowired
	ProductHandler productHandler;
	
	@Autowired
	EntityHandler entityHandler;
	
	@Autowired 
	QuickbooksService quickbooksService;
	
	@Autowired
	QBDataHandler qbDataHandler;
	
	@Autowired
	XMLService xmlHelper;
	
	public Order getOrder(String orderId, Integer tenantId) throws Exception {
		OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId));
		Order order = null;
		try {
			order = orderResource.getOrder(orderId);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
		return order;
	}


	public QBResponse processOrderAdd(Integer tenantId, String orderId, String qbTaskResponse) throws Exception {
		QBXML orderAddResp = (QBXML)  xmlHelper.getUnmarshalledValue(qbTaskResponse);
		SalesReceiptAddRsType salesOrderResponse = (SalesReceiptAddRsType) orderAddResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		
		QBResponse qbResponse = new QBResponse();
		qbResponse.setStatusCode(salesOrderResponse.getStatusCode());
		qbResponse.setStatusSeverity(salesOrderResponse.getStatusSeverity());
		qbResponse.setStatusMessage(salesOrderResponse.getStatusMessage());
		if (qbResponse.hasError()) return qbResponse;
		
		QuickBooksOrder order = getQuickBooksOrder(salesOrderResponse.getSalesReceiptRet());
		entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderEntityName(), order.getRefNumber(),order);
		MozuOrderDetail mozuOrderDetail = getMozuOrderDetail(tenantId, orderId);
		mozuOrderDetail.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		entityHandler.addEntity(tenantId, entityHandler.getOrderPostedEntityName(), mozuOrderDetail);
		logger.debug((new StringBuilder())
				.append("Processed order with id: ")
				.append(orderId)
				.append(" with QB status code: ")
				.append(salesOrderResponse.getStatusCode())
				.append(" with status: ")
				.append(salesOrderResponse.getStatusMessage())
				.toString());
		
		return qbResponse;

	}
	
	public QBResponse processOrderUpdate(Integer tenantId, String orderId, String qbTaskResponse) throws Exception {
		QBXML orderModResp = (QBXML)  xmlHelper.getUnmarshalledValue(qbTaskResponse);

		SalesReceiptModRsType orderModRsType = (SalesReceiptModRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		
		QBResponse qbResponse = new QBResponse();
		qbResponse.setStatusCode(orderModRsType.getStatusCode());
		qbResponse.setStatusSeverity(orderModRsType.getStatusSeverity());
		qbResponse.setStatusMessage(orderModRsType.getStatusMessage());
		if (qbResponse.hasError()) return qbResponse;
		
		QuickBooksOrder order = getQuickBooksOrder(orderModRsType.getSalesReceiptRet());
		entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderEntityName(), order.getRefNumber(),order);
		
		MozuOrderDetail mozuOrderDetail = getMozuOrderDetail(tenantId, orderId);
		mozuOrderDetail.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		entityHandler.addEntity(tenantId, entityHandler.getOrderPostedEntityName(), mozuOrderDetail);
		logger.debug((new StringBuilder())
				.append("Processed order with id: ")
				.append(orderId)
				.append(" with QB status code: ")
				.append(orderModRsType.getStatusCode())
				.append(" with status: ")
				.append(orderModRsType.getStatusMessage()).toString());
		return qbResponse;
	}
	
	public QBResponse processOrderQuery(int tenantId, String orderId, String response) throws Exception {
		
		QBXML orderModResp = (QBXML)  xmlHelper.getUnmarshalledValue(response);

		SalesReceiptQueryRsType  orderQueryRsType = (SalesReceiptQueryRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		QBResponse qbResponse = new QBResponse();
		qbResponse.setStatusCode(orderQueryRsType.getStatusCode());
		qbResponse.setStatusSeverity(orderQueryRsType.getStatusSeverity());
		qbResponse.setStatusMessage(orderQueryRsType.getStatusMessage());
		if (qbResponse.hasError() || qbResponse.hasWarning()) return qbResponse;
		
		
		//Get the last modified and store it
		SalesReceiptRet selected = null;
		DateTime timeCreated = null;
		for(SalesReceiptRet salesReceiptRet : orderQueryRsType.getSalesReceiptRet()) {
			if (timeCreated == null) 
				selected = salesReceiptRet;
			
			if (DateTime.parse(selected.getTimeCreated()).getMillis() > DateTime.parse(salesReceiptRet.getTimeCreated()).getMillis()) {
				selected = salesReceiptRet; //Get the most recently created salesrecipt for mozu order number
			}
		}
		QuickBooksOrder order = getQuickBooksOrder(selected);
		entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderEntityName(), order.getRefNumber(),order);
		return qbResponse;
	}
	
	private QuickBooksOrder getQuickBooksOrder(SalesReceiptRet salesOrderRet) {
		QuickBooksOrder order = new QuickBooksOrder();
		order.setBillAddress(salesOrderRet.getBillAddress());
		order.setShipAddress(salesOrderRet.getShipAddress());
		order.setTxnID(salesOrderRet.getTxnID());
		order.setTimeCreated(salesOrderRet.getTimeCreated());
		order.setEditSequence(salesOrderRet.getEditSequence());
		order.setTxnNumber(salesOrderRet.getTxnNumber());
		order.setTxnDate(salesOrderRet.getTxnDate());
		order.setRefNumber(salesOrderRet.getRefNumber());
		order.setShipDate(salesOrderRet.getShipDate());
		order.setTimeModified(salesOrderRet.getTimeModified());
		if (StringUtils.isNotEmpty(salesOrderRet.getSubtotal()))
			order.setSubTotal(Double.parseDouble(salesOrderRet.getSubtotal()));
		if (StringUtils.isNotEmpty(salesOrderRet.getTotalAmount()))
			order.setTotalAmount(Double.parseDouble(salesOrderRet.getTotalAmount()));
		if (StringUtils.isNotEmpty(salesOrderRet.getSalesTaxPercentage()))
			order.setSalesTaxPrecentage(Double.parseDouble(salesOrderRet.getSalesTaxPercentage()));
		if (StringUtils.isNotEmpty(salesOrderRet.getSalesTaxTotal()))
			order.setSalesTaxTotal(Double.parseDouble(salesOrderRet.getSalesTaxTotal()));
		
		order.setOrderLines(new ArrayList<QuickBooksSavedOrderLine>());
		if( salesOrderRet.getSalesReceiptLineRetOrSalesReceiptLineGroupRet() != null) {
			for(Object returnedItem: salesOrderRet.getSalesReceiptLineRetOrSalesReceiptLineGroupRet()) {
				SalesReceiptLineRet salesOrderLineRet = (SalesReceiptLineRet) returnedItem;
				
				QuickBooksSavedOrderLine line = new QuickBooksSavedOrderLine();
				line.setAmount(Double.parseDouble(salesOrderLineRet.getAmount()));
				line.setFullName(salesOrderLineRet.getItemRef().getFullName());
				if (StringUtils.isNotEmpty(salesOrderLineRet.getQuantity()))
					line.setQuantity(Integer.parseInt(salesOrderLineRet.getQuantity()));
				if (StringUtils.isNotEmpty(salesOrderLineRet.getRate()))
					line.setRate(Double.parseDouble(salesOrderLineRet.getRate()));
				line.setTxnLineId(salesOrderLineRet.getTxnLineID());
				
				order.getOrderLines().add(line);
			}
		}
		
		return order;
	}
	
	
	public EntityCollection getMozuOrderDetails(Integer tenantId, String action, String orderBy,
			Integer startIndex, Integer pageSize, String search) throws Exception {

		String entityName = null;
		
		switch (action.toLowerCase()) {//POSTED, UPDATED, CONFLICT, CANCELLED
			case "posted":
				entityName = entityHandler.getOrderPostedEntityName();
				break;
			case "updated":
				entityName = entityHandler.getOrderUpdatedEntityName();
				break;
			case "conflict":
				entityName = entityHandler.getOrderConflictEntityName();
				break;
			case "cancelled":
				entityName = entityHandler.getOrderCancelledEntityName();
				break;
			default:
				throw new Exception("Not implemented");
		}
		
		EntityCollection nodesCollection = null;
		try {
			String filterCriteria = null;
			if (StringUtils.isNotEmpty(search)) {
				filterCriteria = "orderNumber eq "+search;
			}
			nodesCollection = entityHandler.getEntityCollection(tenantId, entityName, 
					filterCriteria, orderBy +" desc", startIndex, pageSize);
			
		} catch (Exception e) {
			logger.error("Error getting orders by action for tenant id: " + tenantId);
			throw e;
		}
		return nodesCollection;
		
	}
	
	public String getQBOrderQueryXml(int tenantId, Order order) throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		
		qbxmlMsgsRq.setOnError("stopOnError");
		
		SalesReceiptQueryRqType salesOrderQueryRqType = new SalesReceiptQueryRqType();
		salesOrderQueryRqType.setRequestID(order.getId());
		
		salesOrderQueryRqType.setIncludeLineItems("true");

		salesOrderQueryRqType.getRefNumber().add(String.valueOf(order.getOrderNumber()));
		
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(salesOrderQueryRqType);
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);

		return xmlHelper.getMarshalledValue(qbxml);
	}
	
	public String getQBOrderSaveXML(int tenantId, String orderId) throws Exception {
		
		GeneralSettings setting = quickbooksService.getSettingsFromEntityList(tenantId);
		//Order singleOrder, String customerQBListID,List<String> itemListIDs
		Order order = getOrder(orderId, tenantId);
		CustomerAccount account = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		String customerQBListID = customerHandler.getQbCustomerId(tenantId, account.getEmailAddress());
		
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptAddRqType salesReceiptAddRqType = new com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptAddRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(salesReceiptAddRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesReceiptAdd salesReceiptAdd = new SalesReceiptAdd();
		salesReceiptAddRqType.setRequestID(order.getId());
		
		salesReceiptAddRqType.setSalesReceiptAdd(salesReceiptAdd);
		
		DateTimeFormatter datefmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		
		DateTimeZone zone = getTimezoneOffset(tenantId, order.getSiteId());
		String orderDate = order.getAcceptedDate().withZone(zone).toString(datefmt);
		salesReceiptAdd.setTxnDate(orderDate);
		
		
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);

		if (order.getIsTaxExempt()) {
			CustomerSalesTaxCodeRef customerSalesTaxCodeRef = new CustomerSalesTaxCodeRef();
			customerSalesTaxCodeRef.setFullName("Non");
			salesReceiptAdd.setCustomerSalesTaxCodeRef(customerSalesTaxCodeRef);
		}
		
		salesReceiptAdd.setCustomerRef(customerRef);
		salesReceiptAdd.setRefNumber(String.valueOf(order.getOrderNumber()));
		if (order.getBillingInfo().getBillingContact().getAddress() != null)
			salesReceiptAdd.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		
		if (order.getFulfillmentInfo() != null && order.getFulfillmentInfo().getFulfillmentContact() != null && order.getFulfillmentInfo().getFulfillmentContact().getAddress() != null)
			salesReceiptAdd.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		
		PaymentMethodRef paymentMethod = getPayment(tenantId, order);
		if(null != paymentMethod) {
			salesReceiptAdd.setPaymentMethodRef(paymentMethod);
		}
		
		salesReceiptAdd.setItemSalesTaxRef(getItemSalesTaxRef(order.getTaxTotal(), setting) );
		
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, true);
		NumberFormat numberFormat = new DecimalFormat("#.00");
		for (MozuOrderItem item : orderItems) {
		
			ItemRef itemRef = new ItemRef();
			SalesReceiptLineAdd salesReceiptLineAdd = new SalesReceiptLineAdd();
			salesReceiptLineAdd.setAmount(numberFormat.format(item.getTotalAmount()));
			
			if (!item.isMic()) {
				itemRef.setListID(item.getQbItemCode());
				salesReceiptLineAdd.setQuantity(item.getQty().toString());
			} else
				itemRef.setFullName(item.getProductCode());
			
			salesReceiptLineAdd.setItemRef(itemRef);
			salesReceiptLineAdd.setDesc(item.getDescription());
			
			if (!StringUtils.isEmpty(item.getTaxCode()))
				salesReceiptLineAdd.setSalesTaxCodeRef(getSalesTaxCodeRef(item.getTaxCode()));
			
			salesReceiptAdd.getSalesReceiptLineAddOrSalesReceiptLineGroupAdd().add(salesReceiptLineAdd);
		}

		return xmlHelper.getMarshalledValue(qbxml);
	}

	public String getQBOrderUpdateXML(int tenantId, String orderId) throws Exception {
		Order order = getOrder(orderId, tenantId);
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderEntityName(), order.getOrderNumber().toString());
		if (node == null)
			throw new Exception("Existing Quickbooks sales order not found");
		
		GeneralSettings setting = quickbooksService.getSettingsFromEntityList(tenantId);
		QuickBooksOrder salesReceiptRet = mapper.readValue(node.toString(), QuickBooksOrder.class);
		CustomerAccount account = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		String customerQBListID = customerHandler.getQbCustomerId(tenantId, account.getEmailAddress());
	
		
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptModRqType salesReceiptModRqType = 
				new com.mozu.qbintegration.model.qbmodel.allgen.SalesReceiptModRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(salesReceiptModRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesReceiptMod salesReceiptmod = new SalesReceiptMod();
		salesReceiptModRqType.setRequestID(order.getId());
		
		salesReceiptmod.setRefNumber(String.valueOf(order.getOrderNumber()));
		
		
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);
		
		if (order.getIsTaxExempt()) {
			CustomerSalesTaxCodeRef customerSalesTaxCodeRef = new CustomerSalesTaxCodeRef();
			customerSalesTaxCodeRef.setFullName("Non");
			salesReceiptmod.setCustomerSalesTaxCodeRef(customerSalesTaxCodeRef);
		}
		salesReceiptmod.setCustomerRef(customerRef);
		
		TxnID txnId = new TxnID();
		txnId.setValue(salesReceiptRet.getTxnID());
		salesReceiptmod.setTxnID(txnId);
		salesReceiptmod.setEditSequence(salesReceiptRet.getEditSequence());
		
		if (order.getBillingInfo().getBillingContact().getAddress() != null)
			salesReceiptmod.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		salesReceiptmod.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		salesReceiptmod.setItemSalesTaxRef(getItemSalesTaxRef(order.getTaxTotal(), setting) );
		
		PaymentMethodRef paymentMethod = getPayment(tenantId, order);
		if(null != paymentMethod) {
			salesReceiptmod.setPaymentMethodRef(paymentMethod);
		}
		
		salesReceiptModRqType.setSalesReceiptMod(salesReceiptmod);
		
		NumberFormat numberFormat = new DecimalFormat("#.00");
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, true);
		for (MozuOrderItem item : orderItems) {
			TxnLineID txnLineId = new TxnLineID();
			if( salesReceiptRet.getOrderLines() != null) {
				for(QuickBooksSavedOrderLine qbItem: salesReceiptRet.getOrderLines()) {
					if((item.getProductCode().equalsIgnoreCase(qbItem.getFullName()) && item.getTotalAmount() == 0.0 && item.getTotalAmount() == qbItem.getAmount())
						|| (item.getProductCode().equalsIgnoreCase(qbItem.getFullName()) && item.getTotalAmount() != 0.0) ) {
						txnLineId.setValue(qbItem.getTxnLineId());
					}
				}
				
				if (StringUtils.isEmpty(txnLineId.getValue()))
					txnLineId.setValue("-1");
			}
			ItemRef itemRef = new ItemRef();

			SalesReceiptLineMod salesReceiptLineMod = new SalesReceiptLineMod();
			salesReceiptLineMod.setTxnLineID(txnLineId);
			salesReceiptLineMod.setAmount(numberFormat.format(item.getTotalAmount()));
			if (!item.isMic()) {
				itemRef.setListID(item.getQbItemCode());
				salesReceiptLineMod.setQuantity(item.getQty().toString());
			} else
				itemRef.setFullName(item.getProductCode());

			salesReceiptLineMod.setDesc(item.getDescription());
			salesReceiptLineMod.setItemRef(itemRef);
			if (!StringUtils.isEmpty(item.getTaxCode()))
				salesReceiptLineMod.setSalesTaxCodeRef(getSalesTaxCodeRef(item.getTaxCode()));
			salesReceiptmod.getSalesReceiptLineModOrSalesReceiptLineGroupMod().add(salesReceiptLineMod);
		}
		return xmlHelper.getMarshalledValue(qbxml);
	}

	
	private SalesTaxCodeRef getSalesTaxCodeRef(String taxCode) {
		SalesTaxCodeRef salesTaxCodeRef = new SalesTaxCodeRef();
		salesTaxCodeRef.setFullName(taxCode);
		return salesTaxCodeRef;
		
	}
	
	private ItemSalesTaxRef getItemSalesTaxRef(double taxTotal, GeneralSettings setting) {
		ItemSalesTaxRef itemSalesTaxRef = new ItemSalesTaxRef();
		if (taxTotal > 0) {
			itemSalesTaxRef.setFullName(setting.getOrderSalesTaxCode());
		} else {
			itemSalesTaxRef.setFullName(setting.getOrderNonSalesTaxCode());
		}
		
		return itemSalesTaxRef;
	}
	
	
	private PaymentMethodRef getPayment(Integer tenantId, Order order) throws Exception {
		PaymentMethodRef paymentRef = new PaymentMethodRef();
		
		int paymentSize = order.getPayments().size(); //Any size > 1 would force not having storecredit as paymenttype.
		boolean areAllStoreCreditPayments = true; //for a condition where there are multiple store credit payments
		String storeCreditPaymentName = null; // To store mozu name for StoreCredit since it might in future.
		for(Payment payment : order.getPayments()) {
			if (payment.getStatus().equalsIgnoreCase("voided")) continue;
			
			if (payment.getBillingInfo().getCard() != null && !StringUtils.isEmpty(payment.getBillingInfo().getCard().getPaymentOrCardType())) {
				
				paymentRef.setFullName(payment.getBillingInfo().getCard().getPaymentOrCardType());
				areAllStoreCreditPayments = false; //at least 1 non store credit payment.
				
			} else if (payment.getBillingInfo().getPaymentType().equalsIgnoreCase("storecredit")) { 
				//Since if any of others exist, we need to show them, NOT storecredit.
				storeCreditPaymentName = payment.getPaymentType();
				continue; //just go to next payment or exit
			} else {
				paymentRef.setFullName(payment.getPaymentType()); //entire storecredit is covered here.
				areAllStoreCreditPayments = false; //at least 1 non store credit payment
			}
		}
		
		//If all payments are storecredit, set storecredit
		if(areAllStoreCreditPayments) {
			paymentRef.setFullName(storeCreditPaymentName);
		}

		if (null != paymentRef && StringUtils.isNotEmpty(paymentRef.getFullName())) {
			DataMapping mapping = qbDataHandler.getMapping(tenantId, paymentRef.getFullName(), "payment");
			if (mapping != null) {
				paymentRef.setFullName(mapping.getQbData().getFullName());
			}
			return paymentRef;
		}
		return null;
	}
	
	/*
	 * Get order delete XML
	 */
	public String getQBOrderDeleteXML(final Integer tenantId, final String orderId) throws Exception {
		
		QBXML qbxml = null; 
		Order order = getOrder(orderId, tenantId);
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderEntityName(), order.getOrderNumber().toString());
		if (node == null)
			throw new Exception("Existing Quickbooks sales order not found");
		QuickBooksOrder salesOrderRet = mapper.readValue(node.toString(), QuickBooksOrder.class);
		
		
		TxnDelRqType deleteTx = new TxnDelRqType();
		deleteTx.setRequestID(orderId);
		deleteTx.setTxnDelType("SalesReceipt");
		deleteTx.setTxnID(salesOrderRet.getTxnID());
		
		qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxmlMsgsRq.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(deleteTx);
		return xmlHelper.getMarshalledValue(qbxml);
	}

	/*
	 * Process the response of cancelling a sales order in QB. 
	 * Initiated on Order Cancelled event in mozu
	 */
	public QBResponse processOrderDelete(Integer tenantId, String orderId,
			String responseXml) throws Exception {
		QBXML deleteTxResp = (QBXML) xmlHelper.getUnmarshalledValue(responseXml);

		TxnDelRsType deleteTxRespType = (TxnDelRsType) deleteTxResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		// An error occurred while processing the order
		QBResponse qbResponse = new QBResponse();
		qbResponse.setStatusCode(deleteTxRespType.getStatusCode());
		qbResponse.setStatusSeverity(deleteTxRespType.getStatusSeverity());
		qbResponse.setStatusMessage(deleteTxRespType.getStatusMessage());
		if (qbResponse.hasError()) return qbResponse;
		
		MozuOrderDetail orderDetail = getMozuOrderDetail(tenantId, orderId);
		orderDetail.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		entityHandler.addEntity(tenantId, entityHandler.getOrderCancelledEntityName(), orderDetail);
		
		logger.debug((new StringBuilder())
				.append("Processed cancelling order with id: ")
				.append(orderId)
				.append(" with QB status code: ")
				.append(deleteTxRespType.getStatusCode())
				.append(" with status: ")
				.append(deleteTxRespType.getStatusMessage())
				.append(" for tenantId: ")
				.append(tenantId)
				.toString());
			
			return qbResponse;
		//}
	}
		
	
	public OrderCompareDetail getOrderCompareDetails(Integer tenantId,String orderId) throws Exception {
		
		Order order = getOrder(orderId, tenantId);
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderEntityName(), order.getOrderNumber().toString());
		
		if (node == null)
			throw new Exception("Posted Order not found to compare");
		
		QuickBooksOrder postedOrder = mapper.readValue(node.toString(), QuickBooksOrder.class);
		
		
		
		QuickBooksOrder newOrder = new QuickBooksOrder();
		
		if (order.getBillingInfo().getBillingContact().getAddress() != null)
			newOrder.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		
		newOrder.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		newOrder.setSubTotal(order.getSubtotal());
		newOrder.setTotalAmount(order.getTotal());
		newOrder.setOrderLines(new ArrayList<QuickBooksSavedOrderLine>() );
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, false);
		for(MozuOrderItem orderItem : orderItems) {
			QuickBooksSavedOrderLine savedOrderLine = new QuickBooksSavedOrderLine();
			if (orderItem.getQty() != null)
				savedOrderLine.setQuantity(orderItem.getQty());
			savedOrderLine.setAmount(orderItem.getAmount());
			savedOrderLine.setFullName(orderItem.getProductCode());
			newOrder.getOrderLines().add(savedOrderLine);
		}
		
		OrderCompareDetail compareDetail = new OrderCompareDetail();
		compareDetail.setPostedOrder(postedOrder);
		compareDetail.setUpdatedOrder(newOrder);
		
		return compareDetail;
		
	}

	
	private BillAddress getBillAddress(Address address) {
		BillAddress billAddress = new BillAddress();
		//Akshay 19-Nov-2014 - Just trim addr line 1 to 
		//Akshay 12-Dec-2014 - per request, need to join all 4 mozu addresses and then slice them up.
		QBDataValidationUtil.populateQBBillToAddrFromMozuAddr(
				billAddress, address);
		//billAddress.setAddr1(address.getAddress1());
		//prevent addr2 set up above from getting spoiled
		//Akshay 12-Dec-2014 -- removed address 2, 3 and 4 setting since now those get set in above method
		
		billAddress.setCity(address.getCityOrTown());
		billAddress.setState(address.getStateOrProvince());
		billAddress.setCountry(address.getCountryCode());
		billAddress.setPostalCode(address.getPostalOrZipCode());
		
		return billAddress;
	}
	
	private ShipAddress getShipAddress(Address address) {
		ShipAddress shipAddress = new ShipAddress();
		//Akshay 19-Nov-2014 - Just trim addr line 1 to 
		QBDataValidationUtil.populateQBShipToAddrFromMozuAddr(
				shipAddress, address);
		//shipAddress.setAddr1(address.getAddress1());
		//prevent addr2 set up above from getting spoiled
		//Akshay 12-Dec-2014 -- removed address 2, 3 and 4 setting since now those get set in above method
		
		shipAddress.setCity(address.getCityOrTown());
		shipAddress.setState(address.getStateOrProvince());
		shipAddress.setCountry(address.getCountryCode());
		shipAddress.setPostalCode(address.getPostalOrZipCode());
		
		return shipAddress;
	}
	
	public MozuOrderDetail getMozuOrderDetail(Integer tenantId, String orderId) throws Exception {
		Order order = getOrder(orderId, tenantId);
		return getMozuOrderDetail(order);
	}
	
	public MozuOrderDetail getMozuOrderDetail(Order order) {
		MozuOrderDetail orderDetail = new MozuOrderDetail();
		orderDetail.setId(order.getId());
		orderDetail.setOrderNumber(order.getOrderNumber());
		orderDetail.setCustomerEmail(order.getEmail());
		orderDetail.setOrderDate(String.valueOf(order.getAcceptedDate().toDate().getTime()));
		orderDetail.setUpdatedDate(String.valueOf(order.getAuditInfo().getUpdateDate().toDate().getTime()));
		orderDetail.setAmount(order.getTotal());
		return orderDetail;
	}
	
	private DateTimeZone getTimezoneOffset(Integer tenantId, Integer siteId) throws Exception {
		GeneralSettingsResource settingResource = new GeneralSettingsResource(new MozuApiContext(tenantId, siteId, 0,0));
		com.mozu.api.contracts.sitesettings.general.GeneralSettings setting = settingResource.getGeneralSettings();
		String offSetStr = "";
		CacheManager<TimeZoneCollection> cache =(CacheManager<TimeZoneCollection>)CacheManagerFactory.getCacheManager();
		String cacheKey = "timezones";
		TimeZoneCollection timeZones = cache.get(cacheKey);
		if (timeZones == null ) {
			ReferenceDataResource reference = new ReferenceDataResource();
			timeZones = reference.getTimeZones();
			cache.put(cacheKey, timeZones);
		} 
		for(TimeZone zone : timeZones.getItems()) {
			if (zone.getId().equals(setting.getSiteTimeZone())) {
				offSetStr = String.valueOf(zone.getOffset());
				break;
			}
		}
		
		
		String[] offSet = offSetStr.split("\\.");
		Integer hours = Integer.parseInt(offSet[0]);
		Integer minutes = 0;
		if (offSet.length == 2) {
			minutes = Integer.parseInt(offSet[1])*60*60;
		}
		DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(hours, minutes  );
		return zone;
	}
}