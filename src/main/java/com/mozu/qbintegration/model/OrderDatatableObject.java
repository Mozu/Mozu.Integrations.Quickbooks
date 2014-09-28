/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class OrderDatatableObject {
	
	protected Long iTotalRecords;

	protected Long iTotalDisplayRecords;

	protected String sEcho;
	
	protected String sColumns;
	

	public Long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public Long getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(Long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public String getsEcho() {
		return sEcho;
	}

	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}

	public String getsColumns() {
		return sColumns;
	}

	/*
	 * public OrderList getOrderList() { return orderList; }
	 * 
	 * public void setOrderList(OrderList orderList) { this.orderList =
	 * orderList; }
	 */

	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}

}
