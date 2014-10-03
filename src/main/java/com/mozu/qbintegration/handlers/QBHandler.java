package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.EntityHelper;

@Component
public class QBHandler {

	private static final Log logger = LogFactory.getLog(QBHandler.class);
			
	@Autowired
	QueueManagerService queueManagerService;
	
	@Autowired
	CustomerHandler customerHandler;
	
	@Autowired
	OrderHandler orderHandler;
	
	@Autowired
	QuickbooksService qbService;
	
	public void processCustomerQuery(int tenantId, WorkTask workTask) throws Exception {
		QBXML response = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());
		CustomerQueryRsType custQueryResponse = (CustomerQueryRsType) response.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		String orderId = workTask.getTaskId(); // this gets the order id
		
		Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());		
		CustomerAccount custAcct = customerHandler.getCustomer(	tenantId, order.getCustomerAccountId());

		if ("warn".equalsIgnoreCase(custQueryResponse.getStatusSeverity())
				&& 500 == custQueryResponse.getStatusCode().intValue()) {
			// Customer not found. So CUST_ADD
			// ENTER the new task
			qbService.addCustAddTaskToQueue(orderId, tenantId,workTask.getSiteId(), custAcct);

		} else {
			String qbCustListID = custQueryResponse.getCustomerRet().get(0).getListID();

			saveCustomer(qbCustListID, order, custAcct, tenantId, workTask);
		}
	}
	
	
	public void processCustomerAdd(Integer tenantId, WorkTask workTask) throws Exception {
		QBXML custAddResp = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());
		CustomerAddRsType custAddResponse = (CustomerAddRsType) custAddResp.getQBXMLMsgsRs()
																			.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																			.get(0);
		
		String customerListId = custAddResponse.getCustomerRet().getListID();
		String orderId = workTask.getTaskId(); // this gets the order id
		Order order = orderHandler.getOrder(orderId, tenantId,workTask.getSiteId());

		CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		saveCustomer(customerListId, order, custAcct, tenantId, workTask);
	}
	
	
	public void processItemQuery(Integer tenantId, WorkTask workTask) throws Exception {
		QBXML itemSearchEle = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());
		ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle.getQBXMLMsgsRs()
																			.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																			.get(0);

		if (500 == itemSearchResponse.getStatusCode().intValue()
				&& "warn".equalsIgnoreCase(itemSearchResponse.getStatusSeverity())) {

			// TODO this is error scenario. So hold on.
			// Log the not found product in error conflict
			String orderId = workTask.getTaskId(); // this gets the order id
			
			// Order order = orderHandler.getOrder(orderId,
			// tenantId,workTask.getSiteId());
			// CustomerAccount custAcct =
			// qbService.getMozuCustomer(order,tenantId,
			// workTask.getSiteId());
			MozuOrderDetails orderDetails = orderHandler.getOrderDetails(tenantId, workTask.getSiteId(),orderId, "CONFLICT", null);
			qbService.saveOrderInEntityList(orderDetails,EntityHelper.getOrderEntityName(), tenantId,workTask.getSiteId());

			// Make entry in conflict reason table
			// Log the not found product in error conflict
			QBXML qbxml2 = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskRequest());
			ItemQueryRqType itemReq = (ItemQueryRqType) qbxml2.getQBXMLMsgsRq()
																.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
																.get(0);

			OrderConflictDetail conflictDetail = new OrderConflictDetail();
			conflictDetail.setMozuOrderNumber(String.valueOf(orderDetails.getMozuOrderNumber()));
			conflictDetail.setNatureOfConflict("Not Found");
			conflictDetail.setDataToFix(itemReq.getFullName().get(0));
			conflictDetail.setConflictReason(itemSearchResponse.getStatusMessage());

			List<OrderConflictDetail> conflictDetails = new ArrayList<OrderConflictDetail>();
			conflictDetails.add(conflictDetail);
			qbService.saveConflictInEntityList(tenantId, Integer.parseInt(conflictDetail.getMozuOrderNumber()),	conflictDetails);
			logger.debug("Saved conflict for: "+ itemSearchResponse.getStatusMessage());

		} else {

			saveProductInEntityList(itemSearchResponse, tenantId,workTask.getSiteId());

			// Check if we can create order add task now
			String orderId = workTask.getTaskId(); // this gets the order id
			Order order = orderHandler.getOrder(orderId, tenantId,	workTask.getSiteId());

			CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
			boolean allItemsInEntityList = true;
			List<String> itemListIds = new ArrayList<String>();
			for (OrderItem singleItem : order.getItems()) {
				String itemQBListId = qbService.getProductFromEntityList(singleItem, tenantId,workTask.getSiteId());
				if (null == itemQBListId) {
					allItemsInEntityList = false;
				}
				// list will anyway be discarded if above flag is false,
				// so no null chck is required
				itemListIds.add(itemQBListId);
			}

			if (allItemsInEntityList) { // Add order ADD task
				qbService.addOrderAddTaskToQueue(tenantId,workTask.getSiteId(), custAcct, order,itemListIds);
			}
		}
	}
	
	public void processItemAdd(Integer tenantId, WorkTask workTask) throws Exception {
		QBXML itemAddEle = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());

		ItemInventoryAddRsType invAddResponse = (ItemInventoryAddRsType) itemAddEle.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);
		String itemListId = invAddResponse.getItemInventoryRet().getListID();

		// Save the item list id in entity list
		OrderItem item = new OrderItem();
		Product product = new Product();
		item.setProduct(product);
		product.setProductCode(invAddResponse.getItemInventoryRet().getFullName());
		product.setName(invAddResponse.getItemInventoryRet().getName());
		qbService.saveProductInEntityList(item, itemListId, tenantId,workTask.getSiteId());

		logger.debug("Added new product to quickbooks: "+ invAddResponse.getItemInventoryRet().getName());
	}
	
	
	public void processOrderAdd(Integer tenantId, WorkTask workTask) throws Exception {
		QBXML orderAddResp = (QBXML) qbService
				.getUnmarshalledValue(workTask.getQbTaskResponse());
		com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType salesOrderResponse = (com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType) orderAddResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);

		// Make an entry in the order entity list with posted status
		String orderId = workTask.getTaskId(); // this gets the order id
		// Order order = orderHandler.getOrder(orderId, tenantId,
		// workTask.getSiteId());
		// CustomerAccount custAcct =
		// customerHandler.getCustomer(tenantId,
		// order.getCustomerAccountId());

		MozuOrderDetails orderDetails = orderHandler.getOrderDetails(tenantId, workTask.getSiteId(), orderId, "POSTED",salesOrderResponse);
		qbService.saveOrderInEntityList(orderDetails,EntityHelper.getOrderEntityName(), tenantId,workTask.getSiteId());

		logger.debug((new StringBuilder())
				.append("Processed order with id: ")
				.append(workTask.getTaskId())
				.append(" with QB status code: ")
				.append(salesOrderResponse.getStatusCode())
				.append(" with status: ")
				.append(salesOrderResponse.getStatusMessage())
				.toString());

	}
	
	public void processItemQueryAll(Integer tenantId, WorkTask workTask) {
		QBXML itemSearchEle = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());
		ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle.getQBXMLMsgsRs()
																.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																.get(0);
		
		List<Object> itemServiceRetCollection = itemSearchResponse.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet();
		for (Object object : itemServiceRetCollection) {
			String productName = null;
			String productQbListID = null;
			if (object instanceof ItemServiceRet) {
				ItemServiceRet itemServiceRet = (ItemServiceRet) object;
				productName = itemServiceRet.getFullName();
				productQbListID = itemServiceRet.getListID();
			} else if (object instanceof ItemInventoryRet) {
				ItemInventoryRet itemInventoryRet = (ItemInventoryRet) object;
				productName = itemInventoryRet.getFullName();
				productQbListID = itemInventoryRet.getListID();
			} // TODO need to identify more item inventory types and add
				// here.
			MozuProduct mozuProduct = new MozuProduct();
			mozuProduct.setProductCode(productName);
			mozuProduct.setQbProductListID(productQbListID);
			mozuProduct.setProductName(productName);
			qbService.saveAllProductInEntityList(mozuProduct, tenantId,	workTask.getSiteId());
			logger.debug("Saved product through refresh all: "+ productName);
		}
	}
	
	
	public void processOrderUpdate(Integer tenantId, WorkTask workTask) throws Exception {
		QBXML orderModResp = (QBXML) qbService.getUnmarshalledValue(workTask.getQbTaskResponse());

		SalesOrderModRsType orderModRsType = (SalesOrderModRsType) orderModResp.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		// Make an entry in the order entity list with posted status
		String orderId = workTask.getTaskId(); // this gets the order id
		// Order order = orderHandler.getOrder(orderId, tenantId,
		// workTask.getSiteId());
		// CustomerAccount custAcct = qbService.getMozuCustomer(order,
		// tenantId, workTask.getSiteId());

		MozuOrderDetails orderDetails = orderHandler.getOrderUpdateDetails(tenantId, workTask.getSiteId(),orderId, "POSTED", orderModRsType);
		qbService.saveOrderInEntityList(orderDetails,EntityHelper.getOrderEntityName(), tenantId,workTask.getSiteId());

		// Updated order in updated EL to POSTED -- that screen picks up
		// orders in UPDATED status so
		// this one will stop showing up.
		qbService.updateOrderInEntityList(orderDetails,EntityHelper.getOrderUpdatedEntityName(), tenantId,workTask.getSiteId());

		logger.debug((new StringBuilder())
				.append("Processed order with id: ")
				.append(workTask.getTaskId())
				.append(" with QB status code: ")
				.append(orderModRsType.getStatusCode())
				.append(" with status: ")
				.append(orderModRsType.getStatusMessage()).toString());
	}
	private void saveCustomer(String qbCustListID, Order order, CustomerAccount custAcct,Integer tenantId, WorkTask workTask) throws Exception {
		qbService.saveCustInEntityList(custAcct, qbCustListID,	tenantId, workTask.getSiteId());
		processItems(tenantId, workTask.getSiteId(), order, custAcct);
	}
	
	private void processItems(Integer tenantId, Integer siteId, Order order, CustomerAccount custAcct) throws Exception {
		boolean allItemsInEntityList = true;
		List<String> itemListIds = new ArrayList<String>();

		for (OrderItem item : order.getItems()) {

			String itemListId = qbService.getProductFromEntityList(item, tenantId, siteId);

			if (null == itemListId) {
				allItemsInEntityList = false;
				qbService.addItemQueryTaskToQueue(order.getId(),tenantId, siteId, item.getProduct().getProductCode());

			} else {
				itemListIds.add(itemListId);
			}
			if (allItemsInEntityList) { // Add order ADD task
				qbService.addOrderAddTaskToQueue(tenantId,siteId, custAcct, order,itemListIds);
			}
		}
	}
	
	private void saveProductInEntityList(ItemQueryRsType itemSearchResponse,
			Integer tenantId, Integer siteId) {
		String itemListId = null;
		Object invObj = itemSearchResponse
				.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet()
				.get(0);

		OrderItem item = new OrderItem();
		Product product = new Product();
		item.setProduct(product);

		if (invObj instanceof ItemServiceRet) {
			ItemServiceRet itemServiceRet = (ItemServiceRet) invObj;
			product.setProductCode(itemServiceRet.getFullName());
			product.setName(itemServiceRet.getName());
			itemListId = itemServiceRet.getListID();
		} else if (invObj instanceof ItemInventoryRet) {
			ItemInventoryRet itemInvRet = (ItemInventoryRet) invObj;
			product.setProductCode(itemInvRet.getFullName());
			product.setName(itemInvRet.getName());
			itemListId = itemInvRet.getListID();
		}
		// Save the item list id in entity list
		qbService.saveProductInEntityList(item, itemListId, tenantId, siteId);

	}

}
