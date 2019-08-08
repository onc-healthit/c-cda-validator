package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/smtpcasesjson")
public class SmtpJsonController {
	private static Logger logger = Logger.getLogger(SmtpJsonController.class.getName());
	private @Autowired ApplicationContext appContext;
	private @Autowired ServletContext servletContext;
	private @Autowired HttpServletRequest httpServletRequest;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getProperties() throws IOException {
		Resource resource = null;
		try {
			resource = appContext.getResource("file:" + System.getProperty("user.dir")+System.getProperty("file.separator")+"smtptestCases.json");
			return FileUtils.readFileToString(resource.getFile());
		}catch(FileNotFoundException fnfe){
			logger.info("smtptestCases.json not found in applicaiton root folder");
			URL requestURL = new URL(httpServletRequest.getRequestURL().toString());
		    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
		    String urlSmtpjson = requestURL.getProtocol() + "://" + requestURL.getHost() + port+servletContext.getContextPath().toString();
		    urlSmtpjson = urlSmtpjson+"/assets/smtptestCases.json";
			InputStream in = new URL(urlSmtpjson).openStream();
			return IOUtils.toString(in);
		}finally {
		}
	}
}