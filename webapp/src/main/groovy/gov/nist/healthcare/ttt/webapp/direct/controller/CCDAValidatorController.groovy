package gov.nist.healthcare.ttt.webapp.direct.controller;

import java.io.File;
import java.io.IOException;

import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/ccdaValidator")
public class CCDAValidatorController {
	
	private static Logger logger = Logger.getLogger(CCDAValidatorController.class.getName());
	
	@Autowired
	private DatabaseInstance db;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String validateCCDA(@RequestBody HashMap<String, String> filePath) throws Exception {
		if(filePath.containsKey("messageFilePath")) {
			String messageFilePath = filePath.get("messageFilePath")
			if(messageFilePath != null && !messageFilePath.equals("")) {
				
				String ccdaType = "NonSpecificCCDA"
				if(filePath.containsKey("ccdaType")) {
					ccdaType = filePath.get("ccdaType")
				}
				
				logger.info("Validating CCDA " + filePath.get("messageFilePath") + " with type " + ccdaType);
				
				CloseableHttpClient client = HttpClients.createDefault();
				File file = new File(filePath.get("messageFilePath"));
				HttpPost post = new HttpPost("http://ttpdstest.sitenv.org:8080/referenceccdaservice/");
				FileBody fileBody = new FileBody(file);
				//
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addPart("file", fileBody);
				builder.addTextBody("type_val", ccdaType);
				HttpEntity entity = builder.build();
				//
				post.setEntity(entity);
				try {
					HttpResponse response = client.execute(post);
					// CONVERT RESPONSE TO STRING
					String result = EntityUtils.toString(response.getEntity());
					
					return result
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw e
				}
			} else {
				throw new TTTCustomException("0x0050", "No CCDA attachment uploaded");
			}
		}
		
	}
}
