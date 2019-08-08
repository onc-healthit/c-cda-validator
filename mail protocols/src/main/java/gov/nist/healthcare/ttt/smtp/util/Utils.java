package gov.nist.healthcare.ttt.smtp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.util.Date;
import java.util.Properties;
import java.util.List;


public class Utils {
	
	public static boolean isProcessedMDN(List<String> headers) {	
		return isAnyMDN7(headers, "processed");
	}
	
	public static boolean isFailureMDN(List<String> headers) {
		return isAnyMDN7(headers, "failure");
	}
	
	public static boolean isFailedMDN(List<String> headers) {
		return isAnyMDN7(headers, "failed");
	}

	public static boolean isDispatchedMDN(List<String> headers) {
		return isAnyMDN7(headers,"dispatched");
	}
	
	public static boolean isAnyMDN7(List<String> headers, String mdnVal) {
		return headers.contains("automatic-action/MDN-sent-automatically;" + mdnVal) || headers.contains("automatic-action/MDN-sent-automatically; " + mdnVal);
	}

	
	public static boolean isAnyMDN(List<String> headers, String mdnVal) {
		String constPrefix = "automatic-action/MDN-sent-automatically;";
		String regexAnyWhitespaceAfterSemiColon = constPrefix + " *" + mdnVal;
		
		return headers.stream().filter(p -> p.matches(regexAnyWhitespaceAfterSemiColon)).count() > 0;
	}
	
	public static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Long getTime(Class<?> cl) {
	    try {
	        String rn = cl.getName().replace('.', '/') + ".class";
	        JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
	        return j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
	    } catch (Exception e) {
	        return new Date().getTime();
	    }
	}
	
	
	public static Properties getProp() {
		Properties prop = new Properties();
		try {
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
		} catch (Exception e) {
		e.printStackTrace();
		}
		return prop;
		}
}
