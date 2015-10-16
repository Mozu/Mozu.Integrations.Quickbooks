package com.mozu.qbintegration.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.base.controllers.AdminControllerHelper;
import com.mozu.base.handlers.TenantHandler;

@Controller
@RequestMapping({ "/", "/index" })
@Scope("session")
public class AdminController {

	private static final Logger logger = LoggerFactory
			.getLogger(AdminController.class);

	protected static final String SECURITY_COOKIE = "MozuToken";

	public AdminController() {
		System.out.println("Initializing AdminController");
	}

	@Value("${SharedSecret}")
	String sharedSecret;

	@RequestMapping(method = RequestMethod.POST)
	public String index(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, ModelMap modelMap)
			throws NumberFormatException, Exception {
		
		 AdminControllerHelper adh = new AdminControllerHelper();
         if (!adh.securityCheck(httpRequest, httpResponse)) {
             logger.warn("Not authorized");
             return "unauthorized";
         }
        String tenantId = httpRequest.getParameter("tenantId");
        Tenant tenant = TenantHandler.getTenant(Integer.parseInt(tenantId)); //DO a get tenant to make sure the app has access
		modelMap.addAttribute("tenantId", tenantId);
		return "index";
	}
}
