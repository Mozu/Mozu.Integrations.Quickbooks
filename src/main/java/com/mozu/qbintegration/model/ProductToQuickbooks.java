/**
 * 
 */
package com.mozu.qbintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Akshay
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductToQuickbooks {

	private String itemNameNumber;

	private String itemPurchaseDesc;

	private String itemSalesDesc;

	private String itemSalesPrice;

	private String itemManuPartNum;

	private String itemTaxCode;

	private String itemExpenseAccount;

	private String itemIncomeAccount;

	private String status;
	
	private String errorMessage;
	
	private String selectedChoice;
	
	private String selectedVendor;
	
	private String itemPurchaseCost;
	
	/**
	 * @return the itemNameNumber
	 */
	public String getItemNameNumber() {
		return itemNameNumber;
	}

	/**
	 * @param itemNameNumber
	 *            the itemNameNumber to set
	 */
	public void setItemNameNumber(String itemNameNumber) {
		this.itemNameNumber = itemNameNumber;
	}

	/**
	 * @return the itemPurchaseDesc
	 */
	public String getItemPurchaseDesc() {
		return itemPurchaseDesc;
	}

	/**
	 * @param itemPurchaseDesc
	 *            the itemPurchaseDesc to set
	 */
	public void setItemPurchaseDesc(String itemPurchaseDesc) {
		this.itemPurchaseDesc = itemPurchaseDesc;
	}

	/**
	 * @return the itemSalesDesc
	 */
	public String getItemSalesDesc() {
		return itemSalesDesc;
	}

	/**
	 * @param itemSalesDesc
	 *            the itemSalesDesc to set
	 */
	public void setItemSalesDesc(String itemSalesDesc) {
		this.itemSalesDesc = itemSalesDesc;
	}

	/**
	 * @return the itemSalesPrice
	 */
	public String getItemSalesPrice() {
		return itemSalesPrice;
	}

	/**
	 * @param itemSalesPrice
	 *            the itemSalesPrice to set
	 */
	public void setItemSalesPrice(String itemSalesPrice) {
		this.itemSalesPrice = itemSalesPrice;
	}

	/**
	 * @return the itemManuPartNum
	 */
	public String getItemManuPartNum() {
		return itemManuPartNum;
	}

	/**
	 * @param itemManuPartNum
	 *            the itemManuPartNum to set
	 */
	public void setItemManuPartNum(String itemManuPartNum) {
		this.itemManuPartNum = itemManuPartNum;
	}

	/**
	 * @return the itemTaxCode
	 */
	public String getItemTaxCode() {
		return itemTaxCode;
	}

	/**
	 * @param itemTaxCode
	 *            the itemTaxCode to set
	 */
	public void setItemTaxCode(String itemTaxCode) {
		this.itemTaxCode = itemTaxCode;
	}

	/**
	 * @return the itemExpenseAccount
	 */
	public String getItemExpenseAccount() {
		return itemExpenseAccount;
	}

	/**
	 * @param itemExpenseAccount
	 *            the itemExpenseAccount to set
	 */
	public void setItemExpenseAccount(String itemExpenseAccount) {
		this.itemExpenseAccount = itemExpenseAccount;
	}

	/**
	 * @return the itemIncomeAccount
	 */
	public String getItemIncomeAccount() {
		return itemIncomeAccount;
	}

	/**
	 * @param itemIncomeAccount
	 *            the itemIncomeAccount to set
	 */
	public void setItemIncomeAccount(String itemIncomeAccount) {
		this.itemIncomeAccount = itemIncomeAccount;
	}

	/**
	 * @return the selectedChoice
	 */
	public String getSelectedChoice() {
		return selectedChoice;
	}

	/**
	 * @param selectedChoice
	 *            the selectedChoice to set
	 */
	public void setSelectedChoice(String selectedChoice) {
		this.selectedChoice = selectedChoice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the selectedVendor
	 */
	public String getSelectedVendor() {
		return selectedVendor;
	}

	/**
	 * @param selectedVendor the selectedVendor to set
	 */
	public void setSelectedVendor(String selectedVendor) {
		this.selectedVendor = selectedVendor;
	}

	/**
	 * @return the itemPurchaseCost
	 */
	public String getItemPurchaseCost() {
		return itemPurchaseCost;
	}

	/**
	 * @param itemPurchaseCost the itemPurchaseCost to set
	 */
	public void setItemPurchaseCost(String itemPurchaseCost) {
		this.itemPurchaseCost = itemPurchaseCost;
	}

	
	

}
