/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.healthcare.ttt.direct.directValidator;

import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.DetailModel;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.apache.commons.lang3.StringEscapeUtils;


public class DirectMimeEntityValidator {

	// ************************************************
	// *********** MIME Entity checks *****************
	// ************************************************
	
	// xxxxxxxxxxxxxx MIME Headers xxxxxxxxxxxxxxxxxxxx

	// DTS 190, All Mime Header Fields, Required
	public DetailModel validateAllMimeHeaderFields(String header) {
		String rfc = "RFC 2045: Section 1, 3, 5;http://tools.ietf.org/html/rfc2045;RFC 5322: Section 2.2, 3.2.2;http://tools.ietf.org/html/rfc5322";
		header = header.replaceAll("\"[^\"]*\"", "");
		if(header.contains("(") || header.contains(")")) {
			return new DetailModel("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc, Status.ERROR);					
		}
		return new DetailModel("190", "All MIME Headers", header, "Content-Disposition must not contain comment", rfc, Status.SUCCESS);
	}
	
	// DTS 102a, MIME-Version, Optinal
	public DetailModel validateAllMIMEVersion(String mimeVersion) {
		return null;
		
	}

	// DTS 133-145-146, Content-Type, Required
	public DetailModel validateContentType(String contentType) {
		String rfc = "RFC 2045: Section 5, 5.2;http://tools.ietf.org/html/rfc2045#section-5;RFC 5751: Section 3.2, 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2";
		final String xContentType =  "^X-.*";
		Pattern pattern = Pattern.compile(xContentType, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentType);
		if(matcher.matches()) {
			return new DetailModel("133-145-146", "Content-Type", contentType, "Content-Type begin by X- and do not need to be verified", rfc, Status.SUCCESS);
		} else {
			if(contentType.contains("/")) {
				return new DetailModel("133-145-146", "Content-Type", contentType, "Content-Type must contain a subtype", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("133-145-146", "Content-Type", contentType, "Content-Type must contain a subtype", rfc, Status.ERROR);
			}
		}
		
	}

	// DTS 191, Content-Type Subtype, Required
	public DetailModel validateContentTypeSubtype(String subtype) {
		String rfc = "RFC 2045: Section 1;http://tools.ietf.org/html/rfc2045#section-1";
		if(subtype.contains("/")) {
			String[] typeAndSubtype = subtype.split("/"); // first one is the type (ex. "text"), second one is the subtype (ex. "plain").
			if (typeAndSubtype[1] != "") {
				return new DetailModel("191", "Content-Type Subtype", subtype, "Content Type Subtype must be present", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("191", "Content-Type Subtype", "Not present", "Content Type Subtype must be present", rfc, Status.ERROR);
			}
		} else {
			return new DetailModel("191", "Content-Type Subtype", "Not present", "Content Type Subtype must be present", rfc, Status.ERROR);
		}
	}

	// DTS 192, Content-Type name, Conditional
	public DetailModel validateContentTypeName(String contentTypeName) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(contentTypeName == null) {
			return new DetailModel("192", "Content-Type Name", contentTypeName, "Content Type Name must be present", rfc, Status.ERROR);
		} else {
			return new DetailModel("192", "Content-Type Name", contentTypeName, "Content Type Name must be present", rfc, Status.SUCCESS);
		}
	}

	// DTS 193, Content-Type S/MIME-Type, Conditional
	public DetailModel validateContentTypeSMIMEType(String contentTypeSMIMEType) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1;RFC 5751: 3.2.2;http://tools.ietf.org/html/rfc5751#section-3.2.2";
		if(contentTypeSMIMEType == null) {
			return new DetailModel("192", "Content-Type S/MIME-Type", contentTypeSMIMEType, "Content Type S/MIME-Type must be present", rfc, Status.ERROR);
		} else {
			return new DetailModel("192", "Content-Type S/MIME-Type", contentTypeSMIMEType, "Content Type S/MIME-Type must be present", rfc, Status.SUCCESS);
		}
	}

	// DTS 137-140, Content-Type Boundary, Conditional
	public DetailModel validateContentTypeBoundary(String contentTypeBoundary) {
		String rfc = "RFC 2046: Section 5.1.1;http://tools.ietf.org/html/rfc2046#section-5.1.1;RFC 2045: Section 5;http://tools.ietf.org/html/rfc2045#section-5";
		// MUST be encapsulated by "" if it contains a colon (:)
		if (contentTypeBoundary.contains(":")) {
			if (!(contentTypeBoundary.charAt(0) == '"') || !(contentTypeBoundary.charAt(contentTypeBoundary.length()-1) == '"')) {
				return new DetailModel("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary must not include a colon (':') and should start with quotes ('\"')", rfc, Status.ERROR);
			}
		}
		
		// MUST be no longer than 70 characters, not counting the two leading hyphens
		else if (contentTypeBoundary.length() > 70) {
			return new DetailModel("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary should not be longer than 70 characters", rfc, Status.ERROR);
		}
		
		// MUST be represented as US-ASCII
		else if (!ValidationUtils.isAscii(contentTypeBoundary)) {
			return new DetailModel("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME boundary must be represented as US-ASCII", rfc, Status.ERROR);
		}
		
		return new DetailModel("137-140", "Content-Type Boundary", contentTypeBoundary, "MIME Boundary is valid (US-ASCII, less than 70 characters)", rfc, Status.SUCCESS);
		
	}

	// DTS 156, Content-type Disposition, Conditional
	public DetailModel validateContentTypeDisposition(String contentTypeDisposition, String contentType) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if (contentType.contains("application/pkcs7-mime")) {
			if (contentTypeDisposition == null) {
				return new DetailModel("156", "Content-Type Disposition", "Not present", "Content-Type Disposition should be present", rfc, Status.ERROR);
			} else if(!contentTypeDisposition.equals("")) {
				return new DetailModel("156", "Content-Type Disposition", contentTypeDisposition, "Content-Type Disposition should be present", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("156", "Content-Type Disposition", "Not present", "Content-Type Disposition should be present", rfc, Status.ERROR);
			}
		} else {
			return new DetailModel("156", "Content-Type Disposition", contentTypeDisposition, "Content Type is not equal to application/pkcs7-mime", rfc, Status.SUCCESS);
		}
		
	}
	
	// DTS 161-194, Content-Disposition filename, Optional
	public DetailModel validateContentDispositionFilename(String content) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		final String extension =  ".*\\.p7c$|.*\\.p7z$|.*\\.p7s$|.*\\.p7m$";
		Pattern pattern = Pattern.compile(extension, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		if(matcher.matches()) {
			String[] temp = content.split("\\.");
			String filename = temp[0];
			if(filename.length()<= 8) {
				final String smime =  "^smime\\..*" ;
				pattern = Pattern.compile(smime, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(content);
				if(matcher.matches()) {
					return new DetailModel("161-194", "Content-Disposition filename", content, "Filename is smime, has the good extension and is less than 8 characters", rfc, Status.SUCCESS);
				} else {
					return new DetailModel("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD be smime", rfc, Status.WARNING);
				}
			} else {
				return new DetailModel("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD be less than 8 characters", rfc, Status.WARNING);				
			}
		} else if(content.equals("")) {
			return new DetailModel("161-194", "Content-Disposition filename", "Not present", "Content Type Disposition filename SHOULD be present", rfc, Status.WARNING);
		} else {
			return new DetailModel("161-194", "Content-Disposition filename", content, "Content Type Disposition filename SHOULD have an extension in .p7m, .p7c, .p7z or .p7s", rfc, Status.WARNING);
		}
		
	}
	
	// DTS 134-143, Content-Id, Optional
	public DetailModel validateContentId(String content) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4;RFC 2045: Section 7;http://tools.ietf.org/html/rfc2045#section-7";
		if(content.equals("")) {
			return new DetailModel("134-143", "Content-Id", "Not present", "Content-Id should be present", rfc, Status.INFO);
		} else {
			//handle display		
			Pattern pattern = Pattern.compile("<" + "[0-9,a-z,_,\\-,.]+" + "@" + "[0-9,a-z,_,\\-,.]+" + ">", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			//er.detail(matcher.matches());
			if(matcher.matches()) {
				return new DetailModel("134-143", "Content-Id", content, "Must be syntactically identical to the Message-ID", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("134-143", "Content-Id", content, "Must be syntactically identical to the Message-ID", rfc, Status.ERROR);
			}
		}
		
	}
	
	// DTS 135-142-144, Content-Description, Optional
	public DetailModel validateContentDescription(String content) {
		String rfc = "RFC 2045: Section 4;http://tools.ietf.org/html/rfc2045#section-4;RFC 2045: Section 8;http://tools.ietf.org/html/rfc2045#section-8";
		if(content.equals("")) {
			return new DetailModel("135-142-144", "Content-Description", "Not present", "", rfc, Status.INFO);
		} else {
			return new DetailModel("135-142-144", "Content-Description", content, "No check needed", rfc, Status.SUCCESS);
		}
		
	}
	
	// DTS 136-148-157, Content-Transfer-Encoding, Optional
	public DetailModel validateContentTransferEncodingOptional(String contentTransfertEncoding, String contentType) {
		String rfc = "RFC 2045: Section 6, 6.1, 6.4, 6.7, 6.8;http://tools.ietf.org/html/rfc2045#section-6;RFC 5751: Section 3.1.2, 3.1.3;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(contentTransfertEncoding == null || contentTransfertEncoding.equals("")) {
			return new DetailModel("136-148-157", "Content-Transfer-Encoding", "Not present", "Content-Transfer-Encoding is not present", rfc, Status.INFO);
		}
		if(contentType.contains("multipart") || contentType.contains("message")) {
			if(contentTransfertEncoding.contains("7bit") || contentTransfertEncoding.contains("8bit") || contentTransfertEncoding.contains("binary")) {
				return new DetailModel("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either 7bit, 8bit or binary", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either 7bit, 8bit or binary", rfc, Status.WARNING);
			}
		} else {
			if(contentTransfertEncoding.contains("quoted-printable") || contentTransfertEncoding.contains("base-64") || contentTransfertEncoding.contains("7-bit") || contentTransfertEncoding.contains("7bit") || contentTransfertEncoding.contains("base64")) {
				return new DetailModel("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either quoted-printable, base64 or 7-bit", rfc, Status.SUCCESS);
			} else if(contentTransfertEncoding.startsWith("X-")) {
				return new DetailModel("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding start with X- and do not need to be checked", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("136-148-157", "Content-Transfer-Encoding", contentTransfertEncoding, "Content-Transfer-Encoding must be either quoted-printable, base64 or 7-bit", rfc, Status.WARNING);
			}
		}

	}
	
	// DTS 138-149, Content-*, Optional
	public DetailModel validateContentAll(String content) {
		String rfc = "RFC 2045: Section 9;http://tools.ietf.org/html/rfc2045#section-9";
		if(content.startsWith("content-")) {
			return new DetailModel("138-149", "Content-*", content, "Should begin by content-*", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("138-149", "Content-*", content, "Should begin by content-*", rfc, Status.ERROR);
		}
		
	}
	
	// xxxxxxxxxxxxxxx MIME Body  xxxxxxxxxxxxxxx
	
	// DTS 195, Body, Required
	@SuppressWarnings("rawtypes")
	public DetailModel validateBody(Part p, String body, String encoding) {
		String rfc = "RFC 2046: Section 5.1.1;http://tools.ietf.org/html/rfc2046#section-5.1.1";
		String bodyTxt = StringEscapeUtils.escapeHtml4(body);
		if(body.length()>50) {
			bodyTxt = StringEscapeUtils.escapeHtml4(body.substring(0, 50) + "...");
		}
		
		// Skip ascii validation if quoted-printable or base64 encoded
		if(encoding.toLowerCase().equals("quoted-printable")) {
			// Check only CRLF and TAB control char
			if(ValidationUtils.controlCharAreOnlyCRLFAndTAB(body)) {
				return new DetailModel("195", "Body", bodyTxt, "Body does not contain illegal character", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("195", "Body", bodyTxt, "Content-Transfer-Encoding = \"quoted-printable\", control characters other than TAB, or CR and LF as parts of CRLF pairs, MUST NOT appear", rfc, Status.ERROR);
			}
		} else if(encoding.toLowerCase().equals("base64")) {
			if(ValidationUtils.isOnlyCRLF(body)) {
				return new DetailModel("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc, Status.ERROR);
			}
		} else {
			if(!ValidationUtils.isAscii(body)) {
				return new DetailModel("195", "Body", bodyTxt, "Document must only contain ASCII characters", rfc, Status.ERROR);
			}
			if(!ValidationUtils.isOnlyCRLF(body)) {
				return new DetailModel("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF", rfc, Status.ERROR);
			}
			return new DetailModel("195", "Body", bodyTxt, "Any linebreak must be represented as a CRLF and body must only contain ASCII characters", rfc, Status.SUCCESS);
		}	
	}

}
