package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api/ccdadocuments")
public class GetCCDADocumentsController {

	private static Logger logger = Logger.getLogger(GetCCDADocumentsController.class.getName());

	@Value("${server.tomcat.basedir}")
	String ccdaFileDirectory;

	public List<String> files2ignore = Arrays.asList("LICENSE", "README.md","README.MD");
	public List<String> extension2ignore = Arrays.asList("");
	public String extensionRegex = ".*\\.[a-zA-Z0-9]{3,4}$";

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody HashMap<String, Object> getDocuments(@RequestParam(value = "testCaseType") String testCaseType) throws Exception {
		// Result map
		HashMap<String, Object> resultMap = new HashMap<>();

		// CCDA cache File path
		String ccdaFilePath = getFilterFiles(testCaseType);
		File ccdaObjectivesFile = new File(ccdaFilePath);
				
		if(ccdaObjectivesFile.exists() && !ccdaObjectivesFile.isDirectory()) {
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};

			resultMap = mapper.readValue(ccdaObjectivesFile, typeRef);
		} else {
			String sha = getHTML("https://api.github.com/repos/siteadmin/2015-Certification-C-CDA-Test-Data/branches/master")
					.getJSONObject("commit").get("sha").toString();
			JSONArray filesArray = getHTML("https://api.github.com/repos/siteadmin/2015-Certification-C-CDA-Test-Data/git/trees/"
					+ sha + "?recursive=1").getJSONArray("tree");

			for(int i=0; i < filesArray.length(); i++) {
				JSONObject file = filesArray.getJSONObject(i);
				if(!files2ignore.contains(file.get("path"))) {
					// Get path array
					String[] path = file.get("path").toString().split("/");
					buildJson(resultMap, path);
				}

			}
			// Write the cache file
			try{
				JSONObject cacheFile = new JSONObject(resultMap);
				FileUtils.writeStringToFile(ccdaObjectivesFile, cacheFile.toString(2));
			} catch(Exception e) {
				logger.error("Could not create ccda cache file: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	public void buildJson(HashMap<String, Object> json, String[] path) {
		if(path.length == 1 && !files2ignore.contains(path[0].toUpperCase())) {
			HashMap<String, Object> newObj = new HashMap<>();
			newObj.put("dirs", new ArrayList<HashMap<String, Object>>());
			newObj.put("files", new ArrayList<HashMap<String, Object>>());
			json.put(path[0], newObj);

		} else {
			HashMap<String, Object> current = (HashMap<String, Object>) json.get(path[0]);
			String fileName = path[path.length-1];
			String fileExtnAry[] = fileName.split("\\.");
			String fileExtn = "";
			if (fileExtnAry.length > 0){
				fileExtn = fileExtnAry[fileExtnAry.length-1];
		    }
			//create directory only when at least one valid file exist
			if(Pattern.matches(extensionRegex, fileName) && !files2ignore.contains(fileName) && !extension2ignore.contains(fileExtn) ) {
				for(int i = 1 ; i < path.length-1 ; i++) {
					String  currentName = path[i];
					boolean firstFile = false;
					//For the first filename the direcotry is not found
					if(containsName((List<Map>) current.get("dirs"), currentName)) {
						List<Map> directories = (List<Map>) current.get("dirs");
						current = (HashMap<String, Object>) directories.get(getObjByName(directories, currentName));
						HashMap<String, Object> newFile = new HashMap<>();
						newFile.put("name", fileName);
						newFile.put("link", getLink(path));
						List filesList = (List) current.get("files");
						filesList.add(newFile);
					} else {
						firstFile = true;
						HashMap<String, Object> newObj = new HashMap<>();
						newObj.put("name", currentName);
						newObj.put("dirs", new ArrayList<HashMap<String, Object>>());
						newObj.put("files", new ArrayList<HashMap<String, Object>>());
						List dirsList = (List) current.get("dirs");
						dirsList.add(newObj);
					}
					//For the first filename the when direcotry is not found and the files
					if(firstFile && containsName((List<Map>) current.get("dirs"), currentName)) {
						current = (HashMap<String, Object>) json.get(path[0]);
						List<Map> directories = (List<Map>) current.get("dirs");
						current = (HashMap<String, Object>) directories.get(getObjByName(directories, currentName));
						HashMap<String, Object> newFile = new HashMap<>();
						newFile.put("name", fileName);
						newFile.put("link", getLink(path));
						List filesList = (List) current.get("files");
						filesList.add(newFile);
					}
				} // end of For loop
			}
		}
	}

	public String getLink(String[] path) {
		String link = String.join("/", path).replace(" ", "%20");
		link = "https://raw.githubusercontent.com/siteadmin/2015-Certification-C-CDA-Test-Data/master/" + link;
		return link;
	}

	public static boolean containsName(List<Map> json, String value) {
		for(Map obj : json) {
			if(obj.containsValue(value)) {
				return true;
			}
		}
		return false;
	}

	public static int getObjByName(List<Map> json, String value) {
		for(int i = 0 ; i < json.size() ; i++) {
			if(json.get(i).containsValue(value)) {
				return i;
			}
		}
		return -1;
	}

	public static JSONObject getHTML(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return new JSONObject(result.toString());
	}
	
	private String getFilterFiles(String testCaseType){
		String fileName = ccdaFileDirectory + File.separator + "ccda_objectives.txt";
		extension2ignore = Arrays.asList("");
		if (testCaseType !=null && testCaseType.equalsIgnoreCase("xdr")){
			fileName = ccdaFileDirectory + File.separator + "ccda_objectives_xdr.txt";
			extension2ignore = Arrays.asList("ZIP","zip");
		}
		return fileName;
	}

}
