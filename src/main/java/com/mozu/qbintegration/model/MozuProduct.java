package com.mozu.qbintegration.model;

public class MozuProduct {

	private String productCode ;
	private String qbProductListID ;
	private String productName ;
	private String productType; //Akshay 08-Jan-2015 fix for GFT payment type

	/**
	 * @return productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return qbProductListID
	 */
	public String getQbProductListID() {
		return qbProductListID;
	}

	/**
	 * @param qbProductListID
	 */
	public void setQbProductListID(String qbProductListID) {
		this.qbProductListID = qbProductListID;
	}

	/**
	 * @return productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	} 
	
}
