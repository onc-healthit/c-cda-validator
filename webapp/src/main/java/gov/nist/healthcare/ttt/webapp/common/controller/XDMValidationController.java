package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;

@Controller
@RequestMapping("/api/xdm")
public class XDMValidationController {

	@Value("${toolkit.url}")
	String toolkitUrl;

	private static Logger logger = Logger.getLogger(XDMValidationController.class.getName());

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String validateXdm(@RequestBody HashMap<String, String> filePath) throws Exception {
		if(filePath.containsKey("messageFilePath")) {
			String messageFilePath = filePath.get("messageFilePath");
			if(messageFilePath != null && !messageFilePath.equals("")) {

				// Get byte array of file
				Path path = Paths.get(messageFilePath);
				byte[] data = null;
				try {
					data = Files.readAllBytes(path);
				} catch (IOException e) {
					logger.error(e.getMessage());
					throw new TTTCustomException("0x0075", "Not able to get the file IOException");
				}

				// Query toolkit endpoint
				CloseableHttpClient client = HttpClients.createDefault();
				HttpPost post = new HttpPost(toolkitUrl + "/rest/simulators/xdmValidation");
				//
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				String reqString = Base64.getEncoder().encodeToString(data);

				JSONObject json = new JSONObject();
				json.put("zip", reqString);   
				StringEntity params = null;
				try {
					params = new StringEntity(json.toString());
				} catch (UnsupportedEncodingException e1) {
					logger.error(e1.getMessage());
					throw new TTTCustomException("0x0076", "An error occured while creating json from file UnsupportedEncodingException");
				}

				post.addHeader("content-type", "application/json");
				post.setEntity(params);

				String result = "";
				try {
					HttpResponse response = client.execute(post);
					// CONVERT RESPONSE TO STRING
					result = EntityUtils.toString(response.getEntity());
				} catch(Exception e) {
					logger.error(e.getMessage());
					throw new TTTCustomException("0x0077", "Error reading response from toolkit");
				}

				return result;
			} else {
				throw new TTTCustomException("0x0052", "No XDM file uploaded");
			}
		} else {
			throw new TTTCustomException("0x0052", "No XDM file uploaded");
		}
	}
}
