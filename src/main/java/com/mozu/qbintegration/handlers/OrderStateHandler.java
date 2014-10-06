package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.XMLHelper;

@Component
public class OrderStateHandler {

	private static final Logger logger = LoggerFactory.getLogger(OrderStateHandler.class);
	private static ObjectMapper mapper = JsonUtils.initObjectMapper();
	
	@Autowired
	CustomerHandler customerHandler;
	
	@Autowired
	private QueueManagerService queueManagerService;
	
	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	private OrderHandler orderHandler;
	
	@Autowired
	private ProductHandler productHandler;
	
	@Autowired
	EntityHandler entityHandler;

	public void processOrder(String orderId, ApiContext apiContext) throws Exception {
		Integer tenantId = apiContext.getTenantId();
		Order order = orderHandler.getOrder(orderId, tenantId);
		if(order.getAcceptedDate() != null) { //log only if order has been previously submitted (accepted)
			final CustomerAccount orderingCust = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
			
			//Check if order has been processed, if not put in process Queue
			boolean isProcessed = isOrderProcessed(tenantId, order.getId());
			boolean isOrderInConflict = isOrderInConflict(tenantId, order.getId());
			
			if (isProcessed && !isOrderInConflict) { //Add to update queue
				
				//Get Posted order
				List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), "mozuOrderId eq "+orderId+" and orderStatus eq POSTED");
				String quickbooksOrderListId = null;
				String quickbooksEditSequence = null;
				if (nodes.size() > 0) {
					MozuOrderDetail previousOrder = mapper.readValue(nodes.get(0).toString(), MozuOrderDetail.class);
					quickbooksOrderListId = previousOrder.getQuickbooksOrderListId();
					quickbooksEditSequence = previousOrder.getEditSequence();
				}
				MozuOrderDetail mozuOrderDetails = orderHandler.getOrderDetails(order, orderingCust, "Updated", quickbooksOrderListId,quickbooksEditSequence, null );
				orderHandler.updateOrderInEntityList(mozuOrderDetails, entityHandler.getOrderUpdatedEntityName(), tenantId);
			} else if (!isOrderInProcessing(tenantId, orderId)) { //Add to queue for processing
				//quickbooksService.saveOrderInQuickbooks(order, tenantId);
				transitionState(orderId, tenantId, null, false );
			}
		}
	}
	
	public void transitionState(String orderId, Integer tenantId, String qbResponse, boolean isUpdate) throws Exception {
		Order order = orderHandler.getOrder(orderId, tenantId);
		final CustomerAccount orderingCust = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		transitionState(tenantId,order, orderingCust, qbResponse, isUpdate);
	}
	
	public void transitionState( Integer tenantId, Order order,CustomerAccount custAcct,String qbResponse, boolean isUpdate) throws Exception {
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getTaskqueueEntityName(), order.getId());
		String currentStep = StringUtils.EMPTY;
		if (node != null) {
			WorkTask task = mapper.readValue(node.toString(), WorkTask.class);
			currentStep = task.getCurrentStep();
		}
		
		boolean queryResult = false;
		
		if (StringUtils.isNotEmpty(qbResponse))
			queryResult = processCurrentStep(tenantId, order, custAcct, currentStep,qbResponse);
		
		String nextStep = getNextStep(tenantId, order, custAcct,currentStep, queryResult, isUpdate); //Need to incorporate reponse from QB
		
		
		if (node == null) {
			queueManagerService.addTask(tenantId, order.getId(), "Order", nextStep, (isUpdate ? "Update" : "Add"));
		} else {
			String status = "PROCESSING";
			if (nextStep.equals(currentStep) || nextStep.equalsIgnoreCase("conflict"))
				status = "COMPLETED";
			
			if (nextStep.equalsIgnoreCase("conflict"))
				addToConflictQueue(tenantId, order, qbResponse);
			queueManagerService.updateTask(tenantId,order.getId(), nextStep, status);
		}
	}
	
	public void retryConflicOrders(Integer tenantId,List<String> orderNumberList) throws Exception {
		
		for(String mozuOrderNum: orderNumberList) {
			
			List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), "mozuOrderId eq "+mozuOrderNum, "enteredTime desc", 200);
			for(JsonNode node : nodes) { //there should be only one ever...just to cover the .001% :)
				MozuOrderDetail conflictOrder = mapper.readValue(node.toString(), MozuOrderDetail.class);
				conflictOrder.setOrderStatus("RETIRED");
				entityHandler.updateEntity(tenantId, entityHandler.getOrderEntityName(), conflictOrder.getEnteredTime(), conflictOrder);
				logger.debug("Updated order number: " + mozuOrderNum + " to RETRIED for tenant ID: " + tenantId);
			}
			
			processOrder(mozuOrderNum, new MozuApiContext(tenantId));
			logger.debug("Slotted conflicted order for retry with order number: " + mozuOrderNum + " for tenant: " + tenantId);
		}

		logger.debug("Slotted all conflicted orders for retry for tenant: " + tenantId);
	}
	
	
	public void addUpdatesToQueue(List<String> orderNumberList,Integer tenantId) throws Exception {
		
		for(String mozuOrderNum: orderNumberList) {
			transitionState(mozuOrderNum, tenantId, null, true);
			List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderUpdatedEntityName(), "mozuOrderId eq "+mozuOrderNum);
			for(JsonNode node : nodes) {
				MozuOrderDetail orderDetail = mapper.readValue(node.toString(), MozuOrderDetail.class);
				entityHandler.deleteEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), orderDetail.getEnteredTime());
			}
			logger.debug("Slotted an order update task for mozu order number: " + mozuOrderNum);
		}
		
	}
	private void addToConflictQueue(Integer tenantId, Order order, String qbResponse) throws Exception {
		MozuOrderDetail orderDetails = orderHandler.getOrderDetails(tenantId, order.getId(), "CONFLICT", null);
		
		
				// Make entry in conflict reason table
		// Log the not found product in error conflict
		QBXML qbXml = (QBXML)  XMLHelper.getUnmarshalledValue(qbResponse);
		Object object = qbXml.getQBXMLMsgsRs().getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs().get(0);
		if (object instanceof ItemQueryRsType) {
			
			List<Object> searchResults = qbXml.getQBXMLMsgsRs().getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
			
			for(Object obj : searchResults) {
				ItemQueryRsType itemSearchResponse = (ItemQueryRsType)obj;
				
				if (500 == itemSearchResponse.getStatusCode().intValue()&& "warn".equalsIgnoreCase(itemSearchResponse.getStatusSeverity())) {
					OrderConflictDetail conflictDetail = new OrderConflictDetail();
					conflictDetail.setMozuOrderNumber(String.valueOf(order.getOrderNumber()));
					conflictDetail.setNatureOfConflict("Not Found");
					for(OrderItem item : order.getItems()) {
						if (itemSearchResponse.getStatusMessage().toLowerCase().contains(item.getProduct().getProductCode().toLowerCase()))
								conflictDetail.setDataToFix(item.getProduct().getProductCode());
					}
					conflictDetail.setConflictReason(itemSearchResponse.getStatusMessage());
			
					conflictDetail.setEnteredTime(String.valueOf(System.currentTimeMillis()));
					conflictDetail.setMozuOrderId(order.getId());
					entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderConflictEntityName(), conflictDetail.getEnteredTime(), conflictDetail);
					logger.debug("Saved conflict for: "+ itemSearchResponse.getStatusMessage());
				}
			}
			
			orderDetails.setConflictReason("Product(s) are missing");
		} else if (object instanceof SalesOrderAddRsType) {
			SalesOrderAddRsType salesOrderAddRsType = (SalesOrderAddRsType)object;
			orderDetails.setConflictReason(salesOrderAddRsType.getStatusMessage());
		} else if (object instanceof SalesOrderModRsType) {
			SalesOrderModRsType salesOrderAddRsType = (SalesOrderModRsType)object;
			orderDetails.setConflictReason(salesOrderAddRsType.getStatusMessage());
		} else if (object instanceof CustomerAddRsType) {
			CustomerAddRsType custAddRsType = (CustomerAddRsType)object;
			orderDetails.setConflictReason(custAddRsType.getStatusMessage());
		}
		
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), "mozuOrderId eq "+order.getId()+" and orderStatus eq CONFLICT");
		String id = orderDetails.getEnteredTime();
		if (nodes.size() > 0) {
			MozuOrderDetail updateOrderDetail = mapper.readValue(nodes.get(0).toString(), MozuOrderDetail.class);
			id = updateOrderDetail.getEnteredTime();
		}
		entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderEntityName(), id, orderDetails);
	}
	
	private String getNextStep(Integer tenantId, Order order,CustomerAccount custAcct,String currentStep, boolean queryResult, boolean isUpdate) throws Exception {
		
		if (currentStep.equalsIgnoreCase("cust_query") && !queryResult)
			return "CUST_ADD";
		else if ((currentStep.equalsIgnoreCase("item_query") ||  currentStep.equalsIgnoreCase("order_add") || currentStep.equalsIgnoreCase("order_update")) && !queryResult)
			return "CONFLICT";
		else if (!isCustomerFound(tenantId, custAcct.getEmailAddress())) {
			return "CUST_QUERY";
		} else if (!allItemsFound(tenantId, order.getItems())) {
			return "ITEM_QUERY";
		} else if (isUpdate) {
			return "ORDER_UPDATE";
		} else {
			return "ORDER_ADD";
		}
	}
	
	private boolean isCustomerFound(Integer tenantId, String emailAddress) throws Exception {
		String qbId = customerHandler.getQbCustomerId(tenantId, emailAddress);
		
		return !StringUtils.isEmpty(qbId);
	}

	private boolean allItemsFound(Integer tenantId, List<OrderItem> orderItems) throws Exception {
		for (OrderItem item : orderItems) {
			String itemListId = productHandler.getQBId(tenantId, item.getProduct().getProductCode());
			if (null == itemListId) {
				return false;
			} 
		}
		
		return true;
	}

	
	
	private boolean isOrderInProcessing(Integer tenantId, String orderId) throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId));
		try {
			JsonNode node = entityResource.getEntity(entityHandler.getOrderEntityName(), orderId);
			return node != null;
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
		
		return false;
	}
	
	private boolean isOrderProcessed(Integer tenantId,  String orderId) throws Exception {
		
		String filterCriteria = "mozuOrderId eq "+orderId+" and orderStatus eq POSTED";
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), filterCriteria );
		
		return nodes.size() > 0;
		
	}
	
	private boolean isOrderInConflict(Integer tenantId, String orderId) throws Exception {
		
		String filterCriteria = "mozuOrderId eq "+orderId+" and orderStatus eq CONFLICT";
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderEntityName(), filterCriteria );
		
		return nodes.size() > 0;
	}	

	private boolean processCurrentStep(Integer tenantId, Order order,CustomerAccount custAcct, String currentStep, String qbResponse) throws Exception {

		switch(currentStep.toLowerCase()) {
			case "cust_query" :
				return customerHandler.processCustomerQuery(tenantId, custAcct,qbResponse);
			case "cust_add":
				return customerHandler.processCustomerAdd(tenantId, custAcct,qbResponse);
			case "order_add":
				return orderHandler.processOrderAdd(tenantId, order.getId(),qbResponse); //TODO : pass order object
			case "order_update":
				return orderHandler.processOrderUpdate(tenantId, order.getId(),qbResponse); //TODO : pass order object
			case "item_query":
				return productHandler.processItemQuery(tenantId,qbResponse);
			default:
				throw new Exception("Not supported");
		}
	}
}
