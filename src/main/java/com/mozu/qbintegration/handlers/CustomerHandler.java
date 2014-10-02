package com.mozu.qbintegration.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;

@Component
public class CustomerHandler {

	private static final Log logger = LogFactory.getLog(CustomerHandler.class);
	
	public CustomerAccount getCustomer(Integer tenantId, Integer customerAccountId) throws Exception {
		CustomerAccountResource accountResource = new CustomerAccountResource(new MozuApiContext(tenantId));
		CustomerAccount orderingCust = null;
		try {
			orderingCust = accountResource.getAccount(customerAccountId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return orderingCust;
	}
}
