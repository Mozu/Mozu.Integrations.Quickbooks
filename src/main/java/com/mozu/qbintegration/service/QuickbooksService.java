/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.List;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.OrderCompareDetail;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Akshay
 * 
 */
public interface QuickbooksService {

	/**
	 * This method accepts mozu customer and returns the customer insert qbXML
	 * 
	 * @param customerAccount
	 * 
	 * @return marshalled string representation of QB CustomerAdd request.
	 */
	String getQBCustomerSaveXML(final Order order,
			final CustomerAccount customerAccount);

	/**
	 * This method accepts mozu customer and returns the customer update qbXML
	 * 
	 * @param customerAccount
	 * 
	 * @return marshalled string representation of QB CustomerUpdate request.
	 */
	String getQBCustomerUpdateXML(final Order order,
			final CustomerAccount customerAccount);

	/**
	 * This method accepts mozu customer and returns the customer GET qbXML.
	 * This method will be used to decide whether to insert or update the
	 * customer in QuickBooks
	 * 
	 * @param orderingCustomer
	 *            customer we are looking for
	 * 
	 * @return marshalled string representation of QB CustomerGet request.
	 */
	String getQBCustomerGetXML(final Order order,
			final CustomerAccount orderingCustomer);

	/**
	 * This method accepts mozu Order and returns the sales order insert qbXML
	 * 
	 * @param singleOrder
	 * @param customerQBListID
	 *            the list ID of customer
	 * @param itemListIDs
	 * 
	 * @return marshalled string representation of QB SalesOrderAdd request.
	 */
	String getQBOrderSaveXML(Order singleOrder, String customerQBListID,
			List<String> itemListIDs);

	/**
	 * This method accepts mozu Order and returns the sales order update qbXML
	 * 
	 * @return marshalled string representation of QB SalesOrderUpdate request.
	 */
	String getQBOrderUpdateXML(final Order order,
			final CustomerAccount customerAccount);

	/**
	 * This method accepts mozu order and returns the order GET qbXML. This
	 * method will be used to decide whether to insert or update the order in
	 * QuickBooks
	 * 
	 * @return marshalled string representation of QB CustomerGet request.
	 */
	String getQBOrderGetXML(final Order order);

	/**
	 * This method accepts mozu product and returns the QB product insert qbXML
	 * 
	 * @param productToQuickbooks
	 * @return marshalled string representation of QB ProductAdd request.
	 */
	String getQBProductSaveXML(ProductToQuickbooks productToQuickbooks);

	/**
	 * This method accepts nothing and returns the QB Product GET qbXML. This
	 * method will be used to fetch all products currently in QB
	 * 
	 * @param productCode
	 * 
	 * @return marshalled string representation of QB ProductGet request.
	 */
	String getQBProductsGetXML(final Order order, String productCode);

	/**
	 * 
	 * @param respFromQB
	 * @return
	 */
	Object getUnmarshalledValue(String respFromQB);

	/**
	 * The step wise execution of order entry into mozu
	 * 
	 * @param order
	 * @param customerAccount
	 * @param tenantId
	 * @param siteId
	 */
	void saveOrderInQuickbooks(Order order, CustomerAccount customerAccount,
			Integer tenantId, Integer siteId);

	/**
	 * Save general settings from 2nd tab or update if already saved.
	 * 
	 * @param generalSettings
	 * @param tenantId
	 * @return
	 */
	GeneralSettings saveOrUpdateSettingsInEntityList(
			GeneralSettings generalSettings, Integer tenantId, String serverUrl) throws Exception;

	/**
	 * Get the general settings while populating the 2nd tab on click
	 * 
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	GeneralSettings getSettingsFromEntityList(Integer tenantId)
			throws Exception;

	/**
	 * Get the orders saved in entity list
	 * 
	 * @param tenantId
	 * @param mozuOrderDetails
	 * @param mapName
	 * @return
	 */
	List<MozuOrderDetails> getMozuOrderDetails(Integer tenantId,
			MozuOrderDetails mozuOrderDetails, String mapName);

