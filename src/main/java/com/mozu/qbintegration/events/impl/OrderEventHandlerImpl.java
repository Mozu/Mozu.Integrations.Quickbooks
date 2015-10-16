package com.mozu.qbintegration.events.impl;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.api.ApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.event.Event;
import com.mozu.api.events.EventManager;
import com.mozu.api.events.handlers.OrderEventHandler;
import com.mozu.api.events.model.EventHandlerStatus;
import com.mozu.qbintegration.handlers.OrderHandler;
import com.mozu.qbintegration.handlers.OrderStateHandler;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.service.QuickbooksService;

@Component
public class OrderEventHandlerImpl implements OrderEventHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(OrderEventHandlerImpl.class);
	
	@Autowired
	private QuickbooksService quickbooksService;
	
	/*@Autowired
	private QueueManagerService queueManagerService;
*/
	@Autowired
	private OrderHandler orderHandler;
	
	@Autowired
	private OrderStateHandler orderStateHandler;
	
	@PostConstruct
	public void initialize() {
		try {
			EventManager.getInstance().registerHandler(this);
		} catch (Exception e) {
			e.getMessage();
		}
		logger.info("Order event handler initialized");
	}

	@Override
	public EventHandlerStatus cancelled(ApiContext apiContext, Event event) {
		
		EventHandlerStatus status = new EventHandlerStatus(HttpStatus.SC_OK);
		final Integer tenantId = apiContext.getTenantId();
		
		//final Integer siteId = apiContext.getSiteId();
		try {
			//Get it in only if User wants to process cancelled orders
			GeneralSettings setting = quickbooksService.getSettingsFromEntityList(tenantId);
			if(setting.getCancelled() != null && setting.getCancelled()) { 
				orderStateHandler.deleteOrder(event.getEntityId(), tenantId);
			}else {
				logger.error("Skipping event "+event.getTopic()+" for "+event.getEntityId()+", user not interested");
			}
		} catch (Exception e) {
			logger.error("Exception while processing order cancelled, tenantID: "+ tenantId + " exception:"	+ e.getMessage(), e);
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
		try {
			GeneralSettings setting = quickbooksService.getSettingsFromEntityList(tenantId);
			if(setting.getAccepted() != null && setting.getAccepted()) {
				orderStateHandler.processOrder(event.getEntityId(), apiContext);
			} else {
				logger.error("Skipping event "+event.getTopic()+" for "+event.getEntityId()+", user not interested");
			}
		} catch (Exception e) {
			logger.error("Exception while processing order oepned, tenantID: "+ tenantId + " exception:"	+ e.getMessage(), e);
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
			GeneralSettings setting = quickbooksService.getSettingsFromEntityList(tenantId);
			Order order = orderHandler.getOrder(orderId, tenantId);
			if (order.getPaymentStatus().equalsIgnoreCase("paid") && setting.getPaid() ||
				order.getFulfillmentStatus().equalsIgnoreCase("fulfilled") && setting.getFulFilled() ||
				order.getStatus().equalsIgnoreCase("completed") && setting.getCompleted() ||
				setting.getUpdated())
				orderStateHandler.processOrder(orderId, apiContext);
			else
				logger.error("Skipping event "+event.getTopic()+" for "+event.getEntityId()+", user not interested");
		} catch (Exception e) {
			logger.error("Exception while processing customer update, tenantID: "+ tenantId + ", exception:"	+ e.getMessage(), e);
			status = new EventHandlerStatus(e.getMessage(),	HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}

		return status;
	}

	@Override
	public EventHandlerStatus abandoned(ApiContext apiContext, Event event) {
		// TODO Auto-generated method stub
		return null;
	}

}
