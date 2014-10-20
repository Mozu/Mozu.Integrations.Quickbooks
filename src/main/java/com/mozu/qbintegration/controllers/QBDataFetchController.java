/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.handlers.EntityHandler;
import com.mozu.qbintegration.handlers.ProductHandler;
import com.mozu.qbintegration.handlers.QBDataHandler;
import com.mozu.qbintegration.model.DataMapping;
import com.mozu.qbintegration.model.MozuProduct;
import com.mozu.qbintegration.model.ProductToMapToQuickbooks;
import com.mozu.qbintegration.model.ProductToQuickbooks;
import com.mozu.qbintegration.model.QBData;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.service.QuickbooksService;

/**
 * @author Akshay
 *
 */
@Controller
@RequestMapping("/api/qb")
public class QBDataFetchController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);
	
	@Autowired
	ProductHandler productHandler;
	
	@Autowired
	private QBDataHandler qbDataHandler;
	
	@Autowired
	private EntityHandler entityHandler;
	
	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	private QueueManagerService queueManagerService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "saveProductToQB", method = RequestMethod.POST)
	public @ResponseBody
	ObjectNode saveProductToQB(
			@RequestParam(value = "tenantId", required = false) Integer tenantId,
			@RequestBody String productToQuickbooksStr,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		ProductToQuickbooks productToQuickbooks = mapper.readValue(productToQuickbooksStr, ProductToQuickbooks.class);
		productHandler.addProductToQB(tenantId, productToQuickbooks );
				
		return (mapper.createObjectNode()).put("savedProduct", productToQuickbooksStr);//Saved a conversion since we already have the str
		
	}
	
	@RequestMapping(value = "getAllPostedProducts", method = RequestMethod.GET)
	public @ResponseBody
	String getAllPostedProducts(HttpServletRequest httpRequest, ModelMap model,
			@RequestParam(value = "tenantId") Integer tenantId) {

		List<MozuProduct> mozuProductDetails = quickbooksService.getMozuProductList(tenantId) ;
		
		String value = null;
		try {
			value = mapper.writeValueAsString(mozuProductDetails);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	@RequestMapping(value = "mapProductToQB", method = RequestMethod.POST)
	public @ResponseBody
	ObjectNode mapProductToQB(
			@RequestParam(value = "tenantId", required = true) Integer tenantId,
			@RequestBody ProductToMapToQuickbooks productToMapToEB,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		//User wants us to make the product code to an existing quickbooks product list id
		// SO do that mapping in EL.
		productHandler.mapProductToQBInEL(productToMapToEB, tenantId);
				
		return (mapper.createObjectNode()).put("savedProduct", mapper.writeValueAsString(productToMapToEB));
		
	}
	
	@RequestMapping(value = "initiateProductRefresh", method = RequestMethod.GET)
	public @ResponseBody String getPostedProducts(@RequestParam(value = "tenantId", required = true) Integer tenantId,  HttpServletRequest httpRequest) throws Exception {

		queueManagerService.addTask(tenantId, String.valueOf(tenantId)+"-Product", "Product", "ITEM", "Refresh");
		
		logger.debug("Saved get all items from quickbooks task at " + new Date());
		return "The request to refresh products has been scheduled.";
		
	}
	
	
	@RequestMapping(value = "initiateDataRefresh", method = RequestMethod.PUT)
	public @ResponseBody
	String initiateDataRefresh(HttpServletRequest httpRequest,
			@RequestParam(value = "tenantId") Integer tenantId, @RequestParam(value = "type") String type) throws Exception {

		qbDataHandler.refreshData(tenantId, type);
		
		logger.debug("Initiated QB data refresh at: " + new Date() + " for "+ type);
		return "The request to refresh QB "+type+" has been scheduled.";
		
	}
	
	
	@RequestMapping(value = "data", method = RequestMethod.GET)
	public @ResponseBody
	List<QBData> getQbData(
			@RequestParam(value = "tenantId", required = true) Integer tenantId,
			@RequestParam(value = "type", required = true) String type,
			final HttpServletRequest request) throws Exception {
		
		List<QBData> qbData = qbDataHandler.getData(tenantId, type);
		return qbData;
	}
	
    @RequestMapping(value = "data", method = RequestMethod.POST)
    public @ResponseBody String mapQbData(
            @RequestParam(value = "tenantId", required = true) Integer tenantId,
            @RequestBody List<DataMapping> dataMapping,
            final HttpServletRequest request) throws Exception {

        // Delete existing
        List<DataMapping> paymentData = getDataMappings(
                entityHandler.getEntityCollection(tenantId, entityHandler.getMappingEntity(), null, null, 100));
        for (DataMapping payment: paymentData) {
            entityHandler.deleteEntity(tenantId, entityHandler.getMappingEntity(), payment.getMozuId());
        }

        // Add back
        for (DataMapping mapping : dataMapping) {
            entityHandler.addUpdateEntity(tenantId,
                    entityHandler.getMappingEntity(), mapping.getMozuId(),
                    mapping);
        }

        return "Mozu to Quickbooks Payment mapping is successful.";
    }
    
	@RequestMapping(value = "getPaymentMappings", method = RequestMethod.GET)
	public @ResponseBody
	List<DataMapping> getExistingPaymentMappings(
			@RequestParam(value = "tenantId", required = true) Integer tenantId,
			final HttpServletRequest request) throws Exception {
		
		List<DataMapping> paymentData = getDataMappings(
				entityHandler.getEntityCollection(tenantId, entityHandler.getMappingEntity(), null, null, 100)); //TODO 100 is placeholder
		
		return paymentData;
	}

	private List<DataMapping> getDataMappings(List<JsonNode> entityCollection) 
			throws JsonParseException, JsonMappingException, IOException {
		List<DataMapping> paymentData = new ArrayList<DataMapping>();
		for(JsonNode singleDataMapping: entityCollection) {
			paymentData.add(mapper.readValue(singleDataMapping.toString(), DataMapping.class));
		}
		return paymentData;
	}
	
	
	
}
