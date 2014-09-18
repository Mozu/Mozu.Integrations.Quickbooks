/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.List;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.utils.SingleTask;

/**
 * @author Akshay
 * 
 */
public interface QuickbooksService {

	/**
	 * This method checks the queue and returns size if it as a boolean.
	 * 
	 * @return True if the taskQueue is not empty else False.
	 */
	boolean gotWorkToDo();

	/**
	 * Get the next QBXML string from the sync list
	 * 
	 * @return XML in the form of String, for any work to be done with
	 *         quickbooks
	 */
	SingleTask getNextPayload();

	/**
	 * Enter the
	 * 
	 * @param qbXMLVal
	 */
	void enterNextPayload(final SingleTask singleTask);

	/**
	 * Must be called by consumer who got the task done
	 * 
	 * @return nothing. The value has been stored in map for future ref
	 */
	void doneWithWork();

	/**
	 * This method accepts mozu customer and returns the customer insert qbXML
	 * 
	 * @param customerAccount
	 * 
	 * @return marshalled string representation of QB CustomerAdd request.
	 */
	String getQBCustomerSaveXML(final CustomerAccount customerAccount);

	/**
	 * This method accepts mozu customer and returns the customer update qbXML
	 * 
	 * @param customerAccount
	 * 
	 * @return marshalled string representation of QB CustomerUpdate request.
	 */
	String getQBCustomerUpdateXML(final CustomerAccount customerAccount);

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
	String getQBCustomerGetXML(final CustomerAccount orderingCustomer);

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
	String getQBOrderUpdateXML();

	/**
	 * This method accepts mozu order and returns the order GET qbXML. This
	 * method will be used to decide whether to insert or update the order in
	 * QuickBooks
	 * 
	 * @return marshalled string representation of QB CustomerGet request.
	 */
	String getQBOrderGetXML();

	/**
	 * This method accepts mozu product and returns the QB product insert qbXML
	 * 
	 * @param orderItem
	 * 
	 * @return marshalled string representation of QB ProductAdd request.
	 */
	String getQBProductSaveXML(OrderItem orderItem);

	/**
	 * This method accepts nothing and returns the QB Product GET qbXML. This
	 * method will be used to fetch all products currently in QB
	 * 
	 * @param productCode
	 * 
	 * @return marshalled string representation of QB ProductGet request.
	 */
	String getQBProductsGetXML(String productCode);

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

}
