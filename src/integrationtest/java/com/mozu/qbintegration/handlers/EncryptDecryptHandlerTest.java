package com.mozu.qbintegration.handlers;



import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mozu.qbintegration.handlers.EncryptDecryptHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class EncryptDecryptHandlerTest {
	
	@Autowired
	EncryptDecryptHandler encryptDecrypt;
	
	Integer tenantId = 0;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void encryptStringTest() {
		try {
			String encryptedString = encryptDecrypt.encrypt( "password");
			
			assertNotNull(encryptedString);
			
			String decryptedString = encryptDecrypt.decrypt(encryptedString);
			
			assertEquals("password", decryptedString);
			
			
		} catch (Exception e) {
			fail(e.getMessage());
		} 
	}
}
