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
		orderId = "053322f2157c2815042101760000119c";
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
	
	/*@Test
	public void someTest() {
		ObjectMapper mapper = JsonUtils.initObjectMapper();
		CustomerAccount account = new CustomerAccount();
        account.setFirstName("test");
        account.setLastName("cust7");
        account.setAcceptsMarketing(true);
        account.setEmailAddress("test_cust7@volusion.com");
        account.setUserName("test_cust7");
        account.setLocaleCode("en-US");
        
        List<CustomerContact> contacts = new ArrayList<CustomerContact>();
        CustomerContact contact = new CustomerContact();
        
        contact.setFirstName(account.getFirstName());
        contact.setLastNameOrSurname(account.getLastName());
        contact.setEmail(account.getEmailAddress());
        
        Address address = new Address();
        address.setAddress1("123 main street");
        address.setCityOrTown("Austin");
        address.setStateOrProvince("TX");
        address.setPostalOrZipCode("78759");
        address.setCountryCode("US");
        
        contact.setAddress(address);
        contacts.add(contact);
        
        account.setContacts(contacts);
        
        List<CustomerAttribute> attributes = new ArrayList<CustomerAttribute>();
        CustomerAttribute attr1 = new CustomerAttribute();
        attr1.setFullyQualifiedName("tenant~customerrole");
        attr1.setAttributeDefinitionId(2);
        List<Object> vals = new ArrayList<Object>();
        vals.add("buyer");
        attr1.setValues(vals);
        attributes.add(attr1);
        account.setAttributes(attributes);
        
        attr1 = new CustomerAttribute();
        attr1.setFullyQualifiedName("tenant~anotherAttr");
        attr1.setAttributeDefinitionId(2);
        vals = new ArrayList<Object>();
        vals.add("some other value");
        attr1.setValues(vals);
        attributes.add(attr1);
        account.setAttributes(attributes);
        
        try{
        	
        	String value = mapper.writeValueAsString(account);
        	System.out.println(value);
        }
        catch(Exception ex){
               System.out.println(ex.getMessage());
        }

	}*/

}
