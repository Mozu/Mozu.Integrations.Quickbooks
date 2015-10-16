package com.mozu.qbintegration.model;

import java.math.BigInteger;

public class QBResponse {
	private BigInteger statusCode = BigInteger.ZERO;
	private String statusSeverity = "";
	private String statusMessage = "";
	public BigInteger getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(BigInteger statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusSeverity() {
		return statusSeverity;
	}
	public void setStatusSeverity(String statusSeverity) {
		this.statusSeverity = statusSeverity;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
	public boolean hasError() {
		return  !statusCode.equals(0) && statusSeverity.equalsIgnoreCase("error");
	}
	
	public boolean hasWarning() {
		return  statusSeverity.equalsIgnoreCase("warn");
	}
}
