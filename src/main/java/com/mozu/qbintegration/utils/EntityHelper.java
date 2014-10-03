package com.mozu.qbintegration.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.security.AppAuthenticator;

public class EntityHelper {

	public static final String CUST_ENTITY = "QB_CUSTOMER";

	public static final String PRODUCT_ENTITY = "QB_PRODUCT";

	public static final String SETTINGS_ENTITY = "QB_SETTINGS";
	
	public static final String ORDERS_ENTITY = "QB_ORDERS";
	
	public static final String TASKQUEUE_ENTITY = "QB_TASKQUEUE";
	
	public static final String ORDER_CONFLICT_ENTITY = "QB_ORDER_CONFLICT";

	public static final String ORDERS_UPDATED_ENTITY = "QB_UPDATED_ORDERS";;

	private static String nameSpace = "";
	
	public static String getAppNamespace(){
		if (StringUtils.isEmpty(nameSpace)) {
			String appId = AppAuthenticator.getInstance().getAppAuthInfo().getApplicationId();
			nameSpace = appId.substring(0, appId.indexOf('.'));
		}
		
		return nameSpace;
    }
	
	public static String getCustomerEntityName() {
		return EntityHelper.CUST_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getProductEntityName() {
		return EntityHelper.PRODUCT_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getOrderEntityName() {
		return EntityHelper.ORDERS_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getSettingEntityName() {
		return EntityHelper.SETTINGS_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getTaskqueueEntityName() {
		return EntityHelper.TASKQUEUE_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getOrderConflictEntityName() {
		return EntityHelper.ORDER_CONFLICT_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getOrderUpdatedEntityName() {
		return EntityHelper.ORDERS_UPDATED_ENTITY + "@" + getAppNamespace();
	}
	
	public static String getSubnavLinksEntityName() {
		return "subnavlinks@mozu";
	}
	
	public static List<JsonNode> searchEntity(Integer tenantId, String entityName, String filter) throws Exception {
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId));
		EntityCollection collection = entityResource.getEntities(entityName, 200, 0, filter, null, null);
		return collection.getItems();
	}
}

