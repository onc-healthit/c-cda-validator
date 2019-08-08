package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
@RequestMapping("/api/dcdt")
public class DCDTValidatorController {

	@Value("${ett.dcdt.2014.hosting.url}")
	String dcdt2014HostingUrl;

	@Value("${ett.dcdt.2015.hosting.url}")
	String dcdt2015HostingUrl;

	@Value("${ett.dcdt.2014.discovery.url}")
	String dcdt2014discoveryUrl;

	@Value("${ett.dcdt.2015.discovery.url}")
	String dcdt2015discoveryUrl;

	private static Logger logger = Logger.getLogger(DCDTValidatorController.class.getName());

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String validateDCDT(@RequestBody HashMap<String, String> requestParms) throws Exception {
		logger.info("Validating DCDT " + dcdt2015HostingUrl);
		String dcdtUrl = "";
		logger.info("requestParms " + requestParms);
		String message;
		String result = "";
		JSONObject mainJosnObject = new JSONObject();
		mainJosnObject.put("@type", "request");

		JSONArray itemArray = new JSONArray();

		JSONObject jsonObjTestcase = new JSONObject();
		jsonObjTestcase.put("@type", requestParms.get("@type"));
		jsonObjTestcase.put("directAddr", requestParms.get("directAddr"));

		if (requestParms.get("year").equalsIgnoreCase("2015")){
			if (requestParms.get("hostingcase").equalsIgnoreCase("YES")){
				dcdtUrl = dcdt2015HostingUrl;
				jsonObjTestcase.put("testcase", requestParms.get("testcase"));
			}else{
				jsonObjTestcase.put("resultsAddr", requestParms.get("resultsAddr"));
				dcdtUrl = dcdt2015discoveryUrl;
			}
		}else{
			if (requestParms.get("hostingcase").equalsIgnoreCase("YES")){
				jsonObjTestcase.put("testcase", requestParms.get("testcase"));
				dcdtUrl = dcdt2014HostingUrl;				
			}else{
				jsonObjTestcase.put("resultsAddr", requestParms.get("resultsAddr"));
				dcdtUrl = dcdt2014discoveryUrl;
			}			
		}
		logger.info("dcdtUrl ::::" + dcdtUrl);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(dcdtUrl);
		//
		itemArray.put(jsonObjTestcase);

		mainJosnObject.put("items", itemArray);

		message = mainJosnObject.toString();


		try {
			post.setEntity(new StringEntity(message));
			post.setHeader("Content-type", "application/json");
			post.setHeader("accept", "application/json");
			post.setProtocolVersion(HttpVersion.HTTP_1_0);

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new TTTCustomException("0x0077",
						"Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			// CONVERT RESPONSE TO STRING
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
				//create json object
				JSONObject jsonObj = new JSONObject(result);
				jsonObjTestcase.put("expandResult", true);
				jsonObj.put("testcase", jsonObjTestcase);
				result = jsonObj.toString();
			}
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage());
			throw new TTTCustomException("0x0076",
					"An error occured while creating json from file UnsupportedEncodingException");
		}

		return result;
	}
}