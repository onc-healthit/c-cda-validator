/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: TTT-DIRECT
Authors: Julien Perugini
		 Andrew McCaffrey
		
 */

package gov.nist.healthcare.ttt.direct.directValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;

import org.apache.commons.lang3.StringEscapeUtils;

import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.DetailModel;

public class DirectMessageHeadersValidator {

	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	

	// DTS 196, All Headers, Required
	public DetailModel validateAllHeaders(String[] header, String[] headerContent, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6;http://tools.ietf.org/html/rfc5322#section-3.6;RFC 5321: Section 2.3.1;http://tools.ietf.org/html/rfc5321.html#section-2.3.1";
		boolean isAscii = true;
		for(int i=0;i<header.length;i++) {
			if(!ValidationUtils.isAscii(header[i]) || !ValidationUtils.isAscii(headerContent[i])) {
				isAscii = false;
			}
		}
		if(isAscii) {
			return new DetailModel("196", "All Headers", "", "Must be ASCII encoding" , rfc, Status.SUCCESS);
		} else if(!isAscii && wrapped) {
			return new DetailModel("196", "All Headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc, Status.ERROR);
		} else {
			return new DetailModel("196", "All headers", "Some headers are not ASCII encoded", "Must be ASCII encoding", rfc, Status.WARNING);
		}
		
	}
	
	// DTS 103-105, Return Path, Conditional
	public DetailModel validateReturnPath(String returnPath, boolean wrapped) {
		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.6.7;http://tools.ietf.org/html/rfc5322#section-3.6.7";
		String txtReturnPath = StringEscapeUtils.escapeHtml4(returnPath);
		if(returnPath.equals("")) {
			return new DetailModel("103-105", "Return Path", "Not present", "Should be present (\"<\" [ A-d-l \":\" ] Mailbox \">\")", rfc, Status.WARNING);
		}
		
		if(ValidationUtils.validateAddrSpec(returnPath)) {
			return new DetailModel("103-105", "Return Path", txtReturnPath, "\"<\" [ A-d-l \":\" ] Mailbox \">\"", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("103-105", "Return Path", txtReturnPath, "\"<\" [ A-d-l \":\" ] Mailbox \">\"", rfc, Status.ERROR);
		}
		
	}
	
	// DTS 104-106, Received, Conditional
	public DetailModel validateReceived(String received, boolean wrapped) {
	
		// Remove break lines
		String noLineBreakReceived = received.replace("\r\n", "");
		
		// String part variable
		String fromText = ValidationUtils.getReceivedPart(noLineBreakReceived, "from ");
		String byText = ValidationUtils.getReceivedPart(noLineBreakReceived, " by ");
		String viaText = ValidationUtils.getReceivedPart(noLineBreakReceived, " via ");
		String withText = ValidationUtils.getReceivedPart(noLineBreakReceived, " with ");
		String idText = ValidationUtils.getReceivedPart(noLineBreakReceived, " id ");
		String forText = ValidationUtils.getReceivedPart(noLineBreakReceived, " for ");
		String dateText = "";
		
		if(received.contains(";")) {
			dateText = noLineBreakReceived.split(";", 2)[1];
			dateText = dateText.replaceAll("\\r", "");
			dateText = dateText.replaceAll("\\n", "");
			while(dateText.startsWith(" ")) {
				dateText = dateText.substring(1);
			}
		}
		
		// Boolean validation
		boolean checkFrom = false;
		boolean checkBy = false;
		boolean checkVia = true;
		boolean checkWith = true;
		boolean checkId = true;
		boolean checkFor = true;
		boolean checkDate = false;
		
		final String ipv6 = "((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:)))(%.+)?";
		final String ipv4 = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
		final String ip = "(" + ipv6 + "|" + ipv4 + ")";
		final String domain = "([0-9a-zA-Z]+([_, \\., \\-][0-9a-zA-Z]+)*)";
		final String from = domain + "\\(" + domain + "?\\[?" + ip + "\\]?\\)*";
		final String by = from;
		final String via = "[0-9a-zA-Z]*";
		final String with = "SMTP|ESMTP|ESMTPA|ESMTPS|ESMTPSA|LMTP|LMTPA|LMTPS|LMTPSA|MMS|UTF8SMTP|UTF8SMTPA|UTF8SMTPS|UTF8SMTPSA|UTF8LMTP|UTF8LMTPA|UTF8LMTPS|UTF8LMTPSA";
		final String id = "[0-9a-zA-Z]+([_, \\., \\-][0-9a-zA-Z]+)*";
		final String fore =  "<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">;";
		
		final String datePattern = ValidationUtils.getDatePattern();
		
		// From clause validation
		checkFrom = ValidationUtils.validateReceivedPart(fromText, from, checkFrom);
		
		// By clause validation
		checkBy = ValidationUtils.validateReceivedPart(byText, by, checkBy);
		
		// Via clause validation
		checkVia = ValidationUtils.validateReceivedPart(viaText, via, checkVia);

		// With clause validation
		checkWith = ValidationUtils.validateReceivedPart(withText, with, checkWith);

		// Id clause validation
		checkId = ValidationUtils.validateReceivedPart(idText, id, checkId);

		// For field validation
		checkFor = ValidationUtils.validateReceivedPart(forText, fore, checkFor);

		// Date validation
		Pattern pattern = Pattern.compile(datePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(dateText);
		if(matcher.matches()) {
			checkDate = true;
		}

		String rfc = "RFC 5321: Section 4.4;http://tools.ietf.org/html/rfc5321.html#section-4.4;RFC 5322: Section 3.3;http://tools.ietf.org/html/rfc5322#section-3.3";
		if(checkFrom && checkBy && checkVia && checkWith && checkId && checkFor && checkDate) {
			return new DetailModel("104-106", "Received", received, "from clause by clause for clause; date", rfc, Status.SUCCESS);
		} else if(!checkFrom) {
			return new DetailModel("104-106", "Received", received, "From clause not formated correctly", rfc, Status.WARNING);
		} else if(!checkBy) {
			return new DetailModel("104-106", "Received", received, "By clause not formated correctly", rfc, Status.WARNING);
		} else if(!checkVia) {
			return new DetailModel("104-106", "Received", received, "Via clause value should be TCP or other allowed values", rfc, Status.WARNING);
		} else if(!checkWith) {
			return new DetailModel("104-106", "Received", received, "With clause should be either SMTP or ESMTP or another allowed value", rfc, Status.WARNING);
		} else if(!checkId) {
			return new DetailModel("104-106", "Received", received, "ID clause not formated correctly", rfc, Status.WARNING);
		} else if(!checkDate) {
			return new DetailModel("104-106", "Received", received, "Date clause not formated correctly", rfc, Status.WARNING);
		} else {
			return new DetailModel("104-106", "Received", received, "from clause by clause for clause; date", rfc, Status.ERROR);
		}
		
	}

	// DTS 197, Resent Fields, Required
	public DetailModel validateResentFields(String[] resentField, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		int i = 0;
		boolean present = false;
		while(i<resentField.length && !resentField[i].contains("resent")) {
			i++;
		}
		while(i<resentField.length && resentField[i].contains("resent")) {
			i++;
			present = true;
		}
		boolean grouped = true;
		for(int k=i;k<resentField.length;k++) {
			if(resentField[k].contains("resent")) {
				grouped = false;
				return new DetailModel("197", "Resent fields", "", "Should be grouped together", rfc, Status.ERROR);
			}
		}

		if(grouped && present) {
			return new DetailModel("197", "Resent fields", "Grouped and present", "Should be grouped together", rfc, Status.SUCCESS);
		}

		return new DetailModel("197", "Resent fields", "Not present", "May not be present", rfc, Status.INFO);
		
	}
	
	// DTS 107, Resent-Date, Conditional
	public DetailModel validateResentDate(String resentDate, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateDate(resentDate)) {
			return new DetailModel("107", "Resent-Date", resentDate, "date-time", rfc, Status.SUCCESS);
		} else if (resentDate.equals("")) { 
			return new DetailModel("107", "Resent-Date", "Not present", "date-time", rfc, Status.INFO);
		} else{
			return new DetailModel("107", "Resent-Date", resentDate, "date-time", rfc, Status.ERROR);
		}

	}

	// DTS 108, Resent-From, Conditional
	public DetailModel validateResentFrom(String resentFrom, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentFrom)) {
			return new DetailModel("108", "Resent-From", resentFrom, "mailbox-list", rfc, Status.SUCCESS);
		} else if (resentFrom.equals("")) { 
			return new DetailModel("108", "Resent-From", "Not present", "mailbox-list", rfc, Status.INFO);
		} else {
			return new DetailModel("108", "Resent-From", resentFrom, "mailbox-list", rfc, Status.ERROR);
		}
		
	}
	
