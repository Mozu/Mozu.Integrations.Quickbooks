/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mozu.api.ApiContext;
import com.mozu.api.Headers;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.api.security.Crypto;
import com.mozu.api.utils.JsonUtils;
import com.mozu.base.controllers.ConfigurationSecurityInterceptor;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.handlers.OrderStateHandler;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.OrderJsonObject;
import com.mozu.qbintegration.model.OrderQueueDataTable;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Admin
 * 
 */
@Controller
@RequestMapping({ "/Orders", "/index" })
@Scope("session")
public class OrdersController {

	public final static String ACCEPTED = "Accepted";

	protected static final String SECURITY_COOKIE = "MozuToken";
	
	private static final Logger logger = LoggerFactory
			.getLogger(OrdersController.class);

	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	OrderHandler orderHandler;
	
	@Autowired
	private EntityHandler entityHandler;
	
	@Autowired
	private OrderStateHandler orderStateHandler;
	
	final ObjectMapper mapper = JsonUtils.initObjectMapper();

	@RequestMapping(method = RequestMethod.POST)
	public String index(HttpServletRequest httpRequest,	HttpServletResponse httpResponse, ModelMap modelMap) throws Exception {

		String body = IOUtils.toString(httpRequest.getInputStream());
		
		String decodedBody = URLDecoder.decode(body, "ISO-8859-1");
		URI params = new URI("?" + decodedBody);
		List<NameValuePair> paramsList = URLEncodedUtils.parse(params, "UTF-8");

		Integer tenantId = Integer.parseInt(getValue(paramsList, Headers.X_VOL_TENANT));

		/*String siteId = getValue(paramsList,Headers.X_VOL_SITE);
		Integer siteId = Integer.parseInt();
		Integer masterCatalog = Integer.parseInt(getValue(paramsList,Headers.X_VOL_MASTER_CATALOG));
		Integer catalog = Integer.parseInt(getValue(paramsList,Headers.X_VOL_CATALOG));*/
		String msgHash = httpRequest.getParameter("messageHash");
		String dateKey = httpRequest.getParameter("dt");
		String tab = httpRequest.getParameter("tab");
		
		ApiContext apiContext = new MozuApiContext(tenantId);
		apiContext.setHeaderDate(dateKey);
		apiContext.setHmacSha256(msgHash);
		if (!Crypto.isRequestValid(apiContext, decodedBody)) {
            return "unauthorized";
        }
	
		httpResponse.addCookie(new Cookie(SECURITY_COOKIE, 
              ConfigurationSecurityInterceptor.encrypt(DateTime.now().toString(), 
                      AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret(), 
                      String.valueOf(apiContext.getTenantId()))));
		modelMap.addAttribute("tenantId", apiContext.getTenantId());
		modelMap.addAttribute("selectedTab", tab);
		return "orders";
	}
	
	private String getValue(List<NameValuePair> values, String key) {
		for(NameValuePair valuePair : values) {
			if (valuePair.getName().equals(key))
				return valuePair.getValue();
		}
		return null;
	}
	
