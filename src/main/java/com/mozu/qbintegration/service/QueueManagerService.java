/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.List;

import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Akshay
 *
 */
public interface QueueManagerService {
	
	/**
	 * Get the single next task in given status and other criteria. Get all records
	 *  and then just pick the top one for now. Status is mandatory

	 * @param tenantId
	 * @param criteria
	 * @return
	 */
	WorkTask getNextTaskWithCriteria(final Integer tenantId, final WorkTask criteria);
	
	/**
	 * Get all tasks matching specified criteria. 
	 * 
	 * @param tenantId
	 * @param criteria
	 * @return
	 */
	List<WorkTask> getAllsTasksWithCriteria(Integer tenantId, WorkTask criteria);
	
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
