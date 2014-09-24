/**
 * 
 */
package com.mozu.qbintegration.tasks;

/**
 * @author Akshay
 * 
 */
public class WorkTask {

	private String qbTaskType;
	
	private Long enteredTime;

	private String taskId;

	private String qbTaskRequest;
	
	private String qbTaskResponse;

	private String qbTaskStatus;
	
	private Integer tenantId;
	
	private Integer siteId;

	/**
	 * @return the qbTaskType
	 */
	public String getQbTaskType() {
		return qbTaskType;
	}

	/**
	 * @param qbTaskType the qbTaskType to set
	 */
	public void setQbTaskType(String qbTaskType) {
		this.qbTaskType = qbTaskType;
	}

	/**
	 * @return the enteredTime
	 */
	public Long getEnteredTime() {
		return enteredTime;
	}

	/**
	 * @param enteredTime the enteredTime to set
	 */
	public void setEnteredTime(Long enteredTime) {
		this.enteredTime = enteredTime;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the qbTaskRequest
	 */
	public String getQbTaskRequest() {
		return qbTaskRequest;
	}

	/**
	 * @param qbTaskRequest the qbTaskRequest to set
	 */
	public void setQbTaskRequest(String qbTaskRequest) {
		this.qbTaskRequest = qbTaskRequest;
	}

	/**
	 * @return the qbTaskResponse
	 */
	public String getQbTaskResponse() {
		return qbTaskResponse;
	}

	/**
	 * @param qbTaskResponse the qbTaskResponse to set
	 */
	public void setQbTaskResponse(String qbTaskResponse) {
		this.qbTaskResponse = qbTaskResponse;
	}

	/**
	 * @return the qbTaskStatus
	 */
	public String getQbTaskStatus() {
		return qbTaskStatus;
	}

	/**
	 * @param qbTaskStatus the qbTaskStatus to set
	 */
	public void setQbTaskStatus(String qbTaskStatus) {
		this.qbTaskStatus = qbTaskStatus;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

}
