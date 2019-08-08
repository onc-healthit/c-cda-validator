package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/properties")

public class PropertiesController {

	private static Logger logger = Logger.getLogger(PropertiesController.class.getName());

	@Value("${direct.listener.domainName}")
	String domainName = "localhost";

	@Value("${ttt.lastUpdated}")
	String lastUpdated = "";

	@Value("${ttt.configfile}")
	String configFilePath = "";
	/*Calendar cal = Calendar.getInstance();
	String date = new SimpleDateFormat("MMMM dd, YYYY").format(cal.getTime());
	String lastUpdated = date;*/

	@Value("${ttt.version}")
	String version = "1.0";

	@Value("${ett.dcdt.2014.hosting.url}")
	String dcdt2014Url = "";

	@Value("${ett.dcdt.2015.hosting.url}")
	String dcdt2015Url = "";

	@Value("${dtt.egroup}")
	String dttgroup = "";
	
	@Value("${dtt.email}")
	String dttemail = "";
	
	private @Autowired ApplicationContext appContext;
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody HashMap<String, String> getProperties() throws IOException {
    	HashMap<String, String> prop = new HashMap<String, String>();
    	Resource resource = null;
		Properties properties = new Properties();
		try {
			resource = appContext.getResource("file:" + System.getProperty("user.dir")+System.getProperty("file.separator")+"config.properties");
			if (resource.exists()){
				properties.load(FileUtils.openInputStream(resource.getFile()));
			}else{
				InputStream in = new URL(configFilePath).openStream();
				properties.load(in);
			}
			version = (String)properties.get("ttt.version");
			lastUpdated = (String)properties.get("ttt.lastUpdated");
			
			URL aURL = new URL(dcdt2014Url);
	    	prop.put("dcdt2014domain", aURL.getHost());
	    	prop.put("dcdt2014Protocol", aURL.getProtocol());
	    	aURL = new URL(dcdt2015Url);
	    	prop.put("dcdt2015domain", aURL.getHost());
	    	prop.put("dcdt2015Protocol", aURL.getProtocol());
			
		}catch (FileNotFoundException fnfe) {
			logger.info(fnfe.getMessage());
		} finally {
		}
    	prop.put("domainName", domainName);
    	prop.put("lastUpdated", lastUpdated);
    	prop.put("version", version);
    	prop.put("dttgroup",dttgroup);
    	prop.put("dttemail",dttemail);
    	return prop;
    }

}