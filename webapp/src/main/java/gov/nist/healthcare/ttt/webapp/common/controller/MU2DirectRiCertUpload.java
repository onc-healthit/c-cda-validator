package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.healthcare.ttt.smtp.TestInput;
import gov.nist.healthcare.ttt.smtp.TestResult;
import gov.nist.healthcare.ttt.smtp.testcases.MU2SenderTests;

@Controller
@RequestMapping("/api/directricert")
public class MU2DirectRiCertUpload {

	private static Logger logger = Logger.getLogger(MU2DirectRiCertUpload.class.getName());

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody TestResult uploadCert(@RequestBody String filePath) throws Exception {
		byte[] certByte = IOUtils.toByteArray(new FileInputStream(new File(filePath)));
		TestInput ti = new TestInput(certByte);
		
		MU2SenderTests testSender = new MU2SenderTests();
		
		return testSender.uploadCertificate(ti);
	}
}
