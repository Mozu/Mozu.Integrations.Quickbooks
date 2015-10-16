package com.mozu.qbintegration.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class OrderStateHandlerTest {
	private static final Logger logger = LoggerFactory.getLogger(OrderStateHandlerTest.class);
	
	@Autowired
	OrderStateHandler orderStateHandler;
	
	@Autowired 
	OrderHandler orderHandler;
	
	@Autowired
	EntityHandler entityHandler;
	
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
	public void processOrderTest() {
		try {
			ApiContext apiContext = new MozuApiContext(tenantId);
			orderStateHandler.processOrder(orderId, apiContext );
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	public void ReprocessOrders() {
		try {
			ApiContext apiContext = new MozuApiContext(tenantId);
			OrderResource orderResource = new OrderResource(apiContext);
			int startIndex = 0;
			int pageSize = 200;
			OrderCollection orders = orderResource.getOrders(startIndex, pageSize, null, "submittedDate gt 2015-01-18T00:00:00z", null, null, null);
			logger.info("Total orders : "+orders.getTotalCount());
			
			while(startIndex < orders.getTotalCount()) {
				List<String> updateOrders = new ArrayList<String>();
				
				for(Order order: orders.getItems()) {
					logger.info("Reprocessing order : "+order.getId()+", orderNuber :"+order.getOrderNumber());
					
					
					if (!order.getStatus().equals("Completed") && !order.getPaymentStatus().equals("Paid")) continue;
					JsonNode pendingQueue = entityHandler.getEntity(tenantId, entityHandler.getTaskqueueEntityName(), order.getId());
					JsonNode conflictQueue = entityHandler.getEntity(tenantId, entityHandler.getOrderConflictEntityName(), order.getId());
					
					if (pendingQueue != null) {
						logger.info("Order in pending queue : "+order.getId()+"..skipping");
						continue;
					}
					if (conflictQueue != null) {
						logger.info("Order in conflict queue : "+order.getId()+"..skipping");
						continue;
					}

					
					logger.info("Processing order to queue : "+order.getId());
					try {
						orderStateHandler.processOrder(order.getId(), apiContext);
						JsonNode updateNode = entityHandler.getEntity(tenantId, entityHandler.getOrderUpdatedEntityName(), order.getId());
						if (updateNode != null) {
							logger.info("Order went into update : "+order.getId()+" adding to pending queue");
							updateOrders.add(order.getId());
						} else {
							logger.info("Order is not in update queue: "+order.getId()+"..skipping");
						}
					} catch (Exception exc) {
						logger.error(exc.getMessage(), exc);
					}
					

				}
				
				orderStateHandler.addUpdatesToQueue(updateOrders, tenantId, "update");
				startIndex = startIndex + pageSize;
				logger.info("Getting orders from "+startIndex);
				orders = orderResource.getOrders(startIndex, pageSize, null, "submittedDate gt 2015-01-18T00:00:00z", null, null, null);
				
			}
			
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	/*@Test
	public void retryConflictOrderTest() {
		try {
			List<String> orders = new ArrayList<String>();
			orders.add(orderId);
			orderStateHandler.retryConflicOrders(tenantId, orders,"retry");
		} catch(Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	
	@Test
	public void allItemsFoundTest() {
		try {
			Order order = orderHandler.getOrder(orderId, tenantId);
			boolean foundAll = orderStateHandler.allItemsFound(tenantId, order);
			assertEquals(true, foundAll);
		}catch(Exception exc) {
			fail(exc.getMessage());
		}
	}*/

}
