package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.ttt.webapp.common.model.certificatesLink.CertificatesLinkModel;

@Controller
@RequestMapping("/api/directcertlinks")
public class DirectCertificateLinksController {

	private static Logger logger = Logger.getLogger(DirectCertificateLinksController.class.getName());

	@Value("${server.tomcat.basedir}")
	String fileDirectory;
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<CertificatesLinkModel> getCertificates() throws Exception {
		// CCDA cache File path
		String certificatesLinkFilePath = fileDirectory + File.separator + "direct_certificates_links.json";
		File certificatesLinkFile = new File(certificatesLinkFilePath);
		
		if(!certificatesLinkFile.exists()) {
			// Create default file if it is not present
			logger.info("File direct_certificates_links.json does not exist. Copying the default file.");
			InputStream defaultFile = DirectCertificateLinksController.class.getResourceAsStream("/assets/empty_direct_certificates_link.json");
			FileUtils.copyInputStreamToFile(defaultFile, certificatesLinkFile);
			logger.info("Creating file " + certificatesLinkFilePath);
		}

		// If file exists just return the values
		JsonFactory factory = new JsonFactory(); 
		ObjectMapper mapper = new ObjectMapper(factory); 
		TypeReference<List<CertificatesLinkModel>> typeRef = new TypeReference<List<CertificatesLinkModel>>() {};
		
		return mapper.readValue(certificatesLinkFile, typeRef);
	}

}
