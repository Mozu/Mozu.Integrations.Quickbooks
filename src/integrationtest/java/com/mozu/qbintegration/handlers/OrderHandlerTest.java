package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.qbintegration.service.QueueManagerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderHandlerTest {
	private static final Logger logger = LoggerFactory.getLogger(OrderHandlerTest.class);
	
	@Autowired
	OrderHandler orderHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
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
		orderId = "05c70ab378bf8a18500685d700001227";
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	
	@Test
	public void initProductRefresh() throws InterruptedException {
		try {
			queueManagerService.addTask(tenantId, String.valueOf(tenantId)+"-Product", "Product", "ITEM", "Refresh");
		}catch(Exception exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	public void getQBOrderSaveXMLTest() throws InterruptedException {
		try {
			String xml = orderHandler.getQBOrderSaveXML(tenantId, orderId);
			System.out.println(xml);
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}

}
