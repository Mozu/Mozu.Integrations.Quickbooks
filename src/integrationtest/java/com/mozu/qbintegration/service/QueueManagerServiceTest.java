package com.mozu.qbintegration.service;

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
public class QueueManagerServiceTest {

	@Autowired
	QueueManagerService queueManagerService;
	Integer tenantId = 0;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tenantId = 4508;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void queueTaskExiststest() {
		try {
			boolean exists = queueManagerService.taskExists(tenantId, "052ca9c178bf8a0d9c9350b40000119c");
			assertEquals(exists, true);
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
		
	}

}
