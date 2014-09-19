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
	
	/**
	 * Get the next task in entered status. Need to get all
	 * in entered status from entitylist and then just pick the top one for 
	 * now.
	 * 
	 * @param tenantId
	 * @param status
	 * @return
	 */
	WorkTask getNextTaskWithStatus(final Integer tenantId, final String status);
	
	/**
	 * Save a new task - this will be in ENTERED status. On need basis, this task might have
	 * already updated the order status befor calling itself processed.

	 * @param workTask
	 * @param tenantId
	 * @return
	 */
	WorkTask saveTask(final WorkTask workTask, final Integer tenantId);
	
	/**
	 * Update a task after processing. Outcome of a task can also 
	 * create a new task for processing.
	 * @param workTask
	 * @param tenantId
	 * @return
	 */
	WorkTask updateTask(final WorkTask workTask, final Integer tenantId);

}
