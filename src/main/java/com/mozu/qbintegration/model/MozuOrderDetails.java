/**
 * 
 */
package com.mozu.qbintegration.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Akshay
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MozuOrderDetails {
	
	private long enteredTime;
	
	/**
	 * 
	 */
	private String mozuOrderNumber;
	
	/**
	 * 
	 */
	private String mozuOrderId;
	
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
	 * 
	 */
	private String orderDate;
	
	/**
	 * 
	 */
	private String orderUpdatedDate;
	
	/**
	 * 
	 */
	private String conflictReason;
	
	/**
	 * 
	 */
	private String amount;
	
	/**
	 * 
	 */
	private List<QuickBooksSavedOrderLine> savedOrderLines;
	
	/**
	 * @return the enteredTime
	 */
	public long getEnteredTime() {
		return enteredTime;
	}

	/**
	 * @param enteredTime the enteredTime to set
	 */
	public void setEnteredTime(long enteredTime) {
		this.enteredTime = enteredTime;
	}
	
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the conflictReason
	 */
	public String getConflictReason() {
		return conflictReason;
	}

	/**
	 * @param conflictReason the conflictReason to set
	 */
	public void setConflictReason(String conflictReason) {
		this.conflictReason = conflictReason;
	}

	/**
	 * @return the orderUpdatedDate
	 */
	public String getOrderUpdatedDate() {
		return orderUpdatedDate;
	}

	/**
	 * @param orderUpdatedDate the orderUpdatedDate to set
	 */
	public void setOrderUpdatedDate(String orderUpdatedDate) {
		this.orderUpdatedDate = orderUpdatedDate;
	}

	/**
	 * @return the orderDate
	 */
	public String getOrderDate() {
		return orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

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
	 * @return the mozuOrderId
	 */
	public String getMozuOrderId() {
		return mozuOrderId;
	}

	/**
	 * @param mozuOrderId the mozuOrderId to set
	 */
	public void setMozuOrderId(String mozuOrderId) {
		this.mozuOrderId = mozuOrderId;
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
	
	/**
	 * @return the savedOrderLines
	 */
	public List<QuickBooksSavedOrderLine> getSavedOrderLines() {
		return savedOrderLines;
	}

	/**
	 * @param savedLines
	 */
	public void setSavedOrderLines(List<QuickBooksSavedOrderLine> savedLines) {
		this.savedOrderLines = savedLines;
	}

}

