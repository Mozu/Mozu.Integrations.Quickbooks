package com.mozu.qbintegration.handlers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.utils.JsonUtils;
import com.mozu.qbintegration.model.DataActions;
import com.mozu.qbintegration.model.DataMapping;
import com.mozu.qbintegration.model.QBData;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountRet;
import com.mozu.qbintegration.model.qbmodel.allgen.PaymentMethodQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.PaymentMethodQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.PaymentMethodRet;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeRet;
import com.mozu.qbintegration.model.qbmodel.allgen.VendorQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.VendorQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.VendorRet;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.XMLHelper;

@Component
public class QBDataHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(QBDataHandler.class);

	
	private ObjectMapper mapper = JsonUtils.initObjectMapper();
	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
	public void refreshAllData(Integer tenantId) throws Exception {
		refreshData(tenantId, "account");
		refreshData(tenantId, "vendor");
		refreshData(tenantId, "salestaxcode");
		refreshData(tenantId, "paymentmethod");
	}
	
	public void refreshData(Integer tenantId, String id) throws Exception {
		String type = "DataSync";
		String currentStep = "QUERY";
		String action = "";
		switch(id.toLowerCase()) {
			case "account":
				action =  DataActions.GETACCOUNTS;
				break;
			case "vendor":
				action =  DataActions.GETVENDORS;
				break;
			case "salestaxcode":
				action = DataActions.GETSALESTAXCODES;
				break;
			case "paymentmethod":
				action = DataActions.GETPAYMENTMETHODS;
				break;
			default:
				throw new Exception("Not Implemented");
		}
		
		queueManagerService.addTask(tenantId, id, type, currentStep, action);
	}
			
	
	public String getRequestXml(String action) throws Exception {
		if (action.equalsIgnoreCase(DataActions.GETACCOUNTS)) {
			return getAccountQueryXml();
		} else if (action.equalsIgnoreCase(DataActions.GETVENDORS)) {
			return getVendorQueryXml();
		} else if (action.equalsIgnoreCase(DataActions.GETSALESTAXCODES)) {
			return getSalesTaxCodeQueryXml();
		} else if (action.equalsIgnoreCase(DataActions.GETPAYMENTMETHODS)) {
			return getPaymentMethodQueryXml();
		} else
			throw new Exception("Not implemented");
		
	}
	
	public void processResponseXml(Integer tenantId, String action, String respXml) throws Exception {
		if (action.equalsIgnoreCase(DataActions.GETACCOUNTS)) {
			processAccountQueryXml(tenantId, respXml);
		} else if (action.equalsIgnoreCase(DataActions.GETVENDORS)) {
			processVendorQueryXml(tenantId, respXml);
		} else if (action.equalsIgnoreCase(DataActions.GETSALESTAXCODES)) {
			processSalesTaxCodeQueryXml(tenantId, respXml);
		} else if (action.equalsIgnoreCase(DataActions.GETPAYMENTMETHODS)) {
			 processPaymentMethodQueryResponse(tenantId, respXml);
		} else
			throw new Exception("Not implemented");

	}
	
	public void processResponseXml(Integer tenantId, WorkTask workTask, String xml) throws Exception {
		processResponseXml(tenantId, workTask.getAction(), xml);
		queueManagerService.updateTask(tenantId, workTask.getId(), workTask.getCurrentStep(), "COMPLETED");
	}
	
	private String getAccountQueryXml() throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		AccountQueryRqType accountQueryRqType = new AccountQueryRqType();
		
		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						accountQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}
	
	private void processAccountQueryXml(Integer tenantId, String respXml) throws Exception {
		QBXML accountQueryResp = (QBXML) XMLHelper.getUnmarshalledValue(respXml);
		
		AccountQueryRsType accountResponse = (AccountQueryRsType) accountQueryResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);
		
		List<AccountRet> returnedAccts = accountResponse.getAccountRet();
		QBData singleAcctData = null;
		for (AccountRet singleAcct : returnedAccts) {
			singleAcctData = new QBData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			//singleAcctData.setId(singleAcct.getListID() + singleAcct.getName().replace(" ", ""));
			singleAcctData.setId(singleAcct.getListID());
			singleAcctData.setDataType("account");
			singleAcctData.setFullName(singleAcct.getFullName());
			
			//Save only as much required - also update if available already
			entityHandler.addUpdateEntity(tenantId, entityHandler.getLookupEntity(), singleAcctData.getId(), singleAcctData);
		}
		logger.debug("Saved all accounts in EL for tenantId: " + tenantId);
	}
	
	private String getVendorQueryXml() throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		VendorQueryRqType vendorQueryRqType = new VendorQueryRqType();

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						vendorQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}
	
	private void processVendorQueryXml(Integer tenantId, String respXml) throws Exception {
		QBXML accountQueryResp = (QBXML) XMLHelper.getUnmarshalledValue(respXml);
		
		VendorQueryRsType vendorResponse = (VendorQueryRsType) accountQueryResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);
		
		List<VendorRet> returnedVendor = vendorResponse.getVendorRet();
		QBData singleAcctData = null;
		for (VendorRet singleVendor : returnedVendor) {
			singleAcctData = new QBData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			//singleAcctData.setId(singleVendor.getListID() + singleVendor.getName());
			singleAcctData.setId(singleVendor.getListID());
			singleAcctData.setDataType("vendor");
			singleAcctData.setFullName(singleVendor.getName());
			
			//Save only as much required - also update if available already
			entityHandler.addUpdateEntity(tenantId, entityHandler.getLookupEntity(), singleAcctData.getId(), singleAcctData);
		}
		logger.debug("Saved all vendors in EL for tenantId: " + tenantId);
	}
	
	private String getSalesTaxCodeQueryXml() throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		SalesTaxCodeQueryRqType salesTaxCodeQueryRqType = new SalesTaxCodeQueryRqType();

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						salesTaxCodeQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}
	
	private void processSalesTaxCodeQueryXml(Integer tenantId, String respXml) throws Exception {
		QBXML accountQueryResp = (QBXML) XMLHelper.getUnmarshalledValue(respXml);
		
		SalesTaxCodeQueryRsType salesTaxCodeResp = (SalesTaxCodeQueryRsType) accountQueryResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);
		
		List<SalesTaxCodeRet> returnedSalesTaxCodes = salesTaxCodeResp.getSalesTaxCodeRet();
		QBData singleAcctData = null;
		for (SalesTaxCodeRet singleSalesTax : returnedSalesTaxCodes) {
			singleAcctData = new QBData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			//singleAcctData.setId(singleSalesTax.getListID() + singleSalesTax.getName());
			singleAcctData.setId(singleSalesTax.getListID());
			singleAcctData.setDataType("taxcode");
			singleAcctData.setFullName(singleSalesTax.getName());
			
			//Save only as much required - also update if available already
			entityHandler.addUpdateEntity(tenantId, entityHandler.getLookupEntity(), singleAcctData.getId(), singleAcctData);
		}
		logger.debug("Saved all tax codes in EL for tenantId: " + tenantId);
	}
	
	private String getPaymentMethodQueryXml() throws Exception {
		QBXML qbxml = getQBXML();
		PaymentMethodQueryRqType salesTaxCodeQueryRqType = new PaymentMethodQueryRqType();

		qbxml.getQBXMLMsgsRq()
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						salesTaxCodeQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}
	
	private void processPaymentMethodQueryResponse(Integer tenantId, String respXml) throws Exception {
		QBXML accountQueryResp = (QBXML) XMLHelper.getUnmarshalledValue(respXml);
		
		PaymentMethodQueryRsType salesTaxCodeResp = (PaymentMethodQueryRsType) accountQueryResp
				.getQBXMLMsgsRs()
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs()
				.get(0);
		
		List<PaymentMethodRet> paymentMethods = salesTaxCodeResp.getPaymentMethodRet();
		QBData singleAcctData = null;
		for (PaymentMethodRet paymentMethod : paymentMethods) {
			singleAcctData = new QBData();
			singleAcctData.setId(paymentMethod.getListID());
			singleAcctData.setDataType("paymentmethod");
			singleAcctData.setFullName(paymentMethod.getName());
			
			//Save only as much required - also update if available already
			entityHandler.addUpdateEntity(tenantId, entityHandler.getLookupEntity(), singleAcctData.getId(), singleAcctData);
		}
		logger.debug("Saved all tax codes in EL for tenantId: " + tenantId);
	}
	
	public List<QBData> getData(Integer tenantId, String type) throws Exception {
		List<JsonNode> nodes = entityHandler.getEntityCollection(tenantId, entityHandler.getLookupEntity(), "dataType eq "+type, "fullName", 200);
		List<QBData> data = new ArrayList<QBData>();
		for(JsonNode node : nodes) {
			data.add(mapper.readValue(node.toString(), QBData.class));
		}
		
		return data;
	}
	
	public DataMapping getMapping(Integer tenantId, String id, String type) throws Exception {
		JsonNode node = entityHandler.getEntity(tenantId, entityHandler.getMappingEntity(), id);
		if (node == null) return null;
		return mapper.readValue(node.toString(), DataMapping.class);
	}
	
	private QBXML getQBXML() {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		return qbxml;
	}
}
