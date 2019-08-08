package gov.nist.healthcare.ttt.smtp.util;

import gov.nist.healthcare.ttt.smtp.TestInput;
import gov.nist.healthcare.ttt.smtp.TestResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.*;

public class ConfigReader {
	public static Logger log = Logger.getLogger(ConfigReader.class);

	public static TestInput getTestInput(int testcase) throws JSONException,
			IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		LinkedHashMap<String, byte[]> attachment = new LinkedHashMap<String, byte[]>();
		attachment.put("Test.txt", "test attachment".getBytes());
		TestResult tr = new TestResult();
		TestInput ti = new TestInput("localhost", "localhost", 25, 25,
				"blue@localhost", "red@localhost", false, "red", "red", "red",
				"red", 25, 600, attachment,"a","b", null);
		JSONObject jsonf = new JSONObject(readFile("config-all.json"));
		JSONArray ts = (JSONArray) jsonf.get("tests");

		for (int i = 0; i < ts.length(); i++) {
			JSONObject t = (JSONObject) ts.get(i);
			String testname = (String) t.get("name");
			if (!testname.contains(String.valueOf(testcase)))
				continue;
			System.out.println("Found testcase " + testname);
			JSONArray fs = (JSONArray) t.get("fields");
			for (int ii = 0; ii < fs.length(); ii++) {
				JSONObject f = (JSONObject) fs.get(ii);
				String nm = (String) f.get("name");
				String val = (String) f.get("value");
				String dt = (String) f.get("datatype");
				String display = (String) f.get("display");
				Field fld = ti.getClass().getDeclaredField(nm);
				boolean dis = display.equals("true");
				/*if (display.equals("true")) */ {
					// for ui fields, the default comes from the above init; else, it comes from the value of the config
					System.out.println("Please enter values for : " + nm
							+ " (Default: " + (dis ? (String) f.get("value") :  fld.get(ti).toString()) + 
							(dis ? " [ui field]" : "[non-ui]") + ")");
					byte[] b = new byte[255];
					System.in.read(b);
					val = new String(b).trim();
				}
				
				if (val.trim().isEmpty()) {
					if (display.equals("true")) 
						val = (String) f.get("value");
					else
						val = fld.get(ti).toString();
				}
				
				System.out.println("Setting " + nm + " to " + val.trim());
				fld.set(ti, conv(dt, val.trim()));
				System.out.println("Value set to "+ fld.get(ti));
			}
			
			break; // matching the shortest
		}

		return ti;
	}

	private static Object conv(String data, String val) {
		if (data.equals("boolean"))
			return val.equals("true") ? true : false;
		else if (data.equals("int"))
			return Integer.parseInt(val);
		return val;
	}

	static String readFile(String path) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			InputStream resourceStream = loader.getResourceAsStream(path);
			String body = IOUtils.toString(resourceStream, "UTF-8");
			return body;
		} catch (Exception e) {
			log.error("Unable to load " + path);
		}

		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
}
