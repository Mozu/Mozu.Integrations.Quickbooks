/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class OrderConflictDetail {
	
	/**
	 * 
	 */
	private String id;
	
	/**
	 * 
	 */
	private String orderId;
	
	
	/*
	 * 
	 */
	private String conflictReason;
	
	/**
	 * 
	 */
	private String natureOfConflict;
	
	/*
	 * 
	 */
	private String dataToFix;
	
	/**
	 * @return the mozuOrderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param mozuOrderId the mozuOrderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the enteredTime
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param enteredTime the enteredTime to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the natureOfConflict
	 */
	public String getNatureOfConflict() {
		return natureOfConflict;
	}

	/**
	 * @param natureOfConflict the natureOfConflict to set
	 */
	public void setNatureOfConflict(String natureOfConflict) {
		this.natureOfConflict = natureOfConflict;
	}

	/**
	 * @return the dataToFix
	 */
	public String getDataToFix() {
		return dataToFix;
	}

	/**
	 * @param dataToFix the dataToFix to set
	 */
	public void setDataToFix(String dataToFix) {
		this.dataToFix = dataToFix;
	}

}
