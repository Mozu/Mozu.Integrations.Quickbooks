/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.mozu.api.security.AppAuthenticator;
import com.mozu.api.security.Crypto;
import com.mozu.base.controllers.ConfigurationSecurityInterceptor;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.handlers.OrderStateHandler;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.OrderJsonObject;
import com.mozu.qbintegration.service.QuickbooksService;

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
	
	final ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(method = RequestMethod.POST)
	public String index(HttpServletRequest httpRequest,	HttpServletResponse httpResponse, ModelMap modelMap) throws Exception {

		String body = IOUtils.toString(httpRequest.getInputStream());

		
		String decodedBody = URLDecoder.decode(body, "ISO-8859-1");
		URI params = new URI("?" + decodedBody);
		List<NameValuePair> paramsList = URLEncodedUtils.parse(params, "UTF-8");

		Integer tenantId = Integer.parseInt(getValue(paramsList, Headers.X_VOL_TENANT));
		Integer siteId = Integer.parseInt(getValue(paramsList,Headers.X_VOL_SITE));
		Integer masterCatalog = Integer.parseInt(getValue(paramsList,Headers.X_VOL_MASTER_CATALOG));
		Integer catalog = Integer.parseInt(getValue(paramsList,Headers.X_VOL_CATALOG));
		String msgHash = httpRequest.getParameter("messageHash");
		String dateKey = httpRequest.getParameter("dt");
		String tab = httpRequest.getParameter("tab");
		
		ApiContext apiContext = new MozuApiContext(tenantId, siteId, masterCatalog, catalog);
		apiContext.setHeaderDate(dateKey);
		apiContext.setHmacSha256(msgHash);
		if (!Crypto.isRequestValid(apiContext, decodedBody)) {
            return "unauthorized";
        }
	
		httpResponse.addCookie(new Cookie(SECURITY_COOKIE, 
              ConfigurationSecurityInterceptor.encrypt(DateTime.now().toString(), 
                      AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret())));
		modelMap.addAttribute("tenantId", apiContext.getTenantId());
		modelMap.addAttribute("siteId", apiContext.getSiteId());
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
	
	
	@RequestMapping(value = "/getPostedOrders", method = RequestMethod.GET)
	public @ResponseBody
	String getPostedOrders(HttpServletRequest httpRequest, ModelMap model, @RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "sSearch") String sSearch) throws Exception {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId")); // TODO do at site level

		MozuOrderDetail criteria = new MozuOrderDetail();
		criteria.setOrderStatus("POSTED");
		List<MozuOrderDetail> mozuOrderDetails = orderHandler.getMozuOrderDetails(tenantId, criteria, entityHandler.getOrderEntityName());
		
		OrderJsonObject orderJsonObject = new OrderJsonObject();
		orderJsonObject.setiTotalDisplayRecords((long)mozuOrderDetails.size());
		orderJsonObject.setiTotalRecords(Long.parseLong(iDisplayLength));
		orderJsonObject.setAaData(mozuOrderDetails);

		String value = null;
		try {
			value = mapper.writeValueAsString(orderJsonObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			value = "";
		}
		return value;
	}
	
	@RequestMapping(value = "/getConflictOrders", method = RequestMethod.GET)
	public @ResponseBody
	String getConflictOrders(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "sSearch") String sSearch) throws Exception {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId")); // TODO do at site level

		MozuOrderDetail criteria = new MozuOrderDetail();
		criteria.setOrderStatus("CONFLICT");
		List<MozuOrderDetail> mozuOrderDetails = orderHandler
				.getMozuOrderDetails(tenantId, criteria, entityHandler.getOrderEntityName());
		
		OrderJsonObject orderJsonObject = new OrderJsonObject();
		orderJsonObject.setiTotalDisplayRecords((long)mozuOrderDetails.size());
		orderJsonObject.setiTotalRecords(Long.parseLong(iDisplayLength));
		orderJsonObject.setAaData(mozuOrderDetails);

		String value = null;
		try {
			value = mapper.writeValueAsString(orderJsonObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			value = "";
		}
		return value;
	}
	
	@RequestMapping(value = "/getOrderConflictsDetails", method = RequestMethod.GET)
	public @ResponseBody
	String getConflictOrdersDetails(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumber") String mozuOrderNumber,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) {	

		List<OrderConflictDetail> conflictDetails = 
				quickbooksService.getOrderConflictReasons(tenantId, mozuOrderNumber);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(conflictDetails);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			value = "";
		}
		return value;
	}
	
	/**
	 * Display orders updated in mozu after those were posted to QB. Just pull the 
	 * orders in updated status.
	 * 
	 * @param httpRequest
	 * @param model
	 * @param mozuOrderNumber
	 * @param tenantId
	 * @param siteId
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getUpdatedOrders", method = RequestMethod.GET)
	public @ResponseBody
	String getUpdatedOrders(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "sSearch") String sSearch,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {	
		
		MozuOrderDetail criteria = new MozuOrderDetail();
		criteria.setOrderStatus("UPDATED");
		List<MozuOrderDetail> mozuOrderDetails = orderHandler.getMozuOrderDetails(tenantId, criteria, 
						entityHandler.getOrderUpdatedEntityName());

		OrderJsonObject orderJsonObject = new OrderJsonObject();
		orderJsonObject.setiTotalDisplayRecords((long)mozuOrderDetails.size());
		orderJsonObject.setiTotalRecords(Long.parseLong(iDisplayLength));
		orderJsonObject.setAaData(mozuOrderDetails);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(orderJsonObject);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return value;
	}
	
	@RequestMapping(value = "/getOrderCompareDetails", method = RequestMethod.GET)
	public @ResponseBody
	String getOrderCompareDetails(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumber") String mozuOrderNumber,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {	

		List<OrderCompareDetail> compareDetails = 
				quickbooksService.getOrderCompareDetails(tenantId, mozuOrderNumber);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(compareDetails);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return value;
	}
	
	@RequestMapping(value = "/postUpdatedOrderToQB", method = RequestMethod.POST)
	public @ResponseBody
	String postUpdatedOrderToQB(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumbers") String mozuOrderNumbers,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {	

		List<String> orderNumberList = getMozuOrderNumbers(mozuOrderNumbers);
		logger.debug("" + orderNumberList.size());
		
		orderStateHandler.addUpdatesToQueue(orderNumberList, tenantId);
		//quickbooksService.updateOrdersInQuickbooks(orderNumberList, tenantId);
		
		return "Selected orders have been successfully updated in Quickbooks.";
	}
	
	private List<String> getMozuOrderNumbers(String mozuOrderNumbers) throws Exception {
		ArrayNode ordersNode = null;
		List<String> orderNumberList = new ArrayList<String>();
		try {
			ordersNode = (ArrayNode) mapper.readTree(mozuOrderNumbers);
			Iterator<JsonNode> orderNums = ordersNode.elements();
			
			while (orderNums.hasNext()) {
				JsonNode numNode = orderNums.next();
				orderNumberList.add(numNode.asText());
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} 
		return orderNumberList; 
	}

	@RequestMapping(value = "/postConflictOrderToQB", method = RequestMethod.POST)
	public @ResponseBody String postConflictOrderToQB(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumbers") String mozuOrderNumbers,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {
		
		List<String> orderNumberList = getMozuOrderNumbers(mozuOrderNumbers);
		logger.debug("" + orderNumberList.size());
		orderStateHandler.retryConflicOrders(tenantId, orderNumberList);
		return "Selected conflicted orders have been successfully slotted for entry in Quickbooks.";
		
	}
	
}
