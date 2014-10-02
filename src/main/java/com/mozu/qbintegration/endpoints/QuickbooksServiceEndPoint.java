package com.mozu.qbintegration.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.handlers.CustomerHandler;
import com.mozu.qbintegration.handlers.EncryptDecryptHandler;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.QuickBooksSavedOrderLine;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.EntityHelper;
import com.mozu.quickbooks.generated.ArrayOfString;
import com.mozu.quickbooks.generated.Authenticate;
import com.mozu.quickbooks.generated.AuthenticateResponse;
import com.mozu.quickbooks.generated.ClientVersion;
import com.mozu.quickbooks.generated.ClientVersionResponse;
import com.mozu.quickbooks.generated.CloseConnection;
import com.mozu.quickbooks.generated.CloseConnectionResponse;
import com.mozu.quickbooks.generated.ConnectionError;
import com.mozu.quickbooks.generated.ConnectionErrorResponse;
import com.mozu.quickbooks.generated.GetLastError;
import com.mozu.quickbooks.generated.GetLastErrorResponse;
import com.mozu.quickbooks.generated.ReceiveResponseXML;
import com.mozu.quickbooks.generated.ReceiveResponseXMLResponse;
import com.mozu.quickbooks.generated.SendRequestXML;
import com.mozu.quickbooks.generated.SendRequestXMLResponse;
import com.mozu.quickbooks.generated.ServerVersion;
import com.mozu.quickbooks.generated.ServerVersionResponse;

/**
 * @author Akshay
 * 
 */
@Endpoint
public class QuickbooksServiceEndPoint {

	private static final Log logger = LogFactory.getLog(QuickbooksServiceEndPoint.class);

    @Resource
    private WebServiceContext context;
	
	@Autowired
	private QuickbooksService qbService;

	@Autowired
	private QueueManagerService queueManagerService;

	@Autowired
	private EncryptDecryptHandler encryptDecryptHandler;
	
	@Autowired
	private OrderHandler orderHandler;
    
	@Autowired
	CustomerHandler customerHandler;
	
