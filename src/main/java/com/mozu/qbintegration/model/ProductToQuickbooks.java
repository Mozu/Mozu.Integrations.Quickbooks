/**
 * 
 */
package com.mozu.qbintegration.model;

/**
 * @author Akshay
 * 
 */
public class ProductToQuickbooks {

	private String itemNameNumber;

	private String itemPurchaseDesc;

	private String itemSalesDesc;

	private String itemSalesPrice;

	private String itemManuPartNum;

	private String itemTaxCode;

	private String itemExpenseAccount;

	private String itemAssetAccount;

	private String itemIncomeAccount;

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
	 * @return the itemAssetAccount
	 */
	public String getItemAssetAccount() {
		return itemAssetAccount;
	}

	/**
	 * @param itemAssetAccount
	 *            the itemAssetAccount to set
	 */
	public void setItemAssetAccount(String itemAssetAccount) {
		this.itemAssetAccount = itemAssetAccount;
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

	private String selectedChoice;

}
