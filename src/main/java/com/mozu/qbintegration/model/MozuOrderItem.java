package com.mozu.qbintegration.model;

public class MozuOrderItem {
	private String productCode;
	private String qbItemCode;
	private String description;
	private Integer qty;
	private double amount;
	private boolean isMics;
	
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getQbItemCode() {
		return qbItemCode;
	}
	public void setQbItemCode(String qbItemCode) {
		this.qbItemCode = qbItemCode;
	}
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public boolean isMic() {
		return isMics;
	}
	public void setMisc(boolean isMics) {
		this.isMics = isMics;
	}
	
	
	public double getTotalAmount() {
		return this.amount * this.qty;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isMics() {
		return isMics;
	}
	public void setMics(boolean isMics) {
		this.isMics = isMics;
	}
	
	
}
