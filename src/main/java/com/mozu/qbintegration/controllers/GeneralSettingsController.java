
/**
 * 
 */
package com.mozu.qbintegration.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.base.handlers.EncryptDecryptHandler;
import com.mozu.qbintegration.model.GeneralSettings;
import com.mozu.qbintegration.model.QuickWebConnector;
import com.mozu.qbintegration.model.Scheduler;
import com.mozu.qbintegration.service.QuickbooksService;

/**
 * @author Akshay
 * 
 */
@Controller
@RequestMapping("/api/config")
public class GeneralSettingsController implements ServletContextAware {

	@Autowired
	private QuickbooksService quickbooksService;
	
	@Autowired
	private EncryptDecryptHandler encryptDecryptHandler;
	
	private static final Logger logger = LoggerFactory
			.getLogger(GeneralSettingsController.class);

	
	@Value("${webserviceName}")
	private String webserviceName;
	
	@Value("${webserviceDesc}")
	private String webserviceDesc;
	
	@Value("${wsdlFileName}")
	private String wsdlFileName;

	
	private ServletContext context;
	    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
	
	@RequestMapping(value = "settings", method = RequestMethod.GET)
	public @ResponseBody
	GeneralSettings getGeneralSettings(@RequestParam(value = "tenantId", required = false) Integer tenantId, final HttpServletRequest request) throws Exception {

		GeneralSettings generalSettings = quickbooksService.getSettingsFromEntityList(tenantId);
		
		if (generalSettings == null)
			generalSettings = new GeneralSettings();
		 
		generalSettings.setWsURL( getSoapUrl(request) );
		return generalSettings;

	}
	
	@RequestMapping(value = "settings", method = RequestMethod.POST)
	public @ResponseBody GeneralSettings saveGeneralSettings(@RequestParam(value = "tenantId", required = false) Integer tenantId,	@RequestBody GeneralSettings generalSettings,
			HttpServletResponse response, HttpServletRequest request) throws Exception {

		generalSettings.setWsURL( "");
		if (StringUtils.isEmpty(generalSettings.getQbPassword()))
			generalSettings.setQbPassword(encryptDecryptHandler.encrypt(tenantId+"~"+generalSettings.getQbAccount()));
		quickbooksService.saveOrUpdateSettingsInEntityList(generalSettings,	tenantId, "https://"+request.getServerName()+ context.getContextPath());
		generalSettings.setWsURL( getSoapUrl(request) );
		
		return generalSettings;
	}

	@RequestMapping(value = "qbefile", method = RequestMethod.GET)
	public @ResponseBody ObjectNode qbefile(@RequestParam(value = "tenantId", required = false) Integer tenantId, final HttpServletRequest request) throws Exception {

		String fileContent = null;
		try {
			GeneralSettings generalSettings = quickbooksService.getSettingsFromEntityList(tenantId);
			generalSettings.setWsURL( getSoapUrl(request) );

			String wsdlUrl = generalSettings.getWsURL();
			
			QuickWebConnector quickWebCon = new QuickWebConnector();
			quickWebCon.setId(tenantId);
			quickWebCon.setName(webserviceName);
			quickWebCon.setUrl(wsdlUrl);
			quickWebCon.setDescription(webserviceDesc);
			quickWebCon.setSupport(wsdlUrl);
			quickWebCon.setOwnerId("{"+java.util.UUID.randomUUID()+"}");
			quickWebCon.setFileId("{"+java.util.UUID.randomUUID()+"}");
			quickWebCon.setUserName(generalSettings.getQbAccount());
			quickWebCon.setQbType("QBFS");
			Scheduler scheduler = new Scheduler();
			scheduler.setRun(1);
			quickWebCon.setScheduler(scheduler);

			JAXBContext jaxbContext = JAXBContext.newInstance(QuickWebConnector.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(quickWebCon, writer);

			fileContent = writer.toString();
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
		ObjectNode node = jsonNodeFactory.objectNode();
		node.put("qbxml", fileContent);
		return node;
	}
	
	/*@RequestMapping(value = "generatePwd", method = RequestMethod.POST)
	public @ResponseBody ObjectNode generatePwd(@RequestParam(value = "tenantId", required = false) Integer tenantId,@RequestBody String name, final HttpServletRequest request) throws Exception {
		String password = encryptDecryptHandler.encrypt(tenantId+"~"+name);
		
		JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
		ObjectNode node = jsonNodeFactory.objectNode();
		node.put("pwd", password);
		
		return node;
	}*/
	
	
	
	@RequestMapping(value = "download", method = RequestMethod.POST)
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
			//e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			out.flush();
			out.close();
		}

	}
	
	private String getSoapUrl(HttpServletRequest request) {
		//return "https://"+request.getServerName()+ context.getContextPath()+"/soap/QuickBooksService";
		return "https://"+request.getServerName()+ context.getContextPath()+ wsdlFileName;
	}

}
