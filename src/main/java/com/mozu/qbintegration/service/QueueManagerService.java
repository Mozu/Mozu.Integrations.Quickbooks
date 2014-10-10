/**
 * 
 */
package com.mozu.qbintegration.service;

import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Akshay
 *
 */
public interface QueueManagerService {
	
	WorkTask getNext(int tenantId) throws Exception;
	
	WorkTask addTask(Integer tenantId, String id, String type, String currentStep, String action) throws Exception;

	WorkTask getActiveTask(Integer tenantId) throws Exception; 
	void updateTask(Integer tenantId, String id, String currentStep, String status) throws Exception;
}
