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
import gov.nist.healthcare.ttt.model.logging.DetailModel;
import gov.nist.healthcare.ttt.model.logging.PartModel;

public class DirectMessageValidator {

	// ************************************************
	// *********** Message headers checks *************
	// ************************************************


	// xxxxxxxxxxxxxxx SMTP Commands  xxxxxxxxxxxxxxx

	// DTS 198, ?, Required
	public DetailModel validateDTS198(String dts198) {
		return null;

	}

	// DTS 100, MAIL FROM SMTP, Required
	public DetailModel validateMailFromSMTP(String mailFromSmtp) {
		return null;

	}

	// DTS 101, RCPT TO, Required
	public DetailModel validateRcptTo(String RcptTo) {
		return null;

	}


	// xxxxxxxxxxxxxxx Outer Enveloped Message  xxxxxxxxxxxxxxx


	// DTS 199, Non-MIME Message Headers, Required
	public DetailModel validateNonMIMEMessageHeaders(PartModel part, String nonMIMEHeader) {
		String rfc = "-";
		if(!part.isStatus()) {
			return new DetailModel("199", "Non-MIME Message Headers", "Some Message Headers are not valid", "All Non-MIME Message Headers must be valid", rfc, Status.ERROR);
		} else {
			return new DetailModel("199", "Non-MIME Message Headers", "All Message Headers are valid", "All Non-MIME Message Headers must be valid", rfc, Status.SUCCESS);
		}

	}

	// DTS 200, MIME Entity, Required
	public DetailModel validateMIMEEntity(PartModel part, String MIMEEntity) {
		String rfc = "-";
		if(!part.isStatus()) {
			return new DetailModel("200", "MIME Entity", "Some MIME Entity Headers are not valid", "All MIME Entity Headers must be valid", rfc, Status.ERROR);
		} else {
			return new DetailModel("200", "MIME Entity", "All MIME Entity Headers are valid", "All MIME Entity Headers must be valid", rfc, Status.SUCCESS);
		}

	}

