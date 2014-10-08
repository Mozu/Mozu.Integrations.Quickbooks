package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerAttribute;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.utils.JsonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderStateHandlerTest {

	@Autowired
	OrderStateHandler orderStateHandler;
	
	@Autowired 
	OrderHandler orderHandler;
	
	Integer tenantId;
	String orderId;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tenantId = 4519;
		orderId = "0535c84d78bf8a13dc8f7637000011a7";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void processOrderTest() {
		try {
			ApiContext apiContext = new MozuApiContext(tenantId);
			orderStateHandler.processOrder(orderId, apiContext );
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	@Test
	public void allItemsFoundTest() {
		try {
			Order order = orderHandler.getOrder(orderId, tenantId);
			boolean foundAll = orderStateHandler.allItemsFound(tenantId, order);
			assertEquals(true, foundAll);
		}catch(Exception exc) {
			fail(exc.getMessage());
		}
	}

}
