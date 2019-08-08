package gov.nist.healthcare.ttt.smtp;

import gov.nist.healthcare.ttt.smtp.server.SocketSMTPSender;
import gov.nist.healthcare.ttt.smtp.testcases.MU2ReceiverTests;
import gov.nist.healthcare.ttt.smtp.testcases.MU2SenderTests;
import gov.nist.healthcare.ttt.smtp.testcases.TTTReceiverTests;
import gov.nist.healthcare.ttt.smtp.testcases.TTTSenderNegativeTests;
import gov.nist.healthcare.ttt.smtp.testcases.TTTSenderTests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author sriniadhi
 *
 *         Runs all the test cases
 * 
 *         Key functions: runTestCase, getTestCaseIds, testXXXX
 * 
 * 
 */
public class SMTPTestRunner implements ISMTPTestRunner {
	static Logger log = Logger.getLogger(SMTPTestRunner.class);
	Properties config = new Properties();
	String configFileName = "config.properties";

	SocketSMTPSender smtpSender = new SocketSMTPSender();

	/**
	 * These implement the testcases; the runTestCase calls them
	 */
	TTTSenderTests sTest = new TTTSenderTests();
	TTTReceiverTests tTest = new TTTReceiverTests();
	TTTSenderNegativeTests nTest = new TTTSenderNegativeTests(smtpSender,
			config);
	MU2SenderTests mu2senderTests = new MU2SenderTests();
	MU2ReceiverTests mu2receiverTests = new MU2ReceiverTests();


