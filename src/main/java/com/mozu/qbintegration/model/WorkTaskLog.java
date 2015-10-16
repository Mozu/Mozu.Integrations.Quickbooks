package com.mozu.qbintegration.model;

import com.mozu.qbintegration.tasks.WorkTask;

public class WorkTaskLog extends WorkTask {
	private String xml;
	private String enteredTime;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getEnteredTime() {
		return enteredTime;
	}

	public void setEnteredTime(String enteredTime) {
		this.enteredTime = enteredTime;
	}
	
	
	
}
