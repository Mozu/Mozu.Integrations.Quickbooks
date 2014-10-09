
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
import com.mozu.qbintegration.tasks.WorkTask;


@Service
public class QueueManagerServiceImpl implements QueueManagerService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(QueueManagerServiceImpl.class);

	private static ObjectMapper mapper = JsonUtils.initObjectMapper();
	
	@Autowired
	EntityHandler entityHandler;
	
	public final String PROCESSING = "Processing";
	public final String PENDING = "Pending";
	public final String COMPLETED = "Completed";
	
	@Override
	public WorkTask getNext(int tenantId) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), null, "createDate desc", 1);
		
		if (nodes.size() > 0) {
			WorkTask task= mapper.readValue(nodes.get(0).toString(), WorkTask.class);
			
			task.setStatus(PROCESSING);
			
			entityHandler.addUpdateEntity(tenantId,entityHandler.getTaskqueueEntityName(), task.getId(), task);

			return task;
		}
		
		return null;
	}
	
	@Override
	public WorkTask addTask(Integer tenantId, String id, String type, String currentStep, String action) throws Exception {
		WorkTask workTask = new WorkTask();
		// Just to make it unique
		workTask.setId(id);
		workTask.setCreateDate(DateTime.now());
		workTask.setType(type);
		workTask.setStatus(PENDING);
		workTask.setAction(action);
		workTask.setCurrentStep(currentStep);
		
		entityHandler.addUpdateEntity(tenantId, entityHandler.getTaskqueueEntityName(), id, workTask);
		
		return workTask;
	}
	
	@Override
	public WorkTask getActiveTask(Integer tenantId) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getTaskqueueEntityName(), "status eq " +PROCESSING, null, 1);
		if (nodes.size() > 0) 
			return mapper.readValue(nodes.get(0).toString(), WorkTask.class);
		
		return null;
	}

	@Override
	public void updateTask(Integer tenantId, String id, String currentStep, String status) throws Exception {
		if(status.equalsIgnoreCase(COMPLETED)) {
			entityHandler.deleteEntity(tenantId, entityHandler.getTaskqueueEntityName(), id);
		} else {
			JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getTaskqueueEntityName(), id);
			
			WorkTask task = mapper.readValue(node.toString(), WorkTask.class);
			task.setCurrentStep(currentStep);
			task.setStatus(status);
			entityHandler.addUpdateEntity(tenantId,entityHandler.getTaskqueueEntityName(), task.getId(), task);
		}
	}
}