	// DTS 109, Resent-Sender, Conditional
	public DetailModel validateResentSender(String resentSender, String resentFrom, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentSender) && !resentSender.equals(resentFrom)) {
			return new DetailModel("109", "Resent-Sender", resentSender, "mailbox-list", rfc, Status.SUCCESS);
		} else if (resentFrom.equals("")) {
			return new DetailModel("109", "Resent-Sender", "Not present", "mailbox-list", rfc, Status.INFO);
		} else if(resentSender.equals(resentFrom)) {
			return new DetailModel("109", "Resent-Sender", resentSender, "Resent-Sender should not be equal to Resent-From", rfc, Status.ERROR);
		} else {
			return new DetailModel("109", "Resent-Sender", resentSender, "mailbox-list", rfc, Status.ERROR);
		}
		
	}

	// DTS 110, Resent-to, Optional
	public DetailModel validateResentTo(String resentTo, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentTo)) {
			return new DetailModel("110", "Resent-To", resentTo, "address-list", rfc, Status.SUCCESS);
		} else if (resentTo.equals("")) { 
			return new DetailModel("110", "Resent-To", "Not present", "address-list", rfc, Status.INFO);
		} else {
			return new DetailModel("110", "Resent-To", resentTo, "address-list", rfc, Status.ERROR);
		}
		
	}

	// DTS 111, Resent-cc, Optional
	public DetailModel validateResentCc(String resentCc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentCc)) {
			return new DetailModel("111", "Resent-Cc", resentCc, "address-list", rfc, Status.SUCCESS);
		} else if (resentCc.equals("")) { 
			return new DetailModel("111", "Resent-Cc", "Not present", "address-list", rfc, Status.INFO);
		} else {
			return new DetailModel("111", "Resent-Cc", resentCc, "address-list", rfc, Status.ERROR);
		}
		
	}

	// DTS 112, Resent-bcc, Optional
	public DetailModel validateResentBcc(String resentBcc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateEmail(resentBcc)) {
			return new DetailModel("112", "Resent-Bcc", resentBcc, "address-list", rfc, Status.SUCCESS);
		} else if (resentBcc.equals("")) { 
			return new DetailModel("112", "Resent-Bcc", "Not present", "address-list", rfc, Status.INFO);
		} else {
			return new DetailModel("112", "Resent-Bcc", resentBcc, "address-list", rfc, Status.ERROR);
		}
		
	}

	// DTS 113, Resent-Msg-Id, Conditional
	public DetailModel validateResentMsgId(String resentMsgId, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.6;http://tools.ietf.org/html/rfc5322#section-3.6.6";
		if(ValidationUtils.validateAddrSpec(resentMsgId)) {
			return new DetailModel("113", "Resent-Msg-Id", StringEscapeUtils.escapeHtml4(resentMsgId), "msg-id", rfc, Status.SUCCESS);
		} else if (resentMsgId.equals("")) { 
			return new DetailModel("113", "Resent-Msg-Id", "Not present", "msg-id", rfc, Status.INFO);
		} else {
			return new DetailModel("113", "Resent-Msg-Id", StringEscapeUtils.escapeHtml4(resentMsgId), "msg-id", rfc, Status.ERROR);
		}
		
	}
	
	// DTS 114, Orig-Date, Required
	public DetailModel validateOrigDate(String origDate, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.1;http://tools.ietf.org/html/rfc5322#section-3.6.1";
		if(origDate.equals("") && !wrapped) {
			return new DetailModel("114", "Orig-Date", "Not present", "Unwrapped Message: Orig-Date should be present", rfc, Status.ERROR);
		} else if(origDate.equals("") && wrapped) {
			return new DetailModel("114", "Orig-Date", "Not present", "Wrapped Message: Orig-Date is not present on the outer (encrypted) message", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateDate(origDate)) {
				return new DetailModel("114", "Orig-Date", origDate, "[ day-of-week \",\" ] date time", rfc, Status.SUCCESS);
			} else {
				// Check if there are more than one space between different elements
				if(ValidationUtils.validateDateWithMoreSpace(origDate)) {
					return new DetailModel("114", "Orig-Date", origDate, "[ day-of-week \",\" ] date time (your date might contain extra white space)", rfc, Status.WARNING);
				} else {
					return new DetailModel("114", "Orig-Date", origDate, "[ day-of-week \",\" ] date time", rfc, Status.ERROR);
				}
			}
		}
	}

	// DTS 115, From, Required
	public DetailModel validateFrom(String from, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(from.equals("") && !wrapped) {
			return new DetailModel("115", "From", "Not present", "Unwrapped Message: From should be present", rfc, Status.ERROR);
		}  else if(from.equals("") && wrapped) {
			return new DetailModel("115", "From", "Not present", "Wrapped Message: From is not present on the outer (encrypted) message", rfc, Status.INFO);
		} else {
			if (ValidationUtils.validateEmail(from)){
				return new DetailModel("115", "From", StringEscapeUtils.escapeHtml4(from), "mailbox-list", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("115", "From", StringEscapeUtils.escapeHtml4(from), "mailbox-list", rfc, Status.ERROR);
			}
		}
		
	}
	
	// DTS 116, Sender, Conditional
	public DetailModel validateSender(String sender, Address[] from, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(from.length>1) {
			if(ValidationUtils.validateEmail(sender)) {
				return new DetailModel("116", "Sender", sender, "mailbox", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("116", "Sender", sender, "mailbox", rfc, Status.ERROR);
			}
		} else {
			if(sender.equals("")) {
				return new DetailModel("116", "Sender", "Not present", "Sender field not used if same as From address", rfc, Status.SUCCESS);
			} else {
				if(sender.equals(from[0])) {
					return new DetailModel("116", "Sender", sender, "Sender and From value are the same therefore sender should not be used", rfc, Status.WARNING);
				} else {
					return new DetailModel("116", "Sender", sender, "mailbox", rfc, Status.SUCCESS);
				}
			}
		}
		
	}
	
	// DTS 117, Reply-To, Optional
	public DetailModel validateReplyTo(String replyTo, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.2;http://tools.ietf.org/html/rfc5322#section-3.6.2";
		if(replyTo.equals("") && !wrapped) {
			return new DetailModel("117", "Reply-To", "Not present", "Unwrapped Message: Reply-To should be present", rfc, Status.WARNING);
		} else if(replyTo.equals("") && wrapped) {
			return new DetailModel("117", "Reply-To", "Not present", "Wrapped Message: Reply-To is not present on the outer (encrypted) message", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateEmail(replyTo)) {
				return new DetailModel("117", "Reply-To", StringEscapeUtils.escapeHtml4(replyTo), "address-list", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("117", "Reply-To", StringEscapeUtils.escapeHtml4(replyTo), "address-list", rfc, Status.ERROR);
			}
		}
		
	}

	// DTS 118, To, Required
	public DetailModel validateTo(String to, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(to.equals("") && !wrapped) {
			return new DetailModel("118", "To", "Not present", "Unwrapped Message: To must be present", rfc, Status.ERROR);
		} else if(to.equals("") && wrapped) {
			return new DetailModel("118", "To", "Not present", "Wrapped Message: To is not present on the outer (encrypted) message", rfc, Status.INFO);
		} else {			
			if(ValidationUtils.validateEmail(to)) {
				return new DetailModel("118", "To", StringEscapeUtils.escapeHtml4(to), "mailbox-list", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("118", "To", StringEscapeUtils.escapeHtml4(to), "mailbox-list", rfc, Status.ERROR);
			}
		}
		
	}
	
	// DTS 119, cc, Optional
	public DetailModel validateCc(String cc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(cc.equals("")) {
			return new DetailModel("119", "Cc", "Not present", "address-list", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateEmail(cc)) {
				return new DetailModel("119", "Cc", StringEscapeUtils.escapeHtml4(cc), "address-list", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("119", "Cc", StringEscapeUtils.escapeHtml4(cc), "address-list", rfc, Status.ERROR);
			}
		}

	}
	
	// DTS 120, Bcc, Optional
	public DetailModel validateBcc(String bcc, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.3;http://tools.ietf.org/html/rfc5322#section-3.6.3";
		if(bcc.equals("")) {
			return new DetailModel("120", "Bcc", "Not present", "Should not be present", rfc, Status.SUCCESS);
		} else {
			if(ValidationUtils.validateEmail(bcc)) {
				return new DetailModel("120", "Bcc", StringEscapeUtils.escapeHtml4(bcc), "address-list", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("120", "Bcc", StringEscapeUtils.escapeHtml4(bcc), "address-list", rfc, Status.ERROR);
			}
		}
		
	}

	// DTS 121, Message-Id, Required
	public DetailModel validateMessageId(String messageId, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		if(messageId.equals("") && !wrapped) {
			return new DetailModel("121", "Message-Id", "Not present", "Unwrapped Message: Message-Id must be present", rfc, Status.ERROR);
		} else if(messageId.equals("") && wrapped) {
			return new DetailModel("121", "Message-Id", "Not present", "Wrapped Message: Message-Id is not present on the outer (encrypted) message", rfc, Status.WARNING);
		} else {
			if(ValidationUtils.validateAddrSpec(messageId)) {
				return new DetailModel("121", "Message-Id", StringEscapeUtils.escapeHtml4(messageId), "<string with no spaces\"@\"string with no spaces>", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("121", "Message-Id", StringEscapeUtils.escapeHtml4(messageId), "<string with no spaces\"@\"string with no spaces>", rfc, Status.ERROR);
			}
		}
	}

	// DTS 122, In-reply-to, Optional
	public DetailModel validateInReplyTo(String inReplyTo, String date, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		// Check 1: Must be formatted as one or more <randomstringwithoutspaces@randomstringwithoutspaces>
		if(ValidationUtils.validateAddrSpec(inReplyTo)) {
			return new DetailModel("122", "In-reply-to", StringEscapeUtils.escapeHtml4(inReplyTo), "<string with no spaces\"@\"string with no spaces>", rfc, Status.SUCCESS);
		} else if(inReplyTo.equals("")) {
			return new DetailModel("122", "In-reply-to", "Not present", "<string with no spaces\"@\"string with no spaces>", rfc, Status.INFO);
		} else {
			return new DetailModel("122", "In-reply-to", StringEscapeUtils.escapeHtml4(inReplyTo), "<string with no spaces\"@\"string with no spaces>", rfc, Status.ERROR);
		}
		
	}
	
	// DTS 123, References, Optional
	public DetailModel validateReferences(String references, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.4;http://tools.ietf.org/html/rfc5322#section-3.6.4";
		if(ValidationUtils.validateAddrSpec(references)) {
			return new DetailModel("123", "References", references, "<string with no spaces\"@\"string with no spaces>", rfc, Status.SUCCESS);
		} else if(references.equals("")) {
			return new DetailModel("123", "References", "Not present", "<string with no spaces\"@\"string with no spaces>", rfc, Status.INFO);
		} else {
			return new DetailModel("123", "References", references, "<string with no spaces\"@\"string with no spaces>", rfc, Status.ERROR);
		}
		
	}
	
	// DTS 124, Subject, Optional
	public DetailModel validateSubject(String subject, String filename, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(subject == null && wrapped) {
			return new DetailModel("124", "Subject", "Not present", "Wrapped Message: Subject is not present on the outer (encrypted) message", rfc, Status.INFO);
		} else if(subject == null && !wrapped) {
			return new DetailModel("124", "Subject", "Not present", "Unwrapped Message: Subject must be present", rfc, Status.ERROR);
		}
		
		if(filename.contains("zip")) {	
			if(subject.contains("XDM/1.0/DDM")) {
				return new DetailModel("124", "Subject", subject, "Filename is ZIP: Subject must contain XDM/1.0/DDM", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("124", "Subject", subject, "Filename is ZIP: Subject must contain XDM/1.0/DDM", rfc, Status.ERROR);
			}
		}
		
		return new DetailModel("124", "Subject", subject, "Subject is present", rfc, Status.INFO);
	}
	
	// DTS 125, Comments, Optional
	public DetailModel validateComments(String comments, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(comments.equals("")) {
			return new DetailModel("125", "Comments", "Not present", "May not be present", rfc, Status.INFO);
		} else {
			return new DetailModel("125", "Comments", comments, "Unstructured CRLF", rfc, Status.SUCCESS);
		}
		
	}
	
	// DTS 126, Keywords, Optional
	public DetailModel validateKeywords(String keyword, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.5;http://tools.ietf.org/html/rfc5322#section-3.6.5";
		if(keyword.equals("")) {
			return new DetailModel("126", "Keywords", "Not present", "May not be present", rfc, Status.INFO);
		} else {
			return new DetailModel("126", "Keywords", keyword, "Unstructured CRLF", rfc, Status.SUCCESS);
		}
		
	}
	
	// DTS 127, Optional-field, Optional
	public DetailModel validateOptionalField(String optionalField, boolean wrapped) {
		String rfc = "RFC 5322: Section 3.6.8;http://tools.ietf.org/html/rfc5322#section-3.6.8";
		if(optionalField.equals("")) {
			return new DetailModel("127", "Optional-field", "Not present", "May not be present", rfc, Status.INFO);
		} else {
			return new DetailModel("127", "Optional-field", optionalField, "text", rfc, Status.SUCCESS);
		}
		
	}
	
	// DTS 128, Disposition-Notification-To, Optional
	public DetailModel validateDispositionNotificationTo(String dispositionNotificationTo, boolean wrapped) {
		String rfc = "IHE Vol2b: Section 3.32.4.1.3;http://www.ihe.net/uploadedFiles/Documents/ITI/IHE_ITI_TF_Vol2b.pdf";
		if(dispositionNotificationTo.equals("")) {
			return new DetailModel("128", "Disposition-Notification-To", "Not present", "May not be present", rfc, Status.INFO);
		} else {
			if(ValidationUtils.validateEmail(dispositionNotificationTo)) {
				return new DetailModel("128", "Disposition-Notification-To", dispositionNotificationTo, "Email address", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("128", "Disposition-Notification-To", dispositionNotificationTo, "Email address", rfc, Status.ERROR);
			}
		}
		
	}
	
	// DTS 102b, MIME-Version, Required
	public DetailModel validateMIMEVersion(String MIMEVersion, boolean wrapped) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4";
		if(MIMEVersion.equals("") && !wrapped) {
			return new DetailModel("102b", "MIME-Version", "Not present", "Unwrapped Message: MIME-Version must be present", rfc, Status.ERROR);
		} else if(MIMEVersion.equals("") && wrapped) {
			return new DetailModel("102b", "MIME-Version", "Not present", "Wrapped Message: MIME-Version is not present on the outer (encrypted) message", rfc, Status.WARNING);
		} else {
			final String mimeFormat = "[0-9]\\.[0-9].*";
			Pattern pattern = Pattern.compile(mimeFormat);
			Matcher matcher = pattern.matcher(MIMEVersion);
			if(matcher.matches()) {
				return new DetailModel("102b", "MIME-Version", MIMEVersion, "1*DIGIT \".\" 1*DIGIT", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("102b", "MIME-Version", MIMEVersion, "1*DIGIT \".\" 1*DIGIT", rfc, Status.ERROR);
			}
		}
		
	}
}
