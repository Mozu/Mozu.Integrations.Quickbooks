/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.List;

import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderConflictDetail;

/**
 * @author Akshay
 * 
 */
public interface QuickbooksService {

	/**
	 * Save general settings from 2nd tab or update if already saved.
	 * 
	 * @param generalSettings
	 * @param tenantId
	 * @return
	 */
	GeneralSettings saveOrUpdateSettingsInEntityList(GeneralSettings generalSettings, Integer tenantId, String serverUrl) throws Exception;

	/**
	 * Get the general settings while populating the 2nd tab on click
	 * 
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	GeneralSettings getSettingsFromEntityList(Integer tenantId)	throws Exception;

	
	/**
	 * @param tenantId
	 * @param orderId
	 * @return
	 * @throws Exception 
	 */
	List<OrderConflictDetail> getOrderConflictReasons(Integer tenantId,	String orderId) throws Exception;

	
	/**
	 * Get all products from the entitylist
	 * @param tenantId
	 * @return
	 */
	public List<MozuProduct> getMozuProductList(Integer tenantId);

	/**
	 * @param tenantId
	 * @throws Exception 
	 */
	void initiateAccountsRefresh(Integer tenantId) throws Exception;

	/**
	 * @param tenantId
	 */
	void initiateVendorRefresh(Integer tenantId) throws Exception;

	/**
	 * @param tenantId
	 */
	void initiateSalesTaxRefresh(Integer tenantId) throws Exception;
	

}
