/**
 * 
 */
package com.mozu.qbintegration.model;

import java.util.List;

/**
 * @author Admin
 * 
 */
public class OrderJsonObject {

	Long iTotalRecords;

	Long iTotalDisplayRecords;

	String sEcho;
	
	String sColumns;
	
	List<MozuOrderDetails> aaData;

	public Long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<MozuOrderDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<MozuOrderDetails> aaData) {
		this.aaData = aaData;
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
