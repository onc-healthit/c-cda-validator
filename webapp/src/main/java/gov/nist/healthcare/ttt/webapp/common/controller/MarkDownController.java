package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("api/markdown")
public class MarkDownController {

	@Value("${announcements.path}")
	String announcementPath;

	@Value("${faq.path}")
	String faqPath;

	@Value("${localinstall.path}")
	String localInstallPath;

	private static Logger logger = Logger.getLogger(MarkDownController.class.getName());
	private @Autowired ApplicationContext appContext;
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getProperties(@RequestParam("moduleInfo") String moduleInfo) throws IOException {
		Resource resource = null;
		try {
			resource = appContext.getResource("file:" + System.getProperty("user.dir")+System.getProperty("file.separator")+getFilePath(moduleInfo));
			if (resource.exists()){
				return FileUtils.readFileToString(new File(resource.getFilename()));					
			}else{
				InputStream in = new URL(getFilePath(moduleInfo)).openStream();
				return IOUtils.toString(in);				
			}
		} catch (FileNotFoundException fnfe) {
			logger.info(fnfe.getMessage());
			return "File does not exist";
		} finally {
		}
	}

	private String getFilePath(String moduleInfo){
		String fileName ="";
		if (moduleInfo.equalsIgnoreCase("announcement")){
			fileName = announcementPath;
		}else if(moduleInfo.equalsIgnoreCase("faq")){
			fileName = faqPath;
		}else if(moduleInfo.equalsIgnoreCase("localinstall")){
			fileName = localInstallPath;
		}
		return fileName;
	}

}