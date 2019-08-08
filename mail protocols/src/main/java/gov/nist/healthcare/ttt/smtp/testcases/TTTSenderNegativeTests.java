package gov.nist.healthcare.ttt.smtp.testcases;

import static gov.nist.healthcare.ttt.smtp.util.TestData.LONG_DATA;
import static gov.nist.healthcare.ttt.smtp.util.TestData.LONG_FROM;
import static gov.nist.healthcare.ttt.smtp.util.TestData.LONG_TO;
import static gov.nist.healthcare.ttt.smtp.util.TestData.VALID_DATA;
import static gov.nist.healthcare.ttt.smtp.util.TestData.VALID_DOMAIN;
import static gov.nist.healthcare.ttt.smtp.util.TestData.VALID_FROM;
import static gov.nist.healthcare.ttt.smtp.util.TestData.VALID_TO;
import gov.nist.healthcare.ttt.smtp.ITestResult;
import gov.nist.healthcare.ttt.smtp.NTestResult;
import gov.nist.healthcare.ttt.smtp.TestInput;
import gov.nist.healthcare.ttt.smtp.TestResult;
import gov.nist.healthcare.ttt.smtp.TestResult.CriteriaStatus;
import gov.nist.healthcare.ttt.smtp.server.AbstractSMTPSender;
import gov.nist.healthcare.ttt.smtp.server.SocketSMTPSender;
import gov.nist.healthcare.ttt.smtp.util.ReqRes;
import gov.nist.healthcare.ttt.smtp.util.TestData;
import gov.nist.healthcare.ttt.smtp.util.Utils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class TTTSenderNegativeTests {
	static Logger log = Logger.getLogger(TTTSenderNegativeTests.class);

	AbstractSMTPSender smtpSender = new SocketSMTPSender();
	Properties config;

	/**
	 * @param ti
	 * 
	 * 
	 * @return TestResult
	 */

	public TTTSenderNegativeTests(AbstractSMTPSender asmtp, Properties _config) {
		smtpSender = asmtp;
		config = _config;
	}

	public void testInit(TestInput ti) throws Exception {
		smtpSender.close(); // just in case, close the previous connection
		smtpSender.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
				: ti.sutSmtpPort);
		smtpSender.sndMsgHELO("testing.com");
		//Utils.pause();
		if (ti.useTLS)
			smtpSender.startTLS();
		//Utils.pause();
		if (!ti.sutUserName.isEmpty())
		//	smtpSender.AUTHLOGIN(ti.sutUserName, ti.sutPassword);
		    smtpSender.AUTHPLAIN(ti.sutUserName, ti.sutPassword);
		//Utils.pause();
	}

	public TestResult testValid(TestInput ti) {
		ti.useTLS = false;
		NTestResult res = new NTestResult();
		try {
			testInit(ti);
		} catch (Exception e) {
			ReqRes r = new ReqRes("ERROR", e.getLocalizedMessage() + "\n");
			res.add(r);
			
		}
		
		res.setTestCaseDesc("TestValidData");
		res.setTestCaseId(0);
		res.add(smtpSender.sndMsgEHLO("testing.com"));
		res.add(smtpSender.sndMsgMAILFROM(ti.tttEmailAddress));
		res.add(smtpSender.sndMsgRCPTTO(ti.sutEmailAddress));
		res.add(smtpSender.sndMsgDATA("Test Mail UseTLS: " + ti.useTLS
				+ "\r\n.\r\n"));
		res.add(smtpSender.sndMsgEHLO("xx.xx"));
		return res;
	}

	public TestResult testTest(TestInput ti) {
		ti.useTLS = true;
		NTestResult res = new NTestResult();
		try {
			testInit(ti);
		} catch (Exception e) {
			ReqRes r = new ReqRes("ERROR", e.getLocalizedMessage() + "\n");
			res.add(r);
		}
		
		smtpSender.setTimeOut(1000); // we wait for twice the time
		res.setTestCaseDesc("Testing TlS: " + config.getProperty("desc13"));
		res.setTestCaseId(0);
		smtpSender.startTLS();
		smtpSender.sndMsgEHLO(VALID_DOMAIN);
		res.add(smtpSender.sndMsgMAILFROM(VALID_FROM));
		return res;
	}

	/**
	 * Implements testcase #10 Send invalid data as part of the DATA command
	 * 
	 * @return
	 */
	public TestResult testBadData(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("TestBadData:" + config.getProperty("desc10"));
		res.setTestCaseId(10);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		
		String fromAddress = "";
		if (ti.sutUserName.contains("@")){
			 fromAddress = ti.sutUserName;
		}
		
		else {
			 fromAddress = ti.sutUserName + ti.sutSmtpAddress;
		}


		smtpSender.setTimeOut(30); // we wait for 30 seconds
		res.expectedResult.add(-2);
		res.add(smtpSender.sndMsgEHLO(VALID_DOMAIN));
		res.add(smtpSender.sndMsgMAILFROM(fromAddress));
		res.add(smtpSender.sndMsgRCPTTO(VALID_TO));
		String badData = String
				.format(AbstractSMTPSender.DATA_LFCR, VALID_DATA);
		res.add(smtpSender.getResponseForRequest(badData), true);
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System rejects invalid data as part of DATA command.\n");
			res.add(r1);
		}
		smtpSender.restoreTimeOut();
		return res;
	}

	/**
	 * Implements testcase #11 Initiate SMTP Session using invalid commands to
	 * ensure the SUT does not behave incorrectly.
	 * 
	 * @param reset
	 * @return
	 */

	public ArrayList<ITestResult> testInvalidSMTP(TestInput ti) {
		ArrayList<ITestResult> r = new ArrayList<ITestResult>();
		r.add(testInvalidSMTP1(ti));
		r.add(testInvalidSMTP2(ti));
		// r.add(testInvalidSMTP3(ti));
		return r;
	}

	public TestResult testInvalidSMTP1(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("TestInvalidSMTP1: " + config.getProperty("desc11"));
		res.setTestCaseId(11);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}

		res.add(smtpSender.sndMsgEHLO(VALID_DOMAIN));
		res.add(smtpSender.sndMsgRCPTTO(TestData.VALID_TO), true);
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System rejects bad SMTP commands.\n");
			res.add(r1);
		}
		return res;
	}

	/**
	 * Implements testcase #11 Initiate SMTP Session using invalid commands to
	 * ensure the SUT does not behave incorrectly.
	 * 
	 * @param reset
	 * @return
	 */
	public TestResult testInvalidSMTP2(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("TestInvalidSMTP2: " + config.getProperty("desc11"));
		res.setTestCaseId(11);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		
		
		res.add(smtpSender.sndMsgEHLO(VALID_DOMAIN));
		res.add(smtpSender.sndMsgDATA("Message: DATA before MAIL"), true);
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System rejects bad SMTP commands.\n");
			res.add(r1);
		}
		return res;
	}

	/**
	 * Implements testcase #11 Initiate SMTP Session using invalid commands to
	 * ensure the SUT does not behave incorrectly.
	 * 
	 * @param ti
	 * @return
	 */
	public TestResult testInvalidSMTP3(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("TestInvalidSMTP2: " + config.getProperty("desc11"));
		res.setTestCaseId(11);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		
		res.add(smtpSender.sndMsgDATA("Message: DATA before HELO and MAIL"), true);
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System rejects bad SMTP commands.\n");
			res.add(r1);
		}
		return res;
	}

	/**
	 * Implements Testcase #12 Test for Size limits of various SMTP commands
	 * 
	 * @return
	 */
	public TestResult testBigData(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("testBigData: " + config.getProperty("desc12"));
		res.setTestCaseId(12);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		res.add(smtpSender.sndMsgEHLO(TestData.VALID_DOMAIN));
		res.add(smtpSender.sndMsgMAILFROM(LONG_FROM));
		for (int i = 0; i < 2; i++)
			res.add(smtpSender.sndMsgRCPTTO(LONG_TO));
		res.add(smtpSender.sndMsgDATA(LONG_DATA));
		return res;
	}

	/**
	 * Implements Testcase #13 Test for timeouts of various SMTP commands
	 * 
	 * @return
	 */

	public ArrayList<ITestResult> testTimeout(TestInput ti) {
		ArrayList<ITestResult> r = new ArrayList<ITestResult>();
		r.add(testTimeout1(ti));
		r.add(testTimeout2(ti));
		return r;
	}

	public TestResult testTimeout1(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("Testing Timeouts: " + config.getProperty("desc13"));
		res.setTestCaseId(13);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		
		int timeout = ti.sutCommandTimeoutInSeconds > 0 ? ti.sutCommandTimeoutInSeconds : 30;
		log.info("Setting timeout for " + (timeout) + " seconds!");
		smtpSender.setTimeOut(timeout); // we wait for
																	// twice the
																	// time
		
		String fromAddress = "";
		if (ti.sutUserName.contains("@")){
			 fromAddress = ti.sutUserName;
		}
		
		else {
			 fromAddress = ti.sutUserName + ti.sutSmtpAddress;
		}
		res.add(smtpSender.sndMsgEHLO(VALID_DOMAIN));
		res.add(smtpSender.sndMsgMAILFROM(fromAddress));
		res.add(smtpSender.sndMsgRCPTTO(VALID_TO));

		res.add(smtpSender.getResponseForRequest("DATA\r\n"), true);
		res.add(smtpSender.getResponseForRequest(""), true);
		res.expectedResult.add(-3);
		smtpSender.restoreTimeOut();
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System correctly timeouts for various SMTP commands.\n");
			res.add(r1);
		}
		return res;
	}

	public TestResult testTimeout2(TestInput ti) {
		NTestResult res = new NTestResult();
		res.setTestCaseDesc("Testing Timeouts: " + config.getProperty("desc13"));
		res.setTestCaseId(13);
		int m = 0;
		try {
			testInit(ti);
		}catch (UnknownHostException e) {
			res.add(new ReqRes("\nERROR", "Unknown Host " + e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			return res;
			
			
		} catch (Exception e) {
			res.add(new ReqRes("\nERROR ", e.getLocalizedMessage() + "\n"));
			m++;
			res.setForcedCriteriaStatus(CriteriaStatus.FALSE);
			smtpSender.close();
			return res;
			
		}
		
		res.expectedResult.add(-3);
		int timeout = ti.sutCommandTimeoutInSeconds > 0 ? ti.sutCommandTimeoutInSeconds : 30;
		smtpSender.setTimeOut(timeout); // we wait for
																	// twice the
																	// time
		
		String fromAddress = "";
		if (ti.sutUserName.contains("@")){
			 fromAddress = ti.sutUserName;
		}
		
		else {
			 fromAddress = ti.sutUserName + ti.sutSmtpAddress;
		}
		res.add(smtpSender.sndMsgEHLO(VALID_DOMAIN));
		res.add(smtpSender.sndMsgMAILFROM(fromAddress));
		res.add(smtpSender.sndMsgRCPTTO(VALID_TO), true);
		res.add(smtpSender.getResponseForRequest(""), true);

		log.info("~~~~ ~~~~~~~~~~~~~~~~~~~~~" + res.getTimeElapsedInSeconds()
				+ " ~~~~~~~~~~~~~~   " + timeout);

		smtpSender.restoreTimeOut();
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System correctly timeouts for various SMTP commands.\n");
			res.add(r1);
		}
		return res;
	}

	/**
	 * Implements Testcase #17 Invalid STARTTLS
	 * 
	 * @return TestResult
	 */
	
	public TestResult testInvalidStartTLS(TestInput ti) {
		NTestResult res = new NTestResult();
		int m =0;
		smtpSender.close(); // just in case, close the previous connection
		try {
	    smtpSender.connect(ti.sutSmtpAddress, ti.startTlsPort);
	    
		} catch (Exception e) {
			ReqRes r = new ReqRes("ERROR", "Unknown Host " + e.getLocalizedMessage() + "\n");
			res.add(r);
			m++;
			
		}
		
		res.setTestCaseDesc("Testing Invalid StartTLS: "
				+ config.getProperty("desc17"));
		res.setTestCaseId(17);

		// no STARTTLS
		res.add(smtpSender.sndMsgHELO("testing.com"));
		res.add(smtpSender.sndMsgSTARTTLS_PARAM(), true);
		
		if (m < 1 && res.getCriteriaMet() == CriteriaStatus.TRUE) {
			ReqRes r1 = new ReqRes("\nSUCCESS", "System rejects invalid STARTTLS command\n");
			res.add(r1);
		}
		
		
		
		return res;
	}

	
	
}