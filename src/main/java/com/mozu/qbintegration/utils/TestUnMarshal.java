/**
 * 
 */
package com.mozu.qbintegration.utils;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.mozu.qbintegration.model.qbmodel.allgen.CustomerRef;
import com.mozu.qbintegration.model.qbmodel.allgen.ItemRef;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXMLMsgsRq;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderLineAdd;

/**
 * @author Akshay
 * 
 */
public class TestUnMarshal {

	public static void main(String[] args) {
		String testresponse = "<?xml version=\"1.0\" ?> <QBXML> <QBXMLMsgsRs> "
				+ "<CustomerAddRs statusCode=\"0\" statusSeverity=\"Info\" statusMessage=\"Status OK\"> "
				+ "<CustomerRet> <ListID>80000001-1409223517</ListID> <TimeCreated>2014-08-28T16:28:37+05:30</TimeCreated> <TimeModified>2014-08-28T16:28:37+05:30</TimeModified> <EditSequence>1409223517</EditSequence> <Name>Akshay Mahajan</Name> <FullName>Akshay Mahajan</FullName> <IsActive>true</IsActive> <Sublevel>0</Sublevel> <FirstName>Akshay</FirstName> <MiddleName>R</MiddleName> <LastName>Mahajan</LastName> <BillAddress> <Addr1>200 W 96th St</Addr1> <City>Bloomington</City> <State>MN</State> <PostalCode>55420</PostalCode> <Country>USA</Country> </BillAddress> <BillAddressBlock> <Addr1>200 W 96th St</Addr1> <Addr2>Bloomington, MN 55420</Addr2> <Addr3>USA</Addr3> </BillAddressBlock> <Phone>5555555555</Phone> <Email>akshaym@ignitiv.com</Email> <Contact>Self</Contact> <AdditionalContactRef> <ContactName>Main Phone</ContactName> <ContactValue>5555555555</ContactValue> </AdditionalContactRef> <AdditionalContactRef> <ContactName>Main Email</ContactName> <ContactValue>akshaym@ignitiv.com</ContactValue> </AdditionalContactRef> <Balance>0.00</Balance> <TotalBalance>0.00</TotalBalance> <SalesTaxCodeRef> <ListID>80000001-1408385557</ListID> <FullName>Tax</FullName> </SalesTaxCodeRef> <JobStatus>None</JobStatus> <PreferredDeliveryMethod>None</PreferredDeliveryMethod> </CustomerRet> </CustomerAddRs> </QBXMLMsgsRs> </QBXML>";
		
		NumberFormat numberFormat = new DecimalFormat("#.00");
		System.out.println(numberFormat.format(249.04));
		printRequest();
		Object value = getUnmarshalledValue(testresponse);
		System.out.println(value.getClass().getName());
	}

	private static void printRequest() {
		QBXML qbxml2 = new QBXML();
		QBXMLMsgsRq qbxmlMsgsRq = new QBXMLMsgsRq();
		qbxml2.setQBXMLMsgsRq(qbxmlMsgsRq);
		com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType salesOrderAddRqType = 
				new com.mozu.qbintegration.model.qbmodel.allgen.SalesOrderAddRqType();
		qbxmlMsgsRq.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(salesOrderAddRqType);
		qbxmlMsgsRq.setOnError("stopOnError");
		SalesOrderAdd salesOrderAdd = new SalesOrderAdd();
		salesOrderAddRqType.setSalesOrderAdd(salesOrderAdd);
		CustomerRef customerRef = new CustomerRef();
		customerRef.setListID("123123");
		customerRef.setFullName("Akshay Mahajan");
		salesOrderAdd.setCustomerRef(customerRef);
		
		ItemRef itemRef = new ItemRef();
		itemRef.setListID("12312-123123");
		itemRef.setFullName("NEW ITEM");
		SalesOrderLineAdd salesOrderLineAdd = new SalesOrderLineAdd();
		salesOrderLineAdd.setAmount("249");
		salesOrderLineAdd.setItemRef(itemRef);
		salesOrderLineAdd.setRatePercent("7.5");
		salesOrderAdd.getSalesOrderLineAddOrSalesOrderLineGroupAdd().add(salesOrderLineAdd);
		
		String qbXMLStr = null;
		try {
			JAXBContext contextObj = JAXBContext
					.newInstance(QBXML.class);
			Marshaller marshallerObj = contextObj.createMarshaller();
			StringWriter writer = new StringWriter();
			marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshallerObj.marshal(qbxml2, writer);
			qbXMLStr = writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		System.out.println(qbXMLStr);
	}

	/**
	 * Just return un-marshalled object. individual callers will get it converted
	 * since they know what they are looking for
	 * 
	 * @param testresponse
	 * @return 
	 */
	private static Object getUnmarshalledValue(String testresponse) {
		Object umValue = null;
		return umValue;
	}
}
