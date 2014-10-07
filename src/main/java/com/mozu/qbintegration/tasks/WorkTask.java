/**
 * 
 */
package com.mozu.qbintegration.tasks;

import java.util.Date;

import org.joda.time.DateTime;

/**
 * @author Akshay
 * 
 */
public class WorkTask {

	private String Id;
	
	private DateTime createDate;

	private String status;
	
	private String currentStep;

	private String type;
	
	private String action;
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
		
	}
	


}
