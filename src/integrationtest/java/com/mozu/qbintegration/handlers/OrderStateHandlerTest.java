package com.mozu.qbintegration.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;

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
		tenantId = 4647;
		orderId = "053bb36078bf8a0ba49bca0600001227";
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
	public void retryConflictOrderTest() {
		try {
			List<String> orders = new ArrayList<String>();
			orders.add(orderId);
			orderStateHandler.retryConflicOrders(tenantId, orders);
		} catch(Exception ex) {
			fail(ex.getMessage());
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
