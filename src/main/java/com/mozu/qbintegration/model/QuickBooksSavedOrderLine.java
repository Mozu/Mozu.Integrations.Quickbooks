/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class QuickBooksSavedOrderLine {
	
	private String productCode; //This will be same as quickbooks product name
	
	private String qbLineItemTxnID;

	private Integer quantity;

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the qbLineItemTxnID
	 */
	public String getQbLineItemTxnID() {
		return qbLineItemTxnID;
	}

	/**
	 * @param qbLineItemTxnID the qbLineItemTxnID to set
	 */
	public void setQbLineItemTxnID(String qbLineItemTxnID) {
		this.qbLineItemTxnID = qbLineItemTxnID;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	
}
