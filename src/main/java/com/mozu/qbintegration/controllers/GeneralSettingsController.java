
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
	public @ResponseBody void saveGeneralSettings(@RequestParam(value = "tenantId", required = false) Integer tenantId,	@RequestBody GeneralSettings generalSettings,
			HttpServletResponse response, HttpServletRequest request) throws Exception {

		generalSettings.setWsURL( "");
		quickbooksService.saveOrUpdateSettingsInEntityList(generalSettings,	tenantId, "https://"+request.getServerName()+ context.getContextPath());
	}

	@RequestMapping(value = "qbefile", method = RequestMethod.GET)
	public @ResponseBody ObjectNode qbefile(@RequestParam(value = "tenantId", required = false) Integer tenantId, final HttpServletRequest request) throws Exception {

		String fileContent = null;
		try {
			GeneralSettings generalSettings = quickbooksService.getSettingsFromEntityList(tenantId);
			generalSettings.setWsURL( getSoapUrl(request) );

			String wsdlUrl = generalSettings.getWsURL();
			
			QuickWebConnector quickWebCon = new QuickWebConnector();
			quickWebCon.setId(100);
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
			e.printStackTrace();
		}
		JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
		ObjectNode node = jsonNodeFactory.objectNode();
		node.put("qbxml", fileContent);
		return node;
	}
	
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
			e.printStackTrace();
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
