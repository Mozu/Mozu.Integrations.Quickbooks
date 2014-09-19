package com.mozu.qbintegration.utils;

public class SingleTask {
	
	/**
	 * 
	 */
	private Integer taskId;
	
	/**
	 * 
	 */
	private String request;
	
	/**
	 * 
	 */
	private String response;
	
	/**
	 * 
	 */
	private Boolean isRetry;
	
	/**
	 * 
	 */
	private String taskType;

	/**
	 * @return the taskId
	 */
	public Integer getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * @return the isRetry
	 */
	public Boolean getIsRetry() {
		return isRetry;
	}

	/**
	 * @param isRetry the isRetry to set
	 */
	public void setIsRetry(Boolean isRetry) {
		this.isRetry = isRetry;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

}
