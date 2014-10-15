package com.mozu.qbintegration.endpoints;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.fasterxml.jackson.databind.JsonNode;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.qbintegration.handlers.CustomerHandler;
import com.mozu.qbintegration.handlers.EncryptDecryptHandler;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.handlers.OrderStateHandler;
import com.mozu.qbintegration.handlers.ProductHandler;
import com.mozu.qbintegration.handlers.QBDataHandler;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.QBSession;
import com.mozu.qbintegration.model.WorkTaskLog;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;
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
	EntityHandler entityHandler;
	
	@Autowired
	ProductHandler productHandler;
	
	@Autowired
	QuickbooksService quickbooksService;
	
	@Autowired
	OrderStateHandler orderStateHandler;
	
	@Autowired
	QBDataHandler qbDataHandler;
	
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
	public AuthenticateResponse authenticate(@RequestPayload Authenticate authRequest)	throws Exception {
		logger.info("Authenticate Request from QWC");
		AuthenticateResponse response = new AuthenticateResponse();

		String password = authRequest.getStrPassword();
		String decryptedPwd = encryptDecryptHandler.decrypt(password);
		Integer tenantId = Integer.parseInt(decryptedPwd.split("~")[0]);
		String userName = decryptedPwd.split("~")[1];

		ArrayOfString arrStr = new ArrayOfString();
		List<String> val = arrStr.getString();

		GeneralSettings generalSetting = quickbooksService.getSettingsFromEntityList(tenantId);
		
		// TODO: Add more security based on tenantId ?

		if (userName.equals(authRequest.getStrUserName()) && userName.equals(generalSetting.getQbAccount()) && password.equals(generalSetting.getQbPassword()) ) {
			QBSession token = quickbooksService.addSession(tenantId);
			
			
			
			val.add(tenantId+"~"+token.getPwd()); // GUID
			
			List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId,entityHandler.getTaskqueueEntityName(), null,null,1);
			if (nodes.size() == 0) {
				val.add("none");
			}
			else if (StringUtils.isNotEmpty(generalSetting.getQbwFile()))
				val.add(generalSetting.getQbwFile()); // Pending work to do?
			else {
				val.add(""); // Pending work to do?
				val.add("");
				val.add(null);
				val.add("10");
			}
		} else {
			val.add(""); // Pending work to do?
			val.add("nvu");
		}

		response.setAuthenticateResult(arrStr);
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "sendRequestXML")
	@ResponsePayload
	public SendRequestXMLResponse sendRequestXML(SendRequestXML requestXML)	throws Exception {

		// Get the tenantID
		try {
			logger.info("request XML : "+requestXML.getTicket());
			Integer tenantId = getTenantId(requestXML.getTicket());
			
			WorkTask workTask = queueManagerService.getNext(tenantId);
	
			SendRequestXMLResponse response = new SendRequestXMLResponse();
			if (workTask != null) {
				String requestXml = getRequestXml(tenantId, workTask);
				logRequestResponse(tenantId, workTask, requestXml);
				logger.info(requestXML.getTicket()+"- Task Request - "+requestXml);
				response.setSendRequestXMLResult(requestXml);
			} else {
				response.setSendRequestXMLResult(null); // all pending work completed
			}
			return response;
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			throw exc;
		}
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "receiveResponseXML")
	@ResponsePayload
	public ReceiveResponseXMLResponse receiveResponseXML(ReceiveResponseXML responseXML) throws Exception {

		logger.info("receive Response XML : "+responseXML.getTicket()+" - Task Message - "+responseXML.getMessage());
		logger.info("receive Response XML : "+responseXML.getTicket()+" - Task Response - "+responseXML.getResponse().substring(0, 100)+"...");
		Integer tenantId = getTenantId(responseXML.getTicket());
		WorkTask workTask = queueManagerService.getActiveTask(tenantId);

		if (workTask == null) { // nothing to do but work is not complete so
								// come back
			ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
			responseToResponse.setReceiveResponseXMLResult(0);
			return responseToResponse;
		}

		try {

			
			switch(workTask.getType().toLowerCase()) {
				case "order":
					try{
						logRequestResponse(tenantId, workTask, responseXML.getResponse());
						orderStateHandler.transitionState(workTask.getId(), tenantId, responseXML.getResponse(),workTask.getAction());
					} catch(Exception ex) {
						orderStateHandler.addToConflictQueue(tenantId, orderHandler.getOrder(workTask.getId(), tenantId), null, ex.getMessage());
						queueManagerService.updateTask(tenantId, workTask.getId(), "ERROR", "COMPLETED");
						throw ex;
					}
					break;
				case "product":
					logRequestResponse(tenantId, workTask, responseXML.getResponse());
					if (workTask.getAction().equalsIgnoreCase("add"))
						productHandler.processItemAdd(tenantId, workTask, responseXML.getResponse());
					else if (workTask.getAction().equalsIgnoreCase("refresh"))
						productHandler.processItemQueryAll(tenantId, workTask,responseXML.getResponse());
					break;
				case "datasync":
					qbDataHandler.processResponseXml(tenantId, workTask, responseXML.getResponse());
					break;
				default:
					throw new Exception("Not supported");

			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}

		ReceiveResponseXMLResponse responseToResponse = new ReceiveResponseXMLResponse();
		responseToResponse.setReceiveResponseXMLResult(1);
		return responseToResponse;
	}

	

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "connectionError")
	@ResponsePayload
	public ConnectionErrorResponse connectionError(ConnectionError connError)
			throws java.rmi.RemoteException {
		logger.info("connection Error : "+connError.getMessage());
		ConnectionErrorResponse errorResponse = new ConnectionErrorResponse();
		errorResponse.setConnectionErrorResult("");
		return errorResponse;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "getLastError")
	@ResponsePayload
	public GetLastErrorResponse getLastError(GetLastError lastError)
			throws Exception {
		logger.info("getLastError : "+lastError.getTicket());
		Integer tenantId = getTenantId(lastError.getTicket());
		GetLastErrorResponse response = new GetLastErrorResponse();
		response.setGetLastErrorResult("");
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "closeConnection")
	@ResponsePayload
	public CloseConnectionResponse closeConnection(
			CloseConnection closeConnection) throws Exception {
		logger.info("close Connection:"+closeConnection.getTicket());
		Integer tenantId = getTenantId(closeConnection.getTicket());
		quickbooksService.deleteSession(tenantId);
		CloseConnectionResponse response = new CloseConnectionResponse();
		response.setCloseConnectionResult("Thank you for using QB Connector");
		return response;
	}
	
	private String getRequestXml(Integer tenantId, WorkTask workTask) throws Exception {
		if (workTask.getType().equalsIgnoreCase("order")) {
			Order order = orderHandler.getOrder(workTask.getId(), tenantId);
			switch(workTask.getCurrentStep().toLowerCase()) {
				case "cust_query" :
					return customerHandler.getQBSearchGetXML(tenantId, workTask.getId(), order.getCustomerAccountId());
				case "cust_add":
					return customerHandler.getQBCustomerSaveXML(tenantId, workTask.getId(), order.getCustomerAccountId());
				case "order_add":
					return orderHandler.getQBOrderSaveXML(tenantId, workTask.getId());
				case "order_update":
					return orderHandler.getQBOrderUpdateXML(tenantId,workTask.getId());
				case "item_query":
					return productHandler.getQBProductsGetXML(tenantId, order);
				case "order_delete":
					return orderHandler.getQBOrderDeleteXML(tenantId, workTask.getId());
				case "order_query":
					return orderHandler.getQBOrderQueryXml(tenantId, order);
				default:
					throw new Exception("Not supported");
			}
		} else if (workTask.getType().equalsIgnoreCase("product")){
			switch(workTask.getAction().toLowerCase()) {
				case "add":
					return productHandler.getQBProductSaveXML(tenantId, workTask.getId());
				case "refresh":
					return productHandler.getAllQBProductsGetXML(tenantId);
				default:
					throw new Exception("Not supported");
			}
		} else  {
			return qbDataHandler.getRequestXml(workTask.getAction());
		}
	}

	private void logRequestResponse(int tenantId, WorkTask workTask, String xml) throws Exception {
		WorkTaskLog log = new WorkTaskLog();
		log.setAction(workTask.getAction());
		log.setCreateDate(workTask.getCreateDate());
		log.setCurrentStep(workTask.getCurrentStep());
		log.setType(workTask.getType());
		log.setId(workTask.getId());
		log.setXml(xml);
		log.setEnteredTime(String.valueOf((new Date()).getTime()));
		//bug fix 10-oct-2014 - status cannot be null
		log.setStatus(workTask.getStatus() == null ? "" : workTask.getStatus());
		entityHandler.addEntity(tenantId, entityHandler.getTaskqueueLogEntityName(), log);
	}
	
	private Integer getTenantId(String token) throws Exception {
		String[] tokens = token.split("~");
		Integer tenantId = Integer.parseInt(tokens[0]);
		QBSession session = quickbooksService.getSession(tenantId);
		if (!tokens[1].equals(session.getPwd()))
			throw new Exception("Unaithorized");
		String decryptedValue = encryptDecryptHandler.decrypt(session.getKey(), session.getPwd());
		
		if (!tenantId.equals(Integer.parseInt(decryptedValue.split("~")[0])))
			throw new Exception("Unaithorized");
		return tenantId;
	}
}