	public QuickbooksServiceEndPoint() throws DatatypeConfigurationException {
	
	}

	
	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "serverVersion")
	@ResponsePayload
	public ServerVersionResponse serverVersion(
			@RequestPayload ServerVersion serverVersion)
			throws IOException {
		/*ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);		
		String version = "";
		InputStream manifestStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");		
		 if (manifestStream== null) {
	            version = "Unknown version";
		 } else {
	        Manifest manifest = new Manifest(manifestStream);
	        Attributes attributes = manifest.getMainAttributes();
	        version = attributes.getValue("Implementation-Version");
		 }*/
		ServerVersionResponse response = new ServerVersionResponse();

		response.setServerVersionResult("");
		return response;
	}
	
	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "clientVersion")
	@ResponsePayload
	public ClientVersionResponse clientVersion(
			@RequestPayload ClientVersion clientVersion)
			throws java.rmi.RemoteException {
		logger.debug(clientVersion.getStrVersion());
		ClientVersionResponse response = new ClientVersionResponse();
		response.setClientVersionResult("");
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "authenticate")
	@ResponsePayload
	public AuthenticateResponse authenticate(
			@RequestPayload Authenticate authRequest)
			throws java.rmi.RemoteException {
		AuthenticateResponse response = new AuthenticateResponse();

		String decryptedPwd = encryptDecryptHandler.decrypt(authRequest.getStrPassword());
		Integer tenantId = Integer.parseInt(decryptedPwd.split("~")[0]);
		String userName = 	decryptedPwd.split("~")[1];
		
		ArrayOfString arrStr = new ArrayOfString();
		List<String> val = arrStr.getString();
		val.add(tenantId + "~" + String.valueOf(System.currentTimeMillis())); // GUID
		
		//TODO: Add more security based on tenantId ?
		
		if (userName.equals(authRequest.getStrUserName())) {
			val.add(""); // Pending work to do?
			val.add("");
			val.add(null);
			val.add("10");
		}

		response.setAuthenticateResult(arrStr);
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "sendRequestXML")
	@ResponsePayload
	public SendRequestXMLResponse sendRequestXML(SendRequestXML requestXML)
			throws java.rmi.RemoteException {

		// Get the tenantID
		Integer tenantId = Integer
				.parseInt(requestXML.getTicket().split("~")[0]);

		// Has the order id reference here
		WorkTask criteria = new WorkTask();
		criteria.setQbTaskStatus("ENTERED");
		WorkTask workTask = queueManagerService.getNextTaskWithCriteria(
				tenantId, criteria);

		SendRequestXMLResponse response = new SendRequestXMLResponse();
		if (workTask != null) {

			workTask.setQbTaskStatus("PROCESSING"); // now this task is
													// processing. will be
													// changed to processed in
			queueManagerService.updateTask(workTask, tenantId);
			response.setSendRequestXMLResult(workTask.getQbTaskRequest());
		} else {
			response.setSendRequestXMLResult(null); // nothing to do - come
														// back after 5 sconds
		}
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "receiveResponseXML")
	@ResponsePayload
	public ReceiveResponseXMLResponse receiveResponseXML(ReceiveResponseXML responseXML) throws Exception {
		logger.debug(responseXML.getMessage());

		// Get the tenantID
		Integer tenantId = Integer
				.parseInt(responseXML.getTicket().split("~")[0]);
		WorkTask criteria = new WorkTask();

		criteria.setQbTaskStatus("PROCESSING");
		WorkTask workTask = queueManagerService.getNextTaskWithCriteria(
				tenantId, criteria);

		if (workTask == null) { // nothing to do but work is not complete so
								// come back
			ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
			responseToResponse.setReceiveResponseXMLResult(0);
			return responseToResponse;
		}

		workTask.setQbTaskResponse(responseXML.getResponse());

		try {

			if ("CUST_QUERY".equals(workTask.getQbTaskType())) {
				// Resumes with response.
				QBXML response = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				CustomerQueryRsType custQueryResponse = (CustomerQueryRsType) response
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);

				String orderId = workTask.getTaskId(); // this gets the order id
				Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());
				CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());

				if ("warn".equalsIgnoreCase(custQueryResponse
						.getStatusSeverity())
						&& 500 == custQueryResponse.getStatusCode().intValue()) { // Customer not found. So CUST_ADD
					// ENTER the new task
					qbService.addCustAddTaskToQueue(orderId, tenantId,
							workTask.getSiteId(), order, custAcct);

				} else {
					String qbCustListID = custQueryResponse.getCustomerRet()
							.get(0).getListID();
					qbService.saveCustInEntityList(custAcct, qbCustListID,
							tenantId, workTask.getSiteId());

					// Now need to enter ITEM_QUERY tasks or ORDER_ADD if all items are present
					// Now create item query tasks for all items
					boolean allItemsInEntityList = true;
					List<String> itemListIds = new ArrayList<String>();
					for (OrderItem item : order.getItems()) {

						String itemListId = qbService.getProductFromEntityList(
								item, tenantId, workTask.getSiteId());

						if (null == itemListId) {
							allItemsInEntityList = false;
							qbService.addItemQueryTaskToQueue(orderId, tenantId,
									workTask.getSiteId(), order, item.getProduct()
											.getProductCode());

						} else {
							itemListIds.add(itemListId);
						}
						if (allItemsInEntityList) { // Add order ADD task
							qbService.addOrderAddTaskToQueue(orderId, tenantId,
									workTask.getSiteId(), custAcct, order,
									itemListIds);
						}
					}
				}
			} else if ("CUST_ADD".equals(workTask.getQbTaskType())) { // received Cust add response

				// Resume with response
				QBXML custAddResp = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				CustomerAddRsType custAddResponse = (CustomerAddRsType) custAddResp
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);
				String customerListId = custAddResponse.getCustomerRet()
						.getListID();
				String orderId = workTask.getTaskId(); // this gets the order id
				Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());
				
				CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
				qbService.saveCustInEntityList(custAcct, customerListId,tenantId, workTask.getSiteId());

				// Now create item query tasks for all items
				boolean allItemsInEntityList = true;
				List<String> itemListIds = new ArrayList<String>();
				for (OrderItem item : order.getItems()) {

					String itemListId = qbService.getProductFromEntityList(
							item, tenantId, workTask.getSiteId());

					if (null == itemListId) {
						allItemsInEntityList = false;
						qbService.addItemQueryTaskToQueue(orderId, tenantId,
								workTask.getSiteId(), order, item.getProduct()
										.getProductCode());

					} else {
						itemListIds.add(itemListId);
					}
					if (allItemsInEntityList) { // Add order ADD task
						qbService.addOrderAddTaskToQueue(orderId, tenantId,
								workTask.getSiteId(), custAcct, order,
								itemListIds);
					}
				}
			} else if ("ITEM_QUERY".equals(workTask.getQbTaskType())) {

				// Item must be found here. Else we need to error out.
				QBXML itemSearchEle = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);

				if (500 == itemSearchResponse.getStatusCode().intValue()
						&& "warn".equalsIgnoreCase(itemSearchResponse
								.getStatusSeverity())) {
					
					// TODO this is error scenario. So hold on.
					//Log the not found product in error conflict 
					String orderId = workTask.getTaskId(); // this gets the order id
					//Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());
					//CustomerAccount custAcct = qbService.getMozuCustomer(order,tenantId, workTask.getSiteId());
					MozuOrderDetails orderDetails = orderHandler.getOrderDetails(tenantId,workTask.getSiteId(),orderId, "CONFLICT", null);
					qbService.saveOrderInEntityList(orderDetails, EntityHelper.getOrderEntityName(), tenantId, workTask.getSiteId());
					
					//Make entry in conflict reason table
					//Log the not found product in error conflict 
					QBXML qbxml2 = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskRequest());
					ItemQueryRqType itemReq = (ItemQueryRqType) qbxml2.getQBXMLMsgsRq().getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().get(0);
					
					OrderConflictDetail conflictDetail = new OrderConflictDetail();
					conflictDetail.setMozuOrderNumber(String.valueOf(orderDetails.getMozuOrderNumber()));
					conflictDetail.setNatureOfConflict("Not Found");
					conflictDetail.setDataToFix(itemReq.getFullName().get(0));
					conflictDetail.setConflictReason(itemSearchResponse.getStatusMessage());
					
					List<OrderConflictDetail> conflictDetails = new ArrayList<OrderConflictDetail>();
					conflictDetails.add(conflictDetail);
					qbService.saveConflictInEntityList(tenantId,Integer.parseInt(conflictDetail.getMozuOrderNumber()), conflictDetails);
					logger.debug("Saved conflict for: " + itemSearchResponse.getStatusMessage());

				} else {

					saveProductInEntityList(itemSearchResponse, tenantId, workTask.getSiteId());

					// Check if we can create order add task now
					String orderId = workTask.getTaskId(); // this gets the
															// order id
					Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());
					
					CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
					boolean allItemsInEntityList = true;
					List<String> itemListIds = new ArrayList<String>();
					for (OrderItem singleItem : order.getItems()) {
						String itemQBListId = qbService
								.getProductFromEntityList(singleItem, tenantId,
										workTask.getSiteId());
						if (null == itemQBListId) {
							allItemsInEntityList = false;
						}
						// list will anyway be discarded if above flag is false, so no null chck is required
						itemListIds.add(itemQBListId); 
					}

					if (allItemsInEntityList) { // Add order ADD task
						qbService.addOrderAddTaskToQueue(orderId, tenantId,
								workTask.getSiteId(), custAcct, order,
								itemListIds);
					}
				}

			} else if ("ITEM_ADD".equals(workTask.getQbTaskType())) {
				// Get back the inserted item
				QBXML itemAddEle = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());

				ItemInventoryAddRsType invAddResponse = (ItemInventoryAddRsType) itemAddEle
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);
				String itemListId = invAddResponse.getItemInventoryRet()
						.getListID();

				// Save the item list id in entity list
				OrderItem item = new OrderItem();
				Product product = new Product();
				item.setProduct(product);
				product.setProductCode(invAddResponse.getItemInventoryRet()
						.getFullName());
				product.setName(invAddResponse.getItemInventoryRet().getName());
				qbService.saveProductInEntityList(item, itemListId, tenantId,
						workTask.getSiteId());

				logger.debug("Added new product to quickbooks: " + invAddResponse.getItemInventoryRet().getName());

			} else if ("ORDER_ADD".equals(workTask.getQbTaskType())) {
				// Resume with response
				QBXML orderAddResp = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType salesOrderResponse = (com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType) orderAddResp
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);

				//Make an entry in the order entity list with posted status
				String orderId = workTask.getTaskId(); // this gets the order id
				//Order order = orderHandler.getOrder(orderId, tenantId,	workTask.getSiteId());
				//CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
				
				MozuOrderDetails orderDetails = orderHandler.getOrderDetails(tenantId, workTask.getSiteId(), orderId, "POSTED", salesOrderResponse);
				qbService.saveOrderInEntityList(orderDetails, EntityHelper.getOrderEntityName(),tenantId, workTask.getSiteId());
				
				logger.debug((new StringBuilder())
						.append("Processed order with id: ")
						.append(workTask.getTaskId())
						.append(" with QB status code: ")
						.append(salesOrderResponse.getStatusCode())
						.append(" with status: ")
						.append(salesOrderResponse.getStatusMessage())
						.toString());
			} else if ("ITEM_QUERY_ALL".equals(workTask.getQbTaskType())) {

				QBXML itemSearchEle = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);
				List<Object> itemServiceRetCollection = itemSearchResponse
						.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet();
				for (Object object : itemServiceRetCollection) {
					String productName = null;
					String productQbListID = null;
					if(object instanceof ItemServiceRet) {
						ItemServiceRet itemServiceRet = (ItemServiceRet) object;
						productName = itemServiceRet.getFullName();
						productQbListID = itemServiceRet.getListID();
					} else if (object instanceof ItemInventoryRet) {
						ItemInventoryRet itemInventoryRet = (ItemInventoryRet) object;
						productName = itemInventoryRet.getFullName();
						productQbListID = itemInventoryRet.getListID();
					} //TODO need to identify more item inventory types and add here.
					MozuProduct mozuProduct = new MozuProduct();
					mozuProduct.setProductCode(productName);
					mozuProduct.setQbProductListID(productQbListID);
					mozuProduct.setProductName(productName);
					qbService.saveAllProductInEntityList(mozuProduct, tenantId,
							workTask.getSiteId());
					logger.debug("Saved product through refresh all: "
							+ productName);
				}

			} else if("ORDER_UPDATE".equals(workTask.getQbTaskType())) {
				QBXML orderModResp = (QBXML) qbService
						.getUnmarshalledValue(workTask.getQbTaskResponse());
				
				SalesOrderModRsType orderModRsType = (SalesOrderModRsType) orderModResp.getQBXMLMsgsRs().
						getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs().get(0);
				
				//Make an entry in the order entity list with posted status
				String orderId = workTask.getTaskId(); // this gets the order id
				//Order order = orderHandler.getOrder(orderId, tenantId, workTask.getSiteId());
				//CustomerAccount custAcct = qbService.getMozuCustomer(order,	tenantId, workTask.getSiteId());
				
				MozuOrderDetails orderDetails = orderHandler.getOrderUpdateDetails(tenantId, workTask.getSiteId(), orderId, "POSTED", orderModRsType);
				qbService.saveOrderInEntityList(orderDetails, EntityHelper.getOrderEntityName(), tenantId, workTask.getSiteId());
				
				//UPdated order in updated EL to POSTED -- that screen picks up orders in UPDATED status so 
				//this one will stop showing up.
				qbService.updateOrderInEntityList(orderDetails, EntityHelper.getOrderUpdatedEntityName(),	tenantId, workTask.getSiteId());
				
				logger.debug((new StringBuilder())
						.append("Processed order with id: ")
						.append(workTask.getTaskId())
						.append(" with QB status code: ")
						.append(orderModRsType.getStatusCode())
						.append(" with status: ")
						.append(orderModRsType.getStatusMessage())
						.toString());
			}

			// Right place to mark this task as PROCESSED
			workTask.setQbTaskStatus("PROCESSED");
			queueManagerService.updateTask(workTask, tenantId);

		} catch (Exception ex) {
			// Any exception, just make the task as entered for now
			workTask.setQbTaskStatus("ERRORED");
			queueManagerService.updateTask(workTask, tenantId);
			//Make an entry in the order entity list with posted status
			String orderId = workTask.getTaskId(); // this gets the order id
			//Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());
			//CustomerAccount custAcct = qbService.getMozuCustomer(order,	tenantId, workTask.getSiteId());
			
			MozuOrderDetails orderDetails = orderHandler.getOrderDetails(tenantId, workTask.getSiteId(), orderId, "CONFLICT", null);
			qbService.saveOrderInEntityList(orderDetails, EntityHelper.getOrderEntityName(), tenantId, workTask.getSiteId());
		}

		ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
		responseToResponse.setReceiveResponseXMLResult(1);
		return responseToResponse;
	}

	

	private void saveProductInEntityList(ItemQueryRsType itemSearchResponse, Integer tenantId, Integer siteId) {
		String itemListId = null;
		Object invObj = itemSearchResponse
				.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet()
				.get(0);

		OrderItem item = new OrderItem();
		Product product = new Product();
		item.setProduct(product);

		if (invObj instanceof ItemServiceRet) {
			ItemServiceRet itemServiceRet = (ItemServiceRet) invObj;
			product.setProductCode(itemServiceRet.getFullName());
			product.setName(itemServiceRet.getName());
			itemListId = itemServiceRet.getListID();
		} else if (invObj instanceof ItemInventoryRet) {
			ItemInventoryRet itemInvRet = (ItemInventoryRet) invObj;
			product.setProductCode(itemInvRet.getFullName());
			product.setName(itemInvRet.getName());
			itemListId = itemInvRet.getListID();
		}
		// Save the item list id in entity list
		qbService.saveProductInEntityList(item, itemListId,
				tenantId, siteId);
		
	}

	private List<String> getItemListIdsIfAllPresent(Order order, Integer tenantId, Integer siteId) {
		boolean allItemsInEntityList = true;
		List<String> itemListIds = new ArrayList<String>();
		for (OrderItem singleItem : order.getItems()) {
			String itemQBListId = qbService.getProductFromEntityList(
					singleItem, tenantId, siteId);
			if (null == itemQBListId) {
				allItemsInEntityList = false;
			}
			// list will anyway be discarded if above flag is false, so no null check is required
			itemListIds.add(itemQBListId); 
		}
		if(allItemsInEntityList) {
			return itemListIds;
		} else {
			return null;
		}
		
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "connectionError")
	@ResponsePayload
	public ConnectionErrorResponse connectionError(ConnectionError connError)
			throws java.rmi.RemoteException {
		logger.debug(connError.getMessage());
		ConnectionErrorResponse errorResponse = new ConnectionErrorResponse();
		errorResponse.setConnectionErrorResult("");
		return errorResponse;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "getLastError")
	@ResponsePayload
	public GetLastErrorResponse getLastError(GetLastError lastError)
			throws java.rmi.RemoteException {
		logger.debug(lastError.getTicket());
		GetLastErrorResponse response = new GetLastErrorResponse();
		response.setGetLastErrorResult("");
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "closeConnection")
	@ResponsePayload
	public CloseConnectionResponse closeConnection(
			CloseConnection closeConnection) throws java.rmi.RemoteException {
		logger.debug(closeConnection.getTicket());
		CloseConnectionResponse response = new CloseConnectionResponse();
		response.setCloseConnectionResult("Thank you for using QB Connector");
		return response;
	}

}
