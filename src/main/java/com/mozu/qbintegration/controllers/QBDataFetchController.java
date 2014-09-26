/**
 * 
 */
package com.mozu.qbintegration.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.service.QuickbooksService;

/**
 * @author Akshay
 *
 */
@Controller
public class QBDataFetchController {
	
	@Autowired
	private QuickbooksService quickbooksService;
	
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
	

}
