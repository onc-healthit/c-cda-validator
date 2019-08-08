package gov.nist.healthcare.ttt.smtp.util;

import java.util.Properties;

public class TestData {

	 
	static Properties prop = Utils.getProp();
	
	public static String LONG_LOCAL_PART = "local" + getLongString(70);
	public static String LONG_DOMAIN = "domain" + getLongString(255);
	public static String LONG_DATA = "data" + getLongString(1000);
	public static String LONG_TO = LONG_LOCAL_PART + "@" + LONG_DOMAIN + ".com";
	public static String LONG_FROM = LONG_LOCAL_PART + "@" + LONG_DOMAIN
			+ ".com";

	public static String VALID_DOMAIN = prop.getProperty("valid.domain");
	public static String VALID_TO = prop.getProperty("valid.to");
	public static String VALID_FROM = prop.getProperty("valid.from");
	public static String VALID_DATA = "This is sample DATA";

	public static String TTT_TIMEOUT_MSG = "-02 Custom Message: Socket Timeout occured";
	public static String SUT_TIMEOUT_MSG = "-03 Custom Message: Null result [Possibly Server Ended the Connection]";

	/**
	 * @param n
	 * @return
	 */
	public static String getLongString(int n) {
		return getLongString(n, 'a');
	}

	public static String getLongString(int n, char a) {
		String s = "";
		for (; n > 0; n--)
			s += a;
		return s;
	}
}
