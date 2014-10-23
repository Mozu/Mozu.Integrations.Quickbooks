package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.contracts.mzdb.EntityList;
import com.mozu.api.contracts.mzdb.IndexedProperty;
import com.mozu.api.resources.platform.EntityListResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.api.utils.JsonUtils;

@Component
public class EntityHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(EntityHandler.class);
	private static ObjectMapper mapper = JsonUtils.initObjectMapper();

	private final String CUST_ENTITY = "QB_CUSTOMER";
	private final String PRODUCT_ENTITY = "QB_PRODUCT";
	private final String PRODUCT_ADD_ENTITY = "QB_PRODUCT_ADD";
	private final String SETTINGS_ENTITY = "QB_SETTINGS";
	private final String TASKQUEUE_ENTITY = "QB_TASKQUEUE";
	private final String TASKQUEUELOG_ENTITY = "QB_TASKQUEUELOG";
	private final String ORDER_CONFLICT_DETAIL_ENTITY = "QB_CONFLICT_DETAIL";
	private final String ORDERS_UPDATED_ENTITY = "QB_UPDATED";
	private final String ORDER_CONFLICT_ENTITY = "QB_CONFLICT";
	private final String ORDER_POSTED_ENTITY = "QB_POSTED";
	private final String ORDER_CANCELLED_ENTITY = "QB_CANCELLED";
	private final String ORDERS_ENTITY = "QB_ORDERS";
	private final String LOOKUP_ENTITY = "QB_LOOKUPDATA";
	private final String MAPPING_ENTITY = "QB_MAPPING";

	private String nameSpace = "";

	public EntityHandler() {
		mapper.registerModule(new JodaModule());
		mapper.configure(
				com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
				false);

	}

	private String getNamespace() {
		if (StringUtils.isEmpty(nameSpace)) {
			String appId = AppAuthenticator.getInstance().getAppAuthInfo()
					.getApplicationId();
			nameSpace = appId.substring(0, appId.indexOf('.'));
		}

		return nameSpace;
	}

	public String getCustomerEntityName() {
		return CUST_ENTITY + "@" + getNamespace();
	}

	public String getProductEntityName() {
		return PRODUCT_ENTITY + "@" + getNamespace();
	}

	public String getOrderEntityName() {
		return ORDERS_ENTITY + "@" + getNamespace();
	}

	public String getSettingEntityName() {
		return SETTINGS_ENTITY + "@" + getNamespace();
	}

	public String getTaskqueueEntityName() {
		return TASKQUEUE_ENTITY + "@" + getNamespace();
	}
	

	public String getTaskqueueLogEntityName() {
		return TASKQUEUELOG_ENTITY + "@" + getNamespace();
	}

	public String getOrderConflictEntityName() {
		return ORDER_CONFLICT_ENTITY + "@" + getNamespace();
	}

	public String getOrderConflictDetailEntityName() {
		return ORDER_CONFLICT_DETAIL_ENTITY + "@" + getNamespace();
	}

	public String getOrderUpdatedEntityName() {
		return ORDERS_UPDATED_ENTITY + "@" + getNamespace();
	}

	public String getOrderPostedEntityName() {
		return ORDER_POSTED_ENTITY + "@" + getNamespace();
	}

	public String getOrderCancelledEntityName() {
		return ORDER_CANCELLED_ENTITY + "@" + getNamespace();
	}

	public String getProdctAddEntity() {
		return PRODUCT_ADD_ENTITY + "@" + getNamespace();
	}

	public String getLookupEntity() {
		return LOOKUP_ENTITY + "@" + getNamespace();
	}

	public String getMappingEntity() {
		return MAPPING_ENTITY + "@" + getNamespace();
	}

	public String getSubnavLinksEntityName() {
		return "subnavlinks@mozu";
	}

	public List<JsonNode> searchEntity(Integer tenantId, String entityName,
			String filter) throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		EntityCollection collection = entityResource.getEntities(entityName,
				200, 0, filter, null, null);
		return collection.getItems();
	}

	public void addSchemas(int tenantId) throws Exception {
		getNamespace();
		installGenSettingsSchema(tenantId);
		installCustomerSchema(tenantId);
		installProductSchema(tenantId);
		installOrdersSchema(tenantId);
		installQBTaskQueueSchema(tenantId);
		installQBTaskQueueLogSchema(tenantId);
		installOrderConflictSchema(tenantId);
		installOrdersUpdatedSchema(tenantId);
		installProductAddSchema(tenantId);
		installOrderPostedSchema(tenantId);
		installOrderConflictDetailsSchema(tenantId);
		installOrderCancelledSchema(tenantId);
		installLookupSchema(tenantId);
		installMappingSchema(tenantId);
	}

	/**
	 * Install the customer schema in entity list
	 * 
	 * @param tenantId
	 * @throws Exception
	 */
	private void installCustomerSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(CUST_ENTITY);
		entityList.setIdProperty(getIndexedProperty("custEmail", "string"));
		entityList.setIndexA(getIndexedProperty("custQBListID", "string"));
		entityList.setIndexB(getIndexedProperty("custName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getCustomerEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	/**
	 * Install the product schema in entity list
	 * 
	 * @param tenantId
	 * @throws Exception
	 */
	private void installProductSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(PRODUCT_ENTITY);
		entityList.setIdProperty(getIndexedProperty("productCode", "string"));
		entityList.setIndexA(getIndexedProperty("qbProdustListID", "string"));
		entityList.setIndexB(getIndexedProperty("productName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getProductEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installGenSettingsSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(SETTINGS_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getSettingEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);

	}

	private void installOrdersSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(ORDERS_ENTITY);
		entityList.setIdProperty(getIndexedProperty("refNumber", "string"));
		entityList.setIndexA(getIndexedProperty("timeCreated", "date"));
		entityList.setIndexB(getIndexedProperty("timeModified", "date"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);

	}

	/**
	 * Install the entity list which will maintain tasks as they are processed
	 * 
	 * @param tenantId
	 * @throws Exception
	 */
	private void installQBTaskQueueSchema(Integer tenantId) throws Exception {

		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(TASKQUEUE_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIndexA(getIndexedProperty("createDate", "date"));
		entityList.setIndexB(getIndexedProperty("status", "string"));
		entityList.setIndexC(getIndexedProperty("currentStep", "string"));
		entityList.setIndexD(getIndexedProperty("type", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getTaskqueueEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installQBTaskQueueLogSchema(Integer tenantId) throws Exception {

		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(TASKQUEUELOG_ENTITY);
		entityList.setIdProperty(getIndexedProperty("enteredTime", "string"));
		entityList.setIndexA(getIndexedProperty("createDate", "date"));
		entityList.setIndexB(getIndexedProperty("status", "string"));
		entityList.setIndexC(getIndexedProperty("type", "string"));
		entityList.setIndexC(getIndexedProperty("id", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getTaskqueueLogEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installOrderPostedSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(this.ORDER_POSTED_ENTITY);
		entityList.setIdProperty(getIndexedProperty("enteredTime", "string"));
		entityList.setIndexA(getIndexedProperty("id", "string"));
		entityList.setIndexB(getIndexedProperty("orderNumber", "integer"));
		entityList.setIndexC(getIndexedProperty("createDate", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderConflictDetailEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installOrderCancelledSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(this.ORDER_CANCELLED_ENTITY);
		entityList.setIdProperty(getIndexedProperty("enteredTime", "string"));
		entityList.setIndexA(getIndexedProperty("id", "string"));
		entityList.setIndexB(getIndexedProperty("orderNumber", "integer"));
		entityList.setIndexC(getIndexedProperty("createDate", "string"));
		entityList.setIndexC(getIndexedProperty("updatedDate", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderCancelledEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installOrderConflictSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(ORDER_CONFLICT_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIndexA(getIndexedProperty("createDate", "string"));
		entityList.setIndexB(getIndexedProperty("orderNumber", "integer"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderConflictEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installOrderConflictDetailsSchema(Integer tenantId)
			throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(this.ORDER_CONFLICT_DETAIL_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIndexA(getIndexedProperty("orderId", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderConflictDetailEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installOrdersUpdatedSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(ORDERS_UPDATED_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIndexB(getIndexedProperty("orderNumber", "string")); // RECEIVED,
																			// POSTED,
																			// ERRORED,
																			// UPDATED
		entityList.setIndexC(getIndexedProperty("updatedDate", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = getOrderUpdatedEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);

	}

	private void installProductAddSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(PRODUCT_ADD_ENTITY);
		entityList
				.setIdProperty(getIndexedProperty("itemNameNumber", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = this.getProdctAddEntity();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installLookupSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(LOOKUP_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIndexA(getIndexedProperty("dataType", "string")); // ACCOUNT,
																		// VENDOR,
																		// TAXCODE
																		// for
																		// now
		entityList.setIndexB(getIndexedProperty("fullname", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = this.getLookupEntity();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installMappingSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(nameSpace);
		entityList.setContextLevel("tenant");
		entityList.setName(this.MAPPING_ENTITY);
		entityList.setIdProperty(getIndexedProperty("mozuId", "string"));
		entityList.setIndexA(getIndexedProperty("type", "string")); // ACCOUNT,
																	// VENDOR,
																	// TAXCODE
																	// for now
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = this.getMappingEntity();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	/*
	 * Create or update entity list
	 */
	private void createOrUpdateEntityList(Integer tenantId,
			EntityList entityList, String mapName) throws Exception {
		EntityList existing = null;
		EntityListResource entityListResource = new EntityListResource(
				new MozuApiContext(tenantId));
		try {
			existing = entityListResource.getEntityList(mapName);
		} catch (ApiException ae) {
			if (!StringUtils.equals(ae.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND"))
				throw ae;
		}
		try {
			if (existing == null) {
				entityListResource.createEntityList(entityList);
			} else {
				entityListResource.updateEntityList(entityList, mapName);
			}
		} catch (ApiException ae) {
			// TODO: log error and throw
			ae.printStackTrace();
		}
	}

	private IndexedProperty getIndexedProperty(String name, String type) {
		IndexedProperty property = new IndexedProperty();
		property.setPropertyName(name);
		property.setDataType(type);

		return property;
	}

	public void addEntity(Integer tenantId, String entityName, Object value)
			throws Exception {
		ObjectNode taskNode = mapper.valueToTree(value);

		// Add the mapping entry
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			entityResource.insertEntity(taskNode, entityName);
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + e.getMessage());
			throw e;
		}
	}

	public void addUpdateEntity(Integer tenantId, String entityName, String id,
			Object value) throws Exception {
		ObjectNode taskNode = mapper.valueToTree(value);

		// Add the mapping entry
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			JsonNode existing = getEntity(tenantId, entityName, id);

			if (existing == null) {
				entityResource.insertEntity(taskNode, entityName);
			} else {
				entityResource.updateEntity(taskNode, entityName, id);
			}
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + id);
			throw e;
		}
	}

	public void updateEntity(Integer tenantId, String entityName, String id,
			Object value) throws Exception {
		ObjectNode taskNode = mapper.valueToTree(value);

		// Add the mapping entry
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			entityResource.updateEntity(taskNode, entityName, id);
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + id);
			throw e;
		}
	}

	public void deleteEntity(Integer tenantId, String entityName, String id)
			throws Exception {
		// Add the mapping entry
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			entityResource.deleteEntity(entityName, id);
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + id);
			throw e;
		}
	}

	public JsonNode getEntity(Integer tenantId, String entityName, String id)
			throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		JsonNode entity = null;
		
		try {
			entity = entityResource.getEntity(entityName, id);
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND")) {
				logger.error("Error retrieving entity for email id: " + entity);
				throw e;
			}
		}
		return entity;
	}

	public List<JsonNode> getEntityCollection(Integer tenantId,
			String entityName, String filterCriteria) throws Exception {
		return getEntityCollection(tenantId, entityName, filterCriteria, null,
				1);
	}

	public List<JsonNode> getEntityCollection(Integer tenantId,
			String entityName, String filterCriteria, String sortBy,
			Integer pageSize) throws Exception {
		EntityCollection nodesCollection = getEntityCollection(tenantId, entityName, filterCriteria, sortBy, null, pageSize);
		if (nodesCollection!=null) {
		    return nodesCollection.getItems();
		} else {
		    return new ArrayList<JsonNode>();
		}
	}
	
	/**
	 * Get the collection so that we get totalCount and nodes.
	 * @param tenantId
	 * @param entityName
	 * @param filterCriteria
	 * @param sortBy
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public EntityCollection getEntityCollection(Integer tenantId,
			String entityName, String filterCriteria, String sortBy,
			Integer startIndex, Integer pageSize) throws Exception {

		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		EntityCollection collection = null;
		try {
			if(startIndex == null) {
				startIndex = 0;
			}
			collection = entityResource.getEntities(
					entityName, pageSize, startIndex, filterCriteria, sortBy, null);
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}

		return collection;
	}
}
