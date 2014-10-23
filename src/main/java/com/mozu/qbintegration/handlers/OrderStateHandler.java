package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuOrderItem;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.OrderStates;
import com.mozu.qbintegration.model.QBResponse;
import com.mozu.qbintegration.model.WorkTaskActions;
import com.mozu.qbintegration.model.WorkTaskStatus;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.service.XMLService;
import com.mozu.qbintegration.tasks.WorkTask;

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
	
	@Autowired
	XMLService xmlHelper;

	private boolean orderExistsInQB = false;
	private String conflictReason = null;
	
	private QBResponse processStatus = new QBResponse();
	//public static String CONFLICT = "CONFLICT";
	
	/*
	 * Bug fix 9-Oct-2014: Added order_delete as a state in the queue
	 */
	private List<String> lastSteps = new ArrayList<String>(){
		{add("order_add"); add("order_update"); add("order_cancel"); add("conflict");add("order_delete");}};
	
	public void processOrder(String orderId, ApiContext apiContext) throws Exception {
		Integer tenantId = apiContext.getTenantId();
		Order order = orderHandler.getOrder(orderId, tenantId);
		if(order.getAcceptedDate() != null) { //log only if order has been previously submitted (accepted)
			final CustomerAccount orderingCust = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
			
			//Check if order has been processed, if not put in process Queue
			boolean isProcessed = isOrderProcessed(tenantId, order.getId());
			boolean isOrderInConflict = isOrderInConflict(tenantId, order.getId());
			boolean isOrderInProcessing = isOrderInProcessing(tenantId, orderId);
			if (!isOrderInProcessing) {
				if (isProcessed && !isOrderInConflict) { //Add to update queue
					MozuOrderDetail orderDetails = orderHandler.getMozuOrderDetail(order);
					entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), orderDetails.getId(), orderDetails);
				} else  { //Add to queue for processing
					transitionState(tenantId, order, orderingCust, null, "Add" );
				}
			}
		}
	}
	
	public void transitionState(String orderId, Integer tenantId, String qbResponse, String action) throws Exception {
		Order order = orderHandler.getOrder(orderId, tenantId);
		final CustomerAccount orderingCust = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		action = (order.getStatus().equalsIgnoreCase("cancelled") ? "Delete" : action); //Akshay: bug fix - Cancel was being compared with Delete
		transitionState(tenantId,order, orderingCust, qbResponse, action);
	}
	
	public void transitionState( Integer tenantId, Order order,CustomerAccount custAcct,String qbResponse, String action) throws Exception {
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getTaskqueueEntityName(), order.getId());
		String currentStep = StringUtils.EMPTY;
		if (node != null) {
			WorkTask task = mapper.readValue(node.toString(), WorkTask.class);
			currentStep = task.getCurrentStep();
		}
		
		//boolean processResult = false;
		if (StringUtils.isNotEmpty(qbResponse))
			processCurrentStep(tenantId, order, custAcct, currentStep,qbResponse);
		
		String nextStep = getNextStep(tenantId, order, custAcct,currentStep, action); //Need to incorporate reponse from QB
		
		if (node == null) {
			queueManagerService.addTask(tenantId, order.getId(), "Order", nextStep, action);
		} else {
			String status = WorkTaskStatus.PROCESSING;
			if (lastSteps.contains(currentStep.toLowerCase()) && !processStatus.hasError())
				status = WorkTaskStatus.COMPLETED;
			
			if (nextStep.equalsIgnoreCase(OrderStates.CONFLICT)) {
				addToConflictQueue(tenantId, order, qbResponse, conflictReason);
				status = WorkTaskStatus.COMPLETED;
			}
			queueManagerService.updateTask(tenantId,order.getId(), nextStep, status);
		}
	}
	
	public void retryConflicOrders(Integer tenantId,List<String> orderNumberList, String action) throws Exception {
		
		for(String orderId: orderNumberList) {
			
			JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderConflictEntityName(), orderId);
			if (node == null) continue;
			if (action == "retry") {
				MozuOrderDetail orderDetail = mapper.readValue(node.toString(), MozuOrderDetail.class);
				transitionState(orderId, tenantId, null, (orderDetail.isExistsInQb() ? "Update" : "Add") );
				logger.debug("Slotted conflicted order for retry with order number: " + orderId + " for tenant: " + tenantId);
			}
			deleteConfictEntities(tenantId,orderId);
			entityHandler.deleteEntity(tenantId, entityHandler.getOrderConflictEntityName(), orderId);
		}
	}
		
	public void addUpdatesToQueue(List<String> orderNumberList,Integer tenantId, String action) throws Exception {
		
		for(String orderId: orderNumberList) {
			JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), orderId);
			if (node == null) continue;
			if (action == "update") {
				transitionState(orderId, tenantId, null, "Update");
				logger.debug("Slotted an order update task for mozu order number: " + orderId);
			}
			entityHandler.deleteEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), orderId);
			
		}
		
	}

	public void addToConflictQueue(Integer tenantId, Order order, String qbResponse, String error) throws Exception {
		MozuOrderDetail orderDetails = orderHandler.getMozuOrderDetail(order);
		
		if (StringUtils.isNotEmpty(qbResponse)) {
			// Make entry in conflict reason table
			// Log the not found product in error conflict
			QBXML qbXml = (QBXML)  xmlHelper.getUnmarshalledValue(qbResponse);
			Object object = qbXml.getQBXMLMsgsRs().getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs().get(0);
			if (object instanceof ItemQueryRsType) {
				
				deleteConfictEntities(tenantId, order.getId());
				List<Object> searchResults = qbXml.getQBXMLMsgsRs().getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
				List<MozuOrderItem> productCodes = productHandler.getProductCodes(tenantId,order, false);
				for(Object obj : searchResults) {
					ItemQueryRsType itemSearchResponse = (ItemQueryRsType)obj;
					
					if (500 == itemSearchResponse.getStatusCode().intValue()&& "warn".equalsIgnoreCase(itemSearchResponse.getStatusSeverity())) {
						for(MozuOrderItem mzOrderItem : productCodes) {
							if (itemSearchResponse.getStatusMessage().toLowerCase().contains(mzOrderItem.getProductCode().toLowerCase())) {
								OrderConflictDetail conflictDetail = new OrderConflictDetail();
								conflictDetail.setNatureOfConflict("Not Found");
								conflictDetail.setDataToFix(mzOrderItem.getProductCode());
								conflictDetail.setId(order.getId()+"-"+mzOrderItem.getProductCode());
								conflictDetail.setConflictReason(itemSearchResponse.getStatusMessage());
								conflictDetail.setOrderId(order.getId());
								entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderConflictDetailEntityName(), conflictDetail.getId(), conflictDetail);
								logger.debug("Saved conflict for: "+ itemSearchResponse.getStatusMessage());
								break;
							}
						}
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
		} 
		if (StringUtils.isEmpty(orderDetails.getConflictReason()))
			orderDetails.setConflictReason(error);
		
		orderDetails.setExistsInQb(this.orderExistsInQB);
		entityHandler.addUpdateEntity(tenantId, entityHandler.getOrderConflictEntityName(), orderDetails.getId(), orderDetails);
	}
	
	private String ORDER_FAIL_ADD_REASON = "QuickBooks error message: You need to delete this transaction from the deposit before you can edit its name or amount.";
	private String getNextStep(Integer tenantId, Order order,CustomerAccount custAcct,String currentStep, String action) throws Exception {
		if ( processStatus.hasError()) {
			if (currentStep.equals(OrderStates.UPDATE) && 
					processStatus.getStatusMessage().toLowerCase().contains(ORDER_FAIL_ADD_REASON.toLowerCase()))
				return OrderStates.ADD;
			else {
				conflictReason = processStatus.getStatusMessage();
				return OrderStates.CONFLICT;
			}
		}
		if (currentStep.equals(OrderStates.CUST_QUERY) && 
				processStatus.hasWarning() && processStatus.getStatusCode().intValue() == 500) //Customer not found...add new customer
			return OrderStates.CUST_ADD;
		else if (!isCustomerFound(tenantId, custAcct.getEmailAddress())) 
			return OrderStates.CUST_QUERY;
		else if (currentStep.equals(OrderStates.ITEM_QUERY) && processStatus.hasWarning())
			return OrderStates.CONFLICT;
		else if (!allItemsFound(tenantId, order))
			return OrderStates.ITEM_QUERY;
		else if (currentStep.equals(OrderStates.ORDER_QUERY) && 
				action.equals(WorkTaskActions.ADD) && 
				this.orderExistsInQB) {
			this.conflictReason = "Order Already exists in Quickbooks";
			return OrderStates.CONFLICT;
		}
		else if ((action.equals(WorkTaskActions.UPDATE) || 
				action.equals(WorkTaskActions.ADD))  && 
				!currentStep.equals(OrderStates.ORDER_QUERY)) 
			return OrderStates.ORDER_QUERY;
		else if (action.equals(WorkTaskActions.UPDATE) && 
				this.orderExistsInQB) {
			return OrderStates.UPDATE;
		} else if (action.equals(WorkTaskActions.ADD) || (action.equals(WorkTaskActions.UPDATE) && !this.orderExistsInQB))
			return OrderStates.ADD;
		else if (action.equals(WorkTaskActions.DELETE))
			return OrderStates.DELETE;
		else
			throw new Exception("Could not determine next step");
	}
	
	private boolean isCustomerFound(Integer tenantId, String emailAddress) throws Exception {
		String qbId = customerHandler.getQbCustomerId(tenantId, emailAddress);
		
		return !StringUtils.isEmpty(qbId);
	}

	public boolean allItemsFound(Integer tenantId, Order order) throws Exception {
		List<MozuOrderItem> productCodes = productHandler.getProductCodes(tenantId, order, true);
		for(MozuOrderItem mzOrderItem : productCodes) {
			if (StringUtils.isEmpty(mzOrderItem.getQbItemCode())) return false;
		}
		
		return true;
	}
	
	private boolean isOrderInProcessing(Integer tenantId, String orderId) throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId));
		try {
			JsonNode node = entityResource.getEntity(entityHandler.getTaskqueueEntityName(), orderId);
			if (node != null)
			{
				WorkTask task = mapper.readValue(node.toString(), WorkTask.class);
				
				if (task.getStatus().equalsIgnoreCase("error")) return false;
				else return true;
				
			} else
				return false;
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
		
		return false;
	}
	
	private boolean isOrderProcessed(Integer tenantId,  String orderId) throws Exception {
		
		String filterCriteria = "id eq "+orderId;
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderPostedEntityName(), filterCriteria );
		
		return nodes.size() > 0;
		
	}
	
	private boolean isOrderInConflict(Integer tenantId, String orderId) throws Exception {
		
		String filterCriteria = "id eq "+orderId;
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderConflictEntityName(), filterCriteria );
		
		return nodes.size() > 0;
	}	


	
	private void processCurrentStep(Integer tenantId, Order order,CustomerAccount custAcct, String currentStep, String qbResponse) throws Exception {

		
		if (currentStep.equals(OrderStates.CUST_QUERY)) {
			processStatus = customerHandler.processCustomerQuery(tenantId, custAcct,qbResponse);
		} else if (currentStep.equals(OrderStates.CUST_ADD)) {
			processStatus = customerHandler.processCustomerAdd(tenantId, custAcct,qbResponse);
		} else if (currentStep.equals(OrderStates.ADD)) {
			processStatus = orderHandler.processOrderAdd(tenantId, order.getId(),qbResponse); //TODO : pass order object
		} else if (currentStep.equals(OrderStates.UPDATE)) {
			processStatus = orderHandler.processOrderUpdate(tenantId, order.getId(),qbResponse); //TODO : pass order object
		} else if (currentStep.equals(OrderStates.DELETE)) {
			processStatus = orderHandler.processOrderDelete(tenantId, order.getId(),qbResponse);
		} else if (currentStep.equals(OrderStates.ITEM_QUERY)) {
			processStatus =  productHandler.processItemQuery(tenantId,qbResponse);
		} else if (currentStep.equals(OrderStates.ORDER_QUERY)) {
			processStatus = orderHandler.processOrderQuery(tenantId, order.getId(), qbResponse);
			if (!processStatus.hasWarning()) {
				this.orderExistsInQB = true;
			} else
				this.orderExistsInQB = false;
		} else
			throw new Exception("Not supported");
		
	}

	private void deleteConflictOrders(Integer tenantId, String orderId) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderConflictEntityName(), "id eq "+orderId);
		
		for(JsonNode jNode : nodes) {
			String id =  jNode.findValue("enteredTime").asText();
			entityHandler.deleteEntity(tenantId, entityHandler.getOrderEntityName(), id);
		}
		deleteConfictEntities(tenantId, orderId);
	}
	
	private void deleteConfictEntities(Integer tenantId, String orderId) throws Exception {
		List<JsonNode> conflictNodes = entityHandler.getEntityCollection(tenantId, entityHandler.getOrderConflictDetailEntityName(), "orderId eq "+orderId, null, 200);
		for(JsonNode conflictNode : conflictNodes) {
			String id =  conflictNode.findValue("id").asText();
			entityHandler.deleteEntity(tenantId, entityHandler.getOrderConflictDetailEntityName(), id);
		}
	}
	
	/**
	 * Delete an order that is deleted in mozu.
	 * 
	 * @param entityId
	 * @param tenantId
	 * @throws Exception 
	 */
	public boolean deleteOrder(String entityId, Integer tenantId) throws Exception {
		//Check if order has been processed, if not put in process Queue
		boolean isProcessed = isOrderProcessed(tenantId, entityId);
		boolean isOrderInConflict = isOrderInConflict(tenantId, entityId);

		if (isOrderInConflict) { //Delete conflict orders
			deleteConflictOrders(tenantId, entityId);
		}

		if (isProcessed) { //Delete only if it has been successfully posted to QB
			transitionState(entityId, tenantId, null, "Delete");
			return true;
		} else {
			//throw new Exception("Did not find an order with id: " + entityId + " in POSTED or CONFLICT status. " +
				//	"Tenant id: " + tenantId + ". So nothing to Cancel.");
			return false;
		}
		
	}
}
