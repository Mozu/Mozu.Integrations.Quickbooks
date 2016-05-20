
/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.model.WorkTaskStatus;
import com.mozu.qbintegration.tasks.WorkTask;


@Service
public class QueueManagerServiceImpl implements QueueManagerService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(QueueManagerServiceImpl.class);

	private static ObjectMapper mapper = JsonUtils.initObjectMapper();
	
	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	OrderTrackingService orderTrackingService;
	
	/*public final String PROCESSING = "Processing";
	public final String PENDING = "Pending";
	public final String COMPLETED = "Completed";*/
	
	@Override
	public WorkTask getNext(int tenantId) throws Exception {
		// try to complete tasks in processing
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), 
				"status eq PROCESSING", "createDate desc", 1);
		
		if (nodes.size() == 0) {
			// otherwise get the next task candidate
			nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), 
				"status ne ERROR and status ne PROCESS_IN_MEM", "createDate desc", 1);
		}
		
		if (nodes.size() > 0) {
			WorkTask task= mapper.readValue(nodes.get(0).toString(), WorkTask.class);
			
			task.setStatus(WorkTaskStatus.PROCESSING);	
			entityHandler.addUpdateEntity(tenantId,entityHandler.getTaskqueueEntityName(), task.getId(), task);
			return task;			
		}
		logger.debug("No WorkTask to return from QueueManagerServiceImpl for tenantId: " + tenantId);
		return null;
	}
	
	@Override
	public WorkTask addTask(Integer tenantId, String id, String type, String currentStep, String action) throws Exception {
		WorkTask workTask = new WorkTask();
		// Just to make it unique
		workTask.setId(id);
		workTask.setCreateDate(DateTime.now());
		workTask.setType(type);
		workTask.setStatus(WorkTaskStatus.PENDING);
		workTask.setAction(action);
		workTask.setCurrentStep(currentStep);
		
		entityHandler.addUpdateEntity(tenantId, entityHandler.getTaskqueueEntityName(), id, workTask);
		
		return workTask;
	}
	
	@Override
	public WorkTask getActiveTask(Integer tenantId) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), 
				"status eq " +WorkTaskStatus.PROCESSING, "createDate desc", 1);
		if (nodes.size() > 0) 
			return mapper.readValue(nodes.get(0).toString(), WorkTask.class);
		
		return null;
	}
	
	@Override
	public int getActiveTaskCount(Integer tenantId) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), 
				"status eq " +WorkTaskStatus.PROCESSING, null, 2);
		return nodes.size();
	}

	@Override
	public boolean getInMemProcessingTask(Integer tenantId, String taskType) throws Exception {
		boolean isInMemTaskRunning = false;
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), 
				"status eq " + WorkTaskStatus.PROCESS_IN_MEM + " and " + "type eq " + taskType, null, 1);
		if (nodes.size() > 0) {
			isInMemTaskRunning = true;
		}
		return isInMemTaskRunning;
	}

	@Override
	public void updateTask(Integer tenantId, String id, String currentStep, String status) throws Exception {
		if(status.equalsIgnoreCase(WorkTaskStatus.COMPLETED)) {
			entityHandler.deleteEntity(tenantId, entityHandler.getTaskqueueEntityName(), id);
			orderTrackingService.removeOrder(id);
			return;
		} 
		
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getTaskqueueEntityName(), id);
		
		WorkTask task = mapper.readValue(node.toString(), WorkTask.class);
		task.setCurrentStep(currentStep);
		task.setStatus(status);
		entityHandler.addUpdateEntity(tenantId,entityHandler.getTaskqueueEntityName(), task.getId(), task);
	}

	@Override
	public void deleteTask(Integer tenantId, String workTaskId) throws Exception {
		
		entityHandler.deleteEntity(tenantId, entityHandler.getTaskqueueEntityName(), workTaskId);
		orderTrackingService.removeOrder(workTaskId);
		logger.debug("Deleted task from queue with ID: " + workTaskId + ", for tenant " + tenantId);
	}
}

