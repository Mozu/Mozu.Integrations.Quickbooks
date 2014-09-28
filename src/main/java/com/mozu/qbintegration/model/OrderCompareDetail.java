/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 *
 */
public class OrderCompareDetail {
	
    /**
     * 
     */
    private String parameter;
    
    /**
     * 
     */
    private String postedOrderDetail;
    
    /**
     * 
     */
    private String updatedOrderDetail;

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the postedOrderDetail
	 */
	public String getPostedOrderDetail() {
		return postedOrderDetail;
	}

	/**
	 * @param postedOrderDetail the postedOrderDetail to set
	 */
	public void setPostedOrderDetail(String postedOrderDetail) {
		this.postedOrderDetail = postedOrderDetail;
	}

	/**
	 * @return the updatedOrderDetail
	 */
	public String getUpdatedOrderDetail() {
		return updatedOrderDetail;
	}

	/**
	 * @param updatedOrderDetail the updatedOrderDetail to set
	 */
	public void setUpdatedOrderDetail(String updatedOrderDetail) {
		this.updatedOrderDetail = updatedOrderDetail;
	}

}
