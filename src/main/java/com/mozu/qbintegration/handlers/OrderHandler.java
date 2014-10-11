package com.mozu.qbintegration.handlers;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuOrderItem;
import com.mozu.qbintegration.model.OrderCompareDetail;
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
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderRet;
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
import com.mozu.qbintegration.utils.XMLHelper;

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


	public boolean processOrderAdd(Integer tenantId, String orderId, String qbTaskResponse) throws Exception {
		QBXML orderAddResp = (QBXML)  XMLHelper.getUnmarshalledValue(qbTaskResponse);
		SalesReceiptAddRsType salesOrderResponse = (SalesReceiptAddRsType) orderAddResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		
		if (salesOrderResponse.getStatusSeverity().equalsIgnoreCase("error")) //An error occurred while processing the order
		{
			return false;
		} 
		else 
		{
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
			return true;
		}

	}
	
	public boolean processOrderUpdate(Integer tenantId, String orderId, String qbTaskResponse) throws Exception {
		QBXML orderModResp = (QBXML)  XMLHelper.getUnmarshalledValue(qbTaskResponse);

		SalesReceiptModRsType orderModRsType = (SalesReceiptModRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		
		if (orderModRsType.getStatusSeverity().equalsIgnoreCase("error"))
			return false;
		else {
			
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
			return true;
		}
	}
	
	public boolean processOrderQuery(int tenantId, String orderId, String qbResponse) throws Exception {
		
		QBXML orderModResp = (QBXML)  XMLHelper.getUnmarshalledValue(qbResponse);

		SalesReceiptQueryRsType  orderQueryRsType = (SalesReceiptQueryRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		
		if (orderQueryRsType.getStatusSeverity().equalsIgnoreCase("error"))
			throw new Exception(orderQueryRsType.getStatusMessage());
		else if (orderQueryRsType.getStatusSeverity().equalsIgnoreCase("warn"))
			return false;
		else {
			
			QuickBooksOrder order = getQuickBooksOrder(orderQueryRsType.getSalesReceiptRet().get(0));
			entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderEntityName(), order.getRefNumber(),order);
			return true;
		}
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
	
	
	public List<MozuOrderDetail> getMozuOrderDetails(Integer tenantId, String action, String orderBy) throws Exception {

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
		
		List<MozuOrderDetail> mozuOrders = new ArrayList<MozuOrderDetail>();
		
		
		try {
			List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityName, null, orderBy +" desc", null);
			if (nodes.size() > 0) {
				for (JsonNode node : nodes) {
					mozuOrders.add(mapper.readValue(node.toString(), MozuOrderDetail.class));
				}
			}
		} catch (Exception e) {
			logger.error("Error saving settings for tenant id: " + tenantId);
			throw e;
		}
		return mozuOrders;
		
	}
	
	public String getQBOrderQueryXml(int tenantId, Order order) throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		
		qbxmlMsgsRq.setOnError("stopOnError");
		
		SalesReceiptQueryRqType  salesOrderQueryRqType = new SalesReceiptQueryRqType();
		salesOrderQueryRqType.setRequestID(order.getId());
		
		salesOrderQueryRqType.setIncludeLineItems("true");

		salesOrderQueryRqType.getRefNumber().add(String.valueOf(order.getOrderNumber()));
		
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(salesOrderQueryRqType);
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);

		return XMLHelper.getMarshalledValue(qbxml);
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
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);

		if (order.getIsTaxExempt()) {
			CustomerSalesTaxCodeRef customerSalesTaxCodeRef = new CustomerSalesTaxCodeRef();
			customerSalesTaxCodeRef.setFullName("Non");
			salesReceiptAdd.setCustomerSalesTaxCodeRef(customerSalesTaxCodeRef);
		}
		
		salesReceiptAdd.setCustomerRef(customerRef);
		salesReceiptAdd.setRefNumber(String.valueOf(order.getOrderNumber()));
		salesReceiptAdd.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		salesReceiptAdd.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		salesReceiptAdd.setPaymentMethodRef(getPayment(order) );
		
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
			
			salesReceiptLineAdd.setSalesTaxCodeRef(getSalesTaxCodeRef(item.getTaxCode()));
			
			salesReceiptAdd.getSalesReceiptLineAddOrSalesReceiptLineGroupAdd().add(salesReceiptLineAdd);
		}

		return XMLHelper.getMarshalledValue(qbxml);
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
		salesReceiptmod.setPaymentMethodRef(getPayment(order));
		salesReceiptmod.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		salesReceiptmod.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		salesReceiptmod.setItemSalesTaxRef(getItemSalesTaxRef(order.getTaxTotal(), setting) );
		salesReceiptmod.setPaymentMethodRef(getPayment(order) );
		salesReceiptModRqType.setSalesReceiptMod(salesReceiptmod);
		
		NumberFormat numberFormat = new DecimalFormat("#.00");
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, true);
		for (MozuOrderItem item : orderItems) {
			TxnLineID txnLineId = new TxnLineID();
			if( salesReceiptRet.getOrderLines() != null) {
				for(QuickBooksSavedOrderLine qbItem: salesReceiptRet.getOrderLines()) {
					if(item.getProductCode().equalsIgnoreCase(qbItem.getFullName())) {
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
			salesReceiptLineMod.setSalesTaxCodeRef(getSalesTaxCodeRef(item.getTaxCode()));
			salesReceiptmod.getSalesReceiptLineModOrSalesReceiptLineGroupMod().add(salesReceiptLineMod);
		}
		return XMLHelper.getMarshalledValue(qbxml);
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
	
	
	private PaymentMethodRef getPayment(Order order) {
		PaymentMethodRef paymentRef = new PaymentMethodRef();
		for(Payment payment : order.getPayments()) {
			if (payment.getStatus().equalsIgnoreCase("voided")) continue;
			if (order.getPayments().get(0).getPaymentType().equalsIgnoreCase("check")) {
				paymentRef.setFullName(order.getPayments().get(0).getPaymentType());
			}
			else {
				paymentRef.setFullName(order.getPayments().get(0).getBillingInfo().getCard().getPaymentOrCardType());
			}
		}
		return paymentRef;
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
		return XMLHelper.getMarshalledValue(qbxml);
	}

	/*
	 * Process the response of cancelling a sales order in QB. 
	 * Initiated on Order Cancelled event in mozu
	 */
	public boolean processOrderDelete(Integer tenantId, String orderId,
			String qbResponse) throws Exception {
		QBXML deleteTxResp = (QBXML) XMLHelper.getUnmarshalledValue(qbResponse);

		TxnDelRsType deleteTxRespType = (TxnDelRsType) deleteTxResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		// An error occurred while processing the order
		if (deleteTxRespType.getStatusSeverity().equalsIgnoreCase("error")) {
			return false;
		} else {
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
			
			return true;
		}
		
	}
		
	
	public OrderCompareDetail getOrderCompareDetails(Integer tenantId,String orderId) throws Exception {
		
		Order order = getOrder(orderId, tenantId);
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderEntityName(), order.getOrderNumber().toString());
		
		if (node == null)
			throw new Exception("Posted Order not found to compare");
		
		QuickBooksOrder postedOrder = mapper.readValue(node.toString(), QuickBooksOrder.class);
		
		
		
		QuickBooksOrder newOrder = new QuickBooksOrder();
		
		newOrder.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		newOrder.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		newOrder.setSubTotal(order.getSubtotal());
		newOrder.setTotalAmount(order.getTotal());
		newOrder.setOrderLines(new ArrayList<QuickBooksSavedOrderLine>() );
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, false);
		for(MozuOrderItem orderItem : orderItems) {
			QuickBooksSavedOrderLine savedOrderLine = new QuickBooksSavedOrderLine();
			if (orderItem.getQty() == null)
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
		billAddress.setAddr1(address.getAddress1());
		billAddress.setAddr2(address.getAddress2());
		billAddress.setAddr3(address.getAddress3());
		billAddress.setAddr4(address.getAddress4());
		billAddress.setCity(address.getCityOrTown());
		billAddress.setState(address.getStateOrProvince());
		billAddress.setCountry(address.getCountryCode());
		billAddress.setPostalCode(address.getPostalOrZipCode());
		
		return billAddress;
	}
	
	private ShipAddress getShipAddress(Address address) {
		ShipAddress shipAddress = new ShipAddress();
		shipAddress.setAddr1(address.getAddress1());
		shipAddress.setAddr2(address.getAddress2());
		shipAddress.setAddr3(address.getAddress3());
		shipAddress.setAddr4(address.getAddress4());
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
}
