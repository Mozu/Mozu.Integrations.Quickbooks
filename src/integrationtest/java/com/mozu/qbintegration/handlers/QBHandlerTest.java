package com.mozu.qbintegration.handlers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mozu.qbintegration.service.QueueManagerService;
import com.mozu.qbintegration.tasks.WorkTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/quickbooks/servlet-context.xml" })
public class QBHandlerTest {

	@Autowired
	private QueueManagerService queueManagerService;
	
	@Autowired
	private QBHandler qbHandler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void updateOrdertest() {
		try {
			WorkTask workTask = new WorkTask();
			workTask.setQbTaskType("order_update");
			workTask.setQbTaskRequest("<?xml version=\"1.0\" encoding=\"utf-8\"?><?qbxml version=\"13.0\"?><QBXML><QBXMLMsgsRq onError=\"stopOnError\"><SalesOrderModRq requestID=\"0524e2ae157c2808e0a95c7f0000119c\"><SalesOrderMod><TxnID>7D-1412309365</TxnID><EditSequence>1412309365</EditSequence><CustomerRef><ListID>80000001-1411680931</ListID></CustomerRef><SalesOrderLineMod><TxnLineID>-1</TxnLineID><ItemRef><ListID>80000003-1411679496</ListID></ItemRef><Amount>2.80</Amount></SalesOrderLineMod></SalesOrderMod></SalesOrderModRq></QBXMLMsgsRq></QBXML>");
			workTask.setQbTaskResponse("<?xml version=\"1.0\" ?>\n<QBXML>\n<QBXMLMsgsRs>\n<SalesOrderModRs requestID=\"0524e2ae157c2808e0a95c7f0000119c\" statusCode=\"0\" statusSeverity=\"Info\" statusMessage=\"Status OK\">\n<SalesOrderRet>\n<TxnID>7D-1412309365</TxnID>\n<TimeCreated>2014-10-02T23:09:25-06:00</TimeCreated>\n<TimeModified>2014-10-03T08:28:40-06:00</TimeModified>\n<EditSequence>1412342920</EditSequence>\n<TxnNumber>32</TxnNumber>\n<CustomerRef>\n<ListID>80000001-1411680931</ListID>\n<FullName>S M</FullName>\n</CustomerRef>\n<TemplateRef>\n<ListID>80000008-1411582726</ListID>\n<FullName>Custom Sales Order</FullName>\n</TemplateRef>\n<TxnDate>2014-09-30</TxnDate>\n<RefNumber>32</RefNumber>\n<BillAddress>\n<Addr1>1835 Kramer Ln</Addr1>\n<City>Austin</City>\n<State>TX</State>\n<PostalCode>78758</PostalCode>\n<Country>USA</Country>\n</BillAddress>\n<BillAddressBlock>\n<Addr1>1835 Kramer Ln</Addr1>\n<Addr2>Austin, TX 78758</Addr2>\n<Addr3>US</Addr3>\n</BillAddressBlock>\n<DueDate>2014-09-30</DueDate>\n<ShipDate>2014-09-30</ShipDate>\n<Subtotal>2.80</Subtotal>\n<ItemSalesTaxRef>\n<ListID>80000004-1411735996</ListID>\n<FullName>goulet sales tax</FullName>\n</ItemSalesTaxRef>\n<SalesTaxPercentage>8.25</SalesTaxPercentage>\n<SalesTaxTotal>0.23</SalesTaxTotal>\n<TotalAmount>3.03</TotalAmount>\n<IsManuallyClosed>false</IsManuallyClosed>\n<IsFullyInvoiced>false</IsFullyInvoiced>\n<IsToBePrinted>true</IsToBePrinted>\n<IsToBeEmailed>false</IsToBeEmailed>\n<CustomerSalesTaxCodeRef>\n<ListID>80000001-1411582727</ListID>\n<FullName>Tax</FullName>\n</CustomerSalesTaxCodeRef>\n<SalesOrderLineRet>\n<TxnLineID>81-1412309365</TxnLineID>\n<ItemRef>\n<ListID>80000003-1411679496</ListID>\n<FullName>Ap-CD10-BK</FullName>\n</ItemRef>\n<Rate>2.8</Rate>\n<Amount>2.80</Amount>\n<SalesTaxCodeRef>\n<ListID>80000001-1411582727</ListID>\n<FullName>Tax</FullName>\n</SalesTaxCodeRef>\n<Invoiced>0</Invoiced>\n<IsManuallyClosed>false</IsManuallyClosed>\n</SalesOrderLineRet>\n</SalesOrderRet>\n</SalesOrderModRs>\n</QBXMLMsgsRs>\n</QBXML>\n");
			workTask.setTaskId("0524e2ae157c2808e0a95c7f0000119c");
			workTask.setEnteredTime(Long.parseLong("1412342901052"));
			workTask.setSiteId(7343);
			workTask.setTenantId(4508);
			
			qbHandler.processOrderUpdate(workTask.getTenantId(), workTask);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
