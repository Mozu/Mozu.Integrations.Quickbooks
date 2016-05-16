package com.mozu.qbintegration.service;

public interface OrderTrackingService {
	boolean addOrder(String orderId);
	boolean acquireOrder(String orderId);
	void releaseOrder(String orderId);
	void removeOrder(String orderId);

}
