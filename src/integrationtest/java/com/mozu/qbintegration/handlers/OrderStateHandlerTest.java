package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderStateHandlerTest {

	@Autowired
	OrderStateHandler orderStateHandler;
	
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
		tenantId = 4508;
		orderId = "05323246157c280c9426aa680000119c";
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

}
