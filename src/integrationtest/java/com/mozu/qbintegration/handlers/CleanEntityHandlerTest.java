package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.joda.time.DateTime;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.platform.EntityListResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.OrderStates;
import com.mozu.qbintegration.model.WorkTaskStatus;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/integrationtest/resources/servlet-context.xml" })
public class CleanEntityHandlerTest {

	private static final Logger logger = LoggerFactory.getLogger(CleanEntityHandlerTest.class);
	
	@Autowired
	QuickbooksService quickbooksService;
	
	@Autowired
	EntityHandler entityHandler;
	
	ApiContext mozuContext;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mozuContext = new MozuApiContext(6639);
		//mozuContext = new MozuApiContext(11674);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void installSchemaTest() {
		try {
			//entityHandler.addSchemas(tenantId);
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
	}
	
	
	@Test
	public void cleanupEntity() {

		try {
//			runCleanup(entityHandler.getOrderEntityName(), "refNumber");
			//runCleanup(entityHandler.getTaskqueueEntityName(), "id");
			runCleanup(entityHandler.getCustomerEntityName(), "custEmail", null);
			runCleanup(entityHandler.getProductEntityName(), "productCode", null);
			//runCleanup(entityHandler.getLookupEntity(), "id");
			//runCleanup(entityHandler.getTaskqueueLogEntityName(), "enteredTime");
			//runCleanup(entityHandler.getOrderConflictEntityName(), "id");
			//runCleanup(entityHandler.getOrderUpdatedEntityName(), "id");
//			runCleanup(entityHandler.getOrderEntityName(), "refNumber");
//			runCleanup(entityHandler.getTaskqueueEntityName(), "id");
//			runCleanup(entityHandler.getTaskqueueLogEntityName(), "enteredTime");
//			runCleanup(entityHandler.getOrderConflictEntityName(), "id");
//			runCleanup(entityHandler.getOrderUpdatedEntityName(), "id");
			//runCleanup(entityHandler.getLookupEntity(), "id");
//			runCleanup(entityHandler.getOrderPostedEntityName(), "enteredTime");
//			runCleanup(entityHandler.getOrderCancelledEntityName(), "enteredTime");
			
			//runCleanup(entityHandler.getLookupEntity(), "mozuId");
			
		} catch(Exception exc) {
			fail(exc.getMessage());
		}
		
	}
	
	private void runCleanup(String entityName, String key, String filter) throws Exception {
		EntityResource entityResource = new EntityResource(mozuContext);
		int totalCount = 0;
		int startIndex = 0;
		
		while (totalCount >= startIndex) {
			EntityCollection entities = entityResource.getEntities(entityName, 200, startIndex, filter, null, null);
			totalCount = entities.getTotalCount();
			startIndex += 200;
			
			for(JsonNode entity : entities.getItems()) {
				if (entity.has(key)) {
					String id  = entity.get(key).asText();
					logger.info("Deleting id:"+id+" from "+entityName);
					try {
					entityResource.deleteEntity(entityName, id);
					}catch (Exception e) {
						logger.error("Failed to remove entity", e);
					}
				}
			}
		}
	}
	
	private void show(String entityName) throws Exception {
		final ObjectMapper mapper = JsonUtils.initObjectMapper();
		EntityResource entityResource = new EntityResource(mozuContext);
		
		EntityCollection entities = entityResource.getEntities(entityName, 200, 0, null, null, null);
		
		logger.info("Listing all " + entityName);
		
		for (JsonNode entity : entities.getItems()) {
			Object object = mapper.treeToValue(entity, Object.class);
			logger.info(mapper.writeValueAsString(object));
		}
	}
	
	@Test
	public void findObject() throws Exception {
		final ObjectMapper mapper = JsonUtils.initObjectMapper();
		EntityResource entityResource = new EntityResource(mozuContext);
		EntityCollection entityCollection = entityResource.getEntities(entityHandler.getCustomerEntityName(), 200, 0, "custQBListID eq '800116C6-1425056994'", null, null);
		
		if (entityCollection != null) {
			for (JsonNode node : entityCollection.getItems()) {
				logger.info(mapper.writeValueAsString(node));
				//entityResource.deleteEntity(entityHandler.getCustomerEntityName(), node.get("custEmail").textValue());
			}
		}
	}
	
	//@Test
	public void deleteEntities() {
		try {
			/*EntityListResource entityListResource = new EntityListResource(mozuConfig);
			
			entityListResource.deleteEntityList(entityHandler.getCustomerEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderConflictEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderEntityName());
			entityListResource.deleteEntityList(entityHandler.getOrderUpdatedEntityName());
			entityListResource.deleteEntityList(entityHandler.getProdctAddEntity());
			entityListResource.deleteEntityList(entityHandler.getTaskqueueEntityName());
			entityListResource.deleteEntityList(entityHandler.getTaskqueueLogEntityName());
			entityListResource.deleteEntityList(entityHandler.getLookupEntity());
			entityListResource.deleteEntityList(entityHandler.getMappingEntity());
			entityListResource.deleteEntityList(entityHandler.getProductEntityName());*/
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void showEntities() {
		try {
			//show(entityHandler.getTaskqueueEntityName());
			//show(entityHandler.getOrderEntityName());
			show(entityHandler.getCustomerEntityName());
			//show(entityHandler.getProductEntityName());
			//show(entityHandler.getSettingEntityName());
			//show(entityHandler.getLookupEntity());
			
//			JsonNode entity = entityHandler.getEntity(mozuContext.getTenantId(), entityHandler.getCustomerEntityName(), "acorrect+email@mailinator.com");
//			
//			if (entity == null) {
//				fail("Nothing found");
//			}
		}catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void addTask() {
		WorkTask task = new WorkTask();
		task.setId("073c2ccd5621e133d447590800002d9a");
		task.setCreateDate(DateTime.now());
		task.setStatus(WorkTaskStatus.PENDING);
		task.setCurrentStep(OrderStates.CUST_QUERY);
		task.setType("Order");
		
		try {
			entityHandler.addUpdateEntity(mozuContext.getTenantId(), entityHandler.getTaskqueueEntityName(), task.getId(), task);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
