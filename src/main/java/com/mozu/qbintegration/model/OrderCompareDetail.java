/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class OrderCompareDetail {
	
	QuickBooksOrder postedOrder;
	QuickBooksOrder updatedOrder;
    
	public QuickBooksOrder getPostedOrder() {
		return postedOrder;
	}
	public void setPostedOrder(QuickBooksOrder postedOrder) {
		this.postedOrder = postedOrder;
	}
	public QuickBooksOrder getUpdatedOrder() {
		return updatedOrder;
	}
	public void setUpdatedOrder(QuickBooksOrder updatedOrder) {
		this.updatedOrder = updatedOrder;
	}
    
    

}
