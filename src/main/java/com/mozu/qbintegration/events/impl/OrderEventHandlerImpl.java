package com.mozu.qbintegration.events.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.api.ApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.event.Event;
import com.mozu.api.events.EventManager;
import com.mozu.api.events.handlers.OrderEventHandler;
import com.mozu.api.events.model.EventHandlerStatus;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.OrderStateHandler;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;

@Component
public class OrderEventHandlerImpl implements OrderEventHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(OrderEventHandlerImpl.class);
	
	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	private QueueManagerService queueManagerService;

	@Autowired
	private OrderStateHandler orderStateHandler;
	
	@PostConstruct
	public void initialize() {
		try {
			EventManager.getInstance().registerHandler(this);
		} catch (Exception e) {
			e.getMessage();
		}
		logger.info("Application event handler initialized");
	}

	@Override
	public EventHandlerStatus cancelled(ApiContext apiContext, Event event) {
		
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		final Integer tenantId = apiContext.getTenantId();
		final Integer siteId = apiContext.getSiteId();
		try {
			orderStateHandler.deleteOrder(event.getEntityId(), tenantId);
		} catch (Exception e) {
			logger.error("Exception while processing customer oepned, tenantID: "+ tenantId + " Site Id : " + siteId, " exception:"	+ e.getMessage(), e);
			status = new EventHandlerStatus(e.getMessage(),	HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
					
		return status;
		
	}

	@Override
	public EventHandlerStatus closed(ApiContext apiContext, Event event) {
		return null;
	}

	@Override
	public EventHandlerStatus fulfilled(ApiContext apiContext, Event event) {
		return null;
	}

	@Override
	public EventHandlerStatus opened(final ApiContext apiContext, Event event) {
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		final Integer tenantId = apiContext.getTenantId();
		final Integer siteId = apiContext.getSiteId();
		try {
			//quickbooksService.saveOrderInQuickbooks(event.getEntityId(),  tenantId);
			orderStateHandler.processOrder(event.getEntityId(), apiContext);
			status = new EventHandlerStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			logger.error("Exception while processing customer oepned, tenantID: "+ tenantId + " Site Id : " + siteId, " exception:"	+ e.getMessage(), e);
			status = new EventHandlerStatus(e.getMessage(),	HttpStatus.SC_INTERNAL_SERVER_ERROR);

		}
					
		return status;
	}

	@Override
	public EventHandlerStatus pendingreview(ApiContext apiContext, Event event) {
		return null;
	}

	@Override
	public EventHandlerStatus updated(final ApiContext apiContext, Event event) {
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		final Integer tenantId = apiContext.getTenantId();
		final String orderId = event.getEntityId();
		try {
			/*OrderResource orderResource = new OrderResource(apiContext);
			Order order = null;
			order = orderResource.getOrder(orderId);
			if(order.getAcceptedDate() != null) { //log only if order has been previously submitted (accepted)
				CustomerAccountResource accountResource = new CustomerAccountResource(apiContext);
				final CustomerAccount orderingCust = accountResource.getAccount(order.getCustomerAccountId());
				
				//Check if order has been processed, if not put in process Queue
				boolean isProcessed = quickbooksService.isOrderProcessed(tenantId, order.getOrderNumber());
				
				if (isProcessed) {
					MozuOrderDetail mozuOrderDetails = populateOrderDetails(order, orderingCust.getEmailAddress());
					
					//Check if already present in EL. If yes update else, insert
					//Step 2: Get updated order from qb_updated_orders EL
					MozuOrderDetail criteriaForUpDate = new MozuOrderDetail();
					criteriaForUpDate.setOrderStatus("UPDATED");
					criteriaForUpDate.setMozuOrderNumber(String.valueOf(order.getOrderNumber()));
					
					//1. Get from EL the order
					List<MozuOrderDetail> updatedOrders = quickbooksService.getMozuOrderDetails(tenantId, 
							criteriaForUpDate, EntityHelper.getOrderUpdatedEntityName());
					String mapName = EntityHelper.getOrderUpdatedEntityName();
					if(updatedOrders.isEmpty()) {
						quickbooksService.saveOrderInEntityList(mozuOrderDetails, mapName , tenantId);
					} else {
						mozuOrderDetails.setEnteredTime(updatedOrders.get(0).getEnteredTime());
						quickbooksService.updateOrderInEntityList(mozuOrderDetails, mapName, tenantId);
					}
				} else {
					quickbooksService.saveOrderInQuickbooks(order, tenantId);
				}
			}*/
			orderStateHandler.processOrder(orderId, apiContext);
			status = new EventHandlerStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			logger.error("Exception while processing customer update, tenantID: "+ tenantId + ", exception:"	+ e.getMessage(), e);
			status = new EventHandlerStatus(e.getMessage(),	HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}

		return status;
	}

	private MozuOrderDetail populateOrderDetails(final Order order, String emailAddress) {
		MozuOrderDetail orderDetails = new MozuOrderDetail();
		orderDetails.setEnteredTime(String.valueOf(System.currentTimeMillis()));
		orderDetails.setMozuOrderNumber(order.getOrderNumber().toString());
		orderDetails.setMozuOrderId(order.getId());
		orderDetails.setQuickbooksOrderListId("");
		orderDetails.setOrderStatus("UPDATED");
		orderDetails.setCustomerEmail(emailAddress);
		
		DateTimeFormatter timeFormat = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		orderDetails.setOrderDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setOrderUpdatedDate(timeFormat.print(order.getAcceptedDate().getMillis()));
		orderDetails.setAmount(String.valueOf(order.getSubtotal()));
		return orderDetails;
	}

	@Override
	public EventHandlerStatus abandoned(ApiContext apiContext, Event event) {
		// TODO Auto-generated method stub
		return null;
	}

}
