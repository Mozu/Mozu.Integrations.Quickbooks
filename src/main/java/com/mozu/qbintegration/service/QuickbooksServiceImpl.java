/**
 * 
 */
package com.mozu.qbintegration.service;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderDetails;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.qbmodel.allgen.AssetAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.COGSAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.IncomeAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeRef;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.ApplicationUtils;
import com.mozu.qbintegration.utils.EntityHelper;

/**
 * @author Akshay
 * 
 */
@Service
public class QuickbooksServiceImpl implements QuickbooksService {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<?qbxml version=\"13.0\"?>";

	private static final Logger logger = LoggerFactory
			.getLogger(QuickbooksServiceImpl.class);

	private static ObjectMapper mapper = JsonUtils.initObjectMapper();
	
	// Heavy object, initialize in constructor
	private JAXBContext contextObj = null;

	// One time as well
	Marshaller marshallerObj = null;

	@Autowired
	private QueueManagerService queueManagerService;

	public QuickbooksServiceImpl() throws JAXBException {
		contextObj = JAXBContext.newInstance(QBXML.class);
		marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	}

	@Override
	public String getQBCustomerSaveXML(final Order order, final CustomerAccount customerAccount) {

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
		qbXMLCustomerAddRqType.setRequestID(order.getId());
		qbXMLCustomerAddRqType.setCustomerAdd(qbXMCustomerAddType);
		qbXMCustomerAddType.setFirstName(customerAccount.getFirstName());
		qbXMCustomerAddType.setLastName(customerAccount.getLastName());
		qbXMCustomerAddType.setMiddleName("");
		qbXMCustomerAddType.setName(customerAccount.getFirstName() + " "
				+ customerAccount.getLastName());
		qbXMCustomerAddType.setPhone(customerAccount.getContacts().get(0)
				.getPhoneNumbers().getMobile());
		qbXMCustomerAddType.setEmail(customerAccount.getEmailAddress());
		qbXMCustomerAddType.setContact("Self");

		// Set billing address
		BillAddress qbXMLBillAddressType = new BillAddress();
		qbXMCustomerAddType.setBillAddress(qbXMLBillAddressType);

		CustomerContact cc = customerAccount.getContacts().get(0);
		qbXMLBillAddressType.setAddr1(cc.getAddress().getAddress1());
		qbXMLBillAddressType.setCity(cc.getAddress().getCityOrTown());
		qbXMLBillAddressType.setState(cc.getAddress().getStateOrProvince());
		qbXMLBillAddressType.setCountry(cc.getAddress().getCountryCode());
		qbXMLBillAddressType
				.setPostalCode(cc.getAddress().getPostalOrZipCode());

		return getMarshalledValue(qbXML);
	}

