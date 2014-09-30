/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.http.HttpStatus;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.Headers;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.events.model.EventHandlerStatus;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.Crypto;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.OrderCompareObject;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.OrderJsonObject;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.utils.ApplicationUtils;
import com.mozu.qbintegration.utils.EntityHelper;

/**
 * @author Admin
 * 
 */
@Controller
@RequestMapping({ "/Orders", "/index" })
@Scope("session")
public class OrdersController {

	public final static String ACCEPTED = "Accepted";

	private static final Logger logger = LoggerFactory
			.getLogger(OrdersController.class);

	@Autowired
	private QuickbooksService quickbooksService;
	
	final ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(method = RequestMethod.POST)
	public String index(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, ModelMap modelMap)
			throws IOException, URISyntaxException {

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
			@RequestParam(value = "sSearch") String sSearch) {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId")); // TODO do at site level

		MozuOrderDetails criteria = new MozuOrderDetails();
		criteria.setOrderStatus("POSTED");
		List<MozuOrderDetails> mozuOrderDetails = quickbooksService
				.getMozuOrderDetails(tenantId, criteria, EntityHelper.getOrderEntityName());
		
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
			@RequestParam(value = "sSearch") String sSearch) {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId")); // TODO do at site level

		MozuOrderDetails criteria = new MozuOrderDetails();
		criteria.setOrderStatus("CONFLICT");
		List<MozuOrderDetails> mozuOrderDetails = quickbooksService
				.getMozuOrderDetails(tenantId, criteria, EntityHelper.getOrderEntityName());
		
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
	 */
	@RequestMapping(value = "/getUpdatedOrders", method = RequestMethod.GET)
	public @ResponseBody
	String getUpdatedOrders(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "iDisplayStart") String iDisplayStart,
			@RequestParam(value = "iDisplayLength") String iDisplayLength,
			@RequestParam(value = "sSearch") String sSearch,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) {	
		
		MozuOrderDetails criteria = new MozuOrderDetails();
		criteria.setOrderStatus("UPDATED");
		List<MozuOrderDetails> mozuOrderDetails = quickbooksService
				.getMozuOrderDetails(tenantId, criteria, 
						EntityHelper.getOrderUpdatedEntityName());

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
	
	@RequestMapping(value = "/getOrderCompareDetails", method = RequestMethod.GET)
	public @ResponseBody
	String getOrderCompareDetails(HttpServletRequest httpRequest, ModelMap model, 
			@RequestParam(value = "mozuOrderNumber") String mozuOrderNumber,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) {	

		List<OrderCompareDetail> compareDetails = 
				quickbooksService.getOrderCompareDetails(tenantId, mozuOrderNumber);
		
		String value = null;
		try {
			value = mapper.writeValueAsString(compareDetails);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			value = "";
		}
		return value;
	}
	
}
