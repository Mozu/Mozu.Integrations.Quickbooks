/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.QuickWebConnector;
import com.mozu.qbintegration.model.Scheduler;
import com.mozu.qbintegration.service.QuickbooksService;

/**
 * @author Akshay
 * 
 */
@Controller
public class GeneralSettingsController {

	@Autowired
	private QuickbooksService quickbooksService;

	@RequestMapping(value = "/getgeneralsettings", method = RequestMethod.GET)
	public @ResponseBody
	GeneralSettings getGeneralSettings(
			@RequestParam(value = "tenantId", required = false) Integer tenantId) {

		GeneralSettings generalSettings = quickbooksService
				.getSettingsFromEntityList(tenantId);
		return generalSettings;

	}

	@RequestMapping(value = "/generalsettings", method = RequestMethod.POST)
	public @ResponseBody
	ObjectNode saveGeneralSettings(
			@RequestParam(value = "tenantId", required = false) Integer tenantId,
			@RequestBody GeneralSettings generalSettings,
			HttpServletResponse response) {

		quickbooksService.saveOrUpdateSettingsInEntityList(generalSettings,
				tenantId);
		
		QuickWebConnector quickWebCon = new QuickWebConnector();
		quickWebCon.setId(100);
		quickWebCon.setName("HTTPWebService");
		quickWebCon.setUrl(generalSettings.getWsURL());
		quickWebCon.setDescription("A short description for WCWebService1");
		quickWebCon
				.setSupport("http://localhost:8080/quickbooks-integration-0.0.1-LocalBuild/quickbooks.wsdl");
		quickWebCon.setUserName(generalSettings.getQbAccount());
		quickWebCon.setFileId("{90A44FB5-33D9-4815-AC85-BC87A7E7D1EB}");
		quickWebCon.setOwnerId("{57F3B9B1-86F1-4fcc-B1EE-566DE1813D20}");
		quickWebCon.setQbType("QBFS");
		Scheduler scheduler = new Scheduler();
		scheduler.setRun(1);
		quickWebCon.setScheduler(scheduler);

		String fileContent = null;
		try {

			JAXBContext jaxbContext = JAXBContext
					.newInstance(QuickWebConnector.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(quickWebCon, writer);

			fileContent = writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
		ObjectNode node = jsonNodeFactory.objectNode();
		node.put("qbxml", fileContent);
		return node;
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public void download(@RequestParam(value="qwcfilestr", required=false) String fileContent,
			HttpServletResponse response) throws Exception {

		response.setContentType("application/xml"); // APPLICATION/OCTET-STREAM
		response.setContentLength((int) fileContent.length());
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ "quickbooksservice.qwc" + "\"");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}

	}

}
