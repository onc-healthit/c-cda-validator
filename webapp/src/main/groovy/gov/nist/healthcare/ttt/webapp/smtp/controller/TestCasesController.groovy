package gov.nist.healthcare.ttt.webapp.smtp.controller;

import java.util.ArrayList;

import gov.nist.healthcare.ttt.smtp.ISMTPTestRunner;
import gov.nist.healthcare.ttt.smtp.ITestResult;
import gov.nist.healthcare.ttt.smtp.SMTPTestRunner;
import gov.nist.healthcare.ttt.smtp.testcases.MU2ReceiverTests;
import gov.nist.healthcare.ttt.webapp.smtp.model.SmtpTestInput;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smtpTestCases")
class TestCasesController {
	
	@Value('${direct.listener.domainName}')
	String domainName
	
	@Value('${ett.smtp.host}')
	String smtpHost

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
    ArrayList<ITestResult> startTestCases(@RequestBody SmtpTestInput ti) throws Exception {
		ISMTPTestRunner smtpTestRunner = new SMTPTestRunner()
		MU2ReceiverTests fetchMail = new MU2ReceiverTests()
		ArrayList<ITestResult> res = new ArrayList<ITestResult>()
		def trs;
		if(ti.status !=null && ti.status.toLowerCase().equals("fetching")) {
			trs = fetchMail.fetchMail(ti.convert(domainName, smtpHost))
		} else {
			trs = smtpTestRunner.runTestCase(ti.getTestCaseNumber().toInteger(), ti.convert(domainName, smtpHost))
		}
		trs.each { res << it }
		
		res
	}
}