	/**
	 * @param workTask
	 * @param tenantId
	 */
	void setNextTask(WorkTask workTask, Integer tenantId);

	/**
	 * @param orderId
	 * @param tenantId
	 * @return
	 */
	Order getMozuOrder(String orderId, Integer tenantId, Integer siteId);

	void saveCustInEntityList(CustomerAccount custAcct, String customerListId,
			Integer tenantId, Integer siteId);

	/**
	 * Get the customer based on the email address
	 * 
	 * @param custAcct
	 * @return
	 */
	String getCustFromEntityList(CustomerAccount custAcct, Integer tenantId,
			Integer siteId);

	/**
	 * @param orderItem
	 * @param qbProdustListID
	 * @param tenantId
	 * @param siteId
	 */
	void saveProductInEntityList(OrderItem orderItem, String qbProdustListID,
			Integer tenantId, Integer siteId);

	/**
	 * Get the product quickbooks list id based on product code
	 * 
	 * @param orderItem
	 * @return
	 */
	String getProductFromEntityList(OrderItem orderItem, Integer tenantId,
			Integer siteId);

	/**
	 * @param order
	 * @param tenantId
	 * @param siteId
	 * @return
	 */
	CustomerAccount getMozuCustomer(Order order, Integer tenantId,
			Integer siteId);

	/**
	 * @param orderId
	 * @param tenantId
	 * @param siteId
	 * @param custAcct
	 * @param order
	 * @param itemListIds
	 */
	void addOrderAddTaskToQueue(String orderId, Integer tenantId,
			Integer siteId, CustomerAccount custAcct, Order order,
			List<String> itemListIds);

	/**
	 * @param orderId
	 * @param tenantId
	 * @param siteId
	 * @param order
	 * @param productCode
	 */
	void addItemQueryTaskToQueue(String orderId, Integer tenantId,
			Integer siteId, Order order, String productCode);

	/**
	 * @param orderId
	 * @param tenantId
	 * @param siteId
	 * @param order
	 * @param custAcct
	 */
	void addCustAddTaskToQueue(String orderId, Integer tenantId,
			Integer siteId, Order order, CustomerAccount custAcct);

	/**
	 * @param mozuOrderDetails
	 * @param custAccount
	 * @param mapName
	 * @param tenantId
	 * @param siteId
	 */
	void saveOrderInEntityList(MozuOrderDetails mozuOrderDetails,
			CustomerAccount custAccount, String mapName, Integer tenantId,
			Integer siteId);

	/**
	 * @param mozuOrderDetails
	 * @param custAccount
	 * @param mapName
	 * @param tenantId
	 * @param siteId
	 */
	void updateOrderInEntityList(MozuOrderDetails mozuOrderDetails,
			CustomerAccount custAccount, String mapName, Integer tenantId,
			Integer siteId);

	/**
	 * @param tenantId
	 * @param mozuOrderNumber
	 * @param conflictReasons
	 */
	void saveConflictInEntityList(Integer tenantId, Integer mozuOrderNumber,
			List<OrderConflictDetail> conflictReasons);

	/**
	 * @param tenantId
	 * @param orderId
	 * @return
	 */
	List<OrderConflictDetail> getOrderConflictReasons(Integer tenantId,
			String orderId);

	/**
	 * @param productToQuickbooks
	 * @param tenantId
	 * @param siteId
	 */
	void saveNewProductToQB(ProductToQuickbooks productToQuickbooks,
			Integer tenantId, Integer siteId);

	/**
	 * Get the order comparison data for originally posted order and updated order
	 * 
	 * @param tenantId
	 * @param mozuOrderNumber
	 * @return
	 */
	List<OrderCompareDetail> getOrderCompareDetails(Integer tenantId,
			String mozuOrderNumber);
}
