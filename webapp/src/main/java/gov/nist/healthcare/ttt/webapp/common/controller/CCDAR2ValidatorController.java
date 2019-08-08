package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.File;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;

@Controller
@RequestMapping("/api/ccdar2")
public class CCDAR2ValidatorController {
	
	@Value("${ett.mdht.r2.url}")
	String mdhtUrl;
	
	private static Logger logger = Logger.getLogger(CCDAR2ValidatorController.class.getName());

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String validateCCDAR2(@RequestBody HashMap<String, String> filePath) throws Exception {
		if(filePath.containsKey("messageFilePath")) {
			String messageFilePath = filePath.get("messageFilePath");
			if(messageFilePath != null && !messageFilePath.equals("")) {
				
				String validationObjective = "170.315(b)(1)";
				if(filePath.containsKey("validationObjective")) {
					validationObjective = filePath.get("validationObjective");
				}
				String referenceFileName = "CP_Sample1.pdf";
				if(filePath.containsKey("referenceFileName")) {
					referenceFileName = filePath.get("referenceFileName");
				}
				
				logger.info("Validating CCDA " + filePath.get("messageFilePath") + " with validation objective " + validationObjective + " and reference filename " + referenceFileName);
				
				// Query MDHT war endpoint
				CloseableHttpClient client = HttpClients.createDefault();
				File file = new File(messageFilePath);
				HttpPost post = new HttpPost(mdhtUrl);
				FileBody fileBody = new FileBody(file);
				//
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addTextBody("validationObjective", validationObjective);
				builder.addTextBody("referenceFileName", referenceFileName);
				builder.addPart("ccdaFile", fileBody);
				HttpEntity entity = builder.build();
				//
				post.setEntity(entity);
				String result = "";
				try {
					HttpResponse response = client.execute(post);
					// CONVERT RESPONSE TO STRING
					result = EntityUtils.toString(response.getEntity());
				} catch(Exception e) {
					e.printStackTrace();
					throw e;
				}

				JSONObject json = new JSONObject(result);
				json.put("hasError", false);
				// Check errors
				JSONArray resultMetadata = json.getJSONObject("resultsMetaData").getJSONArray("resultMetaData");
				for (int i = 0; i < resultMetadata.length(); i++) {
					JSONObject metatada = resultMetadata.getJSONObject(i);
					if(metatada.getString("type").toLowerCase().contains("error")) {
						if(metatada.getInt("count") > 0) {
							json.put("hasError", true);
						}
					}

				}

				return json.toString();
				
				
				
				
			} else {
				throw new TTTCustomException("0x0050", "No CCDA attachment uploaded");
			}
		}
		return null;
		
	}
}
