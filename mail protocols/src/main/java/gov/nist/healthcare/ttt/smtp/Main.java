package gov.nist.healthcare.ttt.smtp;

import gov.nist.healthcare.ttt.smtp.listener.SimpleSMTPListener;
import gov.nist.healthcare.ttt.smtp.util.ConfigReader;
import gov.nist.healthcare.ttt.smtp.util.TestData;
import gov.nist.healthcare.ttt.smtp.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONException;

public class Main {
	public static Logger log = Logger.getLogger("Main");

	/*public static void main(String[] args) throws Exception {
		// Run all tests
		long time = Utils.getTime(Main.class);
		log.info("SMTP Module. Build " + time + " ---- " + new Date(time).toLocaleString());
		
		SMTPTestRunner smtpTestRunner = new SMTPTestRunner();

		if (args.length == 0) {
			System.out.println("SMTPTests");
			System.out.println("Parameters: all | listener [port behavior] | testone n");
			byte[] b = new byte[255];
			System.in.read(b);
			if (b.length == 0)
				return;

			String[] args2 = new String(b).split(" ");
			for (int i = 0; i < args2.length; i++)
				args2[i] = args2[i].trim();

			args = args2;
		}

		switch (args[0]) {
		case "all":
			runAllCases();
			break;
		case "listener":
			System.out.println("Running Chameleon Server on Port: "
					+ (args.length <= 1 ? 8000 : Integer.parseInt(args[1])));
			try {
				new SimpleSMTPListener().listen(args.length <= 1 ? 8000
						: Integer.parseInt(args[1]), 
						args.length <= 2 ? 0 : Integer.parseInt(args[2]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "testone": // given an id, extracts the json file and prepares the
						// test input
			int testcase = 0;
			try {
				testcase = args.length <= 1 ? 1 : Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestInput ti;
			
			ti = ConfigReader.getTestInput(testcase);

			ITestResult[] trs = smtpTestRunner.runTestCase(testcase, ti);
			if (trs.length == 0)
				System.out.println("Test returned no results!");
			for (ITestResult t : trs)
				logResponses(t);

			for (ITestResult t : trs)
				log.info(String.format("ID:%d  %s", t.getTestCaseId(), TestData
						.getLongString(50, t.getCriteriaMet() ? '+' : '-')));
			break;

		default:
			System.out.println("Arguments do not match. Quiting.");

		}

	}

	public static void runAllCases() throws FileNotFoundException, IOException {
		SMTPTestRunner smtpTestRunner = new SMTPTestRunner();


		LinkedHashMap<String, byte[]> attachment = new LinkedHashMap<String, byte[]>();
	//	attachment.put("Tes.txt", "attachment".getBytes());

		// TestInput ti = new TestInput("localhost","localhost",25,
		// "blue@localhost", "red@localhost", false, "red", "red","red", "red",
		// 25, 600, attachement);
		TestInput ti = new TestInput("localhost", "localhost", 25, 25,
				"blue@localhost", "red@localhost", false, "red", "red", "red",
				"red", 8000, 60, attachment);

		ITestResult[] trs = smtpTestRunner.runAllTests(ti);


		for (ITestResult t : trs) {
			if (t != null)
				logResponses(t);
		}

		// summary
		logBanner("Summary of Test Results");
		for (ITestResult t : trs) {
			log.info(String.format("ID:%d  %s", t.getTestCaseId(),
					TestData.getLongString(50, t.getCriteriaMet() ? '+' : '-')));
		}
	}

	public static void logBanner(String topic) {
		log.info("");
		log.info("");
		log.info("**************************************************************************************");
		log.info("************                       " + topic);
		log.info("************************************************************************************");
		log.info("");
	}

	public static void logResponses(ITestResult _res) {
		TestResult res = (TestResult) _res;
		logBanner("START: " + _res.getTestCaseDesc());
		log.info("Test result: " + (res.getCriteriaMet() ? " Pass " : " Fail "));
		log.info(TestData.getLongString(100, res.getCriteriaMet() ? '+' : '-'));
		log.info("Test return Value: " + res.getLastTestResultStatus());
		log.info("Test return: " + res.getLastTestResponse());
		log.info("Details:");
		log.info("Time elapsed: " + res.getTimeElapsedInSeconds());
		log.info("Was there a timeout: " + res.isDidRequestTimeOut());
		int ii = 0;
		for (Entry<String, String> si : res.reqres.entrySet()) {
			log.info(ii++ + ". Req: " + si.getKey() + " Response: "
					+ si.getValue());
		}
		logBanner("END: " + res.getTestCaseDesc());
	}

*/}