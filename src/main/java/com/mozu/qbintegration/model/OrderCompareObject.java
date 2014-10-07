/**
 * 
 */
package com.mozu.qbintegration.model;

import java.util.List;

/**
 * @author Admin
 * 
 */
public class OrderCompareObject extends OrderDatatableObject {

	List<OrderCompareDetail> aaData;

	public List<OrderCompareDetail> getAaData() {
		return aaData;
	}

	public void setAaData(List<OrderCompareDetail> aaData) {
		this.aaData = aaData;
	}
}
