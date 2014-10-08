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
import com.mozu.qbintegration.handlers.CustomerHandler;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.handlers.ProductHandler;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.SubnavLink;
import com.mozu.base.utils.ApplicationUtils;

/**
 * @author Akshay
 * 
 */
@Service
public class QuickbooksServiceImpl implements QuickbooksService {

	private static final Logger logger = LoggerFactory.getLogger(QuickbooksServiceImpl.class);

	private static ObjectMapper mapper = JsonUtils.initObjectMapper();



	@Autowired 
	OrderHandler orderHandler;
	
	@Autowired
	EntityHandler entityHandler;
	
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

		JsonNode custNode = mapper.valueToTree(generalSettings);
		try {
			if (!isUpdate) { // insert scenario.
				custNode = entityResource.insertEntity(custNode, mapName);
			} else {
				custNode = entityResource.updateEntity(custNode, mapName,generalSettings.getId());
			}

			Application application = ApplicationUtils.setApplicationToInitialized(context);
			addUpdateExtensionLinks(tenantId, application, serverUrl);
		} catch (ApiException e) {
			logger.error("Error saving settings for tenant id: " + tenantId, e);
			throw e;
		}

		return generalSettings;
	}

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
	public List<OrderConflictDetail> getOrderConflictReasons(Integer tenantId, String orderId) {
		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); 
		String mapName = entityHandler.getOrderConflictEntityName();
		
		EntityCollection orderConflictCollection = null;
		
		List<OrderConflictDetail> conflictDetails = new ArrayList<OrderConflictDetail>();
		try {
			orderConflictCollection = entityResource.getEntities(mapName, null, null, 
					"mozuOrderId eq " + orderId, null, null);
			
			if (null != orderConflictCollection) {
				for (JsonNode singleOrderConflict : orderConflictCollection.getItems()) {
					conflictDetails.add(mapper.readValue(singleOrderConflict.toString(), OrderConflictDetail.class));
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error getting order conflict details for order id: " + orderId);
		}
		
		return conflictDetails;
	}


	@Override
	public List<OrderCompareDetail> getOrderCompareDetails(Integer tenantId,String mozuOrderId) throws Exception {
		
		//Step 1: Get original order from qb_orders EL
		MozuOrderDetail criteria = new MozuOrderDetail();
		criteria.setOrderStatus("POSTED");
		criteria.setMozuOrderId(mozuOrderId);
		
		//Step 2: Get updated order from qb_updated_orders EL
		MozuOrderDetail criteriaForUpDate = new MozuOrderDetail();
		criteriaForUpDate.setOrderStatus("UPDATED");
		criteriaForUpDate.setMozuOrderId(mozuOrderId);
		
		//1. Get from EL the order
		List<MozuOrderDetail> postedOrders = orderHandler.getMozuOrderDetails(tenantId, 
				criteria, entityHandler.getOrderEntityName());
		
		//2. Get from EL the updated order
		List<MozuOrderDetail> updatedOrders = orderHandler.getMozuOrderDetails(tenantId, 
				criteriaForUpDate, entityHandler.getOrderUpdatedEntityName());
		
		//1. Assume one only since mozuOrderNumber is going to be unique for a tenant (or is it?)
		MozuOrderDetail postedOrder = postedOrders.get(0);
		
		//2. Get the updated order
		MozuOrderDetail updatedOrder = updatedOrders.get(0);
		
		//3. Populate one order detail each for each difference
		List<OrderCompareDetail> getOrderCompareData = getOrderCompareData(postedOrder, updatedOrder);
		return getOrderCompareData;
	}

	private List<OrderCompareDetail> getOrderCompareData(
			MozuOrderDetail postedOrder, MozuOrderDetail updatedOrder) {
		List<OrderCompareDetail> compareDetails = new ArrayList<OrderCompareDetail>();
		
		if(postedOrder.getAmount() != null && !postedOrder.getAmount().equals(updatedOrder.getAmount())) {
			OrderCompareDetail orderCompareDetail = new OrderCompareDetail();
			orderCompareDetail.setParameter("Amount");
			orderCompareDetail.setPostedOrderDetail(postedOrder.getAmount());
			orderCompareDetail.setUpdatedOrderDetail(updatedOrder.getAmount());
			compareDetails.add(orderCompareDetail);
		}
		
		return compareDetails;
	}
	
	private void addUpdateExtensionLinks(Integer tenantId, Application application, String serverUrl) throws Exception {
		ApiContext apiContext = new MozuApiContext(tenantId);
		EntityContainerResource entityContainerResource = new EntityContainerResource(apiContext);
		EntityResource entityResource = new EntityResource(apiContext);
		
		EntityContainerCollection collection = entityContainerResource.getEntityContainers(entityHandler.getSubnavLinksEntityName(),200,null,null,null,null);

		
		SubnavLink postedOrdersLink = new SubnavLink();
		postedOrdersLink.setParentId("orders");
		postedOrdersLink.setAppId(application.getAppId());
		postedOrdersLink.setPath(new String[] {"Quickbooks","Orders","Posted"});
		postedOrdersLink.setWindowTitle("Quickbooks order Management");
		postedOrdersLink.setHref(serverUrl+"/Orders?tab=posted");
		addUpdateSubNavLink(postedOrdersLink, collection, entityResource);
		
		SubnavLink conflictOrdersLink = new SubnavLink();
		conflictOrdersLink.setParentId("orders");
		conflictOrdersLink.setAppId(application.getAppId());
		conflictOrdersLink.setPath(new String[] {"Quickbooks","Orders","Conflicts"});
		conflictOrdersLink.setWindowTitle("Quickbooks order Management");
		conflictOrdersLink.setHref(serverUrl+"/Orders?tab=conflicts");
		addUpdateSubNavLink(conflictOrdersLink, collection, entityResource);
		
		SubnavLink updatedOrdersLink = new SubnavLink();
		updatedOrdersLink.setParentId("orders");
		updatedOrdersLink.setAppId(application.getAppId());
		updatedOrdersLink.setPath(new String[] {"Quickbooks","Orders","Updates"});
		updatedOrdersLink.setWindowTitle("Quickbooks order Management");
		updatedOrdersLink.setHref(serverUrl+"/Orders?tab=updates");
		addUpdateSubNavLink(updatedOrdersLink, collection, entityResource);
		
		SubnavLink cancelledOrdersLink = new SubnavLink();
		cancelledOrdersLink.setParentId("orders");
		cancelledOrdersLink.setAppId(application.getAppId());
		cancelledOrdersLink.setPath(new String[] {"Quickbooks","Orders","Cancelled"});
		cancelledOrdersLink.setWindowTitle("Quickbooks order Management");
		cancelledOrdersLink.setHref(serverUrl+"/Orders?tab=cancels");
		addUpdateSubNavLink(cancelledOrdersLink, collection, entityResource);
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


}
