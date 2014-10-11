/**
 * 
 */
package com.mozu.qbintegration.model;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;

/**
 * @author Akshay
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MozuOrderDetail {
	
	private String enteredTime;
	
	/**
	 * 
	 */
	private Integer orderNumber;
	
	/**
	 * 
	 */
	private String id;
	
	
	
	/**
	 * 
	 */
	private String customerEmail;

	/**
	 * 
	 */
	private String createDate;
	
	/**
	 * 
	 */
	private String updatedDate;

	
	/**
	 * 
	 */
	private String conflictReason;
	
	private boolean existsInQb;
	
	/**
	 * 
	 */
	private double amount;
	
	
	
	/**
	 * @return the enteredTime
	 */
	public String getEnteredTime() {
		return enteredTime;
	}

	/**
	 * @param enteredTime the enteredTime to set
	 */
	public void setEnteredTime(String enteredTime) {
		this.enteredTime = enteredTime;
	}
	
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
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
	public String getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param l the orderUpdatedDate to set
	 */
	public void setUpdatedDate(String updateDate) {
		this.updatedDate = updateDate;
	}

	/**
	 * @return the orderDate
	 */
	public String getCreateDate() {
		return createDate;
	}

	/**
	 * @param l the orderDate to set
	 */
	public void setOrderDate(String createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the mozuOrderNumber
	 */
	public Integer getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param mozuOrderNumber the mozuOrderNumber to set
	 */
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * @return the mozuOrderId
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param mozuOrderId the mozuOrderId to set
	 */
	public void setId(String id) {
		this.id = id;
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

	public boolean isExistsInQb() {
		return existsInQb;
	}

	public void setExistsInQb(boolean existsInQb) {
		this.existsInQb = existsInQb;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	

	
}

