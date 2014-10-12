package com.mozu.qbintegration.model;

public class DataMapping {
	private QBData qbData;
	private MozuData mzData;
	private String type;
	private String mozuId;
	
	public QBData getQbData() {
		return qbData;
	}
	public void setQbData(QBData qbData) {
		this.qbData = qbData;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMozuId() {
		return mozuId;
	}
	public void setMozuId(String mozuId) {
		this.mozuId = mozuId;
	}
	public MozuData getMzData() {
		return mzData;
	}
	public void setMzData(MozuData mzData) {
		this.mzData = mzData;
	}
	
	
	
}
