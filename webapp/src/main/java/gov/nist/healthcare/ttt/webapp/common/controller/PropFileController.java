package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/propfile")

public class PropFileController {

	private static Logger logger = Logger.getLogger(PropFileController.class.getName());

	@Value("${direct.listener.domainName}")
	String domainName = "localhost";

	@Value("${ttt.lastUpdated}")
	String lastUpdated = "";

	@Value("${ttt.version}")
	String version = "1.0";

	@Value("${ett.dcdt.2014.hosting.url}")
	String dcdt2014Url = "";

	@Value("${ett.dcdt.2015.hosting.url}")
	String dcdt2015Url = "";

	@Value("${direct.listener.domainName}")
	String ettEdgeDomain = "";

	@Value("${ett.smtp.host}")
	String ettDsDomain = "";

	@Value("${dir.hostname}")
	String ettDs2Domain = "";

	@Value("${not.trusted}")
	String ettDirect2Domain = "";

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody HashMap<String, String> getProperties() throws IOException {
    	HashMap<String, String> prop = new HashMap<String, String>();
		try {
			URL aURL = new URL(dcdt2014Url);
			prop.put("dcdt2014domain", aURL.getHost());
			prop.put("dcdt2014Protocol", aURL.getProtocol());
			aURL = new URL(dcdt2015Url);
			prop.put("dcdt2015domain", aURL.getHost());
			prop.put("dcdt2015Protocol", aURL.getProtocol());
			prop.put("domainName", domainName);
			prop.put("lastUpdated", lastUpdated);
			prop.put("version", version);
			prop.put("ettEdgeDomain", ettEdgeDomain);
			prop.put("ettDsDomain", ettDsDomain);
			prop.put("ettDs2Domain", ettDs2Domain);
			prop.put("ettDirect2Domain", ettDirect2Domain);
		} catch (Exception ex) {
			logger.info(ex.getMessage());
		} finally {
		}
    	return prop;
    }

}