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

import com.mozu.qbintegration.model.GeneralSettings;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class QuickbooksServiceImplTest {

	@Autowired
	QuickbooksService quickbooksService;
	
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
	public void getSetttingsTest() {
		try {
			GeneralSettings settings = quickbooksService.getSettingsFromEntityList(tenantId);
			
			if (settings != null) {
				assertNotNull(settings.getQbAccount());
				assertNotNull(settings.getQbPassword());
				assertNotNull(settings.getWsURL());
			}
			
		} catch(Exception e) {
			fail(e.getMessage());
		}
		
	}

}
