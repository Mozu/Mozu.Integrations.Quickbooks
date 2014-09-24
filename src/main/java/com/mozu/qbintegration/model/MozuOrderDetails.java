/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class MozuOrderDetails {
	
	/**
	 * 
	 */
	private String mozuOrderNumber;
	
	/**
	 * 
	 */
	private String quickbooksOrderListId;
	
	/**
	 * can be RECEIVED, POSTED, ERRORED, UPDATED
	 */
	private String orderStatus;
	
	/**
	 * 
	 */
	private String customerEmail;

	/**
	 * @return the mozuOrderNumber
	 */
	public String getMozuOrderNumber() {
		return mozuOrderNumber;
	}

	/**
	 * @param mozuOrderNumber the mozuOrderNumber to set
	 */
	public void setMozuOrderNumber(String mozuOrderNumber) {
		this.mozuOrderNumber = mozuOrderNumber;
	}

	/**
	 * @return the quickbooksOrderListId
	 */
	public String getQuickbooksOrderListId() {
		return quickbooksOrderListId;
	}

	/**
	 * @param quickbooksOrderListId the quickbooksOrderListId to set
	 */
	public void setQuickbooksOrderListId(String quickbooksOrderListId) {
		this.quickbooksOrderListId = quickbooksOrderListId;
	}

	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

}

