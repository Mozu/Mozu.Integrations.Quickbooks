/**
 * 
 */
package com.mozu.qbintegration.model;

import java.util.List;

/**
 * @author Admin
 * 
 */
public class OrderJsonObject extends OrderDatatableObject {

	List<MozuOrderDetail> aaData;

	public List<MozuOrderDetail> getAaData() {
		return aaData;
	}

	public void setAaData(List<MozuOrderDetail> aaData) {
		this.aaData = aaData;
	}

}
