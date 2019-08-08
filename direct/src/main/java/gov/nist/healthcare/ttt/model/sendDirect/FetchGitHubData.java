package gov.nist.healthcare.ttt.model.sendDirect;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class FetchGitHubData {
	public InputStream fetch(String filename) throws Exception {
		Properties prop = new Properties();
		String path = "./application.properties";
		FileInputStream file = new FileInputStream(path);
		prop.load(file);
		file.close();
		
		String link = prop.getProperty("github.data")+"Receiver%20SUT%20Test%20Data/170.315_b1_ToC_Amb/"+filename;
		URL Url = new URL(link);
		HttpURLConnection Http = (HttpURLConnection) Url.openConnection();
		InputStream Stream = Http.getInputStream();
		return Stream;
	}
}
