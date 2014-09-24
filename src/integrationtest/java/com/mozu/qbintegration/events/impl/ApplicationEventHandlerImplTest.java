package com.mozu.qbintegration.events.impl;

import static org.junit.Assert.*;

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
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class ApplicationEventHandlerImplTest {

	@Autowired
	ApplicationEventHandlerImpl applicationEventHandler;
	
	Integer tenantId = 0;
	ApiContext apiContext;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tenantId = 4508;
		apiContext = new MozuApiContext(tenantId);
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void upgradedTest() {
		try {
	
			Event event = new Event();
			event.setTopic("application.upgraded");
			applicationEventHandler.upgraded(apiContext, event);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