	// DTS 133a, Content-Type, Required
	public DetailModel validateMessageContentTypeA(String messageContentTypeA) {
		String rfc = "RFC 5751: 3.2;http://tools.ietf.org/html/rfc5751#section-3.2";
		messageContentTypeA = messageContentTypeA.split(";")[0];
		if (messageContentTypeA.equals("application/pkcs7-mime")){
			return new DetailModel("133a", "Content-Type", messageContentTypeA, "Must be application/pkcs7-mime", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("133a", "Content-Type", messageContentTypeA, "Must be application/pkcs7-mime", rfc, Status.ERROR);
		}

	}

	// DTS 201, Content-Type Name, Optional
	public DetailModel validateContentTypeNameOptional(String contentTypeName) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(contentTypeName.contains("name")) {
			contentTypeName = contentTypeName.split("name=")[1];
			contentTypeName = contentTypeName.split(";")[0];
			contentTypeName = contentTypeName.replace("\"", "");
			if (contentTypeName.equals("smime.p7m")) {
				return new DetailModel("201", "Content-Type Name", contentTypeName, "Should be smime.p7m", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("201", "Content-Type Name", contentTypeName, "Should be smime.p7m", rfc, Status.WARNING);
			}
		} else {
			return new DetailModel("201", "Content-Type Name", "Not present", "Should be present", rfc, Status.ERROR);
		} 

	}

	// DTS 202, Content-Type S/MIME-Type, Optional
	public DetailModel validateContentTypeSMIMETypeOptional(String contentTypeSMIME) {
		String rfc = "RFC 5751: 3.2;http://tools.ietf.org/html/rfc5751#section-3.2;RFC 5751: 3.3;http://tools.ietf.org/html/rfc5751#section-3.3";
		if(contentTypeSMIME.contains("smime-type")) {
			contentTypeSMIME = contentTypeSMIME.split("smime-type=")[1];
			if(contentTypeSMIME.contains(";")) {
				contentTypeSMIME = contentTypeSMIME.split(";")[0];
			}
			contentTypeSMIME = contentTypeSMIME.replace("\"", "");
			if (contentTypeSMIME.equals("enveloped-data")) {
				return new DetailModel("202", "Content-Type S/MIME Type", contentTypeSMIME, "Should be enveloped-data", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("202", "Content-Type S/MIME Type", contentTypeSMIME, "Should be enveloped-data", rfc, Status.ERROR);
			}
		} else {
			return new DetailModel("202", "Content-Type S/MIME Type", "Not present: " + contentTypeSMIME, "Should be enveloped-data", rfc, Status.WARNING);
		}

	}

	// DTS 203, Content Disposition, Optional
	public DetailModel validateContentDispositionOptional(String contentDisposition) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1";
		if(!contentDisposition.equals("")) {
			if (contentDisposition.contains("smime.p7m")) {
				return new DetailModel("203", "Content-Disposition", contentDisposition, "Should have filename smime.p7m", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("203", "Content-Disposition", contentDisposition, "Should have filename smime.p7m", rfc, Status.WARNING);
			}
		} else {
			return new DetailModel("203", "Content-Disposition", "Not present", "Should have filename smime.p7m", rfc, Status.ERROR);
		}

	}

	// DTS 129, Message Body, Required
	public DetailModel validateMessageBody(boolean decrypted) {
		String rfc = "-";
		if(decrypted) {
			return new DetailModel("129", "Message Body", "Message has been decrypted", "Must be encrypted", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("129", "Message Body", "Message has not been decrypted", "Must be encrypted", rfc, Status.ERROR);
		}

	}


	// xxxxxxxxxxxxxxx Inner Decrypted Message  xxxxxxxxxxxxxxx


	// DTS 204, MIME Entity, Required
	public DetailModel validateMIMEEntity2(boolean mimeEntity) {
		String rfc = "-";
		if(mimeEntity) {
			return new DetailModel("204", "MIME Entity", "All MIME Entity are valid", "All MIME Entity must be valid", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("204", "MIME Entity", "Some MIME Entity are not valid", "All MIME Entity must be valid", rfc, Status.ERROR);
		}

	}

	// DTS 133b, Content-Type, Required
	public DetailModel validateMessageContentTypeB(String messageContentTypeB) {
		String rfc = "RFC 2045: Section 5;http://tools.ietf.org/html/rfc2045#section-5;RFC 5751: Section 3.4.3.2;http://tools.ietf.org/html/rfc5751#section-3.4.3.2";
		if (messageContentTypeB.contains("multipart/signed")){
			return new DetailModel("133b", "Content-Type", messageContentTypeB, "Must be multipart/signed", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("133b", "Content-Type", messageContentTypeB, "Must be multipart/signed", rfc, Status.ERROR);
		}

	}

	// DTS 160, Content-Type micalg, Required
	public DetailModel validateContentTypeMicalg(String contentTypeMicalg) {
		String rfc = "RFC 5751: Section 2.2;http://tools.ietf.org/html/rfc5751#section-2.2;RFC 5751: Section 3.4.3.2;http://tools.ietf.org/html/rfc5751#section-3.4.3.2";
		if (contentTypeMicalg.equals("")) {
			return new DetailModel("160", "Content-Type micalg", "Not present", "Must be present", rfc, Status.ERROR);
		} else {
			// Validates the "micalg" parameter value
			if ((contentTypeMicalg.contains("sha")) && (contentTypeMicalg.contains("1") || contentTypeMicalg.contains("256"))) {
				return new DetailModel("160", "Content-Type micalg", contentTypeMicalg, "Must be sha-1, sha-256 or sha1", rfc, Status.SUCCESS);
			} else {
				// error code 133-3
				return new DetailModel("160", "Content-Type micalg", contentTypeMicalg, "Must be sha-1, sha-256 or sha1", rfc, Status.ERROR);
			}
		}

	}

	// DTS 205, Content-Type protocol, Required
	public DetailModel validateContentTypeProtocol(String contentTypeProtocol) {
		String rfc = "RFC 5751: Section 3.4.3.2;http://tools.ietf.org/html/rfc5751#section-3.4.3.2";
		if (contentTypeProtocol.equals("\"application/pkcs7-signature\"")){
			return new DetailModel("205", "Content-Type protocol", contentTypeProtocol, "Must be application/pkcs7-signature", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("205", "Content-Type protocol", contentTypeProtocol, "Must be application/pkcs7-signature", rfc, Status.ERROR);
		}

	}

	// DTS 206, Content-Transfer-Encoding, Required
	public DetailModel validateContentTransferEncoding(String contentTransfertEncoding) {
		String rfc = "RFC 5751: Section 3.1.3;http://tools.ietf.org/html/rfc5751#section-3.1.3";
		if (contentTransfertEncoding.equals("quoted-printable") || contentTransfertEncoding.equals("base64") || contentTransfertEncoding.equals("7-bit")){
			return new DetailModel("206", "Content-Transfer-Encoding", contentTransfertEncoding, "Must be quoted-printable, base64 or 7-bit", rfc, Status.SUCCESS);
		} else if(contentTransfertEncoding.equals("")) {
			return new DetailModel("206", "Content-Transfer-Encoding", "Not present", "Must be quoted-printable, base64 or 7-bit", rfc, Status.WARNING);
		} else {
			return new DetailModel("206", "Content-Transfer-Encoding", contentTransfertEncoding, "Must be quoted-printable, base64 or 7-bit", rfc, Status.ERROR);
		}

	}

	// DTS 207, MIME Entity Body, Required
	public DetailModel validateMIMEEntityBody(int nbBody) {
		String rfc = "-";
		if (nbBody != 2){
			return new DetailModel("207", "MIME Entity Body", "Number of part: " + nbBody, "Must have 2 parts", rfc, Status.ERROR);
		} else {
			return new DetailModel("207", "MIME Entity Body", "Number of part: " + nbBody, "Must have 2 parts", rfc, Status.SUCCESS);
		}

	}


	// xxxxxxxxxxxxxxx Health Content Container  xxxxxxxxxxxxxxx


	// DTS 139, First MIME Part, Required
	public DetailModel validateFirstMIMEPart(boolean firstMIMEPart) {
		String rfc = "-";
		if(firstMIMEPart) {
			return new DetailModel("139", "First MIME Part", "MIME Entity are valid", "MIME Entity of Health Container", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("139", "First MIME Part", "MIME Entity are not valid", "MIME Entity of Health Container", rfc, Status.ERROR);
		}

	}

	// DTS 151, First MIME Part Body, Required
	public DetailModel validateFirstMIMEPartBody(boolean firstMIMEPartBody) {
		String rfc = "RFC 2046: Section 4.1.1;http://tools.ietf.org/html/rfc2046#section-4.1.1";
		if(firstMIMEPartBody) {
			return new DetailModel("151", "First MIME Part Body", "Valid First Part Body", "", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("151", "First MIME Part Body", "Invalid First Part Body", "", rfc, Status.ERROR);
		}
	}


	// xxxxxxxxxxxxxxx Signature  xxxxxxxxxxxxxxx


	// DTS 152, Second MIME Part, Required
	public DetailModel validateSecondMIMEPart(boolean secondMIMEPart) {
		String rfc = "-";
		if(secondMIMEPart) {
			return new DetailModel("152", "Second MIME Part", "MIME Entity are valid", "MIME Entity of Second Part", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("152", "Second MIME Part", "MIME Entity are not valid", "MIME Entity of Second Part", rfc, Status.ERROR);
		}

	}

	// DTS 208, All Non-MIME Message Headers
	public DetailModel validateAllNonMIMEMessageHeaders(PartModel part, String nonMIMEHeader) {
		String rfc = "-";
		if(!part.isStatus()) {
			return new DetailModel("152", "All Non-MIME Message Headers", "Some Non-MIME Headers are not valid", "All Non-MIME Headers must be valid", rfc, Status.ERROR);
		} else {
			return new DetailModel("152", "All Non-MIME Message Headers", "All Non-MIME Headers are valid", "All Non-MIME Headers must be valid", rfc, Status.SUCCESS);
		}

	}

	// DTS 155, Content-Type, Required
	public DetailModel validateContentType2(String contentType) {
		String rfc = "RFC 5751: Section 3.2.1;http://tools.ietf.org/html/rfc5751#section-3.2.1;RFC 5751: Section 5.1.1;http://tools.ietf.org/html/rfc5751#section-5.1.1;RFC 5751: 3.4.3.2;http://tools.ietf.org/html/rfc5751#section-3.4.3.2";
		if(contentType.contains("multipart/signed")) {
			return new DetailModel("155", "Content-Type", contentType, "Must be application/pkcs7-signature", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("155", "Content-Type", contentType, "Must be application/pkcs7-signature", rfc, Status.ERROR);
		}

	}

	// DTS 158, Second MIME Part Body, Required
	public DetailModel validateSecondMIMEPartBody(String secondMIMEPartBody) {
		String rfc = "RFC 5751: Section 3.4.3.2;http://tools.ietf.org/html/rfc5751#section-3.4.3.2";
		return new DetailModel("155", "Second MIME Part Body", "Second MIME Part Body is base64 encoded", "Must be base64 encoded", rfc, Status.SUCCESS);

	}

	// DTS 163, ?, Required
	public DetailModel validateDTS163(String dts163) {
		return null;

	}


	// DTS 165, DigestAlgorithm, Required
	public DetailModel validateDigestAlgorithm(String digestAlgorithm) {
		return null;

	}

	// DTS 166, EncapsuledContentInfo, Required
	public DetailModel validateEncapsuledInfo(String encapsulatedInfo) {
		return null;

	}

	// DTS 182, EncapsuledContentInfo.eContentInfo, Required
	public DetailModel validateEncapsuledInfo2(String encapsulatedInfo) {
		String rfc = "RFC 5652: 5.2;http://tools.ietf.org/html/rfc5652#section-5.2";
		if(encapsulatedInfo.equals("")) {
			return new DetailModel("182", "EncapsuledContentInfo.eContentInfo", "Not present", "Must be not be present", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("182", "EncapsuledContentInfo.eContentInfo", encapsulatedInfo, "Must be not be present", rfc, Status.ERROR);
		}

	}

	// DTS 183, EncapsuledContentInfo.eContent, Optional
	public DetailModel validateEncapsuledInfo3(String encapsulatedInfo) {
		return null;

	}

	// DTS 167, Certificates
	public DetailModel validateCertificates(String certificates) {
		return null;

	}

	// DTS 168, Crls
	public DetailModel validateCrls(String crls) {
		return null;

	}

	// DTS 169, SignerInfos, Optional
	public DetailModel validateSignerInfos(String signerInfos) {
		return null;

	}

	// DTS 173, SignerInfos.sid, Optional
	public DetailModel validateSignerInfosSid(String signerInfosSid) {
		return null;

	}

	// DTS 174, SignerInfos.signerIdentifier, Required
	public DetailModel validateSignerIdentifier(String signerIdentifier) {
		return null;

	}

	// DTS 175, SignerInfos.signerIdentifier.issuerAndSerialNumber, Conditional
	public DetailModel validateSignerIdentifierIssueAndSerialNumber(String signerInfos) {
		return null;

	}

	// DTS 176, SignerInfos.signerIdentifier.subjectKeyIdentifier, Condtional
	public DetailModel validateSignerIdentifierSubjectKeyIdentifier(String signerInfos) {
		return null;

	}

	// DTS 177, SignerInfos.digestAlgorithm, Required
	public DetailModel validateSignerInfosDigestAlgorithm(String signerInfosDigestAlgorithm) {
		return null;

	}

	// DTS 178, SignerInfos.signedAttrs, Conditional
	public DetailModel validateSignedAttrs(String signerInfos) {
		return null;

	}

	// DTS 179, SignerInfos.signedAttrs.messageDigest, Conditional
	public DetailModel validateSignedAttrsMessageDigest(String signerInfos) {
		return null;

	}

	// DTS 180, SignerInfos.signedAttrs.contentType, Conditional
	public DetailModel validateSignedAttrsContentType(String signerInfos) {
		return null;

	}

	// DTS 170, SignerInfos.SignatureAlgorithm, Required
	public DetailModel validateSignerInfosSignatureAlgorithm(String signerInfosSignatureAlgorithm) {
		return null;

	}

	// DTS 171, SignerInfos.Signature, Required
	public DetailModel validateSignerInfosSignature(String signerInfosSignature) {
		return null;

	}

	// DTS 181, SignerInfos.unsignedAttrs, Optional
	public DetailModel validateSignerInfosUnsignedAttrs(String signerInfosUnsignedAttrs) {
		return null;

	}

	// DTS 172, Boundary, Required
	public DetailModel validateBoundary(String boundary) {
		return null;

	}

}
