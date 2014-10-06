package com.mozu.qbintegration.handlers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.QuickBooksSavedOrderLine;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderMod;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRsType;
import com.mozu.qbintegration.service.QuickbooksServiceImpl;
import com.mozu.qbintegration.tasks.WorkTask;
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

	
	public MozuOrderDetail getOrderDetails(Integer tenantId,String orderId, String status,SalesOrderAddRsType salesOrderResponse) throws Exception {
		String qbTransactionId = null;
		List<Object> salesOrderLineRet = null;
		
		if(salesOrderResponse == null) {
			qbTransactionId = "";
		} else {
			qbTransactionId = salesOrderResponse.getSalesOrderRet().getTxnID();
			salesOrderLineRet = 
					salesOrderResponse.getSalesOrderRet().getSalesOrderLineRetOrSalesOrderLineGroupRet();
		}
		
		//Set the edit sequence to be used while updating
		String editSequence = "";
		
		if (salesOrderResponse != null)
			editSequence = salesOrderResponse.getSalesOrderRet().getEditSequence();
		
		return getOrderDetails(tenantId, orderId, status,qbTransactionId, editSequence, salesOrderLineRet);
	}
	
	public MozuOrderDetail getOrderUpdateDetails(Integer tenantId,String orderId, String status, SalesOrderModRsType salesOrderModResponse) throws Exception {
		
		String qbTransactionId = null;
		List<Object> salesOrderLineRet = null;
		
		if(salesOrderModResponse == null) {
			qbTransactionId = "";
		} else {
			qbTransactionId = salesOrderModResponse.getSalesOrderRet().getTxnID();
			salesOrderLineRet = 
					salesOrderModResponse.getSalesOrderRet().getSalesOrderLineRetOrSalesOrderLineGroupRet();
		}
		//Set the edit sequence to be used while updating
		
		String editSequence = "";
		if (salesOrderModResponse != null)
			editSequence = salesOrderModResponse.getSalesOrderRet().getEditSequence();
		
		return getOrderDetails(tenantId, orderId, status, qbTransactionId, editSequence, salesOrderLineRet);
	}
	
	public MozuOrderDetail getOrderDetails(Integer tenantId,String orderId, String status, String qbTransactionId, String editSequence, List<Object> salesOrderLineRet) throws Exception {
		
		Order order = getOrder(orderId, tenantId);

		CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		return getOrderDetails(order, custAcct, status, qbTransactionId,editSequence, salesOrderLineRet);
		
	}
	
	/*public MozuOrderDetail getOrderDetails(Order order, CustomerAccount custAcct, String status) {
		return getOrderDetails(order, custAcct, status, null,null, null);
	}*/
	
	public MozuOrderDetail getOrderDetails(Order order, CustomerAccount custAcct, String status, String qbTransactionId, String editSequence, List<Object> salesOrderLineRet) {
		MozuOrderDetail orderDetails = new MozuOrderDetail();
		orderDetails.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		orderDetails.setMozuOrderNumber(order.getOrderNumber().toString());
		orderDetails.setMozuOrderId(order.getId());
		orderDetails.setQuickbooksOrderListId(qbTransactionId);
		orderDetails.setOrderStatus(status);
		orderDetails.setCustomerEmail(custAcct.getEmailAddress());
		
		DateTimeFormatter timeFormat = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		orderDetails.setOrderDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setOrderUpdatedDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setConflictReason("");
		orderDetails.setAmount(String.valueOf(order.getSubtotal()));
		
		//Set item ids
		List<QuickBooksSavedOrderLine> savedLines = new ArrayList<QuickBooksSavedOrderLine>();
		QuickBooksSavedOrderLine savedOrderLine = null;
		SalesOrderLineRet orderLineRet = null;
		if(salesOrderLineRet != null) {
			//for(OrderItem item: order.getItems()) {
				for(Object returnedItem: salesOrderLineRet) {
					//if (item.getProduct().getProductCode().equalsIgnoreCase(orderLineRet.getItemRef().getFullName())) {
						orderLineRet = (SalesOrderLineRet) returnedItem;
						savedOrderLine = new QuickBooksSavedOrderLine();
						savedOrderLine.setProductCode(orderLineRet.getItemRef().getFullName());
						savedOrderLine.setQbLineItemTxnID(orderLineRet.getTxnLineID());
						if(orderLineRet.getQuantity() != null) { //Discount and Shipping might not hav qty - the way they are set up in QB matters
							savedOrderLine.setQuantity(Integer.valueOf(orderLineRet.getQuantity()));
						}
						savedLines.add(savedOrderLine);
					//}
				}
			//}

			orderDetails.setSavedOrderLinesList(savedLines);
		}
		
		//Set the edit sequence
		orderDetails.setEditSequence(editSequence);
		
		return orderDetails;
	}
	
	public boolean processOrderAdd(Integer tenantId, String orderId, String qbTaskResponse) throws Exception {
		QBXML orderAddResp = (QBXML)  XMLHelper.getUnmarshalledValue(qbTaskResponse);
		SalesOrderAddRsType salesOrderResponse = (SalesOrderAddRsType) orderAddResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		
		if (salesOrderResponse.getStatusSeverity().equalsIgnoreCase("error")) //An error occurred while processing the order
		{
			return false;
		} 
		else 
		{
			MozuOrderDetail orderDetails = getOrderDetails(tenantId, orderId, "POSTED",salesOrderResponse);
			saveOrderInEntityList(orderDetails,entityHandler.getOrderEntityName(), tenantId);
	
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

		SalesOrderModRsType orderModRsType = (SalesOrderModRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		
		if (orderModRsType.getStatusSeverity().equalsIgnoreCase("error"))
			return false;
		else {
			MozuOrderDetail orderDetails = getOrderUpdateDetails(tenantId,orderId, "POSTED", orderModRsType);
			saveOrderInEntityList(orderDetails,entityHandler.getOrderEntityName(), tenantId);
			
	
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
	
	public void saveOrderInEntityList(MozuOrderDetail orderDetails, String mapName,Integer tenantId) throws Exception {
		saveOrUpdateOrderInEL(orderDetails, mapName, tenantId, Boolean.FALSE);
	}
	
	public void updateOrderInEntityList(MozuOrderDetail orderDetails, String mapName,	Integer tenantId) throws Exception {
		saveOrUpdateOrderInEL(orderDetails, mapName, tenantId,  Boolean.TRUE);
	}
	
	//Used for saving to orders as well as updated orders entity lists
	private void saveOrUpdateOrderInEL(MozuOrderDetail orderDetails,String mapName, Integer tenantId, Boolean isUpdate) throws Exception {
		
		try {
			if(!isUpdate) {
				entityHandler.addUpdateEntity(tenantId, mapName, orderDetails.getEnteredTime(), orderDetails);
			} else {
				
				List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderUpdatedEntityName(), "mozuOrderId eq "+orderDetails.getMozuOrderId());

				if (nodes.size() > 0) { //Delete existing update
					MozuOrderDetail existing = mapper.readValue(nodes.get(0).toString(), MozuOrderDetail.class);
					entityHandler.deleteEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), existing.getEnteredTime());
				} 
				entityHandler.addUpdateEntity(tenantId, mapName, orderDetails.getEnteredTime(), orderDetails);
			}
		} catch (Exception e) {
			logger.error("Error saving order details for tenant id: " + tenantId);
			throw e;
		}
		
	}

	public List<MozuOrderDetail> getMozuOrderDetails(Integer tenantId, MozuOrderDetail mozuOrderDetails, String mapName) throws Exception {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId)); 
		
		StringBuilder sb = new StringBuilder();
		//Assuming status will never be null - it is meaningless to filter without it at this point.
		//TODO throw exception if status is null
		sb.append("orderStatus eq " + mozuOrderDetails.getOrderStatus());
		
		if(mozuOrderDetails.getMozuOrderNumber() != null) {
			sb.append(" and mozuOrderId eq " + mozuOrderDetails.getMozuOrderId());
		}

		List<MozuOrderDetail> mozuOrders = new ArrayList<MozuOrderDetail>();
		EntityCollection orderCollection = null;
		
		try {
			orderCollection = entityResource.getEntities(mapName, null, null, sb.toString(), "enteredTime desc", null);
			if (null != orderCollection) {
				for (JsonNode singleOrder : orderCollection.getItems()) {
					mozuOrders.add(mapper.readValue(singleOrder.toString(), MozuOrderDetail.class));
				}
			}
		} catch (Exception e) {
			logger.error("Error saving settings for tenant id: " + tenantId);
			throw e;
		}
		return mozuOrders;
		
	}
	
	public String getQBOrderSaveXML(int tenantId, String orderId) throws Exception {
		
		//Order singleOrder, String customerQBListID,List<String> itemListIDs
		Order order = getOrder(orderId, tenantId);
		CustomerAccount account = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		String customerQBListID = customerHandler.getQbCustomerId(tenantId, account.getEmailAddress());
		
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType salesOrderAddRqType = new com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(salesOrderAddRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesOrderAdd salesOrderAdd = new SalesOrderAdd();
		salesOrderAddRqType.setRequestID(order.getId());
		
		salesOrderAddRqType.setSalesOrderAdd(salesOrderAdd);
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);
		salesOrderAdd.setCustomerRef(customerRef);

		List<OrderItem> items = order.getItems();
		ItemRef itemRef = null;
		SalesOrderLineAdd salesOrderLineAdd = null;

		NumberFormat numberFormat = new DecimalFormat("#.00");
		Double productDiscounts = 0.0;
		for (OrderItem item : items) {
			itemRef = new ItemRef();
			String qbProductId = productHandler.getQBId(tenantId, item.getProduct().getProductCode());
			itemRef.setListID(qbProductId);
			salesOrderLineAdd = new SalesOrderLineAdd();
			if(item.getUnitPrice().getSaleAmount() != null) {
				salesOrderLineAdd.setAmount(numberFormat.format(
						item.getUnitPrice().getSaleAmount() * item.getQuantity()));
			} else {
				salesOrderLineAdd.setAmount(numberFormat.format(
						item.getUnitPrice().getListAmount() * item.getQuantity()));
			}
			salesOrderLineAdd.setItemRef(itemRef);
			salesOrderLineAdd.setQuantity(item.getQuantity().toString());
			salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(salesOrderLineAdd);
			
			if(item.getDiscountTotal() > 0.0) {
				productDiscounts += item.getDiscountTotal();
				addSOAddLineItemAmount(salesOrderAdd, numberFormat.format(item.getDiscountTotal()), "DISC-PRODUCT",1);
			}
		}

		addSOAddLineItemAmount(salesOrderAdd, numberFormat.format(order.getShippingTotal()), "Shipping",1);
		if (order.getDiscountTotal() > 0.0)
			addSOAddLineItemAmount(salesOrderAdd, numberFormat.format(order.getDiscountTotal() - productDiscounts), "DISC-ORDER",1);
		return XMLHelper.getMarshalledValue(qbxml);
	}

	public String getQBOrderUpdateXML(int tenantId, String orderId) throws Exception {
		//Order singleOrder, String customerQBListID, List<String> itemListIDs,
		Order order = getOrder(orderId, tenantId);
		CustomerAccount account = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		String customerQBListID = customerHandler.getQbCustomerId(tenantId, account.getEmailAddress());
		
		//get to the top posted order
		MozuOrderDetail mozuOrderDetails = new MozuOrderDetail();
		mozuOrderDetails.setOrderStatus("POSTED");
		mozuOrderDetails.setMozuOrderId(order.getId());
		List<MozuOrderDetail> postedOrders = getMozuOrderDetails(tenantId, mozuOrderDetails, entityHandler.getOrderEntityName());
		MozuOrderDetail postedOrder = postedOrders.get(0);
		
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRqType salesOrderModRqType = 
				new com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(salesOrderModRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesOrderMod salesOrdermod = new SalesOrderMod();
		salesOrderModRqType.setRequestID(order.getId());
		
		salesOrderModRqType.setSalesOrderMod(salesOrdermod);
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);
		salesOrdermod.setCustomerRef(customerRef);
		
		salesOrdermod.setTxnID(postedOrder.getQuickbooksOrderListId());
		salesOrdermod.setEditSequence(postedOrder.getEditSequence());
		
		List<OrderItem> items = order.getItems();
		ItemRef itemRef = null;
		SalesOrderLineMod salesOrderLineMod = null;

		NumberFormat numberFormat = new DecimalFormat("#.00");
		Double productDiscounts = 0.0;
		for (OrderItem item : items) {
			itemRef = new ItemRef();
			String qbProductId = productHandler.getQBId(tenantId, item.getProduct().getProductCode());
			itemRef.setListID(qbProductId);
			salesOrderLineMod = new SalesOrderLineMod();
			
			if(postedOrder.getSavedOrderLinesList() != null) {
				for(QuickBooksSavedOrderLine singleLine: postedOrder.getSavedOrderLinesList()) {
					if(singleLine.getProductCode().toLowerCase().equals(item.getProduct().getProductCode().toLowerCase())) {
						salesOrderLineMod.setTxnLineID(singleLine.getQbLineItemTxnID());
					}
				}
				if (StringUtils.isEmpty(salesOrderLineMod.getTxnLineID())) {
					salesOrderLineMod.setTxnLineID("-1");
				}
			}
			
			if(item.getUnitPrice().getSaleAmount() != null) {
				salesOrderLineMod.setAmount(numberFormat.format(
						item.getUnitPrice().getSaleAmount() * item.getQuantity()));
			} else {
				salesOrderLineMod.setAmount(numberFormat.format(
						item.getUnitPrice().getListAmount() * item.getQuantity()));
			}
			salesOrderLineMod.setQuantity(item.getQuantity().toString());
			salesOrderLineMod.setItemRef(itemRef);
			
			salesOrdermod.getSalesOrderLineModOrSalesOrderLineGroupMod().add(salesOrderLineMod);
			if(item.getDiscountTotal() > 0.0) {
				productDiscounts += item.getDiscountTotal();
				addSOModLineItemAmount(salesOrdermod, numberFormat.format(item.getDiscountTotal()), "DISC-PRODUCT",1);
			}
		}

		addSOModLineItemAmount(salesOrdermod, numberFormat.format(order.getShippingTotal()), "Shipping",1);
		if (order.getDiscountTotal() > 0.0)
			addSOModLineItemAmount(salesOrdermod, numberFormat.format(order.getDiscountTotal() - productDiscounts), "DISC-ORDER",1);
		return XMLHelper.getMarshalledValue(qbxml);
	}

	private void addSOAddLineItemAmount(SalesOrderAdd salesOrderAdd, String amount, String fieldName, Integer qty) {
		SalesOrderLineAdd salesOrderLineAdd = new SalesOrderLineAdd();
		salesOrderLineAdd.setAmount(amount);
		ItemRef itemRef = new ItemRef();
		itemRef.setFullName(fieldName);
		salesOrderLineAdd.setItemRef(itemRef);
		//salesOrderLineAdd.setQuantity(String.valueOf(qty));
		salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(salesOrderLineAdd);
	}
	
	private void addSOModLineItemAmount(SalesOrderMod salesOrdermod, String amount, String fieldName, Integer qty) {
		SalesOrderLineMod salesOrderLineMod = new SalesOrderLineMod();
		salesOrderLineMod.setAmount(amount);
		ItemRef itemRef = new ItemRef();
		itemRef.setFullName(fieldName);
		salesOrderLineMod.setItemRef(itemRef);
		salesOrderLineMod.setTxnLineID("-1");
		//salesOrderLineMod.setQuantity(String.valueOf(qty));
		salesOrdermod.getSalesOrderLineModOrSalesOrderLineGroupMod().add(salesOrderLineMod);
	}
	
	/*
	 * Get order delete XML
	 */
	public String getQBOrderDeleteXML(final Integer tenantId, final String orderId) throws Exception {
		
		QBXML qbxml = null; 
		MozuOrderDetail criteria = new MozuOrderDetail();
		criteria.setOrderStatus("POSTED");
		criteria.setMozuOrderId(orderId);
		List<MozuOrderDetail> results = getMozuOrderDetails(tenantId, criteria, entityHandler.getOrderEntityName());
		
		if(!results.isEmpty()) {
			MozuOrderDetail singleResult = results.get(0);
			TxnDelRqType deleteTx = new TxnDelRqType();
			deleteTx.setRequestID(orderId);
			deleteTx.setTxnDelType("SalesOrder");
			deleteTx.setTxnID(singleResult.getQuickbooksOrderListId());
			
			qbxml = new QBXML();
			QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
			qbxmlMsgsRq.setOnError("stopOnError");
			qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
			qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(deleteTx);
			
		} else {
			throw new Exception("No orders found in posted status to delete with order id: " + orderId
					+ " for tenant id: " + tenantId);
		}
			
		return XMLHelper.getMarshalledValue(qbxml);
	}


	/*
	 * Process the response of cancelling a sales order in QB. 
	 * Initiated on Order Cancelled event in mozu
	 */
	public boolean processOrderDelete(Integer tenantId, String id,
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
			return true;
		}
		
	}
}
