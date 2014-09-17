package com.mozu.qbintegration.service;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.utils.SingleTask;

/**
 * MongoService will define interface methods to perform
 * CRUD operations on orders, customers and products for
 * quickbooks integration
 * 
 * @author Akshay
 * 
 * @version 1.0
 * 
 * @since 1.0
 */
public interface MongoService {

	Order saveMozuOrder(Order order);
	
	Order updateMozuOrder(Order order);
	
	Order getMozuOrder(Order order);
	
	CustomerAccount saveMozuCustomerAccount(CustomerAccount account);
	
	CustomerAccount updateMozuCustomerAccount(CustomerAccount account);
	
	CustomerAccount getMozuCustomerAccount(CustomerAccount account);
	
	Product saveMozuProducts(Product product);
	
	Product updateMozuProducts(Product product);
	
	Product getMozuProducts(Product product);
	
	void enterTask(SingleTask singleTask);
	
	void updateCompletedTask(SingleTask task);
}
