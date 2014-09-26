/**
 * 
 */
package com.mozu.qbintegration.service;

import java.util.ArrayList;
import java.util.List;

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
import com.mozu.qbintegration.utils.EntityHelper;


@Service
public class QueueManagerServiceImpl implements QueueManagerService {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(QueueManagerServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QueueManagerService#getNextTaskWithStatus(java.lang.Integer)
	 */
	@Override
	public WorkTask getNextTaskWithCriteria(Integer tenantId, WorkTask criteria) {
		List<WorkTask> workTasks = getFilteredTasks(tenantId, criteria);
		return workTasks.isEmpty() ? null : workTasks.get(0); //get only the first task if any.
	}
	
	public List<WorkTask> getAllsTasksWithCriteria(Integer tenantId, WorkTask criteria) {
		return getFilteredTasks(tenantId, criteria);
	}
	
	/*
	 * Return the order entities based on the filter provided. For now, order id or status
	 */
	private List<WorkTask> getFilteredTasks(Integer tenantId, WorkTask criteria) {
		
		StringBuilder sb = new StringBuilder();
		//Assuming status will never be null - it is meaningless to filter without it at this point.
		//TODO throw exception if status is null
		sb.append("qbTaskStatus eq " + criteria.getQbTaskStatus());
		
		if(criteria.getQbTaskType() != null) {
			sb.append(" and qbTaskType eq " + criteria.getQbTaskType());
		}
		
		if(criteria.getTaskId() != null) {
			sb.append(" and taskId eq " + criteria.getTaskId());
		}
		
		// Add the mapping entry
		String mapName = EntityHelper.getTaskqueueEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		
		List<WorkTask> workTasks = new ArrayList<WorkTask>();
		EntityCollection workTasksCollection = null;
		try {
			workTasksCollection = entityResource.getEntities(mapName, null, null, sb.toString(), "taskId", null);
			if(!workTasksCollection.getItems().isEmpty()) {
				for(JsonNode jsonNode: workTasksCollection.getItems()) {
					workTasks.add(getPopulatedTask(jsonNode));
				}
			}
		} catch (Exception e) {
			logger.error("Error getting next unprocessed task from entity list: "
					+ sb.toString());
		}
		logger.debug("Retrieved entity: " + workTasks);
		return workTasks;
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
		String mapName = EntityHelper.getTaskqueueEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		try {
			if(isInsert) {
				rtnEntry = entityResource.insertEntity(taskNode, mapName);
			} else {
				rtnEntry = entityResource.updateEntity(taskNode, mapName, String.valueOf(workTask.getEnteredTime()));
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
		workTask.setEnteredTime(rtnEntry.get("enteredTime").asLong());
		workTask.setTaskId(rtnEntry.get("taskId").asText());
		workTask.setQbTaskRequest(rtnEntry.get("qbTaskRequest").asText());
		workTask.setQbTaskResponse(rtnEntry.get("qbTaskResponse").asText());
		workTask.setQbTaskStatus(rtnEntry.get("qbTaskStatus").asText());
		workTask.setQbTaskType(rtnEntry.get("qbTaskType").asText());
		workTask.setTenantId(rtnEntry.get("tenantId").asInt());
		workTask.setSiteId(rtnEntry.get("siteId").asInt());
		
		return workTask;
	}

	private ObjectNode getFilledTaskNode(WorkTask workTask) {
		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode taskNode = nodeFactory.objectNode();

		taskNode.put("enteredTime", String.valueOf(workTask.getEnteredTime()));
		taskNode.put("taskId", workTask.getTaskId()); //For item we need to save multiple tasks
		taskNode.put("tenantId", workTask.getTenantId());
		taskNode.put("siteId", workTask.getSiteId());
		taskNode.put("qbTaskStatus", workTask.getQbTaskStatus());
		taskNode.put("qbTaskType", workTask.getQbTaskType());
		taskNode.put("qbTaskRequest", workTask.getQbTaskRequest());
		taskNode.put("qbTaskResponse", workTask.getQbTaskResponse() == null ? "": workTask.getQbTaskResponse());
		return taskNode;
	}

}
