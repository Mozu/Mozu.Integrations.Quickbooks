
package com.mozu.qbintegration.model;

public class GeneralSettings {
	
	private String id;
	
	private String wsURL;
	
	private String qbAccount;
	
	private String qbPassword;
	
	private Boolean accepted = false;
	
	private Boolean completed = false;
	
	private Boolean cancelled = false;

	private Boolean paid = false;
	
	private Boolean fulFilled = false;
	
	private Boolean updated = false;
	
	private String shippingProductCode;
	
	private String discountProductCode;
	
	private String qbwFile;
	/**
	 * @return the wsURL
	 */
	public String getWsURL() {
		return wsURL;
	}

	/**
	 * @param wsURL the wsURL to set
	 */
	public void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}

	/**
	 * @return the qbAccount
	 */
	public String getQbAccount() {
		return qbAccount;
	}

	/**
	 * @param qbAccount the qbAccount to set
	 */
	public void setQbAccount(String qbAccount) {
		this.qbAccount = qbAccount;
	}

	/**
	 * @return the qbPassword
	 */
	public String getQbPassword() {
		return qbPassword;
	}

	/**
	 * @param qbPassword the qbPassword to set
	 */
	public void setQbPassword(String qbPassword) {
		this.qbPassword = qbPassword;
	}

	/**
	 * @return the accepted
	 */
	public Boolean getAccepted() {
		return accepted;
	}

	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	/**
	 * @return the completed
	 */
	public Boolean getCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the cancelled
	 */
	public Boolean getCancelled() {
		return cancelled;
	}

	/**
	 * @param cancelled the cancelled to set
	 */
	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @return get id
	 */
	public String getId() {
		return id;
	}
	

	/**
	 * @param set id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getShippingProductCode() {
		return shippingProductCode;
	}

	public void setShippingProductCode(String shippingProductCode) {
		this.shippingProductCode = shippingProductCode;
	}

	public String getDiscountProductCode() {
		return discountProductCode;
	}

	public void setDiscountProductCode(String discountProductCode) {
		this.discountProductCode = discountProductCode;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public Boolean getFulFilled() {
		return fulFilled;
	}

	public void setFulFilled(Boolean fulFilled) {
		this.fulFilled = fulFilled;
	}

	public Boolean getUpdated() {
		return updated;
	}

	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}

	public String getQbwFile() {
		return qbwFile;
	}

	public void setQbwFile(String qbwFile) {
		this.qbwFile = qbwFile;
	}
	
	
	
}

