/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class QBData {
	
	/**
	 * 
	 */
	private String id; //ID of mozu payment method. E.g. MC corresponds to mastercard.
	
	/**
	 * 
	 */
	private String dataType;
	
	/**
	 * 
	 */
	private String fullName; //name of QB payment method

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param type the type to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
