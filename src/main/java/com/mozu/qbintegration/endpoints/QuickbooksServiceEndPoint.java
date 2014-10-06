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
import com.mozu.qbintegration.handlers.QBHandler;
import com.mozu.qbintegration.model.MozuOrderDetail;
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

	@Autowired
	QBHandler qbHandler;
	
	public QuickbooksServiceEndPoint() throws DatatypeConfigurationException {

	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "serverVersion")
	@ResponsePayload
	public ServerVersionResponse serverVersion(@RequestPayload ServerVersion serverVersion) throws IOException {
		/*
		 * ServletContext servletContext = (ServletContext)
		 * context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		 * String version = ""; InputStream manifestStream =
		 * servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"); if
		 * (manifestStream== null) { version = "Unknown version"; } else {
		 * Manifest manifest = new Manifest(manifestStream); Attributes
		 * attributes = manifest.getMainAttributes(); version =
		 * attributes.getValue("Implementation-Version"); }
		 */
		ServerVersionResponse response = new ServerVersionResponse();

		response.setServerVersionResult("");
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "clientVersion")
	@ResponsePayload
	public ClientVersionResponse clientVersion(@RequestPayload ClientVersion clientVersion)	throws java.rmi.RemoteException {
		logger.debug(clientVersion.getStrVersion());
		ClientVersionResponse response = new ClientVersionResponse();
		response.setClientVersionResult("");
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "authenticate")
	@ResponsePayload
	public AuthenticateResponse authenticate(@RequestPayload Authenticate authRequest)	throws java.rmi.RemoteException {
		AuthenticateResponse response = new AuthenticateResponse();

		String decryptedPwd = encryptDecryptHandler.decrypt(authRequest
				.getStrPassword());
		Integer tenantId = Integer.parseInt(decryptedPwd.split("~")[0]);
		String userName = decryptedPwd.split("~")[1];

		ArrayOfString arrStr = new ArrayOfString();
		List<String> val = arrStr.getString();
		val.add(tenantId + "~" + String.valueOf(System.currentTimeMillis())); // GUID

		// TODO: Add more security based on tenantId ?

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
	public SendRequestXMLResponse sendRequestXML(SendRequestXML requestXML)	throws java.rmi.RemoteException {

		// Get the tenantID
		Integer tenantId = Integer.parseInt(requestXML.getTicket().split("~")[0]);

		// Has the order id reference here
		WorkTask criteria = new WorkTask();
		criteria.setQbTaskStatus("ENTERED");
		WorkTask workTask = queueManagerService.getNextTaskWithCriteria(tenantId, criteria);

		SendRequestXMLResponse response = new SendRequestXMLResponse();
		if (workTask != null) {

			workTask.setQbTaskStatus("PROCESSING"); // now this task is processing. will be changed to processed in
			queueManagerService.updateTask(workTask, tenantId);
			response.setSendRequestXMLResult(workTask.getQbTaskRequest());
		} else {
			response.setSendRequestXMLResult(null); // all pending work completed
		}
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "receiveResponseXML")
	@ResponsePayload
	public ReceiveResponseXMLResponse receiveResponseXML(ReceiveResponseXML responseXML) throws Exception {

		logger.debug(responseXML.getMessage());
		Integer tenantId = Integer.parseInt(responseXML.getTicket().split("~")[0]);
		WorkTask criteria = new WorkTask();
		criteria.setQbTaskStatus("PROCESSING");
		WorkTask workTask = queueManagerService.getNextTaskWithCriteria(tenantId, criteria);

		if (workTask == null) { // nothing to do but work is not complete so
								// come back
			ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
			responseToResponse.setReceiveResponseXMLResult(0);
			return responseToResponse;
		}

		try {

			// Right place to mark this task as PROCESSED
			workTask.setQbTaskResponse(responseXML.getResponse());
			workTask.setQbTaskStatus("PROCESSED");
			queueManagerService.updateTask(workTask, tenantId);
			
			switch(workTask.getQbTaskType().toLowerCase()) {
				case "cust_query" :
					qbHandler.processCustomerQuery(tenantId, workTask);
					break;
				case "cust_add":
					qbHandler.processCustomerAdd(tenantId, workTask);
					break;
				case "item_query":
					qbHandler.processItemQuery(tenantId, workTask);
					break;
				case "item_add":
					qbHandler.processItemQuery(tenantId, workTask);
					break;
				case "order_add":
					qbHandler.processOrderAdd(tenantId, workTask);
					break;
				case "item_query_all":
					qbHandler.processItemQueryAll(tenantId, workTask);
					break;
				case "order_update":
					qbHandler.processOrderUpdate(tenantId, workTask);
					break;
				default:
					throw new Exception("Not supported");
			}
			
		} catch (Exception ex) {
			// Any exception, just make the task as entered for now
			workTask.setQbTaskStatus("ERRORED");
			queueManagerService.updateTask(workTask, tenantId);
			// Make an entry in the order entity list with posted status
			String orderId = workTask.getTaskId(); // this gets the order id
	
			MozuOrderDetail orderDetails = orderHandler.getOrderDetails(tenantId, workTask.getSiteId(), orderId, "CONFLICT", null);
			qbService.saveOrderInEntityList(orderDetails,EntityHelper.getOrderEntityName(), tenantId,workTask.getSiteId());
		}

		ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
		responseToResponse.setReceiveResponseXMLResult(1);
		return responseToResponse;
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
