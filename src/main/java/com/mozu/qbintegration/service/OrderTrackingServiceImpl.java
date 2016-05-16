package com.mozu.qbintegration.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service; 

@Service
public class OrderTrackingServiceImpl implements OrderTrackingService {
	private Map<String, Semaphore> processingOrders = new HashMap<String, Semaphore>();
	
	
	@Override
	public synchronized boolean addOrder(String orderId) {
		boolean success = true;
		
		if (processingOrders.containsKey(orderId)) {
			success = false;
		} else {
			Semaphore semaphore = new Semaphore(1, true);
			try {
				if (semaphore.tryAcquire(5l, TimeUnit.SECONDS)) {
					processingOrders.put(orderId, semaphore);
				}
			} catch (InterruptedException e) {
				success = false;
			}
		}

		return success;
	}

	@Override
	public boolean acquireOrder(String orderId) {
		boolean success = true;
		Semaphore semaphore = processingOrders.get(orderId);
		
		if (semaphore != null) {
			try {
				success = semaphore.tryAcquire(5l, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				success = false;
			}
		}
		
		return success;
	}

	@Override
	public void releaseOrder(String orderId) {
		Semaphore semaphore = processingOrders.get(orderId);
		
		if (semaphore != null) {
			semaphore.release();
		}
	}

	@Override
	public void removeOrder(String orderId) {
		releaseOrder(orderId);
		processingOrders.remove(orderId);
	}

}