	public SMTPTestRunner() throws FileNotFoundException, IOException {
		super();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			InputStream resourceStream = loader
					.getResourceAsStream(configFileName);
			config.load(resourceStream);
		} catch (Exception e) {
			log.error("Unable to load config.properties!");
		}
	}

	/**
	 * Loops through getTestCaseIds and calls runTest for each on the given
	 * TestInput ti
	 */
	@Override
	public ITestResult[] runAllTests(TestInput ti) {
		int[] tids = getTestCaseIds();
		ITestResult[] all = new ITestResult[0];
		for (int t : tids) {
			ITestResult[] tr = runTestCase(t, ti);
			if (tr != null)
				all = concat(all, tr);
		}

		return all;
	}

	/**
	 * @return array of integers containing the implemented testcase ids
	 */
	@Override
	public int[] getTestCaseIds() {
		final int[] ntestCaseIds = { 10, 11, 12, 13, 17 };
		final int[] ptestCaseIds = { 9, 16, 18, 20, 21, 22, 23 };
		final int[] alltestCaseIds = concati(ntestCaseIds, ptestCaseIds);
		final int[] testTest = { 9,16,20,22 };
		return testTest;
	}

	/**
	 * @throws InterruptedException 
	 *  
	 */
	@Override
	public ITestResult[] runTestCase(int i, TestInput ti) {

		System.setProperty("java.net.preferIPv4Stack", "true");

		ArrayList<ITestResult> res = new ArrayList<ITestResult>();
		log.info("------------------------->  runTestCase " + i);
	//	//ti.useTLS = true; // Enforcing STARTTLS for all cases.

		switch (i) {
		case 0:
			// stub to test; not a real test case	
			res.add(nTest.testValid(ti));
			break;

		case 1:
			log.info("*****************   BEGIN  Testcase 1 *******************************");

			TestResult tr;
			try {
					Properties prop = new Properties();
					String path = "./application.properties";
					FileInputStream file;
					file = new FileInputStream(path);
					prop.load(file);
					file.close();
				tr = tTest.fetchMail(ti,prop.getProperty("ett.starttls.address"));
				tr.id = 1;
				res.add(tr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 1 *******************************");
			break;
			
		case 2:
			log.info("*****************   BEGIN  Testcase 2 *******************************");

			TestResult tr2;
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				tr2 = tTest.fetchMail(ti,prop.getProperty("ett.starttls.hisp.address"));
				tr2.id = 2;
				res.add(tr2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 2 *******************************");
			break;

		case 91:

			log.info("*****************   BEGIN  Testcase 91 *******************************");
			//ti.useTLS = false;
			TestResult tr91;
			try {
				tr91 = sTest.testStarttlsTextandCCDA(ti);
				tr91.id = 91;
				res.add(tr91);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 91 *******************************");
			break;

		case 92:

			log.info("*****************   BEGIN  Testcase 92 *******************************");
			//ti.useTLS = false;
			TestResult tr92;
			try {
				tr92 = sTest.testStarttlsPdfandCCDA(ti);
				tr92.id = 92;
				res.add(tr92);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 92 *******************************");
			break;	

		case 93:

			log.info("*****************   BEGIN  Testcase 93 *******************************");
			//ti.useTLS = false;
			TestResult tr93;
			try {
				tr93 = sTest.testStarttlsTextandXDM(ti);
				tr93.id = 93;
				res.add(tr93);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 93 *******************************");
			break;	

		case 94:

			log.info("*****************   BEGIN  Testcase 94 *******************************");
			//ti.useTLS = false;
			TestResult tr94;
			try {
				tr94 = sTest.testStarttlsCCDAandText(ti);
				tr94.id = 94;
				res.add(tr94);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 94 *******************************");
			break;

		case 95:

			log.info("*****************   BEGIN  Testcase 95 *******************************");
			//ti.useTLS = false;
			TestResult tr95;
			try {
				tr95 = sTest.testStarttlsCCDAandPdf(ti);
				tr95.id = 95;
				res.add(tr95);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 95 *******************************");
			break;	

		case 96:

			log.info("*****************   BEGIN  Testcase 96 *******************************");
			//ti.useTLS = false;
			TestResult tr96;
			try {
				tr96 = sTest.testStarttlsXDMandText(ti);
				tr96.id = 96;
				res.add(tr96);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 96 *******************************");
			break;	

		case 97:

			log.info("*****************   BEGIN  Testcase 97 *******************************");
			//ti.useTLS = false;
			TestResult tr97;
			try {
				tr97 = sTest.testSendBadCCDA(ti, "/cda-samples/ToC_Ambulatory_No_Stylesheet.xml");
				tr97.id = 97;
				res.add(tr97);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 97 *******************************");
			break;	

		case 98:

			log.info("*****************   BEGIN  Testcase 98 *******************************");
			//ti.useTLS = false;
			TestResult tr98;
			try {
				tr98 = sTest.testSendBadCCDA(ti, "/cda-samples/ToC_Ambulatory_Bad_Stylesheet.xml");
				tr98.id = 98;
				res.add(tr98);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 98 *******************************");
			break;	
		case 99:

			log.info("*****************   BEGIN  Testcase 99 *******************************");
			//ti.useTLS = false;
			TestResult tr99;
			try {
				tr96 = sTest.testStarttlsXDMBadHtml(ti);
				tr96.id = 96;
				res.add(tr96);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 99 *******************************");
			break;	

		case 100:

			log.info("*****************   BEGIN  Testcase 100 *******************************");
			//ti.useTLS = false;
			TestResult tr100;
			try {
				tr100 = sTest.testSendXDMApplicationOctect(ti, "/cda-samples/CCDA_Ambulatory_in_XDM.zip");
				tr100.id = 100;
				res.add(tr100);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 100 *******************************");
			break;	

		case 90:

			log.info("*****************   BEGIN  Testcase 90 *******************************");
			//ti.useTLS = false;
			TestResult tr90;
			try {
				tr90 = sTest.testSendXDMApplicationXml(ti, "/cda-samples/CCDA_Ambulatory_in_XDM.zip");
				tr90.id = 90;
				res.add(tr90);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			log.info("*****************   END  Testcase 90 *******************************");
			break;	
		case 10:
			res.add(nTest.testBadData(ti));
			break;

		case 11:
			res.addAll(nTest.testInvalidSMTP(ti));
			break;

		case 12:
			res.add(nTest.testBigData(ti));
			break;

		case 13:
			res.addAll(nTest.testTimeout(ti));
			break;

		case 15:
			log.warn("------------------------->    Test case (bad certificate) "
					+ i + " not implemented!!!!!!!!!!!!!!!!!!!!!!!!!!");
			break;

		case 16:
			log.info("*****************   BEGIN  Testcase 16 *******************************");
		//	ti.useTLS = true;
			TestResult tr16 = sTest.testStarttls(ti);
			tr16.id = 16;
			res.add(tr16);
			log.info("*****************   END  Testcase 16 *******************************");
			break;

		case 17:
			res.add(nTest.testInvalidStartTLS(ti));
			break;

		case 18:
			log.info("*****************   BEGIN  Testcase 18 *******************************");

			TestResult tr18;
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				tr18 = tTest.fetchMail(ti,prop.getProperty("ett.hisp.address"));
				tr18.id = 18;
				res.add(tr18);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 18 *******************************");
			break;

		case 19:
			log.warn("------------------------->    Test case (MD5)" + i
					+ " not implemented!!!!!!!!!!!!!!!!!!!!!!!!!!");
			break;

		case 20:
			log.info("*****************   BEGIN  Testcase 20 *******************************");
			TestResult tr20 = sTest.testPlainSasl(ti, false);
			tr20.id = 20;
			res.add(tr20);
			log.info("*****************   END  Testcase 20 *******************************");
			break;


		case 22:
			log.info("*****************   BEGIN  Testcase 22 *******************************");
			TestResult tr22 = sTest.testPlainSasl(ti, true);
			tr22.id = 22;
			res.add(tr22);
			log.info("*****************   END  Testcase 22 *******************************");
			break;

		


		case 101:
			log.info("*****************   BEGIN  Testcase 101 *******************************");
		//	ti.useTLS = true;
			TestResult tr101;
			tr101 = mu2senderTests.testBadAddressSmtp(ti,false);
			tr101.id = 101;
			res.add(tr101);

			log.info("*****************   END  Testcase 101 *******************************");
			break;

		case 1012:
			log.info("*****************   BEGIN  Testcase 1012 *******************************");
		//	ti.useTLS = true;
			TestResult tr1012;
			try {
				tr1012 = mu2receiverTests.fetchMail(ti);
				tr1012.id = 1012;
				res.add(tr1012);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			log.info("*****************   END  Testcase 1012 *******************************");
			break;

		case 102:
			log.info("*****************   BEGIN  Testcase 102 *******************************");
		//	ti.useTLS = true;
			TestResult tr102;
			tr102 = mu2senderTests.testMu2TwoSmtp(ti,false);
			tr102.id = 102;
			res.add(tr102);
			log.info("*****************   END  Testcase 102 *******************************");
			break;

		case 103:
			log.info("*****************   BEGIN  Testcase 103 *******************************");
			//ti.useTLS = true;
			TestResult tr103;
			tr103 = mu2senderTests.testMu2ThreeSmtp(ti,false);
			tr103.id = 103;
			res.add(tr103);

			log.info("*****************   END  Testcase 103 *******************************");
			break;

		case 104:
			log.info("*****************   BEGIN  Testcase 104 *******************************");
			//ti.useTLS = true;
			TestResult tr104;
			tr104 = mu2senderTests.testMu2FourSmtp(ti,false);
			tr104.id = 104;
			res.add(tr104);

			log.info("*****************   END  Testcase 104 *******************************");
			break;

		case 105:
			log.info("*****************   BEGIN  Testcase 105 *******************************");
			//ti.useTLS = true;
			TestResult tr105;
			tr105 = mu2senderTests.testBadAddress(ti,false);
			tr105.id = 105;
			res.add(tr105);

			log.info("*****************   END  Testcase 105 *******************************");
			break;

		case 106:
			log.info("*****************   BEGIN  Testcase 106 *******************************");
			//ti.useTLS = true;
			TestResult tr106;
			tr106 = mu2senderTests.testMu2Two(ti,false);
			tr106.id = 106;
			res.add(tr106);
			log.info("*****************   END  Testcase 106 *******************************");
			break;

		case 107:
			log.info("*****************   BEGIN  Testcase 107 *******************************");
			//ti.useTLS = true;
			TestResult tr107;
			tr107 = mu2senderTests.testMu2Three(ti,false);
			tr107.id = 107;
			res.add(tr107);

			log.info("*****************   END  Testcase 107 *******************************");
			break;

		case 108:
			log.info("*****************   BEGIN  Testcase 108 *******************************");
			//ti.useTLS = true;
			TestResult tr108;
			tr108 = mu2senderTests.testMu2Four(ti,false);
			tr108.id = 108;
			res.add(tr108);

			log.info("*****************   END  Testcase 108 *******************************");
			break;

		case 109:
			log.info("*****************   BEGIN  Testcase 109 *******************************");
			//ti.useTLS = true;
			TestResult tr109;
			tr109 = mu2senderTests.testBadAddressPop(ti,false);
			tr109.id = 109;
			res.add(tr109);

			log.info("*****************   END  Testcase 109 *******************************");
			break;

		case 110:
			log.info("*****************   BEGIN  Testcase 110 *******************************");
			//ti.useTLS = true;
			TestResult tr110;
			tr110 = mu2senderTests.testMu2TwoPop(ti,false);
			tr110.id = 110;
			res.add(tr110);
			log.info("*****************   END  Testcase 110 *******************************");
			break;

		case 111:
			log.info("*****************   BEGIN  Testcase 111 *******************************");
			//ti.useTLS = true;
			TestResult tr111;
			tr111 = mu2senderTests.testMu2ThreePop(ti,false);
			tr111.id = 111;
			res.add(tr111);

			log.info("*****************   END  Testcase 111 *******************************");
			break;

		case 112:
			log.info("*****************   BEGIN  Testcase 112 *******************************");
			//ti.useTLS = true;
			TestResult tr112;
			tr112 = mu2senderTests.testMu2FourPop(ti,false);
			tr112.id = 112;
			res.add(tr112);

			log.info("*****************   END  Testcase 112 *******************************");
			break;
		case 117:
			log.info("*****************   BEGIN  Testcase 117 *******************************");

			TestResult tr117;
			try {
				tr117 = tTest.fetchUniqueId(ti);
				tr117.id = 117;
				res.add(tr117);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 117 *******************************");
			break;

		case 118:
			log.info("*****************   BEGIN  Testcase 118 *******************************");

			TestResult tr118;
			try {
				tr118 = tTest.fetchManualTest(ti);
				tr118.id = 118;
				res.add(tr118);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 118 *******************************");
			break;

		case 119:
			log.info("*****************   BEGIN  Testcase 119 *******************************");

			TestResult tr119;
			try {
				tr119 = tTest.fetchandSendMDN(ti);
				tr119.id = 119;
				res.add(tr119);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 119 *******************************");
			break;

		case 120:
			log.info("*****************   BEGIN  Testcase 120 *******************************");

			TestResult tr120;
			try {
				tr120 = tTest.fetchandSendMDN(ti);
				tr120.id = 120;
				res.add(tr120);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 120 *******************************");
			break;

		case 121:
			log.info("*****************   BEGIN  Testcase 121 *******************************");

			TestResult tr121;
			tr121 = mu2senderTests.testDispositionNotification(ti);
			tr121.id = 121;
			res.add(tr121);
			log.info("*****************   END  Testcase 121 *******************************");
			break;

		case 521:
			log.info("*****************   BEGIN  Testcase 521 *******************************");

			TestResult tr521;
			tr521 = mu2senderTests.testDispositionNotificationSmtp(ti);
			tr521.id = 521;
			res.add(tr521);
			log.info("*****************   END  Testcase 521 *******************************");
			break;

		case 151:
			log.info("*****************   BEGIN  Testcase 151 *******************************");

			TestResult tr151;
			tr151 = mu2senderTests.testDispositionNotificationPop(ti);
			tr151.id = 151;
			res.add(tr151);
			log.info("*****************   END  Testcase 151 *******************************");
			break;


		case 122:
			log.info("*****************   BEGIN  Testcase 122 *******************************");

			TestResult tr122;
			tr122 = mu2senderTests.testBadDispositionNotification(ti);
			tr122.id = 122;
			res.add(tr122);
			log.info("*****************   END  Testcase 122 *******************************");
			break;

		case 522:
			log.info("*****************   BEGIN  Testcase 522 *******************************");

			TestResult tr522;
			tr522 = mu2senderTests.testBadDispositionNotificationSmtp(ti);
			tr522.id = 522;
			res.add(tr522);
			log.info("*****************   END  Testcase 522 *******************************");
			break;

		case 152:
			log.info("*****************   BEGIN  Testcase 152 *******************************");

			TestResult tr152;
			tr152 = mu2senderTests.testBadDispositionNotificationPop(ti);
			tr152.id = 152;
			res.add(tr152);
			log.info("*****************   END  Testcase 152 *******************************");
			break;

		case 523:
			log.info("*****************   BEGIN  Testcase 523 *******************************");
			//ti.useTLS = true;
			TestResult tr523;
			tr523 = mu2senderTests.testBadAddressSmtp(ti,true);
			tr523.id = 523;
			res.add(tr523);
			log.info("*****************   END  Testcase 523 *******************************");
			break;	

		case 123:
			log.info("*****************   BEGIN  Testcase 123 *******************************");
			//ti.useTLS = true;
			TestResult tr123;
			tr123 = mu2senderTests.testBadAddress(ti,true);
			tr123.id = 123;
			res.add(tr123);
			log.info("*****************   END  Testcase 123 *******************************");
			break;

		case 623:
			log.info("*****************   BEGIN  Testcase 623 *******************************");
			//ti.useTLS = true;
			TestResult tr623;
			tr623 = mu2senderTests.testBadAddressPop(ti,true);
			tr623.id = 623;
			res.add(tr623);
			log.info("*****************   END  Testcase 623 *******************************");
			break;

		case 524:
			log.info("*****************   BEGIN  Testcase 524 *******************************");
			//ti.useTLS = true;
			TestResult tr524;
			tr524 = mu2senderTests.testMu2TwoSmtp(ti,true);
			tr524.id = 524;
			res.add(tr524);

			log.info("*****************   END  Testcase 524 *******************************");
			break;

		case 124:
			log.info("*****************   BEGIN  Testcase 124 *******************************");
			//ti.useTLS = true;
			TestResult tr124;
			tr124 = mu2senderTests.testMu2Two(ti,true);
			tr124.id = 124;
			res.add(tr124);

			log.info("*****************   END  Testcase 124 *******************************");
			break;

		case 624:
			log.info("*****************   BEGIN  Testcase 624 *******************************");
			//ti.useTLS = true;
			TestResult tr624;
			tr624 = mu2senderTests.testMu2TwoPop(ti,true);
			tr624.id = 624;
			res.add(tr624);

			log.info("*****************   END  Testcase 124 *******************************");
			break;

		case 125:
			log.info("*****************   BEGIN  Testcase 125 *******************************");
			//ti.useTLS = true;
			TestResult tr125;
			tr125 = mu2senderTests.testMu2Three(ti,true);
			tr125.id = 125;
			res.add(tr125);

			log.info("*****************   END  Testcase 125 *******************************");
			break;

		case 525:
			log.info("*****************   BEGIN  Testcase 525 *******************************");
			//ti.useTLS = true;
			TestResult tr525;
			tr525 = mu2senderTests.testMu2ThreeSmtp(ti,true);
			tr525.id = 125;
			res.add(tr525);

			log.info("*****************   END  Testcase 525 *******************************");
			break;

		case 625:
			log.info("*****************   BEGIN  Testcase 625 *******************************");
			//ti.useTLS = true;
			TestResult tr625;
			tr625 = mu2senderTests.testMu2ThreePop(ti,true);
			tr625.id = 625;
			res.add(tr625);

			log.info("*****************   END  Testcase 625 *******************************");
			break;

		case 126:
			log.info("*****************   BEGIN  Testcase 126 *******************************");
			//ti.useTLS = true;
			TestResult tr126;
			tr126 = mu2senderTests.testMu2Four(ti,true);
			tr126.id = 126;
			res.add(tr126);

			log.info("*****************   END  Testcase 126 *******************************");
			break;

		case 526:
			log.info("*****************   BEGIN  Testcase 526 *******************************");
			//ti.useTLS = true;
			TestResult tr526;
			tr526 = mu2senderTests.testMu2FourSmtp(ti,true);
			tr526.id = 526;
			res.add(tr526);

			log.info("*****************   END  Testcase 526 *******************************");
			break;

		case 626:
			log.info("*****************   BEGIN  Testcase 626 *******************************");
			//ti.useTLS = true;
			TestResult tr626;
			tr626 = mu2senderTests.testMu2FourPop(ti,true);
			tr626.id = 126;
			res.add(tr626);

			log.info("*****************   END  Testcase 626 *******************************");
			break;

		case 127:
			log.info("*****************   BEGIN  Testcase 127 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				TestResult tr127;
				tr127 = mu2senderTests.testMu2TwoSeven(ti,prop.getProperty("processed.only"));
				tr127.id = 127;
				res.add(tr127);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 127 *******************************");
			break;

		case 527:
			log.info("*****************   BEGIN  Testcase 527 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				TestResult tr527;
				tr527 = mu2senderTests.testMu2TwoSevenSmtp(ti,prop.getProperty("processed.only"));
				tr527.id = 527;
				res.add(tr527);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 527 *******************************");
			break;

		case 157:
			log.info("*****************   BEGIN  Testcase 157 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				TestResult tr157;
				tr157 = mu2senderTests.testMu2TwoSevenPop(ti,prop.getProperty("processed.only"));
				tr157.id = 157;
				res.add(tr157);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 157 *******************************");
			break;
		case 128:
			log.info("*****************   BEGIN  Testcase 128 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				String address = "delaydispatched"+ti.sutCommandTimeoutInSeconds+"@"+prop.getProperty("direct.listener.domainName");
				TestResult tr128;
				tr128 = mu2senderTests.testMu2TwoEight(ti,address);
				tr128.id = 128;
				res.add(tr128);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 128 *******************************");
			break;

		case 528:
			log.info("*****************   BEGIN  Testcase 528 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				TestResult tr528;
				String address = "delaydispatched"+ti.sutCommandTimeoutInSeconds+"@"+prop.getProperty("direct.listener.domainName");
				tr528 = mu2senderTests.testMu2TwoEightSmtp(ti,address);
				tr528.id = 528;
				res.add(tr528);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 528 *******************************");
			break;

		case 158:
			log.info("*****************   BEGIN  Testcase 158 *******************************");
			try {
				Properties prop = new Properties();
				String path = "./application.properties";
				FileInputStream file;
				file = new FileInputStream(path);
				prop.load(file);
				file.close();
				//ti.useTLS = true;
				TestResult tr158;
				String address = "delaydispatched"+ti.sutCommandTimeoutInSeconds+"@"+prop.getProperty("direct.listener.domainName");
				tr158 = mu2senderTests.testMu2TwoEightPop(ti,address);
				tr158.id = 158;
				res.add(tr158);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			log.info("*****************   END  Testcase 158 *******************************");
			break;


		case 129:
			log.info("*****************   BEGIN  Testcase 129 *******************************");

			TestResult tr129;
			tr129 = mu2senderTests.testPositiveDeliveryNotification(ti);
			tr129.id = 129;
			res.add(tr129);
			log.info("*****************   END  Testcase 129 *******************************");
			break;

		case 529:
			log.info("*****************   BEGIN  Testcase 529 *******************************");

			TestResult tr529;
			tr529 = mu2senderTests.testPositiveDeliveryNotificationSmtp(ti);
			tr529.id = 529;
			res.add(tr529);
			log.info("*****************   END  Testcase 529 *******************************");
			break;

		case 159:
			log.info("*****************   BEGIN  Testcase 159 *******************************");

			TestResult tr159;
			tr159 = mu2senderTests.testPositiveDeliveryNotificationPop(ti);
			tr159.id = 159;
			res.add(tr159);
			log.info("*****************   END  Testcase 159 *******************************");
			break;

		case 139:
			log.info("*****************   BEGIN  Testcase 139 *******************************");

			TestResult tr139;
			tr139 = mu2senderTests.testDispositionNotificationSutReceiver(ti);
			tr139.id = 139;
			res.add(tr139);
			log.info("*****************   END  Testcase 139 *******************************");
			break;

		case 140:
			log.info("*****************   BEGIN  Testcase 140 *******************************");

			TestResult tr140;
			tr140 = mu2senderTests.testBadDispositionNotificationSutReceiver(ti);
			tr140.id = 140;
			res.add(tr140);
			log.info("*****************   END  Testcase 140 *******************************");
			break;

		case 141:
			log.info("*****************   BEGIN  Testcase 141 *******************************");

			TestResult tr141;
			tr141 = mu2senderTests.testBadAddressSutReceiverTimeout(ti);
			tr141.id = 141;
			res.add(tr141);
			log.info("*****************   END  Testcase 141 *******************************");
			break;

		case 142:
			log.info("*****************   BEGIN  Testcase 142 *******************************");

			TestResult tr142;
			tr142 = mu2senderTests.testBadAddressSutReceiver(ti);
			tr142.id = 142;
			res.add(tr142);
			log.info("*****************   END  Testcase 142 *******************************");
			break;

		case 145:
			log.info("*****************   BEGIN  Testcase 145 *******************************");

			TestResult tr145;
			try {
				tr145 = tTest.fetchUniqueIdHeaders(ti);
				tr145.id = 145;
				res.add(tr145);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 145 *******************************");
			break;

		case 146:
			log.info("*****************   BEGIN  Testcase 146 *******************************");

			TestResult tr146;
			try {
				tr146 = tTest.fetchDispositionNotificaton(ti);
				tr146.id = 146;
				res.add(tr146);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 146 *******************************");
			break;

		case 147:
			log.info("*****************   BEGIN  Testcase 147 *******************************");

			TestResult tr147;
			try {
				tr147 = tTest.fetchManualTest(ti);
				tr147.id = 147;
				res.add(tr147);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 147 *******************************");
			break;

		case 201:
			log.info("*****************   BEGIN  Testcase 201 *******************************");

			TestResult tr201;

			try {
				tr201 = tTest.SocketImap(ti);
				tr201.id = 201;
				res.add(tr201);
			} catch (KeyManagementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// This 

			log.info("*****************   END  Testcase 201 *******************************");
			break;

		case 204:
			log.info("*****************   BEGIN  Testcase 204 *******************************");

			TestResult tr204;

			tr204 = tTest.SocketStarttls(ti);
			tr204.id = 204;
			res.add(tr204);

			log.info("*****************   END  Testcase 204 *******************************");
			break;

		case 205:
			log.info("*****************   BEGIN  Testcase 205 *******************************");

			TestResult tr205;
			try {
				tr205 = tTest.imapFetch(ti);
				tr205.id = 205;
				res.add(tr205);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 205 *******************************");
			break;

		case 209:
			log.info("*****************   BEGIN  Testcase 209 *******************************");

			TestResult tr209;

			tr209 = tTest.SocketImapBadSyntax(ti);
			tr209.id = 209;
			res.add(tr209);

			log.info("*****************   END  Testcase 209 *******************************");
			break;

		case 210:
			log.info("*****************   BEGIN  Testcase 210 *******************************");

			TestResult tr210;

			tr210 = tTest.SocketImapBadState(ti);
			tr210.id = 210;
			res.add(tr210);

			log.info("*****************   END  Testcase 210 *******************************");
			break;

		case 212:
			log.info("*****************   BEGIN  Testcase 212 *******************************");

			TestResult tr212;
			try {
				tr212 = tTest.imapFetchUid(ti);
				tr212.id = 212;
				res.add(tr212);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 212 *******************************");
			break;

		case 312:
			log.info("*****************   BEGIN  Testcase 312 *******************************");

			TestResult tr312;
			try {
				tr312 = tTest.SocketPopUid(ti);
				tr312.id = 312;
				res.add(tr312);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 312 *******************************");
			break;


		case 217:
			log.info("*****************   BEGIN  Testcase 217 *******************************");

			TestResult tr217;
			try {
				tr217 = tTest.imapFetchWrongPass(ti);
				tr217.id = 217;
				res.add(tr217);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 217 *******************************");
			break;

		case 219:
			log.info("*****************   BEGIN  Testcase 219 *******************************");

			TestResult tr219;
			try {
				tr219 = tTest.fetchManualTestEdge(ti);
				tr219.id = 219;
				res.add(tr219);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 219 *******************************");
			break;

		case 227:
			log.info("*****************   BEGIN  Testcase 227 *******************************");

			TestResult tr227;
			try {
				tr227 = tTest.fetchTestMailboxNames(ti);
				tr227.id = 227;
				res.add(tr227);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 227 *******************************");
			break;

		case 228:
			log.info("*****************   BEGIN  Testcase 228 *******************************");

			TestResult tr228;
			try {
				tr228 = tTest.fetchTestMailboxNames(ti);
				tr228.id = 228;
				res.add(tr228);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 228 *******************************");
			break;
		case 2291:
			log.info("*****************   BEGIN  Testcase 2291 *******************************");

			TestResult tr2291;
			try {
				tr2291 = tTest.fetchTestMailboxNames(ti);
				tr2291.id = 2291;
				res.add(tr2291);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 2291 *******************************");
			break;
		case 2292:
			log.info("*****************   BEGIN  Testcase 2292 *******************************");

			TestResult tr2292;
			try {
				tr2292 = tTest.fetchTestMailboxNames(ti);
				tr2292.id = 2292;
				res.add(tr2292);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 2292 *******************************");
			break;
		case 230:
			log.info("*****************   BEGIN  Testcase 230 *******************************");

			TestResult tr230;
			try {
				tr230 = tTest.fetchTestMailboxNames(ti);
				tr230.id = 230;
				res.add(tr230);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 230 *******************************");
			break;
		case 231:
			log.info("*****************   BEGIN  Testcase 231 *******************************");

			TestResult tr231;
			try {
				tr231 = tTest.fetchTestMailboxNames(ti);
				tr231.id = 221;
				res.add(tr231);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 231 *******************************");
			break;

		case 221:
			log.info("*****************   BEGIN  Testcase 221 *******************************");

			TestResult tr221;
			try {
				tr221 = tTest.fetchTestMailboxNames(ti);
				tr221.id = 221;
				res.add(tr221);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 221 *******************************");
			break;

		case 222:
			log.info("*****************   BEGIN  Testcase 222 *******************************");

			TestResult tr222;
			try {
				tr222 = tTest.fetchTestMailboxNames(ti);
				tr222.id = 222;
				res.add(tr222);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 222 *******************************");
			break;
		case 225:
			log.info("*****************   BEGIN  Testcase 225 *******************************");

			TestResult tr225;

			tr225 = tTest.testStatusUpdate(ti);
			tr225.id = 225;
			res.add(tr225);

			log.info("*****************   END  Testcase 225 *******************************");
			break;

		case 232:
			log.info("*****************   BEGIN  Testcase 232 *******************************");

			TestResult tr232;

			try {
				tr232 = tTest.fetchMailValidateImap(ti);
				tr232.id = 232;
				res.add(tr232);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}


			log.info("*****************   END  Testcase 232 *******************************");
			break;

		case 301:
			log.info("*****************   BEGIN  Testcase 301 *******************************");

			TestResult tr301;

			try {
				tr301 = tTest.SocketPop(ti);
				tr301.id = 301;
				res.add(tr301);
			} catch (KeyManagementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			log.info("*****************   END  Testcase 301 *******************************");
			break;

		case 303:
			log.info("*****************   BEGIN  Testcase 303 *******************************");

			TestResult tr303;
			try {
				tr303 = tTest.fetchMailPop(ti);
				tr303.id = 303;
				res.add(tr303);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log.info("*****************   END  Testcase 303 *******************************");
			break;

		case 305:
			log.info("*****************   BEGIN  Testcase 305 *******************************");

			TestResult tr305;
			try {
				tr305 = tTest.SocketPopStat(ti);
				tr305.id = 305;
				res.add(tr305);
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			log.info("*****************   END  Testcase 305 *******************************");
			break;

		case 309:
			log.info("*****************   BEGIN  Testcase 309 *******************************");

			TestResult tr309;

			try {
				tr309 = tTest.SocketPopBadSyntax(ti);
				tr309.id = 309;
				res.add(tr309);
			} catch (KeyManagementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			log.info("*****************   END  Testcase 309 *******************************");
			break;

		case 310:
			log.info("*****************   BEGIN  Testcase 310 *******************************");

			TestResult tr310;

			try {
				tr310 = tTest.SocketPopBadState(ti);
				tr310.id = 310;
				res.add(tr310);
			} catch (KeyManagementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			log.info("*****************   END  Testcase 310 *******************************");
			break;

		case 317:
			log.info("*****************   BEGIN  Testcase 317 *******************************");

			TestResult tr317;
			try {
				tr317 = tTest.popFetchWrongPass(ti);
				tr317.id = 317;
				res.add(tr317);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 317 *******************************");
			break;

		case 319:
			log.info("*****************   BEGIN  Testcase 319 *******************************");

			TestResult tr319;
			try {
				tr319 = tTest.fetchTestMailboxNames(ti);
				tr319.id = 319;
				res.add(tr319);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 319 *******************************");
			break;

		case 322:
			log.info("*****************   BEGIN  Testcase 322 *******************************");

			TestResult tr322;
			try {
				tr322 = tTest.fetchTestMailboxNames(ti);
				tr322.id = 322;
				res.add(tr322);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 322 *******************************");
			break;

		case 327:
			log.info("*****************   BEGIN  Testcase 327 *******************************");

			TestResult tr327;
			try {
				tr327 = tTest.fetchTestMailboxNames(ti);
				tr327.id = 327;
				res.add(tr327);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 327 *******************************");
			break;

		case 328:
			log.info("*****************   BEGIN  Testcase 328 *******************************");

			TestResult tr328;
			try {
				tr328 = tTest.fetchTestMailboxNames(ti);
				tr328.id = 328;
				res.add(tr328);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 328 *******************************");
			break;

		case 3291:
			log.info("*****************   BEGIN  Testcase 3291 *******************************");

			TestResult tr3291;
			try {
				tr3291 = tTest.fetchTestMailboxNames(ti);
				tr3291.id = 3291;
				res.add(tr3291);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 3291 *******************************");
			break;
			
		case 3292:
			log.info("*****************   BEGIN  Testcase 3292 *******************************");

			TestResult tr3292;
			try {
				tr3292 = tTest.fetchTestMailboxNames(ti);
				tr3292.id = 3292;
				res.add(tr3292);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 3292 *******************************");
			break;

		case 330:
			log.info("*****************   BEGIN  Testcase 330 *******************************");

			TestResult tr330;
			try {
				tr330 = tTest.fetchTestMailboxNames(ti);
				tr330.id = 330;
				res.add(tr330);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 330 *******************************");
			break;

		case 331:
			log.info("*****************   BEGIN  Testcase 331 *******************************");

			TestResult tr331;
			try {
				tr331 = tTest.fetchTestMailboxNames(ti);
				res.add(tr331);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("*****************   END  Testcase 331 *******************************");
			break;


		case 332:
			log.info("*****************   BEGIN  Testcase 332 *******************************");

			TestResult tr332;

			try {
				tr332 = tTest.fetchMailValidatePop(ti);
				tr332.id = 332;
				res.add(tr332);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}


			log.info("*****************   END  Testcase 332 *******************************");
			break;

		default:
			log.warn("------------------------->    Test case " + i
					+ " not implemented!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}

		log.info("-------------------------> END runTestCase " + i);
		return res.toArray(new ITestResult[0]);
	}

	/**
	 * Concatenates two arrays and returns the concatenated array
	 * 
	 * @param first
	 *            array
	 * @param second
	 *            array
	 * @return concatenated array
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static int[] concati(int[] first, int[] second) {
		int[] third = new int[first.length + second.length];

		System.arraycopy(first, 0, third, 0, first.length);
		System.arraycopy(second, 0, third, first.length, second.length);
		return third;
	}
}