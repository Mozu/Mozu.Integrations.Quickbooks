/**
 * 
 */
package com.mozu.qbintegration.model;


/**
 * @author Admin
 * 
 */
public class Order {

	private int orderNumber;
	private String date;
	private double total;
	private String customerName;

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		String str = "OrderData"; // "[ id :"+id+" , status: "+status+",FeeToatal: "+feeTotal+",discountTotal:"
		// +discountTotal+",Email: "+email+",orderNumber :"+orderNumber+"]";
		return str;
	}

}
