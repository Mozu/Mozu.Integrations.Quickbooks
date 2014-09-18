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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.qbmodel.allgen.AssetAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.COGSAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.IncomeAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineAdd;
import com.mozu.qbintegration.utils.ApplicationUtils;
import com.mozu.qbintegration.utils.EntityHelper;
import com.mozu.qbintegration.utils.SingleTask;

/**
 * @author Akshay
 * 
 */
@Service
public class QuickbooksServiceImpl implements QuickbooksService {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<?qbxml version=\"13.0\"?>";

	private String appNameSpace;


	private static final Logger logger = LoggerFactory
			.getLogger(QuickbooksServiceImpl.class);

	private Queue<SingleTask> taskQueue;

	@Autowired
	private MongoService mongoService;

	// Heavy object, initialize in constructor
	private JAXBContext contextObj = null;

	// One time as well
	Marshaller marshallerObj = null;

	public QuickbooksServiceImpl() throws JAXBException {
		appNameSpace = ApplicationUtils.getAppNamespace();
		taskQueue = new ArrayBlockingQueue<SingleTask>(1000);
		contextObj = JAXBContext.newInstance(QBXML.class);
		marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	}

	@Override
	public boolean gotWorkToDo() {
		return !taskQueue.isEmpty();
	}

	/*
	 * To be used by sendREquestXML method of QBEndpoint. Just read the element,
	 * removal happens when work is completed. (non-Javadoc)
	 * 
	 * @see com.mozu.qbintegration.service.QuickbooksService#getNextPayload()
	 */
	@Override
	public SingleTask getNextPayload() {
		SingleTask work = ((ArrayBlockingQueue<SingleTask>) taskQueue).peek();
		return work;
	}

	/*
	 * To be used by receiveResponseXML method of QBEndpoint. Remove once the
	 * work has been completed. Put into a map for other threads to read as
	 * needed
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.mozu.qbintegration.service.QuickbooksService#getNextPayload()
	 */
	@Override
	public void doneWithWork() {
		SingleTask task = ((ArrayBlockingQueue<SingleTask>) taskQueue).poll();
		// update completed task
		mongoService.updateCompletedTask(task);

		// if this task is retry for any reason, put it back into queue
		if (task != null && task.getIsRetry()) {
			// Clear off the response and retry privilege
			task.setResponse(null);
			task.setIsRetry(Boolean.FALSE);
			enterNextPayload(task);
		}
		synchronized (task) {
			if (task != null) {
				task.notify();
			}
		}
	}

	/*
	 * To be used by all consumers who want requests processed by QB connector
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mozu.qbintegration.service.QuickbooksService#enterNextPayload(com
	 * .mozu.qbintegration.utils.SingleTask)
	 */
	@Override
	public void enterNextPayload(final SingleTask singleTask) {
		try {
			((ArrayBlockingQueue<SingleTask>) taskQueue).put(singleTask);
			// Also make an entry in task table
			singleTask.setTaskId(singleTask.hashCode());
			mongoService.enterTask(singleTask);
		} catch (InterruptedException e) {
			// TODO LOG Error
			e.printStackTrace();
		}
	}

