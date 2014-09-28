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

	List<MozuOrderDetails> aaData;

	public List<MozuOrderDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<MozuOrderDetails> aaData) {
		this.aaData = aaData;
	}

}
