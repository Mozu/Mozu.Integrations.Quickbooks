package com.mozu.qbintegration.events.impl;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
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
import com.mozu.qbintegration.service.QuickbooksService;

@Component
public class OrderEventHandlerImpl implements OrderEventHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(OrderEventHandlerImpl.class);
	
	@Autowired
	private QuickbooksService quickbooksService;

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
		return null;
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
		final String orderId = event.getEntityId();
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					OrderResource orderResource = new OrderResource(apiContext);
					Order order = null;
					try {
						order = orderResource.getOrder(orderId);
						CustomerAccountResource accountResource = new CustomerAccountResource(apiContext);
						final CustomerAccount orderingCust = accountResource.getAccount(order.getCustomerAccountId());
						quickbooksService.saveOrderInQuickbooks(order, orderingCust, tenantId, siteId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}).start();
			
			
		} catch (Exception e) {
			logger.error(
					"Exception while processing customer update, tenantID: "
							+ tenantId + " Site Id : " + siteId, " exception:"
							+ e);
			status = new EventHandlerStatus(e.getMessage(),
					HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}

		return status;
	}

	@Override
	public EventHandlerStatus pendingreview(ApiContext apiContext, Event event) {
		return null;
	}

	@Override
	public EventHandlerStatus updated(ApiContext apiContext, Event event) {
		return null;
	}

	@Override
	public EventHandlerStatus abandoned(ApiContext apiContext, Event event) {
		// TODO Auto-generated method stub
		return null;
	}

}
