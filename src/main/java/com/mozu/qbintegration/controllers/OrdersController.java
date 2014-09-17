/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.util.ArrayList;
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
import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.qbintegration.service.MongoService;
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

	@Autowired
	private MongoService mongoService;

	@RequestMapping(value = "/getOrders", method = RequestMethod.GET)
	public @ResponseBody
	ObjectNode getOrders(HttpServletRequest httpRequest, ModelMap model) {

		final Integer tenantId = Integer.parseInt(httpRequest
				.getParameter("tenantId"));
		final Integer siteId = Integer.parseInt(httpRequest
				.getParameter("siteId"));

		ApiContext apiContext = new MozuApiContext(tenantId, siteId);
		OrderResource orderResource = new OrderResource(apiContext);

		OrderCollection orderCollection = null;
		ObjectNode returnObj = null;

		try {

			orderCollection = orderResource.getOrders(0, null, null,
					"Status eq " + ACCEPTED, null, null, null);

			final List<Order> orders = orderCollection.getItems();

			// for (final Order order : orders) {
			// mongoService.saveMozuOrder(order);
			mongoService.updateMozuOrder(orders.get(0));
			CustomerAccountResource accountResource = new CustomerAccountResource(
					apiContext);
			final CustomerAccount custAcct = accountResource.getAccount(orders
					.get(0).getCustomerAccountId());
			new Thread(new Runnable() {
				@Override
				public void run() {
					quickbooksService.saveOrderInQuickbooks(orders.get(0),
							custAcct, tenantId, siteId);
				}
			}).start();

			// }

			// returnObj = getOrdersJson(orders);
			/*
			 * CustomerAccount account = getAccount(); String customerSaveXML =
			 * quickbooksService.getQBCustomerSaveXML(account); SingleTask task
			 * = new SingleTask(); task.setRequest(customerSaveXML);
			 * quickbooksService.enterNextPayload(task);
			 */
			/*
			 * Order singleOrder = orders.get(0); CustomerAccountResource
			 * accountResource = new CustomerAccountResource(apiContext);
			 * CustomerAccount orderingCust =
			 * accountResource.getAccount(singleOrder.getCustomerAccountId());
			 * 
			 * String customerSaveXML =
			 * quickbooksService.getQBCustomerSaveXML(orderingCust); SingleTask
			 * task = new SingleTask(); task.setRequest(customerSaveXML);
			 * task.setTaskType("customeradd");
			 * quickbooksService.enterNextPayload(task);
			 * 
			 * String orderSaveXML =
			 * quickbooksService.getQBOrderSaveXML(singleOrder, orderingCust);
			 * SingleTask orderTask = new SingleTask();
			 * orderTask.setTaskType("saveorder");
			 * orderTask.setRequest(orderSaveXML);
			 * quickbooksService.enterNextPayload(orderTask);
			 */

			/*
			 * EntityListResource entityListResource = new
			 * EntityListResource(apiContext); EntityList orderList = new
			 * EntityList(); ObjectMapper mapper = new ObjectMapper();
			 * mapper.getNodeFactory().objectNode();
			 * orderList.setMetadata(metadata);
			 * entityListResource.createEntityList(orderList);
			 */

		} catch (JsonProcessingException e) {
			logger.error("Error processing history json response: " + e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return returnObj;
	}

	private CustomerAccount getAccount() {
		CustomerAccount account = new CustomerAccount();
		account.setFirstName("Amit");
		account.setLastName("Pardeshi");
		account.setLastName("Pardeshi");
		account.setEmailAddress("amitsp@ignitiv.com");
		account.setId(1001);

		Phone phone = new Phone();
		phone.setMobile("5127791917");
		CustomerContact contact = new CustomerContact();
		contact.setPhoneNumbers(phone);

		Address address = new Address();
		address.setAddress1("200 w 96th st");
		address.setCityOrTown("Bloomington");
		address.setStateOrProvince("MN");
		address.setCountryCode("USA");
		address.setPostalOrZipCode("55420");
		contact.setAddress(address);
		List<CustomerContact> contactList = new ArrayList<CustomerContact>();
		contactList.add(contact);
		account.setContacts(contactList);
		return account;
	}

	private ObjectNode getOrdersJson(List<Order> orders)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode returnObj = mapper.createObjectNode();

		String value = (new StringBuilder()).append("'")
				.append(mapper.writeValueAsString(orders)).append("'")
				.toString();
		returnObj.put("orderListData", value);

		return returnObj;
	}
}
