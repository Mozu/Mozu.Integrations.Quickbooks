package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

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
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.platform.EntityListResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.qbintegration.service.QuickbooksService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class EntityHandlerTest {

	private static final Logger logger = LoggerFactory.getLogger(EntityHandlerTest.class);
	
	@Autowired
	QuickbooksService quickbooksService;
	
	@Autowired
	EntityHandler entityHandler;
	
	Integer tenantId = 0;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		tenantId = 4519;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void installSchemaTest() {
		try {
			entityHandler.addSchemas(tenantId);
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	
	@Test
	public void cleanupEntity() {

		try {
//			runCleanup(entityHandler.getOrderEntityName(), "refNumber");
			runCleanup(entityHandler.getTaskqueueEntityName(), "id");
			/*runCleanup(entityHandler.getTaskqueueLogEntityName(), "enteredTime");
			runCleanup(entityHandler.getOrderConflictEntityName(), "id");
			runCleanup(entityHandler.getOrderUpdatedEntityName(), "id");
//			runCleanup(entityHandler.getOrderEntityName(), "refNumber");
//			runCleanup(entityHandler.getTaskqueueEntityName(), "id");
//			runCleanup(entityHandler.getTaskqueueLogEntityName(), "enteredTime");
//			runCleanup(entityHandler.getOrderConflictEntityName(), "id");
//			runCleanup(entityHandler.getOrderUpdatedEntityName(), "id");
			runCleanup(entityHandler.getLookupEntity(), "id");
//			runCleanup(entityHandler.getOrderPostedEntityName(), "enteredTime");
//			runCleanup(entityHandler.getOrderCancelledEntityName(), "enteredTime");
//			runCleanup(entityHandler.getLookupEntity(), "id");
			runCleanup(entityHandler.getLookupEntity(), "mozuId");
//			runCleanup(entityHandler.getCustomerEntityName(), "custEmail");
//			runCleanup(entityHandler.getProductEntityName(), "productCode");
			*/
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
		
	}
	
	private void runCleanup(String entityName, String key) throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId));
		
		EntityCollection entities = entityResource.getEntities(entityName, 200, 0, null, null, null);
		
		for(JsonNode entity : entities.getItems()) {
			if (entity.has(key)) {
				String id  = entity.get(key).asText();
				logger.info("Deleting id:"+id+" from "+entityName);
				entityResource.deleteEntity(entityName, id);
			}
		}
	}
	
	//@Test
	public void deleteEntities() {
		try {
			EntityListResource entityListResource = new EntityListResource(new MozuApiContext(tenantId));
			
			entityListResource.deleteEntityList(entityHandler.getCustomerEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderConflictEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderUpdatedEntityName());
			entityListResource.deleteEntityList(entityHandler.getProdctAddEntity());
			entityListResource.deleteEntityList(entityHandler.getTaskqueueEntityName());
			entityListResource.deleteEntityList(entityHandler.getTaskqueueLogEntityName());
			entityListResource.deleteEntityList(entityHandler.getLookupEntity());
			entityListResource.deleteEntityList(entityHandler.getMappingEntity());
			entityListResource.deleteEntityList(entityHandler.getProductEntityName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
