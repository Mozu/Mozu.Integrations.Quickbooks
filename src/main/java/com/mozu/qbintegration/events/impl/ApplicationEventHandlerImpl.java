package com.mozu.qbintegration.events.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.api.ApiContext;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.event.Event;
import com.mozu.api.contracts.mzdb.EntityList;
import com.mozu.api.contracts.mzdb.IndexedProperty;
import com.mozu.api.events.EventManager;
import com.mozu.api.events.handlers.ApplicationEventHandler;
import com.mozu.api.events.model.EventHandlerStatus;
import com.mozu.api.resources.platform.EntityListResource;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.utils.ApplicationUtils;
import com.mozu.qbintegration.utils.EntityHelper;

@Component
public class ApplicationEventHandlerImpl implements ApplicationEventHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationEventHandlerImpl.class);

	private String appNamespace;

	@Autowired
	private QuickbooksService quickbooksService;


	@PostConstruct
	public void initialize() {
		EventManager.getInstance().registerHandler(this);
		appNamespace = ApplicationUtils.getAppNamespace();
		logger.info("Application event handler initialized");
	}

	@Override
	public EventHandlerStatus disabled(ApiContext apiContext, Event event) {
		return new EventHandlerStatus(HttpStatus.SC_OK);
	}

	@Override
	public EventHandlerStatus enabled(ApiContext apiContext, Event event) {
		logger.debug("Application enabled event");
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		return status;
	}

	@Override
	public EventHandlerStatus installed(ApiContext apiContext, Event event) {
		logger.debug("Application installed event");

		return enableApplication(apiContext);
	}

	@Override
	public EventHandlerStatus uninstalled(ApiContext apiContext, Event event) {
		return new EventHandlerStatus(HttpStatus.SC_OK);
	}

	@Override
	public EventHandlerStatus upgraded(ApiContext apiContext, Event event) {
		logger.debug("Application upgraded event");
		return enableApplication(apiContext);
	}

	@PreDestroy
	public void cleanup() {
		EventManager.getInstance().unregisterHandler(this.getClass());
		logger.debug("Application event handler unregistered");
	}

	private EventHandlerStatus enableApplication(ApiContext apiContext) {
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);

		logger.debug("Enable application for tenant "
				+ apiContext.getTenantId());

		// Only set initialized if there are valid values in the settings
		try {
			GeneralSettings settings = quickbooksService.getSettingsFromEntityList(apiContext.getTenantId());
			installGenSettingsSchema(apiContext.getTenantId());
			installCustomerSchema(apiContext.getTenantId());
			installProductSchema(apiContext.getTenantId());
			installOrdersSchema(apiContext.getTenantId());
			installQBTaskQueueSchema(apiContext.getTenantId());
			if (settings != null && StringUtils.isNotEmpty(settings.getQbAccount()) && StringUtils.isNoneEmpty(settings.getQbPassword())) {
				ApplicationUtils.setApplicationToInitialized(apiContext);
			}
			
		} catch (Exception e) {
			status = new EventHandlerStatus(e.getMessage(),
					HttpStatus.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getMessage(), e);
		}
		return status;
	}

	/**
	 * Install the customer schema in entity list
	 * 
	 * @param tenantId
	 * @throws Exception
	 */
	private void installCustomerSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(appNamespace);
		entityList.setContextLevel("tenant");
		entityList.setName(EntityHelper.CUST_ENTITY);
		entityList.setIdProperty(getIndexedProperty("custEmail", "string"));
		entityList.setIndexA(getIndexedProperty("custQBListID", "string"));
		entityList.setIndexB(getIndexedProperty("custName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = EntityHelper.getCustomerEntityName();
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
		entityList.setNameSpace(appNamespace);
		entityList.setContextLevel("tenant");
		entityList.setName(EntityHelper.PRODUCT_ENTITY);
		entityList.setIdProperty(getIndexedProperty("productCode", "string"));
		entityList.setIndexA(getIndexedProperty("qbProdustListID", "string"));
		entityList.setIndexB(getIndexedProperty("productName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = EntityHelper.getProductEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);
	}

	private void installGenSettingsSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(appNamespace);
		entityList.setContextLevel("tenant");
		entityList.setName(EntityHelper.SETTINGS_ENTITY);
		entityList.setIdProperty(getIndexedProperty("id", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = EntityHelper.getSettingEntityName();
		createOrUpdateEntityList(tenantId, entityList, mapName);

	}

	private void installOrdersSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(appNamespace);
		entityList.setContextLevel("tenant");
		entityList.setName(EntityHelper.ORDERS_ENTITY);
		entityList
				.setIdProperty(getIndexedProperty("mozuOrderNumber", "string"));
		entityList.setIndexA(getIndexedProperty("quickbooksOrderListId",
				"string"));
		entityList.setIndexB(getIndexedProperty("orderStatus", "string")); // RECEIVED,
																			// POSTED,
																			// ERRORED,
																			// UPDATED
		entityList.setIndexC(getIndexedProperty("customerEmail", "string"));
		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = EntityHelper.getOrderEntityName();
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
		entityList.setNameSpace(appNamespace);
		entityList.setContextLevel("tenant");
		entityList.setName(EntityHelper.TASKQUEUE_ENTITY);
		entityList.setIdProperty(getIndexedProperty("enteredTime", "string"));
		entityList.setIndexA(getIndexedProperty("taskId", "string"));
		entityList.setIndexB(getIndexedProperty("tenantId", "string"));
		entityList.setIndexC(getIndexedProperty("qbTaskStatus", "string"));
		entityList.setIndexD(getIndexedProperty("qbTaskType", "string"));

		entityList.setIsVisibleInStorefront(Boolean.FALSE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		String mapName = EntityHelper.getTaskqueueEntityName();
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
			//TODO comment
//			EntityResource entityResource = new EntityResource(
//					new MozuApiContext(tenantId));
//			EntityCollection collection = entityResource.getEntities(mapName);
//			for (JsonNode jsonNode: collection.getItems()) {
//				entityResource.deleteEntity(EntityHelper.getTaskqueueEntityName(), jsonNode.get("enteredTime").asText());
//			}
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
			ae.printStackTrace();
		}
	}

	private IndexedProperty getIndexedProperty(String name, String type) {
		IndexedProperty property = new IndexedProperty();
		property.setPropertyName(name);
		property.setDataType(type);

		return property;
	}
}
