/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
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
import com.mozu.qbintegration.handlers.ProductHandler;
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
	
	private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
	
	@Autowired
	ProductHandler productHandler;
	
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
		
		productHandler.addProductToQB(tenantId, productToQuickbooks );
				
		return (mapper.createObjectNode()).put("savedProduct", mapper.writeValueAsString(productToQuickbooks));
		
	}

	@RequestMapping(value = "/getAllProductsFromQB", method = RequestMethod.GET)
	public @ResponseBody
	String getPostedProducts(HttpServletRequest httpRequest,
			@RequestParam(value = "tenantId") Integer tenantId,
			@RequestParam(value = "siteId") Integer siteId) throws Exception {

		//String requestXML = productHandler.getAllQBProductsGetXML(tenantId);

		queueManagerService.addTask(tenantId, String.valueOf(tenantId)+"-Product", "Product", "ITEM", "Refresh");
		
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
		productHandler.mapProductToQBInEL(productToMapToEB, tenantId);
				
		return (mapper.createObjectNode()).put("savedProduct", mapper.writeValueAsString(productToMapToEB));
		
	}
	
}