	/**
	 * Display orders filtered on certain status, that is passed as parameter to the method.
	 * 
	 * @param httpRequest
	 * @param model
	 * @param mozuOrderNumber
	 * @param tenantId
	 * @param siteId
	 * @param action posted, updated, conflict, cancelled
	 * 
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getOrdersFilteredByAction", method = RequestMethod.GET)
	public @ResponseBody
	String getOrdersFilteredByAction(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "sSearch") String sSearch,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId,
			@RequestParam(value = "action") String action) throws Exception {	
		
		//MozuOrderDetail criteria = new MozuOrderDetail();
		
		int dispStartIdx = Integer.parseInt(iDisplayStart);
		int dispLength = Integer.parseInt(iDisplayLength);
		
		//criteria.setOrderStatus(action); //POSTED, UPDATED, CONFLICT, CANCELLED
		//Akshay 16-Oct-2014 - server side pagination by passing pageSize and startIndex to EL.
		EntityCollection mozuOrderDetailsCollection = orderHandler.getMozuOrderDetails(tenantId, action, 
				("UPDATED".equalsIgnoreCase(action) || "CONFLICT".equalsIgnoreCase(action)) ? 
										"orderNumber" : "enteredTime", dispStartIdx, dispLength, sSearch);

		List<MozuOrderDetail> mozuOrders = new ArrayList<MozuOrderDetail>();
		List<JsonNode> nodes = mozuOrderDetailsCollection.getItems();
		if (nodes != null && nodes.size() > 0) {
			for (JsonNode node : nodes) {
				mozuOrders.add(mapper.readValue(node.toString(), MozuOrderDetail.class));
			}
		}
		
		OrderJsonObject orderJsonObject = new OrderJsonObject();
		orderJsonObject.setiTotalDisplayRecords((long) mozuOrderDetailsCollection.getTotalCount());
		orderJsonObject.setiTotalRecords((long) mozuOrderDetailsCollection.getTotalCount());
		orderJsonObject.setAaData(mozuOrders);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(orderJsonObject);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return value;
	}
	
	@RequestMapping(value = "/getOrdersQueue", method = RequestMethod.GET)
	public @ResponseBody
	String getOrdersQueue(HttpServletRequest httpRequest,
			@RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "status") String status) throws Exception {	
		
		int dispStartIdx = Integer.parseInt(iDisplayStart);
		int dispLength = Integer.parseInt(iDisplayLength);
		
		String filter = "(Status eq "+status;
		
		if (status.equals("PENDING"))
			filter += " or Status eq PROCESSING";
		
		filter += ")";
		
		EntityCollection collection =  entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), filter, "createDate", dispStartIdx, dispLength);
		List<WorkTask> workTasks = new ArrayList<WorkTask>();
		OrderQueueDataTable dataTable = new OrderQueueDataTable();
		if(collection.getItems().size() > 0) {
			for(JsonNode node : collection.getItems()) {
				workTasks.add(mapper.readValue(node.toString(),WorkTask.class));
			}
		}
		
		dataTable.setiTotalDisplayRecords((long)collection.getTotalCount());
		dataTable.setiTotalRecords((long)collection.getTotalCount());
		dataTable.setAaData(workTasks);
		
		
		String value = null;
		try {
			value = mapper.writeValueAsString(dataTable);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return value;
	}
	
	
	@RequestMapping(value = "/getOrderConflictsDetails", method = RequestMethod.GET)
	public @ResponseBody
	String getConflictOrdersDetails(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumber") String mozuOrderNumber,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {

		List<OrderConflictDetail> conflictDetails = 
				quickbooksService.getOrderConflictReasons(tenantId, mozuOrderNumber);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(conflictDetails);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			value = "";
		}
		return value;
	}
	
	@RequestMapping(value = "/getOrderCompareDetails", method = RequestMethod.GET)
	public @ResponseBody
	OrderCompareDetail getOrderCompareDetails(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumber") String mozuOrderNumber,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {	

		OrderCompareDetail compareDetails = 
				orderHandler.getOrderCompareDetails(tenantId, mozuOrderNumber);

		return compareDetails;
		
	}
	
	@RequestMapping(value = "/postUpdatedOrderToQB", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> postUpdatedOrderToQB(HttpServletRequest httpRequest, ModelMap model, 
			@RequestBody List<String> mozuOrderNumbers,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "action") String action) throws Exception {	

		
		orderStateHandler.addUpdatesToQueue(mozuOrderNumbers, tenantId, action);
		
		return new ResponseEntity<String>("Selected orders have been successfully updated in Quickbooks.",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/postConflictOrderToQB", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> postConflictOrderToQB(HttpServletRequest httpRequest, ModelMap model, 
			@RequestBody List<String> mozuOrderNumbers,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "action") String action) throws Exception {
		
		orderStateHandler.retryConflicOrders(tenantId, mozuOrderNumbers, action);
		return new ResponseEntity<String>("OK",HttpStatus.OK);
		
	}
	
	
}
