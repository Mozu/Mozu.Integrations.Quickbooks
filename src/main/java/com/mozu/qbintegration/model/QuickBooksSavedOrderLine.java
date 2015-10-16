/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class QuickBooksSavedOrderLine {
	
	private String fullName; //This will be same as quickbooks product name
	
	private String txnLineId;

	private Integer quantity;

	private double rate;
	
	private double amount;
	
	/**
	 * @return the productCode
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getTxnLineId() {
		return txnLineId;
	}

	public void setTxnLineId(String txnLineId) {
		this.txnLineId = txnLineId;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	
	
}
