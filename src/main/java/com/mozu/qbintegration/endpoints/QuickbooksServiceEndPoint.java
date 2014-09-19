package com.mozu.qbintegration.endpoints;

import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.utils.SingleTask;
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

/**
 * @author Akshay
 * 
 */
@Endpoint
public class QuickbooksServiceEndPoint {

	private static final Log logger = LogFactory
			.getLog(QuickbooksServiceEndPoint.class);
	
	@Autowired
	private QuickbooksService qbService;

	public QuickbooksServiceEndPoint() throws DatatypeConfigurationException {
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
		logger.debug(authRequest.getStrUserName() + " "
				+ authRequest.getStrPassword());
		AuthenticateResponse response = new AuthenticateResponse();

		ArrayOfString arrStr = new ArrayOfString();
		List<String> val = arrStr.getString();
		val.add("ABCD12345"); // GUID
		if (qbService.gotWorkToDo()) {
			val.add(""); // Pending work to do?
			val.add("");
			val.add(null);
			val.add("10");
		} else {
			val.add("none");
			val.add("10"); // Retry time
			val.add("10"); // ?
			val.add("10");
		}

		response.setAuthenticateResult(arrStr);
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "sendRequestXML")
	@ResponsePayload
	public SendRequestXMLResponse sendRequestXML(SendRequestXML requestXML)
			throws java.rmi.RemoteException {

		SendRequestXMLResponse response = new SendRequestXMLResponse();
		if (null != qbService.getNextPayload()) {
			response.setSendRequestXMLResult(qbService.getNextPayload()
					.getRequest());
		} else {
			response.setSendRequestXMLResult("NoOp"); //nothing to do - come back after 5 sconds
		}
		return response;
	}

	@PayloadRoot(namespace = "http://developer.intuit.com/", localPart = "receiveResponseXML")
	@ResponsePayload
	public ReceiveResponseXMLResponse receiveResponseXML(
			ReceiveResponseXML responseXML) throws java.rmi.RemoteException {
		logger.debug(responseXML.getHresult());
		logger.debug(responseXML.getMessage());
		SingleTask task = qbService.getNextPayload();
		if (null != task) {
			task.setResponse(responseXML.getResponse());
			task.setIsRetry(Boolean.FALSE);
			//TODO put decision making code call here that will decide the next task
			qbService.doneWithWork();
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