	@Override
	public String getQBCustomerUpdateXML(final Order order, final CustomerAccount customerAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBCustomerGetXML(final Order order, final CustomerAccount orderingCustomer) {

		QBXML qbXML = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbXML.setQBXMLMsgsRq(qbxmlMsgsRq);
		qbxmlMsgsRq.setOnError("stopOnError");
		CustomerQueryRqType customerQueryRqType = new CustomerQueryRqType();
		customerQueryRqType.setRequestID(order.getId());
		
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(customerQueryRqType);

		customerQueryRqType.getFullName().add(
				orderingCustomer.getFirstName() + " "
						+ orderingCustomer.getLastName());

		return getMarshalledValue(qbXML);
	}

	@Override
	public String getQBOrderSaveXML(Order singleOrder, String customerQBListID,
			List<String> itemListIDs) {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType salesOrderAddRqType = new com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
				.add(salesOrderAddRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesOrderAdd salesOrderAdd = new SalesOrderAdd();
		salesOrderAddRqType.setRequestID(singleOrder.getId());
		
		salesOrderAddRqType.setSalesOrderAdd(salesOrderAdd);
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID(customerQBListID);
		salesOrderAdd.setCustomerRef(customerRef);

		List<OrderItem> items = singleOrder.getItems();
		ItemRef itemRef = null;
		SalesOrderLineAdd salesOrderLineAdd = null;

		NumberFormat numberFormat = new DecimalFormat("#.00");
		int counter = 0;
		for (OrderItem item : items) {
			itemRef = new ItemRef();
			itemRef.setListID(itemListIDs.get(counter));
			salesOrderLineAdd = new SalesOrderLineAdd();
			salesOrderLineAdd.setAmount(numberFormat.format(item
					.getDiscountedTotal()));
			salesOrderLineAdd.setItemRef(itemRef);
			// salesOrderLineAdd.setRatePercent("7.5"); // (getItemtaxTotal *
			// 100)/
			// itemTaxableTotal
			salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(
					salesOrderLineAdd);
			counter++;
		}

		return getMarshalledValue(qbxml);
	}

	@Override
	public String getQBOrderUpdateXML(final Order order, final CustomerAccount customerAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBOrderGetXML(final Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBProductSaveXML(final Order order, final OrderItem orderItem) {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		qbxmlMsgsRqType.setOnError("stopOnError");
		ItemInventoryAddRqType addRqType = new ItemInventoryAddRqType();
		
		addRqType.setRequestID(order.getId());
		
		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						addRqType);
		ItemInventoryAdd inventoryAdd = new ItemInventoryAdd();
		addRqType.setItemInventoryAdd(inventoryAdd);
		inventoryAdd.setName(orderItem.getProduct().getProductCode());
		inventoryAdd.setIsActive("true");

		// TODO move these to either prop files or get these details from
		// customer
		SalesTaxCodeRef salesTax = new SalesTaxCodeRef();
		salesTax.setFullName("goulet sales tax");
		inventoryAdd.setSalesTaxCodeRef(salesTax);

		IncomeAccountRef incomeAccount = new IncomeAccountRef(); // TODO get
																	// client's
																	// details
		incomeAccount.setFullName("Sales - Software");
		inventoryAdd.setIncomeAccountRef(incomeAccount);
		AssetAccountRef assetAccount = new AssetAccountRef(); // TODO get
																// client's
																// details
		assetAccount.setFullName("Inventory Asset");
		inventoryAdd.setAssetAccountRef(assetAccount);
		COGSAccountRef cogsAccountRef = new COGSAccountRef();
		cogsAccountRef.setFullName("Cost of Goods Sold"); // TODO get client's
															// details
		inventoryAdd.setCOGSAccountRef(cogsAccountRef);

		NumberFormat numberFormat = new DecimalFormat("#.00");
		inventoryAdd.setSalesDesc(orderItem.getProduct().getName());
		inventoryAdd.setSalesPrice(numberFormat.format(orderItem.getUnitPrice()
				.getListAmount()));

		return getMarshalledValue(qbxml);
	}

	@Override
	public String getQBProductsGetXML(final Order order, String productCode) {

		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		ItemQueryRqType itemQueryRqType = new ItemQueryRqType();
		itemQueryRqType.getFullName().add(productCode);
		itemQueryRqType.setRequestID(order.getId());

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						itemQueryRqType);

		return getMarshalledValue(qbxml);
	}

	@Override
	public void saveOrderInQuickbooks(Order order, CustomerAccount custAcct,
			Integer tenantId, Integer siteId) {

		try {

			/*
			 * customerListId - the PK of this customer saved in quickbooks 3
			 * ways to get this - entity list query fetch (fastest and the
			 * best), then querying QB, then adding to QB and entityList //
			 * (slowest)
			 */
			String customerListId = null;

			// Check in entity list first
			String isCustInEntityList = getCustFromEntityList(custAcct,
					tenantId, siteId);

			if (null != isCustInEntityList) { // this is the most probable
												// condition at all times once
												// customer is saved.
				customerListId = isCustInEntityList;
				
				//TODO 1. Since customer is present, check for items. This logic is duplicated in endpoint, so
				// refactor into single method once everything works.
				boolean allItemsInEntityList = true;
				List<String> itemListIds = new ArrayList<String>();
				for(OrderItem singleItem: order.getItems()) {
					String itemQBListId = getProductFromEntityList(singleItem, tenantId, siteId);
					if(null == itemQBListId) { //TODO 2. item not found in entity list. So issue a search to QB.
						allItemsInEntityList = false;

						WorkTask itemQueryTask = new WorkTask();
						//Just to make it unique
						itemQueryTask.setEnteredTime(System.currentTimeMillis());
						itemQueryTask.setTaskId(order.getId());
						itemQueryTask.setQbTaskStatus("ENTERED");
						itemQueryTask.setTenantId(tenantId);
						itemQueryTask.setSiteId(siteId);
						itemQueryTask.setQbTaskType("ITEM_QUERY");
						itemQueryTask.setQbTaskRequest(getQBProductsGetXML(order, singleItem.getProduct().getProductCode()));
						queueManagerService.saveTask(itemQueryTask, tenantId);
						
					}
					itemListIds.add(itemQBListId); //list will anyway be discarded if above flag is false, so no null
				}
				
				if(allItemsInEntityList) { //Add order ADD task if all items are already present in EL
					WorkTask sOrderAddTask = new WorkTask();
					//Just to make it unique
					sOrderAddTask.setEnteredTime(System.currentTimeMillis());
					sOrderAddTask.setTaskId(order.getId());
					sOrderAddTask.setQbTaskStatus("ENTERED");
					sOrderAddTask.setTenantId(tenantId);
					sOrderAddTask.setSiteId(siteId);
					sOrderAddTask.setQbTaskType("ORDER_ADD");
					//Get customer from entity list - by this time the user HAS to be present in entity list. 
					//TODO handle error when customer is not found for any reason.
					String custQBListID = getCustFromEntityList(custAcct, tenantId, siteId);
					sOrderAddTask.setQbTaskRequest(getQBOrderSaveXML(order, custQBListID, itemListIds));
					queueManagerService.saveTask(sOrderAddTask, tenantId);
				}
				
			} else { //Customer not found, so add task and let it go
				//TODO all logic has been moved to EL based queue into the endpoint. Refactor it
				WorkTask custAddTask = new WorkTask();
				custAddTask.setEnteredTime(System.currentTimeMillis());
				custAddTask.setTaskId(order.getId());
				custAddTask.setQbTaskStatus("ENTERED"); //since at this moment I think we cannot have an AND in the filter
				custAddTask.setTenantId(tenantId);
				custAddTask.setSiteId(siteId);
				custAddTask.setQbTaskType("CUST_ADD");
				custAddTask.setQbTaskRequest(getQBCustomerSaveXML(order, custAcct));
				queueManagerService.saveTask(custAddTask, tenantId);
			}

			logger.debug("Entire process completed for order id: " + order.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Different exception: " + e.getMessage());
		}

	}

	@Override
	public void saveCustInEntityList(CustomerAccount custAcct,
			String customerListId, Integer tenantId, Integer siteId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("custEmail", custAcct.getEmailAddress());
		custNode.put("custQBListID", customerListId);
		custNode.put("custName",
				custAcct.getFirstName() + " " + custAcct.getLastName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = EntityHelper.getCustomerEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		try {
			rtnEntry = entityResource.insertEntity(custNode, mapName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving customer in entity list: "
					+ custAcct.getEmailAddress());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
	}

	/**
	 * Get the customer based on the email address
	 * 
	 * @param custAcct
	 * @return
	 */
	@Override
	public String getCustFromEntityList(CustomerAccount custAcct,
			Integer tenantId, Integer siteId) {
		String entityIdValue = custAcct.getEmailAddress();
		String mapName = EntityHelper.getCustomerEntityName();

		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		String qbListID = null;
		try {
			JsonNode entity = entityResource.getEntity(mapName, entityIdValue);
			JsonNode result = entity.findValue("custQBListID");
			if (result != null) {
				qbListID = result.asText();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error retrieving entity for email id: "
					+ entityIdValue);
		}
		return qbListID;
	}

	@Override
	public void saveProductInEntityList(OrderItem orderItem,
			String qbProdustListID, Integer tenantId, Integer siteId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("productCode", orderItem.getProduct().getProductCode());
		custNode.put("qbProdustListID", qbProdustListID);
		custNode.put("productName", orderItem.getProduct().getName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = EntityHelper.getProductEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		try {
			rtnEntry = entityResource.insertEntity(custNode, mapName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving product in entity list: "
					+ orderItem.getProduct().getProductCode());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
	}

	/**
	 * Get the product quickbooks list id based on product code
	 * 
	 * @param orderItem
	 * @return
	 */
	@Override
	public String getProductFromEntityList(OrderItem orderItem,
			Integer tenantId, Integer siteId) {
		String entityIdValue = orderItem.getProduct().getProductCode();
		String mapName = EntityHelper.getProductEntityName();

		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		String qbListID = null;
		try {
			JsonNode entity = entityResource.getEntity(mapName, entityIdValue);
			JsonNode result = entity.findValue("qbProdustListID");
			if (result != null) {
				qbListID = result.asText();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error retrieving entity for product code: "
					+ entityIdValue);
		}
		return qbListID;
	}

	@Override
	public GeneralSettings saveOrUpdateSettingsInEntityList(
			GeneralSettings generalSettings, Integer tenantId) throws Exception {

		// First get an entity for settings if already present.
		MozuApiContext context =new MozuApiContext(tenantId); 
		EntityResource entityResource = new EntityResource(context); // TODO replace with real - move this code
		String mapName = EntityHelper.getSettingEntityName();
		generalSettings.setId("generalsettings-new");
		boolean isUpdate = false;

		try {
			entityResource.getEntity(mapName, generalSettings.getId());
			isUpdate = true;
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(),e);
				throw e;
			}
		}

		JsonNode custNode = mapper.valueToTree(generalSettings);
		try {
			if (!isUpdate) { // insert scenario.
				custNode = entityResource.insertEntity(custNode, mapName);
			} else {
				custNode = entityResource.updateEntity(custNode, mapName,generalSettings.getId());
			}

			ApplicationUtils.setApplicationToInitialized(context);
		} catch (ApiException e) {
			logger.error("Error saving settings for tenant id: " + tenantId, e);
			throw e;
		}

		return generalSettings;
	}

	@Override
	public GeneralSettings getSettingsFromEntityList(Integer tenantId) throws Exception {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		JsonNode savedEntry = null;
		String mapName = EntityHelper.getSettingEntityName();

		try {
			savedEntry = entityResource.getEntity(mapName, "generalsettings-new");
		} catch (ApiException e) {
			if (!StringUtils.equals(e.getApiError().getErrorCode(),"ITEM_NOT_FOUND"))
				throw e;
		}

		GeneralSettings savedSettings = null;
		if (savedEntry != null) {
			/*ObjectNode retNode = (ObjectNode) savedEntry;
			savedSettings = new GeneralSettings();
			savedSettings.setWsURL(retNode.get("wsURL").asText());
			savedSettings.setQbAccount(retNode.get("qbAccount").asText());
			savedSettings.setQbPassword(retNode.get("qbPassword").asText());
			savedSettings.setAccepted(retNode.get("accepted").asBoolean());
			savedSettings.setCompleted(retNode.get("completed").asBoolean());
			savedSettings.setCancelled(retNode.get("cancelled").asBoolean());*/
			savedSettings = mapper.readValue(savedEntry.toString(), GeneralSettings.class);
		}
		return savedSettings;
	}

	/**
	 * Marshal the inout qbxml.
	 * 
	 * @param qbxml
	 * @return
	 */
	private String getMarshalledValue(QBXML qbxml) {
		String qbXMLStr = null;
		try {
			StringWriter writer = new StringWriter();
			marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshallerObj.marshal(qbxml, writer);
			qbXMLStr = QBXML_PREFIX + writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return qbXMLStr;
	}

	/**
	 * Just return un-marshalled object. individual callers will get it
	 * converted since they know what they are looking for
	 * 
	 * @param respFromQB
	 * @return
	 */
	@Override
	public Object getUnmarshalledValue(String respFromQB) {
		Object umValue = null;
		try {
			Unmarshaller unmarshallerObj = contextObj.createUnmarshaller();
			Reader r = new StringReader(respFromQB);
			umValue = unmarshallerObj.unmarshal(r);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return umValue;
	}

	@Override
	public void setNextTask(WorkTask workTask, Integer tenantId) {
		//Decide which task will be processed next
		if("CUST_QUERY".equals(workTask)) {
			//This was a cust query task. So check if we found or not. 
			//If found, save in entity list. If not found, we need to add
			// Resumes with response.
			QBXML response = (QBXML) getUnmarshalledValue(workTask
					.getQbTaskResponse());
			CustomerQueryRsType custQueryResponse = (CustomerQueryRsType) response
					.getQBXMLMsgsRs()
					.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
					.get(0);

			if ("warn".equalsIgnoreCase(custQueryResponse
					.getStatusSeverity())
					&& 500 == custQueryResponse.getStatusCode().intValue()) {
				
			} else {
				//saveCustInEntityList(custAcct, customerListId, tenantId, siteId)
			}
			
		}
		
	}

	@Override
	public Order getMozuOrder(String orderId, Integer tenantId, Integer siteId) {
		OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
		Order order = null;
		try {
			order = orderResource.getOrder(orderId);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return order;
	}

	@Override
	public CustomerAccount getMozuCustomer(Order order, Integer tenantId,
			Integer siteId) {
		CustomerAccountResource accountResource = new CustomerAccountResource(new MozuApiContext(tenantId, siteId));
		CustomerAccount orderingCust = null;
		try {
			orderingCust = accountResource.getAccount(order.getCustomerAccountId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderingCust;
	}

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QuickbooksService#addOrderAddTaskToQueue(java.lang.String, java.lang.Integer, java.lang.Integer, com.mozu.api.contracts.customer.CustomerAccount, com.mozu.api.contracts.commerceruntime.orders.Order, java.util.List)
	 */
	@Override
	public void addOrderAddTaskToQueue(String orderId, Integer tenantId,
			Integer siteId, CustomerAccount custAcct, Order order,
			List<String> itemListIds) {
		WorkTask sOrderAddTask = new WorkTask();
		// Just to make it unique
		sOrderAddTask
				.setEnteredTime(System.currentTimeMillis());
		sOrderAddTask.setTaskId(orderId);
		sOrderAddTask.setQbTaskStatus("ENTERED");
		sOrderAddTask.setTenantId(tenantId);
		sOrderAddTask.setSiteId(siteId);
		sOrderAddTask.setQbTaskType("ORDER_ADD");
		// Get customer from entity list - by this time the user
		// HAS
		// to be present in entity list.
		// TODO handle error when customer is not found for any
		// reason.
		String custQBListID = getCustFromEntityList(
				custAcct, tenantId, siteId);
		sOrderAddTask.setQbTaskRequest(getQBOrderSaveXML(order, custQBListID,
						itemListIds));
		queueManagerService.saveTask(sOrderAddTask, tenantId);
	}

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QuickbooksService#addItemQueryTaskToQueue(java.lang.String, java.lang.Integer, java.lang.Integer, com.mozu.api.contracts.commerceruntime.orders.Order, java.lang.String)
	 */
	@Override
	public void addItemQueryTaskToQueue(String orderId, Integer tenantId,
			Integer siteId, Order order, String productCode) {
		WorkTask itemQueryTask = new WorkTask();
		// Just to make it unique
		itemQueryTask
				.setEnteredTime(System.currentTimeMillis());
		itemQueryTask.setTaskId(orderId);
		itemQueryTask.setQbTaskStatus("ENTERED");
		itemQueryTask.setTenantId(tenantId);
		itemQueryTask.setSiteId(siteId);
		itemQueryTask.setQbTaskType("ITEM_QUERY");
		itemQueryTask.setQbTaskRequest(getQBProductsGetXML(order, productCode));
		queueManagerService.saveTask(itemQueryTask, tenantId);
		
	}

	@Override
	public void addCustAddTaskToQueue(String orderId, Integer tenantId,
			Integer siteId,	Order order, CustomerAccount custAcct) {
		WorkTask custAddTask = new WorkTask();
		custAddTask.setEnteredTime(System.currentTimeMillis());
		custAddTask.setTaskId(orderId);
		custAddTask.setQbTaskStatus("ENTERED"); // since at this
												// moment
												// I think we cannot
												// have an AND in
												// the
												// filter
		custAddTask.setTenantId(tenantId);
		custAddTask.setSiteId(siteId);
		custAddTask.setQbTaskType("CUST_ADD");
		custAddTask.setQbTaskRequest(getQBCustomerGetXML(
				order, custAcct));
		queueManagerService.saveTask(custAddTask, tenantId);
	}

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QuickbooksService#saveOrderInEntityList(com.mozu.qbintegration.model.MozuOrderDetails, com.mozu.api.contracts.customer.CustomerAccount, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void saveOrderInEntityList(MozuOrderDetails orderDetails, CustomerAccount custAccount,
			Integer tenantId, Integer siteId) {
		saveOrUpdateOrderInEL(orderDetails, custAccount, tenantId, siteId, Boolean.FALSE);
	}
	
	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QuickbooksService#updateOrderInEntityList(com.mozu.qbintegration.model.MozuOrderDetails, com.mozu.api.contracts.customer.CustomerAccount, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateOrderInEntityList(MozuOrderDetails orderDetails, CustomerAccount custAccount,
			Integer tenantId, Integer siteId) {
		saveOrUpdateOrderInEL(orderDetails, custAccount, tenantId, siteId, Boolean.TRUE);
	}
	
	private void saveOrUpdateOrderInEL(MozuOrderDetails orderDetails, CustomerAccount custAccount,
			Integer tenantId, Integer siteId, Boolean isUpdate) {
		JsonNode orderNode = getOrderNode(orderDetails, tenantId, siteId, custAccount);
		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		String mapName = EntityHelper.getOrderEntityName();
		
		// Add the mapping entry
		JsonNode rtnEntry = null;
		try {
			if(!isUpdate) {
				rtnEntry = entityResource.insertEntity(orderNode, mapName);
			} else {
				rtnEntry = entityResource.updateEntity(orderNode, mapName, orderDetails.getMozuOrderNumber());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving order details for tenant id: " + tenantId);
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
		
	}

	private JsonNode getOrderNode(MozuOrderDetails orderDetails, Integer tenantId, Integer siteId, CustomerAccount custAccount) {
		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode taskNode = nodeFactory.objectNode();

		taskNode.put("mozuOrderNumber", orderDetails.getMozuOrderNumber());
		taskNode.put("quickbooksOrderListId", orderDetails.getQuickbooksOrderListId() == null ? 
				"" : orderDetails.getQuickbooksOrderListId()); //For item we need to save multiple tasks
		taskNode.put("tenantId", tenantId);
		taskNode.put("siteId", siteId);
		taskNode.put("orderStatus", orderDetails.getOrderStatus());
		taskNode.put("customerEmail", custAccount.getEmailAddress());
		taskNode.put("orderDate", orderDetails.getOrderDate());
		taskNode.put("orderUpdatedDate", orderDetails.getOrderUpdatedDate());
		taskNode.put("conflictReason", orderDetails.getConflictReason());
		taskNode.put("amount", orderDetails.getAmount());
		
		return taskNode;
	}
	
	@Override
	public List<MozuOrderDetails> getMozuOrderDetails(Integer tenantId, 
			MozuOrderDetails mozuOrderDetails) {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		String mapName = EntityHelper.getOrderEntityName();
		
		StringBuilder sb = new StringBuilder();
		//Assuming status will never be null - it is meaningless to filter without it at this point.
		//TODO throw exception if status is null
		sb.append("orderStatus eq " + mozuOrderDetails.getOrderStatus());
		
		if(mozuOrderDetails.getMozuOrderNumber() != null) {
			sb.append(" and mozuOrderNumber eq " + mozuOrderDetails.getMozuOrderNumber());
		}

		List<MozuOrderDetails> mozuOrders = new ArrayList<MozuOrderDetails>();
		EntityCollection orderCollection = null;
		
		try {
			orderCollection = entityResource.getEntities(mapName, null, null, sb.toString(), "mozuOrderNumber desc", null);
			MozuOrderDetails singleOrdDetail = null;
			if (null != orderCollection) {
				for (JsonNode singleOrder : orderCollection.getItems()) {
					singleOrdDetail = new MozuOrderDetails();
					singleOrdDetail.setMozuOrderNumber(singleOrder.get(
							"mozuOrderNumber").asText());
					singleOrdDetail.setQuickbooksOrderListId(singleOrder.get(
							"quickbooksOrderListId").asText());
					singleOrdDetail.setOrderStatus(singleOrder.get("orderStatus")
							.asText());
					singleOrdDetail.setCustomerEmail(singleOrder.get(
							"customerEmail").asText());
					singleOrdDetail.setOrderDate(singleOrder.get(
							"orderDate").asText());
					singleOrdDetail.setOrderUpdatedDate(singleOrder.get(
							"orderUpdatedDate").asText());
					singleOrdDetail.setConflictReason(singleOrder.get(
							"conflictReason").asText());
					singleOrdDetail.setAmount(singleOrder.get(
							"amount") == null? "0.00" : singleOrder.get(
									"amount").asText());
					
					mozuOrders.add(singleOrdDetail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving settings for tenant id: " + tenantId);
		}
		return mozuOrders;
		
	}

	/* (non-Javadoc)
	 * @see com.mozu.qbintegration.service.QuickbooksService#saveConflictInEntityList(java.lang.Integer, java.lang.String, java.util.List)
	 */
	@Override
	public void saveConflictInEntityList(Integer tenantId, Integer mozuOrderNumber,
			List<OrderConflictDetail> conflictReasons) {
		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		String mapName = EntityHelper.getOrderConflictEntityName();
		
		for(OrderConflictDetail reason: conflictReasons) {
			ObjectNode conflictNode = mapper.valueToTree(reason);
			conflictNode.put("enteredTime", String.valueOf(System.currentTimeMillis()));
			conflictNode.put("mozuOrderId", reason.getMozuOrderNumber());
			
			JsonNode rtnEntry = null;
			try {
				rtnEntry = entityResource.insertEntity(conflictNode, mapName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Error saving order conflict details for order id: " + mozuOrderNumber);
			}
			logger.info("RtnEntry: " + rtnEntry);
		}
	}

	@Override
	public List<OrderConflictDetail> getOrderConflictReasons(Integer tenantId, String orderId) {
		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		String mapName = EntityHelper.getOrderConflictEntityName();
		
		EntityCollection orderConflictCollection = null;
		
		List<OrderConflictDetail> conflictDetails = new ArrayList<OrderConflictDetail>();
		try {
			orderConflictCollection = entityResource.getEntities(mapName, null, null, 
					"mozuOrderId eq " + orderId, null, null);
			
			if (null != orderConflictCollection) {
				for (JsonNode singleOrderConflict : orderConflictCollection.getItems()) {
					conflictDetails.add(mapper.readValue(singleOrderConflict.toString(), OrderConflictDetail.class));
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error getting order conflict details for order id: " + orderId);
		}
		
		return conflictDetails;
	}

}
