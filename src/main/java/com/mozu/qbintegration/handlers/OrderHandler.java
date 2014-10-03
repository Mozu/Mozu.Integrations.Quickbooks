package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.QuickBooksSavedOrderLine;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineRet;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderModRsType;

@Component
public class OrderHandler {

	@Autowired
	CustomerHandler customerHandler;
	
	private static final Log logger = LogFactory.getLog(OrderHandler.class);
	
	public Order getOrder(String orderId, Integer tenantId, Integer siteId) throws Exception {
		OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
		Order order = null;
		try {
			order = orderResource.getOrder(orderId);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
		return order;
	}

	
	public MozuOrderDetails getOrderDetails(Integer tenantId, Integer siteId,String orderId, String status,SalesOrderAddRsType salesOrderResponse) throws Exception {
		String qbTransactionId = null;
		List<Object> salesOrderLineRet = null;
		
		if(salesOrderResponse == null) {
			qbTransactionId = "";
		} else {
			qbTransactionId = salesOrderResponse.getSalesOrderRet().getTxnID();
			salesOrderLineRet = 
					salesOrderResponse.getSalesOrderRet().getSalesOrderLineRetOrSalesOrderLineGroupRet();
		}
		
		//Set the edit sequence to be used while updating
		String editSequence = "";
		
		if (salesOrderResponse != null)
			editSequence = salesOrderResponse.getSalesOrderRet().getEditSequence();
		
		return getOrderDetails(tenantId, siteId,orderId, status,qbTransactionId, editSequence, salesOrderLineRet);
	}
	
	public MozuOrderDetails getOrderUpdateDetails(Integer tenantId, Integer siteId,String orderId, String status, SalesOrderModRsType salesOrderModResponse) throws Exception {
		
		String qbTransactionId = null;
		List<Object> salesOrderLineRet = null;
		
		if(salesOrderModResponse == null) {
			qbTransactionId = "";
		} else {
			qbTransactionId = salesOrderModResponse.getSalesOrderRet().getTxnID();
			salesOrderLineRet = 
					salesOrderModResponse.getSalesOrderRet().getSalesOrderLineRetOrSalesOrderLineGroupRet();
		}
		//Set the edit sequence to be used while updating
		
		String editSequence = "";
		if (salesOrderModResponse != null)
			salesOrderModResponse.getSalesOrderRet().getEditSequence();
		
		return getOrderDetails(tenantId, siteId, orderId, status, qbTransactionId, editSequence, salesOrderLineRet);
	}
	
	public MozuOrderDetails getOrderDetails(Integer tenantId, Integer siteId,String orderId, String status, String qbTransactionId, String editSequence, List<Object> salesOrderLineRet) throws Exception {
		
		Order order = getOrder(orderId, tenantId, siteId);

		CustomerAccount custAcct = customerHandler.getCustomer(tenantId, order.getCustomerAccountId());
		
		MozuOrderDetails orderDetails = new MozuOrderDetails();
		orderDetails.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		orderDetails.setMozuOrderNumber(order.getOrderNumber().toString());
		orderDetails.setMozuOrderId(order.getId());
		orderDetails.setQuickbooksOrderListId(qbTransactionId);
		orderDetails.setOrderStatus(status);
		orderDetails.setCustomerEmail(custAcct.getEmailAddress());
		
		DateTimeFormatter timeFormat = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		orderDetails.setOrderDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setOrderUpdatedDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setConflictReason("");
		orderDetails.setAmount(String.valueOf(order.getSubtotal()));
		
		//Set item ids
		List<QuickBooksSavedOrderLine> savedLines = new ArrayList<QuickBooksSavedOrderLine>();
		QuickBooksSavedOrderLine savedOrderLine = null;
		SalesOrderLineRet orderLineRet = null;
		if(salesOrderLineRet != null) {
			for(OrderItem item: order.getItems()) {
				for(Object returnedItem: salesOrderLineRet) {
					orderLineRet = (SalesOrderLineRet) returnedItem;
					savedOrderLine = new QuickBooksSavedOrderLine();
					savedOrderLine.setProductCode(orderLineRet.getItemRef().getFullName());
					savedOrderLine.setQbLineItemTxnID(orderLineRet.getTxnLineID());
					savedLines.add(savedOrderLine);
				}
			}

			orderDetails.setSavedOrderLinesList(savedLines);
		}
		
		//Set the edit sequence
		orderDetails.setEditSequence(editSequence);
		
		return orderDetails;
		
	}
}
