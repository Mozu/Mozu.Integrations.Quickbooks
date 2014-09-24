/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.service.QuickbooksService;

/**
 * @author Admin
 * 
 */
@Controller
public class OrdersController {

	public final static String ACCEPTED = "Accepted";

	private static final Logger logger = LoggerFactory
			.getLogger(OrdersController.class);

	@Autowired
	private QuickbooksService quickbooksService;

	@RequestMapping(value = "/getOrders", method = RequestMethod.GET)
	public @ResponseBody
	ObjectNode getOrders(HttpServletRequest httpRequest, ModelMap model) {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId")); //TODO do at site level

		ObjectNode returnObj = null;

		try {

			//TODO this is going to feed the orders posted tab. Work during integration
			List<MozuOrderDetails> savedOrders = quickbooksService.getMozuOrderDetails(tenantId);
			returnObj = getOrdersJson(savedOrders);

		} catch (JsonProcessingException e) {
			logger.error("Error processing order posted json response: " + e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return returnObj;
	}

	private ObjectNode getOrdersJson(List<MozuOrderDetails> savedOrders)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode returnObj = mapper.createObjectNode();

		String value = (new StringBuilder()).append("'")
				.append(mapper.writeValueAsString(savedOrders)).append("'")
				.toString();
		returnObj.put("orderListData", value);

		return returnObj;
	}
}
