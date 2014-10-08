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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderHandlerTest {

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
		tenantId = 5872;
		orderId = "05357e6721f6631de04a4598000016f0";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getQBOrderSaveXMLTest() {
		try {
			String xml = orderHandler.getQBOrderSaveXML(tenantId, orderId);
			System.out.println(xml);
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}

}
