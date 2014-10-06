package com.mozu.qbintegration.handlers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.MozuOrderDetail;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.OrderConflictDetail;
import com.mozu.qbintegration.model.ProductToMapToQuickbooks;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.model.qbmodel.allgen.AssetAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.COGSAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.IncomeAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeRef;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksServiceImpl;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.XMLHelper;

@Component
public class ProductHandler {

	private static final Logger logger = LoggerFactory.getLogger(ProductHandler.class);
	private static ObjectMapper mapper = JsonUtils.initObjectMapper();

	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
	public String getQBId(Integer tenantId, String productCode) throws Exception {
		
		String qbListID = null;
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getProductEntityName(), productCode);
		if (node == null) return qbListID;
		JsonNode result = node.findValue("qbProdustListID");
		if (result != null) {
			qbListID = result.asText();
		}
		return qbListID;
	}
	
	private void saveProductInEntityList(ItemQueryRsType itemSearchResponse,Integer tenantId) {
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
		saveProductInEntityList(item, itemListId, tenantId );

	}
	
	public void processItemQueryAll(Integer tenantId, WorkTask workTask, String qbTaskResponse) throws Exception {
		QBXML itemSearchEle = (QBXML) XMLHelper.getUnmarshalledValue(qbTaskResponse);
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
			} else
				continue;
			MozuProduct mozuProduct = new MozuProduct();
			mozuProduct.setProductCode(productName);
			mozuProduct.setQbProductListID(productQbListID);
			mozuProduct.setProductName(productName);
			saveAllProductInEntityList(mozuProduct, tenantId);
			logger.debug("Saved product through refresh all: "+ productName);
		}
		
		queueManagerService.updateTask(tenantId, workTask.getId(), "Refresh", "COMPLETED");
	}
	
	public boolean processItemQuery(Integer tenantId, String qbTaskResponse) throws Exception {
		QBXML itemSearchEle = (QBXML)  XMLHelper.getUnmarshalledValue(qbTaskResponse);
		ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle.getQBXMLMsgsRs()
																			.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																			.get(0);

		if (500 == itemSearchResponse.getStatusCode().intValue()
				&& "warn".equalsIgnoreCase(itemSearchResponse.getStatusSeverity())) {
			
			return false;

		} else {

			saveProductInEntityList(itemSearchResponse, tenantId);

			return true;
			
		}
	}
	
	public void processItemAdd(Integer tenantId, WorkTask workTask, String qbTaskResponse) throws Exception {
		QBXML itemAddEle = (QBXML)  XMLHelper.getUnmarshalledValue(qbTaskResponse);

		ItemInventoryAddRsType invAddResponse = (ItemInventoryAddRsType) itemAddEle.getQBXMLMsgsRs()
																				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
																				.get(0);
		
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getProdctAddEntity() ,  workTask.getId());
		ProductToQuickbooks productInQuickBooks = mapper.readValue(node.toString(), ProductToQuickbooks.class);
		productInQuickBooks.setStatus(invAddResponse.getStatusSeverity());
		productInQuickBooks.setErrorMessage(invAddResponse.getStatusMessage());

		if (!invAddResponse.getStatusSeverity().equalsIgnoreCase("error")) {
			String itemListId = invAddResponse.getItemInventoryRet().getListID();
			// Save the item list id in entity list
			OrderItem item = new OrderItem();
			Product product = new Product();
			item.setProduct(product);
			product.setProductCode(invAddResponse.getItemInventoryRet().getFullName());
			product.setName(invAddResponse.getItemInventoryRet().getName());
			saveProductInEntityList(item, itemListId, tenantId);
	
			logger.debug("Added new product to quickbooks: "+ invAddResponse.getItemInventoryRet().getName());
		}
		
		entityHandler.updateEntity(tenantId, entityHandler.getProdctAddEntity(),  workTask.getId(), productInQuickBooks);
		queueManagerService.updateTask(tenantId, workTask.getId(), "ADD", "COMPLETED");
	}
	
	private void saveProductInEntityList(OrderItem orderItem,String qbProdustListID, Integer tenantId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("productCode", orderItem.getProduct().getProductCode());
		custNode.put("qbProdustListID", qbProdustListID);
		custNode.put("productName", orderItem.getProduct().getName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = entityHandler.getProductEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(tenantId)); 
		try {
			List<JsonNode> existing = entityHandler.getEntityCollection(tenantId, mapName, "productCode eq "+ orderItem.getProduct().getProductCode(), null, 1);
			if (existing.size() == 0)
				rtnEntry = entityResource.insertEntity(custNode, mapName);
			else
				rtnEntry = entityResource.updateEntity(custNode, mapName, orderItem.getProduct().getProductCode());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving product in entity list: "
					+ orderItem.getProduct().getProductCode());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
	}

	public void saveAllProductInEntityList(MozuProduct product,	Integer tenantId) throws Exception {

		ObjectNode prodNode = mapper.createObjectNode();

		prodNode.put("productCode", product.getProductCode());
		prodNode.put("qbProdustListID", product.getQbProductListID());
		prodNode.put("productName", product.getProductName());

		OrderItem orderItem = new OrderItem();
		Product productItem = new Product();
		productItem.setProductCode(product.getProductCode());

		orderItem.setProduct(productItem);

		String qbListID = getQBId(tenantId, product.getProductCode());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = entityHandler.getProductEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			if (qbListID == null) { //insert if not originally present
				rtnEntry = entityResource.insertEntity(prodNode, mapName);
			} else {
				rtnEntry = entityResource.updateEntity(prodNode, mapName,
						product.getProductCode());
			}
		} catch (Exception e) {
			logger.error("Error saving product in entity list during refresh all: "
					+ product.getProductCode());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
	}
	
	public void mapProductToQBInEL(ProductToMapToQuickbooks productToMapToEB,Integer tenantId) {
		
		//Just save it in entity list. User is going to retry the order anyway
		OrderItem orderItem = new OrderItem();
		Product product = new Product();
		orderItem.setProduct(product);
		product.setProductCode(productToMapToEB.getToBeMappedItemNumber());
		product.setName(productToMapToEB.getToBeMappedItemNumber());
		
		saveProductInEntityList(orderItem, productToMapToEB.getSelectedProductToMap(), tenantId);
		
		logger.debug((new StringBuilder()).append("Saved mapping of a not found item ").
				append(productToMapToEB.getToBeMappedItemNumber()).append(" to an existing qb list id ").
				append(productToMapToEB.getSelectedProductToMap()).append(" in entity list").toString());
		
	}
	
	public String getQBProductSaveXML(Integer tenantId, String productCode) throws Exception {
		JsonNode node = entityHandler.getEntity(tenantId,entityHandler.getProdctAddEntity(), productCode);
		ProductToQuickbooks productToQuickbooks = mapper.readValue(node.toString(), ProductToQuickbooks.class);
		
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		qbxmlMsgsRqType.setOnError("stopOnError");
		ItemInventoryAddRqType addRqType = new ItemInventoryAddRqType();
		
		addRqType.setRequestID(productToQuickbooks.getItemNameNumber());
		
		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						addRqType);
		ItemInventoryAdd inventoryAdd = new ItemInventoryAdd();
		addRqType.setItemInventoryAdd(inventoryAdd);
		inventoryAdd.setName(productToQuickbooks.getItemNameNumber());
		inventoryAdd.setIsActive("true");

		// TODO move these to either prop files or get these details from
		// customer
		SalesTaxCodeRef salesTax = new SalesTaxCodeRef();
		salesTax.setFullName(productToQuickbooks.getItemTaxCode()); //Get tax code from user
		inventoryAdd.setSalesTaxCodeRef(salesTax);

		IncomeAccountRef incomeAccount = new IncomeAccountRef(); // TODO get client's details
		incomeAccount.setFullName(productToQuickbooks.getItemIncomeAccount());
		inventoryAdd.setIncomeAccountRef(incomeAccount);
		
		AssetAccountRef assetAccount = new AssetAccountRef(); // TODO get client's details
		assetAccount.setFullName("Inventory Asset");
		inventoryAdd.setAssetAccountRef(assetAccount);
		
		COGSAccountRef cogsAccountRef = new COGSAccountRef();
		cogsAccountRef.setFullName(productToQuickbooks.getItemExpenseAccount()); // TODO get client's details
		inventoryAdd.setCOGSAccountRef(cogsAccountRef);

		NumberFormat numberFormat = new DecimalFormat("#.00");
		inventoryAdd.setSalesDesc(productToQuickbooks.getItemSalesDesc());
		inventoryAdd.setSalesPrice(numberFormat.format(Double.valueOf(productToQuickbooks.getItemSalesPrice())));

		return XMLHelper.getMarshalledValue(qbxml);
	}

	public String getQBProductsGetXML(final String orderId, String productCode) throws Exception {

		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		ItemQueryRqType itemQueryRqType = new ItemQueryRqType();
		itemQueryRqType.getFullName().add(productCode);
		itemQueryRqType.setRequestID(orderId);

		qbxmlMsgsRqType.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(itemQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}
	
	public String getAllQBProductsGetXML(Integer tenantId) throws Exception {

		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		ItemQueryRqType itemQueryRqType = new ItemQueryRqType();

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						itemQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}

	public void addProductToQB(Integer tenantId, ProductToQuickbooks productToQuickbooks) throws Exception {
		try {
			entityHandler.addUpdateEntity(tenantId, entityHandler.getProdctAddEntity(), productToQuickbooks.getItemNameNumber(), productToQuickbooks);
			queueManagerService.addTask(tenantId, productToQuickbooks.getItemNameNumber(), "PRODUCT", "ADD", "Add");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
