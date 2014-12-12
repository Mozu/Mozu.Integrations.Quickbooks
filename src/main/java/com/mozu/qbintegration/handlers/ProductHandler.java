package com.mozu.qbintegration.handlers;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.discounts.AppliedDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.AppliedLineItemProductDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.ShippingDiscount;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.products.BundledProduct;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.MozuOrderItem;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.ProductToMapToQuickbooks;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.model.QBResponse;
import com.mozu.qbintegration.model.WorkTaskStatus;
import com.mozu.qbintegration.model.qbmodel.allgen.AssetAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.COGSAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.IncomeAccountRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemDiscountRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAddRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryAssemblyRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemNonInventoryRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemOtherChargeRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemPaymentRet;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemServiceRet;
import com.mozu.qbintegration.model.qbmodel.allgen.PrefVendorRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeRef;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;
import com.mozu.qbintegration.service.XMLService;
import com.mozu.qbintegration.tasks.WorkTask;

@Component
public class ProductHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(ProductHandler.class);
	private static ObjectMapper mapper = JsonUtils.initObjectMapper();

	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
	@Autowired
	QuickbooksService quickbooksService;

    @Autowired
    XMLService xmlHelper;
    
	public String getQBId(Integer tenantId, String productCode)
			throws Exception {
		
		String qbListID = null;
		JsonNode node = entityHandler.getEntity(tenantId,
				entityHandler.getProductEntityName(), productCode);
		if (node == null)
			return qbListID;
		JsonNode result = node.findValue("qbProdustListID");
		if (result != null) {
			qbListID = result.asText();
		}
		return qbListID;
	}
	
	private void saveProductInEntityList(ItemQueryRsType itemSearchResponse,
			Integer tenantId) throws Exception {
		List<Object> invObj = itemSearchResponse
				.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet();

		processItemQueryResult(tenantId, invObj);
	}
	
	public void processItemQueryAll(final Integer tenantId, final WorkTask workTask,
			final String qbTaskResponse) throws Exception {
		
		Thread itemQueryAllTask = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					QBXML itemSearchEle = (QBXML) xmlHelper.getUnmarshalledValue(qbTaskResponse);
					ItemQueryRsType itemSearchResponse = (ItemQueryRsType) itemSearchEle
							.getQBXMLMsgsRs()
							.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
							.get(0);
					
					List<Object> itemServiceRetCollection = itemSearchResponse
							.getItemServiceRetOrItemNonInventoryRetOrItemOtherChargeRet();
					processItemQueryResult(tenantId, itemServiceRetCollection);
					
					//Delete the refresh task when successfully completed. Below call takes care of it.
					queueManagerService.updateTask(tenantId, workTask.getId(), "Refresh",
							"COMPLETED");
				} catch (Exception ex) {
					logger.error("Could not complete product refresh from QB to entitylist for tenant: " + tenantId);
					try {
						queueManagerService.updateTask(tenantId, workTask.getId(), "Refresh",
								"COMPLETED");
					} catch (Exception e) {
						logger.error("Could not update queue task to status Pending for tenant id: " + tenantId);
					}
				}
				logger.debug("Completed syncing all products from QB to EL for tenantid: " + tenantId);
			}
		});
		
		queueManagerService.updateTask(tenantId, workTask.getId(), "Refresh",
				WorkTaskStatus.PROCESS_IN_MEM);
		itemQueryAllTask.start();
		
	}
	
	private void processItemQueryResult(Integer tenantId, List<Object> objects)
			throws Exception {
		
			for (Object object : objects) {
				try{
					boolean supported = true;
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
					} else if (object instanceof ItemOtherChargeRet) {
						ItemOtherChargeRet itemInvRet = (ItemOtherChargeRet) object;
						productName = itemInvRet.getFullName();
						productQbListID = itemInvRet.getName();
					} else if (object instanceof ItemDiscountRet) {
						ItemDiscountRet itemInvRet = (ItemDiscountRet) object;
						productName =  itemInvRet.getFullName();
						productQbListID = itemInvRet.getName();
					}else if (object instanceof ItemInventoryAssemblyRet) {
						ItemInventoryAssemblyRet itemInvRet = (ItemInventoryAssemblyRet) object;
						productName =  itemInvRet.getFullName();
						productQbListID = itemInvRet.getListID();
					} else if (object instanceof ItemPaymentRet) {
						ItemPaymentRet itemInvRet = (ItemPaymentRet) object;
						productName =  itemInvRet.getName();
						productQbListID = itemInvRet.getListID();
					} else if (object instanceof ItemNonInventoryRet) {
						ItemNonInventoryRet itemInvRet = (ItemNonInventoryRet) object;
						productName =  itemInvRet.getName();
						productQbListID = itemInvRet.getListID();
					}else {
						logger.info(object.getClass() +" not supported");
						//throw new Exception("Not supported");
						supported = false;
					}
					logger.info("Processing " + productName + " " + productQbListID + " for tenant.");
					productName = productName == null ? null : productName.replaceAll(":", "-").replaceAll("&", "and");
					productQbListID = productQbListID == null ? null : productQbListID.replaceAll(":", "-");
					if (supported) {
						MozuProduct mozuProduct = new MozuProduct();
						mozuProduct.setProductCode(productName);
						mozuProduct.setQbProductListID(productQbListID);
						mozuProduct.setProductName(productName);
						saveAllProductInEntityList(mozuProduct, tenantId);
						logger.debug("Saved product through refresh all: "+ productName);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		
	}
	
	public QBResponse processItemQuery(Integer tenantId, String qbTaskResponse)
			throws Exception {
		QBXML itemSearchEle = (QBXML) xmlHelper.getUnmarshalledValue(qbTaskResponse);
		List<Object> results = itemSearchEle.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
		//boolean foundAllItems = true;
		QBResponse qbResponse = new QBResponse();
		for(Object obj : results) {
			ItemQueryRsType itemSearchResponse = (ItemQueryRsType)obj;
			if (500 == itemSearchResponse.getStatusCode().intValue()
					&& "warn".equalsIgnoreCase(itemSearchResponse
							.getStatusSeverity())) {
				qbResponse.setStatusCode(itemSearchResponse.getStatusCode());
				qbResponse.setStatusSeverity(itemSearchResponse.getStatusSeverity());
				qbResponse.setStatusMessage(itemSearchResponse.getStatusMessage());
			} else {
				saveProductInEntityList(itemSearchResponse, tenantId);
			}
		}

		if (StringUtils.isEmpty(qbResponse.getStatusMessage())) {
			qbResponse.setStatusCode(BigInteger.ZERO);
			qbResponse.setStatusMessage("Status OK");
			qbResponse.setStatusSeverity("Info");
		}
		
		return qbResponse;
	}
	
	public void processItemAdd(Integer tenantId, WorkTask workTask,
			String qbTaskResponse) throws Exception {
		QBXML itemAddEle = (QBXML) xmlHelper.getUnmarshalledValue(qbTaskResponse);

		ItemInventoryAddRsType invAddResponse = (ItemInventoryAddRsType) itemAddEle
				.getQBXMLMsgsRs()
    			.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
    			.get(0);
		
		JsonNode node = entityHandler.getEntity(tenantId,
				entityHandler.getProdctAddEntity(), workTask.getId());
		ProductToQuickbooks productInQuickBooks = mapper.readValue(
				node.toString(), ProductToQuickbooks.class);
		productInQuickBooks.setStatus(invAddResponse.getStatusSeverity());
		productInQuickBooks.setErrorMessage(invAddResponse.getStatusMessage());

		if (!invAddResponse.getStatusSeverity().equalsIgnoreCase("error")) {
			String itemListId = invAddResponse.getItemInventoryRet()
					.getListID();
			// Save the item list id in entity list
			OrderItem item = new OrderItem();
			Product product = new Product();
			item.setProduct(product);
			product.setProductCode(invAddResponse.getItemInventoryRet()
					.getFullName());
			product.setName(invAddResponse.getItemInventoryRet().getName());
			saveProductInEntityList(item, itemListId, tenantId);
	
			logger.debug("Added new product to quickbooks: "
					+ invAddResponse.getItemInventoryRet().getName());
		}
		
		entityHandler.updateEntity(tenantId,
				entityHandler.getProdctAddEntity(), workTask.getId(),
				productInQuickBooks);
		queueManagerService.updateTask(tenantId, workTask.getId(), "ADD",
				"COMPLETED");
	}
	
	private void saveProductInEntityList(OrderItem orderItem,
			String qbProdustListID, Integer tenantId) {

		JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
		ObjectNode custNode = nodeFactory.objectNode();

		custNode.put("productCode", orderItem.getProduct().getProductCode());
		custNode.put("qbProdustListID", qbProdustListID);
		custNode.put("productName", orderItem.getProduct().getName());

		// Add the mapping entry
		JsonNode rtnEntry = null;
		String mapName = entityHandler.getProductEntityName();
		EntityResource entityResource = new EntityResource(new MozuApiContext(
				tenantId));
		try {
			List<JsonNode> existing = entityHandler.getEntityCollection(
					tenantId, mapName, "productCode eq "
							+ orderItem.getProduct().getProductCode(), null, 1);
			if (existing.size() == 0)
				rtnEntry = entityResource.insertEntity(custNode, mapName);
			else
				rtnEntry = entityResource.updateEntity(custNode, mapName,
						orderItem.getProduct().getProductCode());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error saving product in entity list: "
					+ orderItem.getProduct().getProductCode());
		}
		logger.debug("Retrieved entity: " + rtnEntry);
		logger.debug("Returning");
	}

	public void saveAllProductInEntityList(MozuProduct product, Integer tenantId)
			throws Exception {

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
	
	public void mapProductToQBInEL(ProductToMapToQuickbooks productToMapToEB,
			Integer tenantId) throws Exception {
		
		//Just save it in entity list. User is going to retry the order anyway
		OrderItem orderItem = new OrderItem();
		Product product = new Product();
		orderItem.setProduct(product);
		product.setProductCode(productToMapToEB.getToBeMappedItemNumber());
		product.setName(productToMapToEB.getToBeMappedItemNumber());
		
		//Get the qbListId since the field on the UI is textbox, not dropdown anymore.
		List<JsonNode> items = entityHandler.getEntityCollection(tenantId, entityHandler.getProductEntityName(), 
				"productCode eq " + productToMapToEB.getSelectedProductToMap());
		
		if(!items.isEmpty()) {
			String qbProdustListID = ((JsonNode) items.get(0)).get("qbProdustListID").asText();
			saveProductInEntityList(orderItem, qbProdustListID, tenantId);
			
			logger.debug((new StringBuilder())
					.append("Saved mapping of a not found item ")
					.append(productToMapToEB.getToBeMappedItemNumber())
					.append(" to an existing qb list id ")
					.append(qbProdustListID)
					.append(" in entity list").toString());
		} else {
			throw new Exception("Did not find product code " + productToMapToEB.getSelectedProductToMap());
		}
	}
	
	public String getQBProductSaveXML(Integer tenantId, String productCode)
			throws Exception {
		JsonNode node = entityHandler.getEntity(tenantId,
				entityHandler.getProdctAddEntity(), productCode);
		ProductToQuickbooks productToQuickbooks = mapper.readValue(
				node.toString(), ProductToQuickbooks.class);
		
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
		salesTax.setFullName(productToQuickbooks.getItemTaxCode()); // Get tax
																	// code from
																	// user
		inventoryAdd.setSalesTaxCodeRef(salesTax);

		IncomeAccountRef incomeAccount = new IncomeAccountRef(); // TODO get
																	// client's
																	// details
		incomeAccount.setFullName(productToQuickbooks.getItemIncomeAccount());
		inventoryAdd.setIncomeAccountRef(incomeAccount);
		
		AssetAccountRef assetAccount = new AssetAccountRef(); // TODO get
																// client's
																// details
		assetAccount.setFullName("Inventory Asset");
		inventoryAdd.setAssetAccountRef(assetAccount);
		
		COGSAccountRef cogsAccountRef = new COGSAccountRef();
		cogsAccountRef.setFullName(productToQuickbooks.getItemExpenseAccount()); // TODO
																					// get
																					// client's
																					// details
		inventoryAdd.setCOGSAccountRef(cogsAccountRef);

		NumberFormat numberFormat = new DecimalFormat("#.00");
		inventoryAdd.setSalesDesc(productToQuickbooks.getItemSalesDesc());
		inventoryAdd.setSalesPrice(numberFormat.format(Double
				.valueOf(productToQuickbooks.getItemSalesPrice())));
		
		//Akshay: Set purchase information
		inventoryAdd.setPurchaseDesc(productToQuickbooks.getItemPurchaseDesc());
		inventoryAdd.setPurchaseCost(numberFormat.format(Double
				.valueOf(productToQuickbooks.getItemPurchaseCost())));
		
		//Akshay 19-nov-2014 - pref vendor add - was never added I guess
		if(!StringUtils.isEmpty(productToQuickbooks.getSelectedVendor())) {
			PrefVendorRef prefVendor = new PrefVendorRef();
			prefVendor.setFullName(productToQuickbooks.getSelectedVendor());
			inventoryAdd.setPrefVendorRef(prefVendor);
		}

		return xmlHelper.getMarshalledValue(qbxml);
	}

	public String getQBProductsGetXML(Integer tenantId, Order order)
			throws Exception {

		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		List<MozuOrderItem> productCodes = getProductCodes(tenantId, order,true);
		List<String> existing = new ArrayList<String>();
		for(MozuOrderItem orderItem : productCodes) {
			//if (!StringUtils.isEmpty(orderItem.getQbItemCode()))
			//	continue;
			if (!existing.contains(orderItem.getProductCode())) { //eliminate duplicate query
				ItemQueryRqType itemQueryRqType = new ItemQueryRqType();
				itemQueryRqType.getFullName().add(orderItem.getProductCode());	
				itemQueryRqType.setRequestID(order.getId());
				
				qbxmlMsgsRqType
						.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
						.add(itemQueryRqType);
				existing.add(orderItem.getProductCode());
			}
		}
		return xmlHelper.getMarshalledValue(qbxml);
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

		return xmlHelper.getMarshalledValue(qbxml);
	}

	public void addProductToQB(Integer tenantId,
			ProductToQuickbooks productToQuickbooks) throws Exception {
		try {
			entityHandler.addUpdateEntity(tenantId,
					entityHandler.getProdctAddEntity(),
					productToQuickbooks.getItemNameNumber(),
					productToQuickbooks);
			queueManagerService.addTask(tenantId,
					productToQuickbooks.getItemNameNumber(), "PRODUCT", "ADD",
					"Add");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/*public List<MozuOrderItem> getProductCodes(Order order) throws Exception {
		return getProductCodes(0, order, false);
	}*/
	
	public List<MozuOrderItem> getProductCodes(Integer tenantId, Order order,
			boolean queryQBProduct) throws Exception {
		List<MozuOrderItem> productCodes = new ArrayList<MozuOrderItem>();
		
		String qbDiscProductCode = null;
		String shippingProductCode = null;
		String giftCardProductCode = null;
		
		GeneralSettings settings = quickbooksService
				.getSettingsFromEntityList(tenantId);

		if (queryQBProduct) {
			if (StringUtils.isNotEmpty(settings.getDiscountProductCode()))
				qbDiscProductCode = getQBId(tenantId, settings.getDiscountProductCode());

			if (StringUtils.isNotEmpty(settings.getShippingProductCode()))
				shippingProductCode = getQBId(tenantId,	settings.getShippingProductCode());
			
			if (StringUtils.isNotEmpty(settings.getGiftCardProductCode()))
				giftCardProductCode = getQBId(tenantId,	settings.getGiftCardProductCode());
			
		}
		
		double qbDiscount = 0.0; // this sums up all item discounts
		for(OrderItem item : order.getItems()) {
			
			String productCode = null;
			
			if (!StringUtils.isEmpty(item.getProduct()
					.getVariationProductCode()))
				productCode = item.getProduct().getVariationProductCode();	
			else
				productCode = item.getProduct().getProductCode();
	
			MozuOrderItem mzItem = new MozuOrderItem();
			mzItem.setProductCode(productCode);
			mzItem.setDescription(item.getProduct().getName());
			if (queryQBProduct)
				mzItem.setQbItemCode(this.getQBId(tenantId, productCode));
			
			if(item.getUnitPrice().getSaleAmount() != null) {
				mzItem.setAmount(item.getUnitPrice().getSaleAmount());
			} else {
				mzItem.setAmount(item.getUnitPrice().getListAmount());
			}
			
			String taxCode = null;
			if (item.getItemTaxTotal() != null && item.getItemTaxTotal() > 0.0) 
				taxCode = "Tax";
			else
				taxCode = "Non";
			mzItem.setTaxCode(taxCode);
			
			mzItem.setQty(item.getQuantity());
			
			productCodes.add(mzItem);
			
			//Add bundles components as separate line items with 0.00 value
			if (item.getProduct().getBundledProducts() != null
					&& item.getProduct().getBundledProducts().size() > 0) {
				for (BundledProduct bProduct : item.getProduct()
						.getBundledProducts()) {
					mzItem = new MozuOrderItem();

					mzItem.setProductCode(bProduct.getProductCode());
					if (queryQBProduct)
						mzItem.setQbItemCode(getQBId(tenantId,bProduct.getProductCode()));
					mzItem.setDescription(bProduct.getName());
					mzItem.setAmount(0.0);
					mzItem.setTaxCode(taxCode);
					mzItem.setQty(item.getQuantity()*bProduct.getQuantity());
					productCodes.add(mzItem);
				}
			}
			
			if (item.getDiscountTotal() > 0.0	&& StringUtils.isNotEmpty(settings.getDiscountProductCode())) {
				for(AppliedLineItemProductDiscount discount : item.getProductDiscounts()) {
					qbDiscount += discount.getImpact();
				}
			}
		}
		
		String taxCode = "Non";
		if (order.getTaxTotal() > 0.0)
			taxCode = "Tax";
		
		if (order.getShippingSubTotal() > 0.0	&& StringUtils.isNotEmpty(settings.getShippingProductCode())) {
			MozuOrderItem mzItem = new MozuOrderItem();
			mzItem.setProductCode(settings.getShippingProductCode());
			mzItem.setQbItemCode(shippingProductCode);
			mzItem.setAmount(order.getShippingSubTotal()+ (order.getShippingTaxTotal() > 0 ? order.getShippingTaxTotal() : 0));
			mzItem.setMisc(true);
			mzItem.setTaxCode("Non");
			productCodes.add(mzItem);
		}
		
		if(order.getDiscountTotal() != null && order.getDiscountTotal() > 0.0) { //Akshay 10-Oct-2014 -- add order level disc
			for (AppliedDiscount disc: order.getOrderDiscounts()) {
				qbDiscount += disc.getImpact();
			}
		}
		
		if (order.getAdjustment() != null
				&& StringUtils.isNotEmpty(settings.getDiscountProductCode())) {
			qbDiscount += -(order.getAdjustment().getAmount());
			
		}
		
		if (StringUtils.isNotEmpty(settings.getDiscountProductCode()) && order.getShippingDiscounts() != null) {
			for(ShippingDiscount discount : order.getShippingDiscounts()) {
				qbDiscount += discount.getDiscount().getImpact();
			}
		}

		if (order.getShippingAdjustment() != null
				&& StringUtils.isNotEmpty(settings.getDiscountProductCode())) {
			qbDiscount += -(order.getShippingAdjustment().getAmount());
		}
		
		if (qbDiscount != 0.0) {
			MozuOrderItem mzItem = new MozuOrderItem();
			mzItem.setProductCode(settings.getDiscountProductCode());
			mzItem.setQbItemCode(qbDiscProductCode);
			mzItem.setAmount(qbDiscount);
			mzItem.setMisc(true);
			mzItem.setTaxCode(taxCode);
			productCodes.add(mzItem);
		}
		
		//Get store credit product
		if (StringUtils.isNoneEmpty(settings.getGiftCardProductCode())) {
			for(Payment payment : order.getPayments()) {
				if (payment.getBillingInfo().getPaymentType().equalsIgnoreCase("storecredit") && payment.getStatus().equalsIgnoreCase("collected")) {
					MozuOrderItem mzItem = new MozuOrderItem();
					mzItem.setProductCode(settings.getGiftCardProductCode());
					mzItem.setQbItemCode(giftCardProductCode);
					mzItem.setAmount(payment.getAmountCollected());
					mzItem.setMisc(true);
					mzItem.setDescription(payment.getBillingInfo().getStoreCreditCode());
					//mzItem.setTaxCode("Non");
					productCodes.add(mzItem);
				}
			}
		}
		
		return productCodes;
	}
}

