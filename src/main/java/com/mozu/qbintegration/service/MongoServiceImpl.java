package com.mozu.qbintegration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.utils.SingleTask;

/**
 * Implementation class of MongoService Interface
 * 
 * @author Akshay
 * @version 1.0
 * @since 1.0
 * 
 */
@Service
public class MongoServiceImpl implements MongoService {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static final String ORDER_COLLECTION = "mozuorders";

	private static final String CUST_COLLECTION = "mozucustomers";

	private static final String PRODUCT_COLLECTION = "mozuproducts";

	private static final String TASK_COLLECTION = "mozutasks";

	@Override
	public Order saveMozuOrder(Order order) {
		if (!mongoTemplate.collectionExists(ORDER_COLLECTION)) {
			mongoTemplate.createCollection(ORDER_COLLECTION);
		}
		mongoTemplate.insert(order, ORDER_COLLECTION);
		return order;
	}

	@Override
	public Order updateMozuOrder(Order order) {

		Update update = new Update();
		update.set("status", order.getStatus());
		Order retOrder = mongoTemplate.findAndModify(
				new Query(Criteria.where("id").is(order.getId())), update,
				Order.class, ORDER_COLLECTION);

		return retOrder;
	}

	@Override
	public Order getMozuOrder(Order order) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("id").is(order.getId())), Order.class);
	}

	@Override
	public CustomerAccount saveMozuCustomerAccount(CustomerAccount account) {
		if (!mongoTemplate.collectionExists(CUST_COLLECTION)) {
			mongoTemplate.createCollection(CUST_COLLECTION);
		}
		mongoTemplate.insert(account, CUST_COLLECTION);
		return account;
	}

	@Override
	public CustomerAccount updateMozuCustomerAccount(CustomerAccount account) {
		Update update = new Update();
		update.set("emailAddress", account.getEmailAddress());
		CustomerAccount retCustomerAccount = mongoTemplate.findAndModify(
				new Query(Criteria.where("emailAddress").is(
						account.getEmailAddress())), update, CustomerAccount.class,
				CUST_COLLECTION);

		return retCustomerAccount;
	}

	@Override
	public CustomerAccount getMozuCustomerAccount(CustomerAccount account) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("emailAddress").is(account.getEmailAddress())), CustomerAccount.class);
	}

	@Override
	public Product saveMozuProducts(Product product) {
		if (!mongoTemplate.collectionExists(PRODUCT_COLLECTION)) {
			mongoTemplate.createCollection(PRODUCT_COLLECTION);
		}
		mongoTemplate.insert(product, PRODUCT_COLLECTION);
		return product;
	}

	@Override
	public Product updateMozuProducts(Product product) {
		Update update = new Update();
		update.set("listid", product.getDescription());
		mongoTemplate.findAndModify(
				new Query(Criteria.where("productCode").is(product.getProductCode())),
				update, Product.class, PRODUCT_COLLECTION);
		return product;
	}

	@Override
	public Product getMozuProducts(Product product) {
		return mongoTemplate.findOne(
				new Query(Criteria.where("productCode").is(product.getProductCode())), Product.class);
	}
	
	@Override
	public void enterTask(SingleTask singleTask) {
		if (!mongoTemplate.collectionExists(TASK_COLLECTION)) {
			mongoTemplate.createCollection(TASK_COLLECTION);
		}
		mongoTemplate.insert(singleTask, TASK_COLLECTION);
	}

	@Override
	public void updateCompletedTask(SingleTask task) {
		Update update = new Update();
		update.set("response", task.getResponse());
		mongoTemplate.findAndModify(
				new Query(Criteria.where("taskId").is(task.hashCode())),
				update, SingleTask.class, TASK_COLLECTION);
	}
}
