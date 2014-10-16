/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.contracts.mzdb.EntityContainer;
import com.mozu.api.contracts.mzdb.EntityContainerCollection;
import com.mozu.api.contracts.sitesettings.application.Application;
import com.mozu.api.resources.platform.entitylists.EntityContainerResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.base.utils.ApplicationUtils;
import com.mozu.qbintegration.handlers.EncryptDecryptHandler;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.QBDataHandler;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.QBSession;
import com.mozu.qbintegration.model.SubnavLink;

/**
 * @author Akshay
 * 
 */
@Service
public class QuickbooksServiceImpl implements QuickbooksService {

	private static final Logger logger = LoggerFactory.getLogger(QuickbooksServiceImpl.class);

	private static ObjectMapper mapper = JsonUtils.initObjectMapper();

	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	private QBDataHandler qbDataHandler;
	
	@Autowired
	private EncryptDecryptHandler encryptDecryptHandler;
	
	public QuickbooksServiceImpl() {

	}


	@Override
	public GeneralSettings saveOrUpdateSettingsInEntityList(
			GeneralSettings generalSettings, Integer tenantId, String serverUrl) throws Exception {

		// First get an entity for settings if already present.
		MozuApiContext context =new MozuApiContext(tenantId); 
		EntityResource entityResource = new EntityResource(context); 
		String mapName = entityHandler.getSettingEntityName();
		generalSettings.setId(tenantId.toString());
		boolean isUpdate = false;

		try {
			entityResource.getEntity(mapName, tenantId.toString());
			isUpdate = true;
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(),e);
				throw e;
			}
		}

		JsonNode settingsNode = mapper.valueToTree(generalSettings);
		try {
			if (!isUpdate) { // insert scenario.
				settingsNode = entityResource.insertEntity(settingsNode, mapName);
				
				//Akshay 11-Oct-2014 added Account, Vendor and sales tax cod setup data fetch tasks to queue.
				qbDataHandler.refreshAllData(tenantId);
				//configureInitialSetupData(tenantId);
			} else {
				settingsNode = entityResource.updateEntity(settingsNode, mapName,generalSettings.getId());
			}

			Application application = ApplicationUtils.setApplicationToInitialized(context);
			addUpdateExtensionLinks(tenantId, application, serverUrl);
		} catch (ApiException e) {
			logger.error("Error saving settings for tenant id: " + tenantId, e);
			throw e;
		}

		return generalSettings;
	}

	/**
	 * Pull all available Accounts, Vendors and Sales Tax codes from QB and save
	 * while saving for the first time. Subsequent updates happen with the help
	 * of on demand buttons.
	 * 
	 * @param tenantId
	 * @throws Exception 
	 */
	/*private void configureInitialSetupData(Integer tenantId) throws Exception {
		//Account setup
		initiateAccountsRefresh(tenantId);
		
		//Vendor setup
		initiateVendorRefresh(tenantId);
		
		//Sales Tax 
		initiateSalesTaxRefresh(tenantId);
	}*/
	
	@Override
	public GeneralSettings getSettingsFromEntityList(Integer tenantId) throws Exception {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); 
		JsonNode savedEntry = null;
		String mapName = entityHandler.getSettingEntityName();

		try {
			savedEntry = entityResource.getEntity(mapName, tenantId.toString());
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND"))
				throw e;
		}

		GeneralSettings savedSettings = null;
		if (savedEntry != null) {
			savedSettings = mapper.readValue(savedEntry.toString(), GeneralSettings.class);
		}
		return savedSettings;
	}
	

	@Override
	public List<OrderConflictDetail> getOrderConflictReasons(Integer tenantId, String orderId) throws Exception {
		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); 
		String mapName = entityHandler.getOrderConflictDetailEntityName();
		
		EntityCollection orderConflictCollection = null;
		
		List<OrderConflictDetail> conflictDetails = new ArrayList<OrderConflictDetail>();
		try {
			orderConflictCollection = entityResource.getEntities(mapName, null, null, 
					"orderId eq " + orderId, null, null);
			
			if (null != orderConflictCollection) {
				for (JsonNode singleOrderConflict : orderConflictCollection.getItems()) {
					conflictDetails.add(mapper.readValue(singleOrderConflict.toString(), OrderConflictDetail.class));
				}
			}
			
		} catch (Exception e) {
			logger.error("Error getting order conflict details for order id: " + orderId);
			throw e;
		}
		
		return conflictDetails;
	}
	
	
	private void addUpdateExtensionLinks(Integer tenantId, Application application, String serverUrl) throws Exception {
		ApiContext apiContext = new MozuApiContext(tenantId);
		EntityContainerResource entityContainerResource = new EntityContainerResource(apiContext);
		EntityResource entityResource = new EntityResource(apiContext);
		
		EntityContainerCollection collection = entityContainerResource.getEntityContainers(entityHandler.getSubnavLinksEntityName(),200,null,null,null,null);

		String title = "Quickbooks Order Management";
		SubnavLink link = new SubnavLink();
		link.setParentId("orders");
		link.setAppId(application.getAppId());
		link.setWindowTitle(title);
		link.setPath(new String[] {"Quickbooks","Orders","Posted"});
		link.setHref(serverUrl+"/Orders?tab=posted");
		addUpdateSubNavLink(link, collection, entityResource);
		
		link.setPath(new String[] {"Quickbooks","Orders","Conflicts"});
		link.setHref(serverUrl+"/Orders?tab=conflicts");
		addUpdateSubNavLink(link, collection, entityResource);
		
		link.setPath(new String[] {"Quickbooks","Orders","Updates"});
		link.setHref(serverUrl+"/Orders?tab=updates");
		addUpdateSubNavLink(link, collection, entityResource);
		
		link.setPath(new String[] {"Quickbooks","Orders","Cancelled"});
		link.setHref(serverUrl+"/Orders?tab=cancels");
		addUpdateSubNavLink(link, collection, entityResource);
		
		link.setPath(new String[] {"Quickbooks","Orders","Pending"});
		link.setHref(serverUrl+"/Orders?tab=queue");
		addUpdateSubNavLink(link, collection, entityResource);
	}
	
	private void addUpdateSubNavLink(SubnavLink subNavLink,EntityContainerCollection collection,EntityResource entityResource ) throws Exception {
		boolean updated = false;
		
		for(EntityContainer container: collection.getItems()) {
			String id = container.getId();
			SubnavLink link = mapper.readValue(container.getItem().toString(), SubnavLink.class);
			if (Arrays.equals(link.getPath(), subNavLink.getPath())) {
				JsonNode node = mapper.valueToTree(subNavLink);
				entityResource.updateEntity(node, entityHandler.getSubnavLinksEntityName(), id);	
				updated = true;
			} 
		}
		
		if (!updated) {
			JsonNode node = mapper.valueToTree(subNavLink);
			entityResource.insertEntity(node, entityHandler.getSubnavLinksEntityName());			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mozu.qbintegration.service.QuickbooksService#getMozuProductList(java
	 * .lang.Integer)
	 */
	public List<MozuProduct> getMozuProductList(Integer tenantId) {

		List<MozuProduct> mozuProductList = new ArrayList<MozuProduct>();

		try {
			// First get an entity for settings if already present.
			EntityResource entityResource = new EntityResource(
					new MozuApiContext(tenantId));
			String mapName = entityHandler.getProductEntityName();
			EntityCollection mozuProductCollection = entityResource.getEntities(mapName, 1000, 0, null, null, null);

			if (null != mozuProductCollection) {
				MozuProduct mozuProduct = null;
				for (JsonNode singleOrder : mozuProductCollection.getItems()) {
					mozuProduct = new MozuProduct();
					mozuProduct.setProductCode(singleOrder.get("productCode")
							.asText());
					mozuProduct.setQbProductListID(singleOrder.get(
							"qbProdustListID").asText());
					mozuProduct.setProductName(singleOrder.get("productName")
							.asText());
					mozuProductList.add(mozuProduct);
				}
			}
		} catch (Exception e) {
			logger.error("Exception getting all products from entity list: "
					+ e);
		}
		return mozuProductList;
	}


	@Override
	public QBSession addSession(Integer tenantId) throws Exception {
		QBSession session = new QBSession();
		session.setKey(String.valueOf(System.currentTimeMillis()));
		
		String pwd = encryptDecryptHandler.encrypt(session.getKey(),tenantId+"~"+session.getKey());
		session.setPwd(pwd);
		
		entityHandler.addUpdateEntity(tenantId, entityHandler.getSettingEntityName(), session.getId(), session);
		return session;
	}


	@Override
	public void deleteSession(Integer tenantId) throws Exception {
		QBSession session = new QBSession();
		entityHandler.deleteEntity(tenantId, entityHandler.getSettingEntityName(), session.getId());
		
	}


	@Override
	public QBSession getSession(Integer tenantId) throws Exception {
		QBSession session = new QBSession();
		JsonNode node = entityHandler.getEntity(tenantId,  entityHandler.getSettingEntityName(), session.getId());
		if (node != null) {
			return mapper.readValue(node.toString(), QBSession.class);
		} else {
			throw new Exception("Session not found");
		}
	}

	
	
}
