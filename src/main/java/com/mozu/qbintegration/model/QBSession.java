package com.mozu.qbintegration.model;

public class QBSession {

	String id;
	String key;
	String pwd;
	
	public String getId() {
		return "Session";
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
}
