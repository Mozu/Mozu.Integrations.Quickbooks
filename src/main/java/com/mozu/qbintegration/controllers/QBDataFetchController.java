/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.ProductToMapToQuickbooks;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Akshay
 *
 */
@Controller
public class QBDataFetchController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(OrdersController.class);
	
	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	private QueueManagerService queueManagerService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/saveProductToQB", method = RequestMethod.POST)
	public @ResponseBody
	ObjectNode saveProductToQB(
			@RequestParam(value = "tenantId", required = false) Integer tenantId,
			@RequestParam(value = "siteId", required = false) Integer siteId,
			@RequestBody ProductToQuickbooks productToQuickbooks,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		quickbooksService.saveNewProductToQB(productToQuickbooks, tenantId, siteId);
				
		return (mapper.createObjectNode()).put("savedProduct", mapper.writeValueAsString(productToQuickbooks));
		
	}

	@RequestMapping(value = "/getAllProductsFromQB", method = RequestMethod.GET)
	public @ResponseBody
	String getPostedProducts(HttpServletRequest httpRequest,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) {

		String requestXML = quickbooksService.getAllQBProductsGetXML(tenantId,
				siteId);

		WorkTask itemQueryTask = new WorkTask();
		// Just to make it unique
		itemQueryTask.setEnteredTime(System.currentTimeMillis());
		itemQueryTask.setTaskId("");
		itemQueryTask.setQbTaskStatus("ENTERED");
		itemQueryTask.setTenantId(tenantId);
		itemQueryTask.setSiteId(siteId);
		itemQueryTask.setQbTaskType("ITEM_QUERY_ALL");
		itemQueryTask.setQbTaskRequest(requestXML);
		queueManagerService.saveTask(itemQueryTask, tenantId);
		logger.debug("Saved get all items from quickbooks task at " + new Date());
		return "The request to refresh products has been scheduled.";
		
	}
	
	@RequestMapping(value = "/getAllPostedProducts", method = RequestMethod.GET)
	public @ResponseBody
	String getAllPostedProducts(HttpServletRequest httpRequest, ModelMap model,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) {

		List<MozuProduct> mozuProductDetails = quickbooksService.getMozuProductList(tenantId) ;
		
		String value = null;
		try {
			value = mapper.writeValueAsString(mozuProductDetails);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	@RequestMapping(value = "/mapProductToQB", method = RequestMethod.POST)
	public @ResponseBody
	ObjectNode mapProductToQB(
			@RequestParam(value = "tenantId", required = false) Integer tenantId,
			@RequestParam(value = "siteId", required = false) Integer siteId,
			@RequestBody ProductToMapToQuickbooks productToMapToEB,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		//User wants us to make the product code to an existing quickbooks product list id
		// SO do that mapping in EL.
		quickbooksService.mapProductToQBInEL(productToMapToEB, tenantId, siteId);
				
		return (mapper.createObjectNode()).put("savedProduct", mapper.writeValueAsString(productToMapToEB));
		
	}
	
}
