package gov.nist.healthcare.ttt.direct.directValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.DetailModel;
import gov.nist.healthcare.ttt.model.logging.PartModel;

public class DirectMDNValidator {
	/**
	 *  DTS 450, MDN must be signed and encrypted, Required
	 * @param er
	 * @param dts450
	 */
	public DetailModel validateMDNSignatureAndEncryption(boolean signed, boolean encrypted) {
		String rfc = "-";
		if(signed && encrypted) {
			return new DetailModel("450", "Signature", "Signed and Encrypted" , "Must be signed and encrypted", rfc, Status.SUCCESS);
		} else if(signed && !encrypted) {
			return new DetailModel("450", "Signature", "Not Encrypted" , "Must be signed and encrypted", rfc, Status.ERROR);
		} else if(!signed && encrypted) {
			return new DetailModel("450", "Signature", "Not Signed" , "Must be signed and encrypted", rfc, Status.ERROR);
		} else {
			return new DetailModel("450", "Signature", "Not Signed and Not Encrypted" , "Must be signed and encrypted", rfc, Status.ERROR);
		}
	}

	
	
	
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	/**
	 *  DTS 451, Message Headers - Contains DTS 452, 453, 454
	 */
	public DetailModel validateMessageHeaders(String sthg) {
		return new DetailModel("451", "Message Headers", "" , "Contains DTS 452, 453, 454", "-", Status.INFO);
	}
	
	
	/**
	 *  DTS 452, Disposition-Notification-To, Required
	 */
	public DetailModel validateMDNRequestHeader(String dispositionNotificationTo) {
		String rfc = "RFC 3798: Section 2.1;http://tools.ietf.org/html/rfc3798#section-2.1";
		if(dispositionNotificationTo.equals("")) {
			return new DetailModel("452", "Disposition-Notification-To", "Disposition-Notification-To is not present", "Must NOT be present", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("452", "Disposition-Notification-To", "Disposition-Notification-To is present", "Must NOT be present", rfc, Status.ERROR);
		}
	}
		
	
	/**
	 *  DTS 453, Disposition-Notification-Options, warning
	 */
	public DetailModel validateDispositionNotificationOptions(String sthg) {
		return new DetailModel("453", "Disposition-Notification-Options", "" , "", "-", Status.INFO);
	}
	

	/**
	 *  DTS 454, Original-Recipient-Header, warning
	 */
	public DetailModel validateOriginalRecipientHeader(String originalRecipient) {
		String rfc = "RFC 3798: Section 2.3;http://tools.ietf.org/html/rfc3798#section-2.3";
		if(originalRecipient.equals("")) {
			return new DetailModel("454", "Original-Recipient", "Not present", "Might not be present", rfc, Status.INFO);
		} else {
			if(!originalRecipient.contains("rfc822;")) {
				return new DetailModel("454", "Original-Recipient", originalRecipient, "Should normaly contain \"rfc822\"", rfc, Status.WARNING);
			}
			String[] splitHeader = null;
			splitHeader = originalRecipient.split(";");
			String email = splitHeader[1];
			if(ValidationUtils.validateEmail(email)) {
				return new DetailModel("454", "Original-Recipient", originalRecipient, "Should be email address", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("454", "Original-Recipient", originalRecipient, "Should be email address", rfc, Status.ERROR);
			}
		}
	}
	
	
	
	
	// ************************************************
	// *************** Report content *****************
	// ************************************************

	/**
	 *  DTS 455, Report content, warning - Contains DTS 456 to 466
	 */
	public DetailModel validateReportContent(String sthg) {
		return new DetailModel("455", "Report content", "" , "", "-", Status.INFO);
	}
	
	/**
	 *  DTS 456, Disposition-Notification-Content, warning
	 */
	public DetailModel validateDispositionNotificationContent(String reportingUA, String mdnGateway, String originalRecipient, 
			String finalRecipient, String originalMessageID, String disposition, 
			String failure, String error, String warning, String extension) {
		
		String rfc = "RFC 3798: Section 3.1;http://tools.ietf.org/html/rfc3798#section-3.1";
		PartModel tempPart = new PartModel();
		tempPart.addNewDetailLine(validateReportingUAField(reportingUA));
		tempPart.addNewDetailLine(validateMDNGatewayField(mdnGateway));
		tempPart.addNewDetailLine(validateOriginalRecipientField(originalRecipient));
		tempPart.addNewDetailLine(validateFinalRecipientField(finalRecipient));
		tempPart.addNewDetailLine(validateOriginalMessageIdField(originalMessageID));
		tempPart.addNewDetailLine(validateDispositionField(disposition));
		tempPart.addNewDetailLine(validateFailureField(failure));
		tempPart.addNewDetailLine(validateErrorField(error));
		tempPart.addNewDetailLine(validateWarningField(warning));
		tempPart.addNewDetailLine(validateExtensionField(extension));
		
		if(!tempPart.isStatus()) {
			return new DetailModel("456", "Disposition-Notification-Content", "Disposition-Notification-Content is not valid", "", rfc, Status.ERROR);
		} else {
			return new DetailModel("456", "Disposition-Notification-Content", "Disposition-Notification-Content is valid", "", rfc, Status.SUCCESS);
		}
	}
	
	/**
	 *  DTS 457, Reporting-UA-Field, warning
	 */
	public DetailModel validateReportingUAField(String reportingUA) {
		String rfc = "RFC 3798: Section 3.2.1;http://tools.ietf.org/html/rfc3798#section-3.2.1";
		if(reportingUA.equals("")) {
			return new DetailModel("457", "Reporting-UA Field", "Not present", "Should be present", rfc, Status.WARNING);
		} else {
			final String uaName = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaProduct = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaReportingPattern =  uaName + "(;" + uaProduct + ")?";
			Pattern pattern = Pattern.compile(uaReportingPattern, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(reportingUA);
			if(matcher.matches()) {
				return new DetailModel("457", "Reporting-UA Field", reportingUA, "ua-name [ \";\" ua-product ]", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("457", "Reporting-UA Field", reportingUA, "ua-name [ \";\" ua-product ]", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 458, mdn-gateway-field, Required
	 */
	public DetailModel validateMDNGatewayField(String mdnGateway) {
		String rfc = "RFC 3798: Section 3.2.2;http://tools.ietf.org/html/rfc3798#section-3.2.2";
		if(mdnGateway.equals("")) {
			return new DetailModel("458", "MDN-Gateway", "Not present", "Might not be present", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateAtomTextField(mdnGateway)) {
				return new DetailModel("458", "MDN-Gateway", mdnGateway, "mta-name-type \";\" mta-name", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("458", "MDN-Gateway", mdnGateway, "mta-name-type \";\" mta-name", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 459, original-recipient-field, Required
	 */
	public DetailModel validateOriginalRecipientField(String originalRecipient) {
		String rfc = "RFC 3798: Section 3.2.3;http://tools.ietf.org/html/rfc3798#section-3.2.3";
		if(originalRecipient.equals("")) {
			return new DetailModel("459", "Original-Recipient", "Not present", "Might not be present", rfc, Status.INFO);
		} else {
			String[] buf;
			boolean result = true;
			if(originalRecipient.contains(";")) {
				buf = originalRecipient.split(";");
				final String stringPattern =  ValidationUtils.getAtom();
				Pattern pattern = Pattern.compile(stringPattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buf[0]);
				if(!matcher.matches()) {
					result = false;
					return new DetailModel("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
				}
				if(!ValidationUtils.validateEmail(buf[1])) {
					result = false;
					return new DetailModel("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
				}
			} else {
				result = false;
				return new DetailModel("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
			}
			
			if(result) {
				return new DetailModel("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc, Status.SUCCESS);
			}
		}
		return new DetailModel("459", "Original-Recipient", "Not present", "Might not be present", rfc, Status.INFO);
	}
	
	/**
	 *  DTS 460, final-recipient-field, Required
	 */
	public DetailModel validateFinalRecipientField(String finalRecipient) {
		String rfc = "RFC 3798: Section 3.2.4;http://tools.ietf.org/html/rfc3798#section-3.2.4";
		if(finalRecipient.equals("")) {
			return new DetailModel("460", "Final-Recipient", "Not present", "Should be present", rfc, Status.WARNING);
		} else {
			String[] buf;
			boolean result = true;
			if(finalRecipient.contains(";")) {
				buf = finalRecipient.split(";");
				final String stringPattern =  ValidationUtils.getAtom();
				Pattern pattern = Pattern.compile(stringPattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buf[0]);
				if(!matcher.matches()) {
					result = false;
					return new DetailModel("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
				}
				if(!ValidationUtils.validateEmail(buf[1])) {
					result = false;
					return new DetailModel("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
				}
			} else {
				result = false;
				return new DetailModel("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc, Status.ERROR);
			}
			
			if(result) {
				return new DetailModel("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc, Status.SUCCESS);
			}
		}
		return new DetailModel("460", "Final-Recipient", "Not present", "Should be present", rfc, Status.WARNING);
	}

	/**
	 *  DTS 461, original-message-id-field, Required
	 */
	public DetailModel validateOriginalMessageIdField(String originalMessageId) {
		String rfc = "RFC 3798: Section 3.2.5;http://tools.ietf.org/html/rfc3798#section-3.2.5";
		if(originalMessageId.equals("")) {
			return new DetailModel("461", "Original-Message-ID", "Not present", "Should be present", rfc, Status.WARNING);
		} else {
			if(ValidationUtils.validateAddrSpec(originalMessageId)) {
				return new DetailModel("461", "Original-Message-ID", originalMessageId, "\"<\" id-left \"@\" id-right \">\"", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("461", "Original-Message-ID", originalMessageId, "\"<\" id-left \"@\" id-right \">\"", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 462, disposition-field, Required
	 */
	public DetailModel validateDispositionField(String disposition) {
		String rfc = "RFC 3798: Section 3.2.6;http://tools.ietf.org/html/rfc3798#section-3.2.6";
		if(disposition.equals("")) {
			return new DetailModel("462", "Disposition Field", "Not present", "Should be present", rfc, Status.WARNING);
		} else {
			if(ValidationUtils.validateDisposition(disposition)) {
				return new DetailModel("462", "Disposition Field", disposition, "disposition-mode \";\" disposition-type", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("462", "Disposition Field", disposition, "disposition-mode \";\" disposition-type", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 463, failure-field, Required
	 */
	public DetailModel validateFailureField(String failure) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(failure.equals("")) {
			return new DetailModel("463", "Failure Field", "Not present", "", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateTextField(failure)) {
				return new DetailModel("463", "Failure Field", failure, "*text", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("463", "Failure Field", failure, "*text", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 464, error-field, Required
	 */
	public DetailModel validateErrorField(String error) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(error.equals("")) {
			return new DetailModel("464", "Error Field", "Not present", "", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateTextField(error)) {
				return new DetailModel("464", "Error Field", error, "*text", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("464", "Error Field", error, "*text", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 465, warning-field, Required
	 */
	public DetailModel validateWarningField(String warning) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(warning.equals("")) {
			return new DetailModel("465", "Warning Field", "Not present", "", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateTextField(warning)) {
				return new DetailModel("465", "Warning Field", warning, "*text", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("465", "Warning Field", warning, "*text", rfc, Status.ERROR);
			}
		}
	}
	
	/**
	 *  DTS 466, extension-field, Required
	 */
	public DetailModel validateExtensionField(String extension) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(extension.equals("")) {
			return new DetailModel("466", "Extension Field", "Not present", "", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateTextField(extension)) {
				return new DetailModel("466", "Extension Field", extension, "*text", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("466", "Extension Field", extension, "*text", rfc, Status.ERROR);
			}
		}
	}
}
