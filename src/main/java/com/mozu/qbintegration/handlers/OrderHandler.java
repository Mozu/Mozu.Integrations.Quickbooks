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
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuOrderItem;
import com.mozu.qbintegration.model.QuickBooksSavedOrderLine;
import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.RefNumberFilter;
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
import com.mozu.qbintegration.model.qbmodel.allgen.ShipAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.TxnDelRsType;
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

		
	public MozuOrderDetail getOrderDetails(Integer tenantId,String orderId, String status,  SalesOrderRet salesOrderRet) throws Exception {
		Order order = getOrder(orderId, tenantId);
		CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		return getOrderDetails(tenantId, order, custAcct, status, salesOrderRet);
		
	}
	
	
	public MozuOrderDetail getOrderDetails(Integer tenantId, Order order, CustomerAccount custAcct, String status, SalesOrderRet salesOrderRet) throws Exception {
		List<Object> salesOrderLineRet = null;
		/*
		 * Bug fix 9-oct-2014: this field is an indexed property in EL. 
		 * Cannot be null. So made it "". Was failing in case of order delete
		 */
		String qbTransactionId = "";
		String editSequence = null;
		
		if(salesOrderRet != null) {
			qbTransactionId = salesOrderRet.getTxnID();
			salesOrderLineRet = salesOrderRet.getSalesOrderLineRetOrSalesOrderLineGroupRet();
			editSequence = salesOrderRet.getEditSequence();
		} else if (status.equalsIgnoreCase("updated")) {
			//Get Posted order
			List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), "mozuOrderId eq "+order.getId()+" and orderStatus eq POSTED");
			if (nodes.size() > 0) {
				MozuOrderDetail previousOrder = mapper.readValue(nodes.get(0).toString(), MozuOrderDetail.class);
				qbTransactionId = previousOrder.getQuickbooksOrderListId();
				editSequence = previousOrder.getEditSequence();
			}
			
		} 
		
		MozuOrderDetail orderDetails = new MozuOrderDetail();
		orderDetails.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		orderDetails.setMozuOrderNumber(order.getOrderNumber().toString());
		orderDetails.setMozuOrderId(order.getId());
		orderDetails.setQuickbooksOrderListId(qbTransactionId);
		orderDetails.setOrderStatus(status);
		orderDetails.setCustomerEmail(custAcct.getEmailAddress());
		
		if (salesOrderRet != null) {
			orderDetails.setBillToAddress(getAddress(salesOrderRet.getBillAddress()));
			orderDetails.setShipToAddress(getAddress(salesOrderRet.getShipAddress()));
		}
		
		//DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		orderDetails.setOrderDate(String.valueOf(order.getAcceptedDate().toDate().getTime()));
		orderDetails.setOrderUpdatedDate(String.valueOf(order.getAuditInfo().getUpdateDate().toDate().getTime()));
		orderDetails.setConflictReason("");
		orderDetails.setAmount(String.valueOf(order.getTotal()));
		
		//Set item ids
		List<QuickBooksSavedOrderLine> savedLines = new ArrayList<QuickBooksSavedOrderLine>();
		QuickBooksSavedOrderLine savedOrderLine = null;
		SalesOrderLineRet orderLineRet = null;
		if(salesOrderLineRet != null) {
			for(Object returnedItem: salesOrderLineRet) {
					orderLineRet = (SalesOrderLineRet) returnedItem;
					savedOrderLine = new QuickBooksSavedOrderLine();
					savedOrderLine.setProductCode(orderLineRet.getItemRef().getFullName());
					savedOrderLine.setQbLineItemTxnID(orderLineRet.getTxnLineID());
					if(orderLineRet.getQuantity() != null) { //Discount and Shipping might not hav qty - the way they are set up in QB matters
						savedOrderLine.setQuantity(Integer.valueOf(orderLineRet.getQuantity()));
					}
					savedLines.add(savedOrderLine);
			}
			orderDetails.setSavedOrderLinesList(savedLines);
		}
		
		//Set the edit sequence
		orderDetails.setEditSequence(editSequence);
		
		//8-Oct-2014 Add orderitems so that updates can be compared
		orderDetails.setOrderItems(order.getItems());
		
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
			MozuOrderDetail orderDetails = getOrderDetails(tenantId, orderId, "POSTED",salesOrderResponse.getSalesOrderRet());
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
			MozuOrderDetail orderDetails = getOrderDetails(tenantId,orderId, "POSTED", orderModRsType.getSalesOrderRet());
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
	
	public boolean processOrderQuery(int tenantId, String orderId, String qbResponse) throws Exception {
		
		QBXML orderModResp = (QBXML)  XMLHelper.getUnmarshalledValue(qbResponse);

		SalesOrderQueryRsType  orderQueryRsType = (SalesOrderQueryRsType ) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		
		if (orderQueryRsType.getStatusSeverity().equalsIgnoreCase("error"))
			throw new Exception(orderQueryRsType.getStatusMessage());
		else if (orderQueryRsType.getStatusSeverity().equalsIgnoreCase("warn"))
			return false;
		else {
			MozuOrderDetail orderDetails = getOrderDetails(tenantId,orderId, "POSTED", orderQueryRsType.getSalesOrderRet().get(0));
			saveOrderInEntityList(orderDetails,entityHandler.getOrderEntityName(), tenantId);
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
				
				List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderUpdatedEntityName(), 
						"mozuOrderId eq "+orderDetails.getMozuOrderId() + " and orderStatus eq UPDATED");

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
	
	public String getQBOrderQueryXml(int tenantId, Order order) throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		
		qbxmlMsgsRq.setOnError("stopOnError");
		
		SalesOrderQueryRqType  salesOrderQueryRqType = new SalesOrderQueryRqType();
		salesOrderQueryRqType.setRequestID(order.getId());
		
		salesOrderQueryRqType.setIncludeLineItems("true");
		salesOrderQueryRqType.setIncludeLinkedTxns("true");

		salesOrderQueryRqType.getRefNumber().add(String.valueOf(order.getOrderNumber()));
		//RefNumberFilter refNumberFilter = new RefNumberFilter();
		//refNumberFilter.setMatchCriterion("contains");
		//refNumberFilter.setRefNumber(String.valueOf(order.getOrderNumber()));
		//salesOrderQueryRqType.setRefNumberFilter(refNumberFilter);
		
		
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(salesOrderQueryRqType);
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);

		return XMLHelper.getMarshalledValue(qbxml);
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
		salesOrderAdd.setRefNumber(String.valueOf(order.getOrderNumber()));
		salesOrderAdd.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		salesOrderAdd.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, true);
		NumberFormat numberFormat = new DecimalFormat("#.00");
		//Double productDiscounts = 0.0;
		for (MozuOrderItem item : orderItems) {
			if (item.isMic()) {
				addSOAddLineItemAmount(salesOrderAdd, numberFormat.format(item.getAmount()), item.getQbItemCode(), item.getDescription());
			} else {
				ItemRef itemRef = new ItemRef();
				itemRef.setListID(item.getQbItemCode());
				SalesOrderLineAdd salesOrderLineAdd = new SalesOrderLineAdd();
				salesOrderLineAdd.setAmount(numberFormat.format(item.getTotalAmount()));
				salesOrderLineAdd.setItemRef(itemRef);
				salesOrderLineAdd.setDesc(item.getDescription());
				salesOrderLineAdd.setQuantity(item.getQty().toString());
				salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(salesOrderLineAdd);
			}
			
		}

		return XMLHelper.getMarshalledValue(qbxml);
	}

	public String getQBOrderUpdateXML(int tenantId, String orderId) throws Exception {
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
		
		salesOrdermod.setRefNumber(String.valueOf(order.getOrderNumber()));
		
		
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);
		salesOrdermod.setCustomerRef(customerRef);
		
		salesOrdermod.setTxnID(postedOrder.getQuickbooksOrderListId());
		salesOrdermod.setEditSequence(postedOrder.getEditSequence());
		
		salesOrdermod.setBillAddress(getBillAddress(order.getBillingInfo().getBillingContact().getAddress()));
		salesOrdermod.setShipAddress(getShipAddress(order.getFulfillmentInfo().getFulfillmentContact().getAddress()));
		
		salesOrderModRqType.setSalesOrderMod(salesOrdermod);
		
		NumberFormat numberFormat = new DecimalFormat("#.00");
		//Double productDiscounts = 0.0;
		List<MozuOrderItem> orderItems = productHandler.getProductCodes(tenantId, order, true);
		for (MozuOrderItem item : orderItems) {
			if (item.isMic()) {
				addSOModLineItemAmount(salesOrdermod, numberFormat.format(item.getAmount()), item.getQbItemCode(), item.getDescription());
			} else {
				ItemRef itemRef = new ItemRef();
				itemRef.setListID(item.getQbItemCode());
				SalesOrderLineMod salesOrderLineMod = new SalesOrderLineMod();
				
				if(postedOrder.getSavedOrderLinesList() != null) {
					for(QuickBooksSavedOrderLine singleLine: postedOrder.getSavedOrderLinesList()) {
						if(singleLine.getProductCode().equalsIgnoreCase(item.getProductCode())) {
							salesOrderLineMod.setTxnLineID(singleLine.getQbLineItemTxnID());
						}
					}
					if (StringUtils.isEmpty(salesOrderLineMod.getTxnLineID())) {
						salesOrderLineMod.setTxnLineID("-1");
					}
				}
				
				salesOrderLineMod.setAmount(numberFormat.format(item.getTotalAmount()));
				salesOrderLineMod.setQuantity(item.getQty().toString());
				salesOrderLineMod.setDesc(item.getDescription());
				salesOrderLineMod.setItemRef(itemRef);
				
				salesOrdermod.getSalesOrderLineModOrSalesOrderLineGroupMod().add(salesOrderLineMod);

			}
		}
		return XMLHelper.getMarshalledValue(qbxml);
	}

	private void addSOAddLineItemAmount(SalesOrderAdd salesOrderAdd, String amount, String fieldName, String descrption) {
		SalesOrderLineAdd salesOrderLineAdd = new SalesOrderLineAdd();
		salesOrderLineAdd.setAmount(amount);
		ItemRef itemRef = new ItemRef();
		itemRef.setFullName(fieldName);
		salesOrderLineAdd.setDesc(descrption);
		salesOrderLineAdd.setItemRef(itemRef);
		//salesOrderLineAdd.setQuantity(String.valueOf(qty));
		salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(salesOrderLineAdd);
	}
	
	private void addSOModLineItemAmount(SalesOrderMod salesOrdermod, String amount, String fieldName, String descrption) {
		SalesOrderLineMod salesOrderLineMod = new SalesOrderLineMod();
		salesOrderLineMod.setAmount(amount);
		ItemRef itemRef = new ItemRef();
		itemRef.setFullName(fieldName);
		salesOrderLineMod.setItemRef(itemRef);
		salesOrderLineMod.setTxnLineID("-1");
		salesOrderLineMod.setDesc(descrption);
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
			
			MozuOrderDetail orderCancelDetails = getOrderCancelDetails(tenantId, orderId, "CANCELLED", deleteTxRespType);
			saveOrderInEntityList(orderCancelDetails, entityHandler.getOrderEntityName(), tenantId); 
	
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

	
	
	private MozuOrderDetail getOrderCancelDetails(Integer tenantId,
			String orderId, String status, TxnDelRsType deleteTxRespType) throws Exception {
		return getOrderDetails(tenantId, orderId, status, null);
	}

	private Address getAddress(BillAddress address) {
		Address addr = new Address();
		addr.setAddress1(address.getAddr1());
		addr.setAddress2(address.getAddr2());
		addr.setAddress3(address.getAddr3());
		addr.setAddress4(address.getAddr4());
		addr.setCityOrTown(address.getCity());
		addr.setStateOrProvince(address.getState());
		addr.setCountryCode(address.getCountry());
		addr.setPostalOrZipCode(address.getPostalCode());
		return addr;
	}
	
	private Address getAddress(ShipAddress address) {
		Address addr = new Address();
		addr.setAddress1(address.getAddr1());
		addr.setAddress2(address.getAddr2());
		addr.setAddress3(address.getAddr3());
		addr.setAddress4(address.getAddr4());
		addr.setCityOrTown(address.getCity());
		addr.setStateOrProvince(address.getState());
		addr.setCountryCode(address.getCountry());
		addr.setPostalOrZipCode(address.getPostalCode());
		return addr;
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
}
