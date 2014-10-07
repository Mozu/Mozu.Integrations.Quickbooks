package com.mozu.qbintegration.events.impl;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.event.Event;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderEventHandlerTest {

	@Autowired
	OrderEventHandlerImpl orderEventHandlerImpl;
	
	Integer tenantId = 0;
	ApiContext apiContext;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tenantId = 4647;
		apiContext = new MozuApiContext(tenantId);
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	
	@Test
	public void cancelledTest() {
		try {
	
			Event event = new Event();
			event.setTopic("order.canclled");
			event.setEntityId("0534a9c3157c2812e8a8c54500001227");
			orderEventHandlerImpl.cancelled(apiContext, event);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
