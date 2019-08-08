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
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/releasenotes")
public class ReleaseNotesController {

	@Value("${releasesnotes.path}")
	String releaseNotesPath;

	private static Logger logger = Logger.getLogger(ReleaseNotesController.class.getName());
	private @Autowired ApplicationContext appContext;
	@RequestMapping(method = RequestMethod.GET, produces = "application/txt")
	public @ResponseBody String getProperties() throws IOException {
		Resource resource = null;
		try {
			resource = appContext.getResource("file:" + System.getProperty("user.dir")+System.getProperty("file.separator")+"release_notes.txt");
			if (resource.exists()){
				return FileUtils.readFileToString(new File(resource.getFilename()));			
			}else{
				InputStream in = new URL(releaseNotesPath).openStream();
				return IOUtils.toString(in);				
			}
		}catch (FileNotFoundException fnfe) {
			logger.info(fnfe.getMessage());
			return "File does not exist";
		} catch (Exception exce) {
			logger.info(exce.getMessage());
			return "File does not exist";
		} finally {
		}
	}

}