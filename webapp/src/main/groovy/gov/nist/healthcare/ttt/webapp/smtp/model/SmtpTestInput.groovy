package gov.nist.healthcare.ttt.webapp.smtp.model;

import gov.nist.healthcare.ttt.smtp.TestInput
import gov.nist.healthcare.ttt.smtp.TestResult;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

public class SmtpTestInput {

	String testCaseNumber

	String sutSmtpAddress

	String sutSmtpPort

	String tttSmtpPort

	String sutEmailAddress

	String tttEmailAddress

	String useTLS

	String sutCommandTimeoutInSeconds

	String sutUserName

	String sutPassword

	String tttUserName

	String tttPassword

	String tttSmtpAddress

	String startTlsPort
	
	String status
	
	String attachmentType
	
	String ccdaReferenceFilename
	
	String ccdaValidationObjective
	
	String ccdaFileLink
	
	TestResult previousResult;
	

	public SmtpTestInput() {

	}		

	public SmtpTestInput(String testCaseNumber, String sutSmtpAddress, String sutSmtpPort, String tttSmtpPort,
			String sutEmailAddress, String tttEmailAddress, String useTLS, String sutCommandTimeoutInSeconds,
			String sutUserName, String sutPassword, String tttUserName, String tttPassword, String tttSmtpAddress,
			String startTlsPort, String status, String attachmentType, String ccdaReferenceFilename,
			String ccdaValidationObjective, String ccdaFileLink, TestResult previousTr) {
		super();
		this.testCaseNumber = testCaseNumber;
		this.sutSmtpAddress = sutSmtpAddress;
		this.sutSmtpPort = sutSmtpPort;
		this.tttSmtpPort = tttSmtpPort;
		this.sutEmailAddress = sutEmailAddress;
		this.tttEmailAddress = tttEmailAddress;
		this.useTLS = useTLS;
		this.sutCommandTimeoutInSeconds = sutCommandTimeoutInSeconds;
		this.sutUserName = sutUserName;
		this.sutPassword = sutPassword;
		this.tttUserName = tttUserName;
		this.tttPassword = tttPassword;
		this.tttSmtpAddress = tttSmtpAddress;
		this.startTlsPort = startTlsPort;
		this.status = status;
		this.attachmentType = attachmentType;
		this.ccdaReferenceFilename = ccdaReferenceFilename;
		this.ccdaValidationObjective = ccdaValidationObjective;
		this.ccdaFileLink = ccdaFileLink;
		this.previousTr = previousTr;
	}

	public SmtpTestInput(String testCaseNumber, String sutSmtpAddress,
			String sutSmtpPort, String sutEmailAddress, String tttEmailAddress,
			String useTLS, String status) {
		super()
		this.testCaseNumber = testCaseNumber
		this.sutSmtpAddress = sutSmtpAddress
		this.sutSmtpPort = sutSmtpPort
		this.sutEmailAddress = sutEmailAddress
		this.tttEmailAddress = tttEmailAddress
		this.useTLS = useTLS
		this.status = status
	}

	public TestInput convert(String domainName, String smtpHost) throws FileNotFoundException {
		
		// Default value
		if (sutSmtpAddress==null || sutSmtpAddress.equals("")) {
			this.sutSmtpAddress = " "
		}
		if (sutEmailAddress==null || sutEmailAddress.equals("")) {
			this.sutEmailAddress = " "
		}
		if (tttEmailAddress==null || tttEmailAddress.equals("")) {
			this.tttEmailAddress = "wellformed1@" + domainName
		}

		if (tttSmtpAddress==null || tttSmtpAddress.equals("")) {
			this.tttSmtpAddress = smtpHost
		}

		if (sutCommandTimeoutInSeconds==null || sutCommandTimeoutInSeconds.equals("0") || sutCommandTimeoutInSeconds.equals("")) {
			this.sutCommandTimeoutInSeconds = "600"
		}

		this.sutUserName = setDefaultAuthValue(this.sutUserName)
		this.sutPassword = setDefaultAuthValue(this.sutPassword)
		this.tttUserName = setDefaultAuthValue(this.tttUserName)
		this.tttPassword = setDefaultAuthValue(this.tttPassword)

		this.sutSmtpPort = setDefautlPort(this.sutSmtpPort)
		this.tttSmtpPort = setDefautlPort(this.tttSmtpPort)
		this.startTlsPort = setDefautlPort(this.startTlsPort)

		// Generate attachment
		LinkedHashMap<String, byte[]> attachment = new LinkedHashMap<String, byte[]>()
		// Decide which attachment should be used
		InputStream ccdaAttachment = null
		String attachmentName = ""
		if(this.attachmentType != null) {
			if(this.attachmentType.equals("CCR")) {
				ccdaAttachment = getClass().getResourceAsStream("/cda-samples/CCR_Sample1.xml")
				attachmentName = "CCR_Sample1.xml"
			} else if(this.attachmentType.equals("C32")) {
				ccdaAttachment = getClass().getResourceAsStream("/cda-samples/C32_Sample1.xml")
				attachmentName = "C32_Sample1.xml"
			}  else if(this.attachmentType.equals("XDM")) {
				ccdaAttachment = getClass().getResourceAsStream("/cda-samples/ToC_Ambulatory.zip")
				attachmentName = "ToC_Ambulatory.zip"
			} 
			else {
				ccdaAttachment = getClass().getResourceAsStream("/cda-samples/CCDA_Ambulatory.xml")
				attachmentName = "CCDA_Ambulatory.xml.xml"
			}
		} else if(this.ccdaFileLink != null && !this.ccdaFileLink.equals("")) {
			ccdaAttachment = new URL(this.ccdaFileLink).openStream();
			attachmentName = this.ccdaReferenceFilename;
		} else {
			ccdaAttachment = getClass().getResourceAsStream("/cda-samples/CCDA_Ambulatory.xml")
			attachmentName = "CCDA_Ambulatory.xml"
		}
		attachment.put(attachmentName, IOUtils.toByteArray(ccdaAttachment))

		TestInput res = new TestInput(this.sutSmtpAddress, this.tttSmtpAddress,
				Integer.parseInt(this.sutSmtpPort),
				Integer.parseInt(this.tttSmtpPort), this.sutEmailAddress,
				this.tttEmailAddress, getBool(this.useTLS), this.sutUserName,
				this.sutPassword, this.tttUserName, this.tttPassword,
				Integer.parseInt(this.startTlsPort),
				Integer.parseInt(this.sutCommandTimeoutInSeconds), attachment, this.ccdaReferenceFilename, this.ccdaValidationObjective)

		res.setTr(this.previousResult)
		
		return res
	}

	public boolean getBool(String field) {
		if(field == null) {
			return false
		}
		if (field.equals("true")) {
			return true
		}
		return false
	}

	public String setDefaultAuthValue(String param) {
		if (param==null || param.equals("")) {
			return "red"
		}
		return param
	}

	public String setDefautlPort(String param) {
		if (param==null || param.equals("0")) {
			return "25"
		} else {
			return param
		}
	}

}