	@Override
	public String getQBCustomerSaveXML(final CustomerAccount customerAccount) {

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
	public String getQBCustomerUpdateXML(final CustomerAccount customerAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBCustomerGetXML(final CustomerAccount orderingCustomer) {

		QBXML qbXML = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbXML.setQBXMLMsgsRq(qbxmlMsgsRq);
		qbxmlMsgsRq.setOnError("stopOnError");
		CustomerQueryRqType customerQueryRqType = new CustomerQueryRqType();
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
	public String getQBOrderUpdateXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBOrderGetXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQBProductSaveXML(OrderItem orderItem) {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		qbxmlMsgsRqType.setOnError("stopOnError");
		ItemInventoryAddRqType addRqType = new ItemInventoryAddRqType();
		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						addRqType);
		ItemInventoryAdd inventoryAdd = new ItemInventoryAdd();
		addRqType.setItemInventoryAdd(inventoryAdd);
		inventoryAdd.setName(orderItem.getProduct().getProductCode());
		inventoryAdd.setIsActive("true");
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
	public String getQBProductsGetXML(String productCode) {

		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		ItemQueryRqType itemQueryRqType = new ItemQueryRqType();
		itemQueryRqType.getFullName().add(productCode);

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						itemQueryRqType);

		return getMarshalledValue(qbxml);
	}

	@Override
	public void saveOrderInQuickbooks(Order order, CustomerAccount custAcct,
			Integer tenantId, Integer siteId) {

		try {

			// customerListId - the PK of this customer saved in quickbooks
			// 3 ways to get this - entity list query fetch (fastest and the
			// best), then querying QB, then adding to QB and entityList
			// (slowest)
			String customerListId = null;

			// Check in entity list first
			String isCustInEntityList = getCustFromEntityList(custAcct,
					tenantId, siteId);

			if (null != isCustInEntityList) { // this is the most probable
												// condition at all times once
												// customer is saved.
				customerListId = isCustInEntityList;
			} else {
				String custQueryXML = getQBCustomerGetXML(custAcct);
				SingleTask custQueryTask = new SingleTask();
				custQueryTask.setRequest(custQueryXML);

				synchronized (custQueryTask) {
					enterNextPayload(custQueryTask);
					custQueryTask.wait();
				}
				// Resumes with response.
				QBXML response = (QBXML) getUnmarshalledValue(custQueryTask
						.getResponse());
				CustomerQueryRsType custQueryResponse = (CustomerQueryRsType) response
						.getQBXMLMsgsRs()
						.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
						.get(0);

				if ("warn".equalsIgnoreCase(custQueryResponse
						.getStatusSeverity())
						&& 500 == custQueryResponse.getStatusCode().intValue()) {
					// Cust was not found, so insert it
					String custAddXML = getQBCustomerSaveXML(custAcct);
					SingleTask custSaveTask = new SingleTask();
					custSaveTask.setRequest(custAddXML);

					synchronized (custSaveTask) {
						enterNextPayload(custSaveTask);
						custSaveTask.wait();
					}

					// Resume with response
					QBXML custAddResp = (QBXML) getUnmarshalledValue(custSaveTask
							.getResponse());
					CustomerAddRsType custAddResponse = (CustomerAddRsType) custAddResp
							.getQBXMLMsgsRs()
							.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
							.get(0);
					customerListId = custAddResponse.getCustomerRet()
							.getListID();

				} else { // assume found in quickbooks
					customerListId = custQueryResponse.getCustomerRet().get(0)
							.getListID();
				}

				// save this in entitylist to save time next time.
				saveCustInEntityList(custAcct, customerListId, tenantId, siteId);
			}

			// Now proceed with item search to get item list id
			List<String> itemListIDs = new ArrayList<String>();

			for (OrderItem item : order.getItems()) {

				String itemListId = getProductFromEntityList(item, tenantId,
						siteId);

				if (null == itemListId) {

					String itemSearchXML = getQBProductsGetXML(item
							.getProduct().getProductCode());
					SingleTask itemSearchTask = new SingleTask();
					itemSearchTask.setRequest(itemSearchXML);

					synchronized (itemSearchTask) {
						enterNextPayload(itemSearchTask);
						itemSearchTask.wait();
					}

					// Item must be found here. Else we need to error out.
					QBXML itemSearchEle = (QBXML) getUnmarshalledValue(itemSearchTask
							.getResponse());
					ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle
							.getQBXMLMsgsRs()
							.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
							.get(0);

					if (500 == itemSearchResponse.getStatusCode().intValue()
							&& "warn".equalsIgnoreCase(itemSearchResponse
									.getStatusSeverity())) { // item not found

						String itemAddXML = getQBProductSaveXML(item);
						SingleTask itemAddTask = new SingleTask();
						itemAddTask.setRequest(itemAddXML);

						synchronized (itemAddTask) {
							enterNextPayload(itemAddTask);
							itemAddTask.wait();
						}

						// Get back the inserted item
						QBXML itemAddEle = (QBXML) getUnmarshalledValue(itemAddTask
								.getResponse());

						ItemInventoryAddRsType invAddResponse = (ItemInventoryAddRsType) itemAddEle
								.getQBXMLMsgsRs()
								.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
								.get(0);
						itemListId = invAddResponse.getItemInventoryRet()
								.getListID();

					} else {

						Object invObj = itemSearchResponse
								.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet()
								.get(0);

						if (invObj instanceof ItemServiceRet) {
							ItemServiceRet itemServiceRet = (ItemServiceRet) invObj;
							itemListId = itemServiceRet.getListID();
						} else if (invObj instanceof ItemInventoryRet) {
							ItemInventoryRet itemInvRet = (ItemInventoryRet) invObj;
							itemListId = itemInvRet.getListID();
						}
					}

					// Save the item list id in entity list
					saveProductInEntityList(item, itemListId, tenantId, siteId);

				}
				itemListIDs.add(itemListId); // fairly assume we have received
												// it by this time.
			} // end of for - for items

			// Now send the order
			String orderSaveXML = getQBOrderSaveXML(order, customerListId,
					itemListIDs);
			SingleTask orderTask = new SingleTask();
			orderTask.setTaskType("saveorder");
			orderTask.setRequest(orderSaveXML);
			synchronized (orderTask) {
				enterNextPayload(orderTask);
				orderTask.wait();
			}
			// Resume with response
			QBXML orderAddResp = (QBXML) getUnmarshalledValue(orderTask
					.getResponse());
			com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType salesOrderResponse = (com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRsType) orderAddResp
					.getQBXMLMsgsRs()
					.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
					.get(0);
			logger.debug("" + salesOrderResponse.getStatusCode());
			logger.debug(salesOrderResponse.getStatusMessage());
		} catch (InterruptedException e) {
			logger.error("Failing while waiting on task completion: "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Different exception: " + e.getMessage());
		}

	}

	private void saveCustInEntityList(CustomerAccount custAcct,
			String customerListId, Integer tenantId, Integer siteId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("custEmail", custAcct.getEmailAddress());
		custNode.put("custQBListID", customerListId);
		custNode.put("custName",
				custAcct.getFirstName() + " " + custAcct.getLastName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = EntityHelper.CUST_ENTITY + "@" + appNameSpace;
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
	private String getCustFromEntityList(CustomerAccount custAcct,
			Integer tenantId, Integer siteId) {
		String entityIdValue = custAcct.getEmailAddress();
		String mapName = EntityHelper.CUST_ENTITY + "@" + appNameSpace;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error retrieving entity for email id: "
					+ entityIdValue);
		}
		return qbListID;
	}

	private void saveProductInEntityList(OrderItem orderItem,
			String qbProdustListID, Integer tenantId, Integer siteId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("productCode", orderItem.getProduct().getProductCode());
		custNode.put("qbProdustListID", qbProdustListID);
		custNode.put("productName", orderItem.getProduct().getName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = EntityHelper.PRODUCT_ENTITY + "@" + appNameSpace;
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
	private String getProductFromEntityList(OrderItem orderItem,
			Integer tenantId, Integer siteId) {
		String entityIdValue = orderItem.getProduct().getProductCode();
		String mapName = EntityHelper.PRODUCT_ENTITY + "@" + appNameSpace;

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

	public GeneralSettings saveOrUpdateSettingsInEntityList(
			GeneralSettings generalSettings, Integer tenantId) {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		JsonNode savedEntry = null;
		String mapName = EntityHelper.SETTINGS_ENTITY + "@" + appNameSpace;

		try {
			savedEntry = entityResource.getEntity(mapName, "generalsettings");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving settings for tenant id: " + tenantId);
		}

		GeneralSettings savedSettings = null;
		boolean isUpdate = true;

		if (savedEntry == null) { // true only for first time for the tenant.
			isUpdate = false;
		}

		// Add the mapping entry
		JsonNode rtnEntry = null;
		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("generalsettings", "generalsettings");
		custNode.put("wsURL", generalSettings.getWsURL());
		custNode.put("qbAccount", generalSettings.getQbAccount());
		custNode.put("qbPassword", generalSettings.getQbPassword());
		custNode.put("accepted", generalSettings.getAccepted());
		custNode.put("completed", generalSettings.getCompleted());
		custNode.put("cancelled", generalSettings.getCancelled());

		try {
			if (!isUpdate) { // insert scenario.
				rtnEntry = entityResource.insertEntity(custNode, mapName);
			} else {
				rtnEntry = entityResource.updateEntity(custNode, mapName,
						"generalsettings");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving settings for tenant id: " + tenantId);
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");

		return savedSettings;
	}

	public GeneralSettings getSettingsFromEntityList(Integer tenantId) {

		// First get an entity for settings if already present.
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId)); // TODO replace with real - move this code
		JsonNode savedEntry = null;
		String mapName = EntityHelper.SETTINGS_ENTITY + "@" + appNameSpace;

		try {
			savedEntry = entityResource.getEntity(mapName, "generalsettings");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving settings for tenant id: " + tenantId);
		}

		GeneralSettings savedSettings = null;
		if (savedEntry != null) {
			ObjectNode retNode = (ObjectNode) savedEntry;
			savedSettings = new GeneralSettings();
			savedSettings.setWsURL(retNode.get("wsURL").asText());
			savedSettings.setQbAccount(retNode.get("qbAccount").asText());
			savedSettings.setQbPassword(retNode.get("qbPassword").asText());
			savedSettings.setAccepted(retNode.get("accepted").asBoolean());
			savedSettings.setCompleted(retNode.get("completed").asBoolean());
			savedSettings.setCancelled(retNode.get("cancelled").asBoolean());
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

}
