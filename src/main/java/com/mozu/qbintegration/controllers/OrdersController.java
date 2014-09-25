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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.OrderJsonObject;
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
	
	final ObjectMapper mapper = new ObjectMapper();

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
				.getMozuOrderDetails(tenantId, criteria);
		
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

	private ArrayNode getOrdersJson(List<MozuOrderDetails> savedOrders)
			throws JsonProcessingException {

		
		ArrayNode arrayNode = mapper.createArrayNode();
		for(MozuOrderDetails details: savedOrders) {
			ObjectNode singleOrder = mapper.createObjectNode();
			populateSingleOrder(details, singleOrder);
			arrayNode.add(singleOrder);
		}

		return arrayNode;
	}

	private void populateSingleOrder(MozuOrderDetails details,
			ObjectNode singleOrder) {
		singleOrder.put("mozuOrderNumber", details.getMozuOrderNumber());
		singleOrder.put("quickbooksOrderListId", details.getQuickbooksOrderListId());
		singleOrder.put("customerEmail", details.getCustomerEmail());
		singleOrder.put("orderDate", details.getOrderDate());
		singleOrder.put("orderUpdatedDate", details.getOrderUpdatedDate());
		singleOrder.put("amount", details.getAmount());
	}
}
