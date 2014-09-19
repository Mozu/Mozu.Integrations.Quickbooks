/**
 * 
 */
package com.mozu.qbintegration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.qbintegration.tasks.WorkTask;

/**
 * @author Akshay
 *
 */
@Service
public class QueueManagerServiceImpl implements QueueManagerService {
	
	private static final String TASKQUEUE_ENTITY = "QB_TASKQUEUE";
	
	private static final String APP_NAMESPACE = "Ignitiv";
	
	private static final Logger logger = LoggerFactory
			.getLogger(QueueManagerServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QueueManagerService#getNextTaskWithStatus(java.lang.Integer)
	 */
	@Override
	public WorkTask getNextTaskWithStatus(Integer tenantId, String status) {
		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = TASKQUEUE_ENTITY + "@" + APP_NAMESPACE;
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		
		EntityCollection workTasks = null;
		try {
			workTasks = entityResource.getEntities(mapName, null, null, "qbTaskStatus eq " + status, "taskId", null);
			rtnEntry = workTasks.getItems().isEmpty() ? null : workTasks.getItems().get(0);
		} catch (Exception e) {
			logger.error("Error getting next unprocessed task from entity list: "
					+ status);
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		return rtnEntry == null ? null : getPopulatedTask(rtnEntry);
	}

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QueueManagerService#saveTask(com.mozu.qbintegration.tasks.WorkTask, java.lang.Integer)
	 */
	@Override
	public WorkTask saveTask(WorkTask workTask, Integer tenantId) {
		return saveOrUpdateTask(workTask, tenantId, Boolean.TRUE); //True is for insert
	}
	
	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QueueManagerService#updateTask(com.mozu.qbintegration.tasks.WorkTask, java.lang.Integer)
	 */
	@Override
	public WorkTask updateTask(WorkTask workTask, Integer tenantId) {
		return saveOrUpdateTask(workTask, tenantId, Boolean.FALSE); //false is for update
	}

	/*
	 * Save or update the task in entity list
	 */
	private WorkTask saveOrUpdateTask(WorkTask workTask, Integer tenantId,
			Boolean isInsert) {

		ObjectNode taskNode = getFilledTaskNode(workTask);

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = TASKQUEUE_ENTITY + "@" + APP_NAMESPACE;
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		try {
			if(isInsert) {
				rtnEntry = entityResource.insertEntity(taskNode, mapName);
			} else {
				rtnEntry = entityResource.updateEntity(taskNode, mapName, workTask.getTaskId());
			}
		} catch (Exception e) {
			logger.error("Error saving or updating task queue in entity list: "
					+ workTask.getQbTaskRequest());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
		
		return workTask;
		
	}

	private WorkTask getPopulatedTask(JsonNode rtnEntry) {
		WorkTask workTask = new WorkTask();
		workTask.setTaskId(rtnEntry.get("taskId").asText());
		workTask.setQbTaskRequest(rtnEntry.get("qbTaskRequest").asText());
		workTask.setQbTaskResponse(rtnEntry.get("qbTaskResponse").asText());
		workTask.setQbTaskStatus(rtnEntry.get("qbTaskStatus").asText());
		workTask.setQbTaskType(rtnEntry.get("qbTaskType").asText());
		
		return workTask;
	}

	private ObjectNode getFilledTaskNode(WorkTask workTask) {
		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode taskNode = nodeFactory.objectNode();

		taskNode.put("taskId", workTask.getTaskId());
		taskNode.put("qbTaskRequest", workTask.getQbTaskRequest());
		taskNode.put("qbTaskResponse",workTask.getQbTaskResponse());
		taskNode.put("qbTaskStatus", workTask.getQbTaskStatus());
		taskNode.put("qbTaskType", workTask.getQbTaskType());
		return taskNode;
	}

}
