package gov.nist.healthcare.ttt.direct.messageProcessor;

import gov.nist.healthcare.ttt.direct.directValidator.DirectMDNValidator;
import gov.nist.healthcare.ttt.model.logging.PartModel;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.mail.Part;
import javax.mail.internet.InternetHeaders;

import org.apache.commons.io.IOUtils;

import com.sun.mail.dsn.DispositionNotification;

public class ProcessMDN {

	private String dispNotifTo;
	private String originalRecipient;
	private String reportingUA;
	private String mdnGateway;
	private String finalRecipient;
	private String originalMessageID;
	private String disposition;
	private String failure;
	private String error;
	private String warning;
	private String extension;
	
	private ArrayList<String> headerName;
	private ArrayList<String> headerField;
	
	public ProcessMDN(Part p) throws Exception{
		headerName = new ArrayList<String>();
		headerField = new ArrayList<String>();	
		
		InputStream mdnStream = null;
		DispositionNotification dispositionNotification = null;

		if(p.getContent() instanceof DispositionNotification) {
			dispositionNotification = (DispositionNotification) p.getContent();
			InternetHeaders headers = dispositionNotification.getNotifications();
			dispNotifTo = headers.getHeader("disposition-notification-to", null);
			originalRecipient = headers.getHeader("original-recipient", null);
			reportingUA = headers.getHeader("reporting-ua", null);
			mdnGateway = headers.getHeader("mdn-gateway", null);
			finalRecipient = headers.getHeader("final-recipient", null);
			originalMessageID = headers.getHeader("original-message-id", null);
			disposition = headers.getHeader("disposition", null);
			failure = headers.getHeader("failure", null);
			error = headers.getHeader("error", null);
			warning = headers.getHeader("warning", null);
			extension = headers.getHeader("extension", null);
			
			// Check if null
			checkNulls();
		} else {

			StringWriter writer = new StringWriter();
			IOUtils.copy(mdnStream, writer, "UTF-8");

			String mdnPart = writer.toString();
			String[] mdnHeaderSplit = mdnPart.split("\n");
			for (int i = 0; i < mdnHeaderSplit.length; i++) {
				if (mdnHeaderSplit[i].contains("\r")) {
					mdnHeaderSplit[i] = mdnHeaderSplit[i].replaceAll("\\r", "");
				}
				String[] splitHeader;
				if(mdnHeaderSplit[i].contains(":")) {
					splitHeader = mdnHeaderSplit[i].split(":");
					headerName.add(splitHeader[0].toLowerCase().replace(" ", ""));
					if(splitHeader.length>1) {
						headerField.add(splitHeader[1].replace(" ", ""));
					} else {
						headerField.add("");
					}
				}
			}
			mdnPart = mdnPart.toLowerCase();

			dispNotifTo = getMDNHeader("disposition-notification-to");
			originalRecipient = getMDNHeader("original-recipient");
			reportingUA = getMDNHeader("reporting-ua");
			mdnGateway = getMDNHeader("mdn-gateway");
			finalRecipient = getMDNHeader("final-recipient");
			originalMessageID = getMDNHeader("original-message-id");
			disposition = getMDNHeader("disposition");
			failure = getMDNHeader("failure");
			error = getMDNHeader("error");
			warning = getMDNHeader("warning");
			extension = getMDNHeader("extension");
		}
	}

	public void validate(PartModel part){

		DirectMDNValidator validator = new DirectMDNValidator();

		// DTS 452, Disposition-Notification-To, Required
		part.addNewDetailLine(validator.validateMDNRequestHeader(dispNotifTo));
		
		// DTS 454, Original-Recipient-Header, warning
		part.addNewDetailLine(validator.validateOriginalRecipientHeader(originalRecipient));
		
		// DTS 456, Disposition-Notification-Content, warning
		part.addNewDetailLine(validator.validateDispositionNotificationContent(reportingUA, mdnGateway, originalRecipient, finalRecipient, originalMessageID, disposition, failure, error, warning, extension));
		
		// DTS 457, Reporting-UA-Field, warning
		part.addNewDetailLine(validator.validateReportingUAField(reportingUA));
		
		// DTS 458, mdn-gateway-field, Required
		part.addNewDetailLine(validator.validateMDNGatewayField(mdnGateway));
		
		// DTS 459, original-recipient-field, Required
		part.addNewDetailLine(validator.validateOriginalRecipientField(originalRecipient));
		
		// DTS 460, final-recipient-field, Required
		part.addNewDetailLine(validator.validateFinalRecipientField(finalRecipient));
		
		// DTS 461, original-message-id-field, Required
		part.addNewDetailLine(validator.validateOriginalMessageIdField(originalMessageID));
		
		// DTS 462, disposition-field, Required
		part.addNewDetailLine(validator.validateDispositionField(disposition));
		
		// DTS 463, failure-field, Required
		part.addNewDetailLine(validator.validateFailureField(failure));
		
		// DTS 464, error-field, Required
		part.addNewDetailLine(validator.validateErrorField(error));
		
		// DTS 465, warning-field, Required
		part.addNewDetailLine(validator.validateWarningField(warning));
		
		// DTS 466, extension-field, Required
		part.addNewDetailLine(validator.validateExtensionField(extension));		
	}
	
	public String getMDNHeader(String header) {
		String res = "";
		if(!headerName.isEmpty()) {
			for(int i=0;i<headerName.size();i++) {
				if(headerName.get(i).equals(header)) {
					res = headerField.get(i);
				}
			}
		}
		return res;
	}
	
	public void checkNulls() {
		this.dispNotifTo = checkIfNull(this.dispNotifTo);
		this.originalRecipient = checkIfNull(originalRecipient);
		this.reportingUA = checkIfNull(reportingUA);
		this.mdnGateway = checkIfNull(mdnGateway);
		this.finalRecipient = checkIfNull(finalRecipient);
		this.originalMessageID = checkIfNull(originalMessageID);
		this.disposition = checkIfNull(disposition);
		this.failure = checkIfNull(failure);
		this.error = checkIfNull(error);
		this.warning = checkIfNull(warning);
		this.extension = checkIfNull(extension);
	}
	
	public String checkIfNull(String header) {
		if(header == null) {
			return "";
		}
		return header;
	}
	
	public String getDispositionField() {
		return this.disposition;
	}
	
	public String getOriginalMessageId() {
		return this.originalMessageID;
	}

}