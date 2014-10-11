package com.mozu.qbintegration.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mozu.qbintegration.model.qbmodel.allgen.AccountQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesTaxCodeQueryRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.VendorQueryRqType;
import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.tasks.WorkTask;
import com.mozu.qbintegration.utils.XMLHelper;

@Component
public class QBDataHandler {

	@Autowired
	EntityHandler entityHandler;
	
	@Autowired
	QueueManagerService queueManagerService;
	
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
	
	public void processResponseXml(Integer tenantId, WorkTask workTask, String xml) throws Exception {
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
