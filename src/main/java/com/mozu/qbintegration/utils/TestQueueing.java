/**
 * 
 */
package com.mozu.qbintegration.utils;

import java.util.ArrayDeque;

/**
 * this is my service impl
 * @author Akshay
 *
 */
public class TestQueueing {
	
	private ArrayDeque<SingleTask> taskQueue;
	private ArrayDeque<SingleTask> responseQueue;
	
	public TestQueueing() {
		taskQueue = new ArrayDeque<SingleTask>();
		responseQueue = new ArrayDeque<SingleTask>();
	}
	
	/**
	 * @return the requestQueue
	 */
	public ArrayDeque<SingleTask> getTaskQueue() {
		return taskQueue;
	}

	/**
	 * @return the responseQueue
	 */
	public ArrayDeque<SingleTask> getResponseQueue() {
		return responseQueue;
	}

	public void saveIntoTaskQ(SingleTask singleTask) {
		synchronized (taskQueue) {
			singleTask.setResponse("This is the response for req: " + singleTask.getRequest());
			taskQueue.offer(singleTask);
			notifyAll(); //those who are waiting on getting the lock
		}
		
		try {
			Thread.sleep(5000); //Just to emulate time to process
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized SingleTask getFromReqQ() {
		SingleTask returnedTask = null;
		synchronized (taskQueue) {
			returnedTask = taskQueue.poll();
			
		}
		return returnedTask;
	}
	
	public void saveIntoRespQ(SingleTask singleTask) {
		try {
			Thread.sleep(5000); //Just to emulate time to process
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		responseQueue.offer(singleTask);
	}
	
	public SingleTask getFromRespQ() {
		return responseQueue.poll();
	}

}
