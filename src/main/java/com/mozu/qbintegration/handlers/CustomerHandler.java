package com.mozu.qbintegration.handlers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.utils.XMLHelper;

@Component
public class CustomerHandler {
	
	private static final Log logger = LogFactory.getLog(CustomerHandler.class);
	
	@Autowired
	EntityHandler entityHandler;
	
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
	
	public String getQbCustomerId(Integer tenantId, String emailAddress) throws Exception {
		String mapName = entityHandler.getCustomerEntityName();
		String qbListID = null;
		
		JsonNode entity = entityHandler.getEntity(tenantId, entityHandler.getCustomerEntityName(), emailAddress);
		
		if (entity != null) {
			JsonNode result = entity.findValue("custQBListID");
			if (result != null) {
				qbListID = result.asText();
			}
		} 
		
		return qbListID;
	}
	
	public void saveCustInEntityList(CustomerAccount custAcct,String customerListId, Integer tenantId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("custEmail", custAcct.getEmailAddress());
		custNode.put("custQBListID", customerListId);
		custNode.put("custName",custAcct.getFirstName() + " " + custAcct.getLastName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = entityHandler.getCustomerEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId)); 
		try {
			List<JsonNode> existing = entityHandler.getEntityCollection(tenantId, entityHandler.getCustomerEntityName(), "custEmail eq "+custAcct.getEmailAddress(), null, 1);
			if (existing.size() == 0)
				rtnEntry = entityResource.insertEntity(custNode, mapName);
			else
				rtnEntry = entityResource.updateEntity(custNode, mapName,custAcct.getEmailAddress() );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving customer in entity list: "
					+ custAcct.getEmailAddress());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
	}

	
	public String getQBSearchGetXML(int tenantId, String orderId, int custAccountId) throws Exception {

		CustomerAccount cust = getCustomer(tenantId, custAccountId);
		
		QBXML qbXML = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbXML.setQBXMLMsgsRq(qbxmlMsgsRq);
		qbxmlMsgsRq.setOnError("stopOnError");
		CustomerQueryRqType customerQueryRqType = new CustomerQueryRqType();
		customerQueryRqType.setRequestID(orderId);
		
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(customerQueryRqType);

		//customerQueryRqType.getFullName().add(cust.getFirstName() + " "+ cust.getLastName());
		//Akshay 11-oct-2014 - use email address for full name
		customerQueryRqType.getFullName().add(cust.getEmailAddress());

		return XMLHelper.getMarshalledValue(qbXML);
	}
	
	public String getQBCustomerSaveXML(Integer tenantId, String orderId, Integer customerAccountId) throws Exception {
		CustomerAccount cust = getCustomer(tenantId, customerAccountId);
		QBXML qbXML = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();
		qbXML.setQBXMLMsgsRq(qbxmlMsgsRqType);
		qbxmlMsgsRqType.setOnError("stopOnError");

		CustomerAddRqType qbXMLCustomerAddRqType = new CustomerAddRqType();
		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						qbXMLCustomerAddRqType);

		// Set customer information
		CustomerAdd qbXMCustomerAddType = new CustomerAdd();
		qbXMLCustomerAddRqType.setRequestID(orderId);
		qbXMLCustomerAddRqType.setCustomerAdd(qbXMCustomerAddType);
		qbXMCustomerAddType.setFirstName(cust.getFirstName());
		qbXMCustomerAddType.setLastName(cust.getLastName());
		qbXMCustomerAddType.setMiddleName("");
		
		//qbXMCustomerAddType.setName(cust.getFirstName() + " "+ cust.getLastName());
		//Akshay 11-Oct-2014 Use email for full name
		qbXMCustomerAddType.setName(cust.getEmailAddress());
		
		qbXMCustomerAddType.setPhone(cust.getContacts().get(0).getPhoneNumbers().getMobile());
		qbXMCustomerAddType.setEmail(cust.getEmailAddress());
		qbXMCustomerAddType.setContact("Self");

		// Set billing address
		BillAddress qbXMLBillAddressType = new BillAddress();
		qbXMCustomerAddType.setBillAddress(qbXMLBillAddressType);

		CustomerContact cc = cust.getContacts().get(0);
		qbXMLBillAddressType.setAddr1(cc.getAddress().getAddress1());
		qbXMLBillAddressType.setCity(cc.getAddress().getCityOrTown());
		qbXMLBillAddressType.setState(cc.getAddress().getStateOrProvince());
		qbXMLBillAddressType.setCountry(cc.getAddress().getCountryCode());
		qbXMLBillAddressType
				.setPostalCode(cc.getAddress().getPostalOrZipCode());

		return XMLHelper.getMarshalledValue(qbXML);
	}
	
	public String getQBCustomerUpdateXML(final Order order, final CustomerAccount customerAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean processCustomerQuery(int tenantId,CustomerAccount custAcct, String qbTaskResponse) throws Exception {
		QBXML response = (QBXML) XMLHelper.getUnmarshalledValue(qbTaskResponse);
		CustomerQueryRsType custQueryResponse = (CustomerQueryRsType) response.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);

		if ("warn".equalsIgnoreCase(custQueryResponse.getStatusSeverity())
				&& 500 == custQueryResponse.getStatusCode().intValue()) {
			// Customer not found. So CUST_ADD
			// ENTER the new task
			//qbService.addCustAddTaskToQueue(orderId, tenantId, custAcct);
			return false;
		} else {
			String qbCustListID = custQueryResponse.getCustomerRet().get(0).getListID();
			saveCustInEntityList(custAcct, qbCustListID, tenantId);
			
			return true;
		}
	}
	
	public boolean processCustomerAdd(Integer tenantId,CustomerAccount custAcct, String qbTaskResponse) throws Exception {
		QBXML custAddResp = (QBXML) XMLHelper.getUnmarshalledValue(qbTaskResponse);
		CustomerAddRsType custAddResponse = (CustomerAddRsType) custAddResp.getQBXMLMsgsRs()
																			.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																			.get(0);
		
		if (custAddResponse.getStatusSeverity().equalsIgnoreCase("error")) {
			return false;
		} else {
		
			String customerListId = custAddResponse.getCustomerRet().getListID();
			saveCustInEntityList(custAcct, customerListId, tenantId);
			
			return true;
		}
	}
}
