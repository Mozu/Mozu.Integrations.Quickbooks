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
import com.mozu.qbintegration.handlers.ConfigHandler;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.utils.ApplicationUtils;

@Component
public class ApplicationEventHandlerImpl implements ApplicationEventHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(ApplicationEventHandlerImpl.class);

	private static final String APP_NAMESPACE = "Ignitiv";

	private static final String CUST_ENTITY = "QB_CUSTOMER";
	
	private static final String PRODUCT_ENTITY = "QB_PRODUCT";

	@Autowired
	private QuickbooksService quickbooksService;

	@Autowired
	ConfigHandler configHandler;

	@PostConstruct
	public void initialize() {
		EventManager.getInstance().registerHandler(this);
		logger.info("Application event handler initialized");
	}

	@Override
	public EventHandlerStatus disabled(ApiContext apiContext, Event event) {
		return new EventHandlerStatus(HttpStatus.SC_OK);
	}

	@Override
	public EventHandlerStatus enabled(ApiContext apiContext, Event event) {
		logger.debug("Application enabled event");
		logger.debug("Installing entity list schema");
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		Integer tenantId = apiContext.getTenantId();
		try {
			installCustomerSchema(tenantId);
			installProductSchema(tenantId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			status.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			status.setMessage("Could not install schema on tenant " + tenantId + ", terminating.");
		}
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
			if (configHandler.getTenantSetting(apiContext.getTenantId()) != null) {
				logger.debug("tenant settings retrieved");
				try {
					ApplicationUtils.setApplicationToInitialized(apiContext);
					status = new EventHandlerStatus(HttpStatus.SC_OK);
				} catch (Exception e) {
					logger.warn("Exception intializing application: "
							+ e.getMessage());
					status = new EventHandlerStatus(e.getMessage(),
							HttpStatus.SC_INTERNAL_SERVER_ERROR);
				}
			}
		} catch (Exception e) {
			status = new EventHandlerStatus(e.getMessage(),
					HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return status;
	}

	/**
	 * Install the customer schema in entity list
	 * @param tenantId
	 * @throws Exception
	 */
	private void installCustomerSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(APP_NAMESPACE);
		entityList.setContextLevel("tenant");
		entityList.setName(CUST_ENTITY);
		entityList.setIdProperty(getIndexedProperty("custEmail", "string"));
		entityList.setIndexA(getIndexedProperty("custQBListID", "string"));
		entityList.setIndexB(getIndexedProperty("custName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		EntityListResource entityListResource = new EntityListResource(
				new MozuApiContext(tenantId));
		EntityList existing = null;
		String mapName = CUST_ENTITY + "@" + APP_NAMESPACE;
		try {
			//entityListResource.deleteEntityList(mapName);
			existing = entityListResource.getEntityList(mapName);
			
		} catch (ApiException ae) {
			if (!StringUtils.equals(ae.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND"))
				throw ae;
		}
		if (existing == null) {
			entityListResource.createEntityList(entityList);
		} else {
			entityListResource.updateEntityList(entityList, mapName);
		}
	}
	
	/**
	 * Install the product schema in entity list
	 * @param tenantId
	 * @throws Exception 
	 */
	private void installProductSchema(Integer tenantId) throws Exception {
		EntityList entityList = new EntityList();
		entityList.setNameSpace(APP_NAMESPACE);
		entityList.setContextLevel("tenant");
		entityList.setName(PRODUCT_ENTITY);
		entityList.setIdProperty(getIndexedProperty("productCode", "string"));
		entityList.setIndexA(getIndexedProperty("qbProdustListID", "string"));
		entityList.setIndexB(getIndexedProperty("productName", "string"));
		entityList.setIsVisibleInStorefront(Boolean.TRUE);
		entityList.setIsLocaleSpecific(false);
		entityList.setIsSandboxDataCloningSupported(Boolean.TRUE);
		entityList.setIsShopperSpecific(false);

		EntityListResource entityListResource = new EntityListResource(
				new MozuApiContext(tenantId));
		EntityList existing = null;
		String mapName = PRODUCT_ENTITY + "@" + APP_NAMESPACE;
		try {
			//entityListResource.deleteEntityList(mapName);
			existing = entityListResource.getEntityList(mapName);
		} catch (ApiException ae) {
			if (!StringUtils.equals(ae.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND"))
				throw ae;
		}
		if (existing == null) {
			entityListResource.createEntityList(entityList);
		} else {
			entityListResource.updateEntityList(entityList, mapName);
		}
		
	}

	private IndexedProperty getIndexedProperty(String name, String type) {
		IndexedProperty property = new IndexedProperty();
		property.setPropertyName(name);
		property.setDataType(type);

		return property;
	}
}
