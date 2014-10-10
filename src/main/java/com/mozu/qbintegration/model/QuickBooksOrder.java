package com.mozu.qbintegration.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.ShipAddress;

public class QuickBooksOrder {
	String txnID;
	String timeCreated;
	String timeModified;
	String editSequence;
	BigInteger txnNumber;
	String customerRef;
	String txnDate;
	String refNumber;
	BillAddress billAddress;
	ShipAddress shipAddress;
	String shipDate;
	double subTotal;
	double salesTaxPrecentage;
	double salesTaxTotal;
	double totalAmount;
	List<QuickBooksSavedOrderLine> orderLines;
	public String getTxnID() {
		return txnID;
	}
	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
	public String getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}
	public String getTimeModified() {
		return timeModified;
	}
	public void setTimeModified(String timeModified) 	{
		this.timeModified = timeModified;
	}
	public String getEditSequence() {
		return editSequence;
	}
	public void setEditSequence(String editSequence) {
		this.editSequence = editSequence;
	}
	public BigInteger getTxnNumber() {
		return txnNumber;
	}
	public void setTxnNumber(BigInteger txnNumber) {
		this.txnNumber = txnNumber;
	}
	public String getCustomerRef() {
		return customerRef;
	}
	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	public BillAddress getBillAddress() {
		return billAddress;
	}
	public void setBillAddress(BillAddress billAddress) {
		this.billAddress = billAddress;
	}
	public ShipAddress getShipAddress() {
		return shipAddress;
	}
	public void setShipAddress(ShipAddress shipAddress) {
		this.shipAddress = shipAddress;
	}
	public String getShipDate() {
		return shipDate;
	}
	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}
	public double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
	public double getSalesTaxPrecentage() {
		return salesTaxPrecentage;
	}
	public void setSalesTaxPrecentage(double salesTaxPrecentage) {
		this.salesTaxPrecentage = salesTaxPrecentage;
	}
	public double getSalesTaxTotal() {
		return salesTaxTotal;
	}
	public void setSalesTaxTotal(double salesTaxTotal) {
		this.salesTaxTotal = salesTaxTotal;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<QuickBooksSavedOrderLine> getOrderLines() {
		return orderLines;
	}
	public void setOrderLines(List<QuickBooksSavedOrderLine> orderLines) {
		this.orderLines = orderLines;
	}
	
	
}
