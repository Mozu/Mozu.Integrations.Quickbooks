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
	private String enteredTime;
	
	/**
	 * 
	 */
	private String mozuOrderId;
	
	/**
	 * 
	 */
	private String mozuOrderNumber;
	
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
