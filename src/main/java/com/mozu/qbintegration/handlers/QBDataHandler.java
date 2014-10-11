package com.mozu.qbintegration.handlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.qbintegration.model.CreateProductSetupData;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountQueryRsType;
import com.mozu.qbintegration.model.qbmodel.allgen.AccountRet;
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
	
	private static final Logger logger = LoggerFactory
			.getLogger(QBDataHandler.class);

	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
	/**
	 * Slot a task for pulling accounts from QB
	 * @param tenantId
	 * @throws Exception
	 */
	public void initiateAccountDataFetch(Integer tenantId) throws Exception {
		queueManagerService.addTask(tenantId, "Account", "DataSync", "QUERY", "GETACCOUNTS");
	}
	
	/**
	 * Slot a task for pulling vendors from QB
	 * @param tenantId
	 * @throws Exception
	 */
	public void initiateVendorDataFetch(Integer tenantId) throws Exception {
		queueManagerService.addTask(tenantId, "Vendor", "DataSync", "QUERY", "GETVENDORS");
	}
	
	/**
	 * Slot a task for pulling sales tax from QB
	 * @param tenantId
	 * @throws Exception
	 */
	public void initiateSalesTaxDataFetch(Integer tenantId) throws Exception {
		queueManagerService.addTask(tenantId, "SalesTaxCode", "DataSync", "QUERY", "GETSALESTAXCODES");
	}
	
	public String getRequestXml(String action) throws Exception {
		switch(action.toLowerCase()) {
			case "getaccounts":
				return getAccountQueryXml();
			case "getvendors":
				return getVendorQueryXml();
			case "getsalestaxcodes":
				return getSalesTaxCodeQueryXml();
			default:
				throw new Exception("Not supported");
		}
	}
	
	public void processResponseXml(Integer tenantId, String action, String respXml) throws Exception {
		switch(action.toLowerCase()) {
			case "getaccounts":
				processAccountQueryXml(tenantId, respXml);
				break;
			case "getvendors":
				processVendorQueryXml(tenantId, respXml);
				break;
			case "getsalestaxcodes":
				processSalesTaxCodeQueryXml(tenantId, respXml);
				break;
			default:
				throw new Exception("Not supported");
		}
	}
	
	public void processResponseXml(Integer tenantId, WorkTask workTask, String xml) throws Exception {
		logger.debug("Received response: " + xml);
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
		CreateProductSetupData singleAcctData = null;
		for (AccountRet singleAcct : returnedAccts) {
			singleAcctData = new CreateProductSetupData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			singleAcctData.setId(singleAcct.getListID() + singleAcct.getName());
			singleAcctData.setType("ACCOUNT");
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
		CreateProductSetupData singleAcctData = null;
		for (VendorRet singleVendor : returnedVendor) {
			singleAcctData = new CreateProductSetupData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			singleAcctData.setId(singleVendor.getListID() + singleVendor.getName());
			singleAcctData.setType("VENDOR");
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
		CreateProductSetupData singleAcctData = null;
		for (SalesTaxCodeRet singleSalesTax : returnedSalesTaxCodes) {
			singleAcctData = new CreateProductSetupData();
			//Concatenate name since it's composite -- quickbooks has been seen to have same listIDs for diff records
			//search will happen on this composite and
			// returned value's fullName will be used.
			singleAcctData.setId(singleSalesTax.getListID() + singleSalesTax.getName());
			singleAcctData.setType("TAXCODE");
			singleAcctData.setFullName(singleSalesTax.getName());
			
			//Save only as much required - also update if available already
			entityHandler.addUpdateEntity(tenantId, entityHandler.getLookupEntity(), singleAcctData.getId(), singleAcctData);
		}
		logger.debug("Saved all tax codes in EL for tenantId: " + tenantId);
	}
	
	
	
	/*public String getIncomeAccountQueryXml() throws Exception {
		QBXML qbxml = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRqType = new QBXMLMsgsRq();

		qbxmlMsgsRqType.setOnError("stopOnError");
		qbxml.setQBXMLMsgsRq(qbxmlMsgsRqType);
		IncomeAccountQueryRqType itemQueryRqType = new IncomeAccountQueryRqType();

		qbxmlMsgsRqType
				.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						itemQueryRqType);

		return XMLHelper.getMarshalledValue(qbxml);
	}*/
}
