package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class CustomerHandlerTest {

    @Autowired
    CustomerHandler customerHandler;
    
    Integer tenantId;
    String orderId;
    Integer customerAccountId;

    @Before
    public void setUp() throws Exception {
        tenantId = 3293;
        orderId = "0536168a157c280b28b2929200000cdd";
        customerAccountId = 1005;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        try {
            String xml = customerHandler.getQBCustomerSaveXML(tenantId, orderId, customerAccountId);
            System.out.println(xml);
        } catch(Exception exc) {
            fail(exc.getMessage());
        }
    }

}
