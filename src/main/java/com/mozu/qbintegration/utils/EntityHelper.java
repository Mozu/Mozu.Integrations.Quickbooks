package com.mozu.qbintegration.utils;

public class EntityHelper {

	public static final String CUST_ENTITY = "QB_CUSTOMER";

	public static final String PRODUCT_ENTITY = "QB_PRODUCT";

	public static final String SETTINGS_ENTITY = "QB_SETTINGS";
	
	public static final String ORDERS_ENTITY = "QB_ORDERS";
	
	public static final String TASKQUEUE_ENTITY = "QB_TASKQUEUE";

	public static String getCustomerEntityName() {
		return EntityHelper.CUST_ENTITY + "@" + ApplicationUtils.getAppNamespace();
	}
	
	public static String getProductEntityName() {
		return EntityHelper.PRODUCT_ENTITY + "@" + ApplicationUtils.getAppNamespace();
	}

	
	public static String getOrderEntityName() {
		return EntityHelper.ORDERS_ENTITY + "@" + ApplicationUtils.getAppNamespace();
	}
	
	public static String getSettingEntityName() {
		return EntityHelper.SETTINGS_ENTITY + "@" + ApplicationUtils.getAppNamespace();
	}
	
	public static String getTaskqueueEntityName() {
		return EntityHelper.TASKQUEUE_ENTITY + "@" + ApplicationUtils.getAppNamespace();
	}
}
