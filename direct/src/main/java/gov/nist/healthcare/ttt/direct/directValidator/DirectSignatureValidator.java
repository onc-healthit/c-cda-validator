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

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.DetailModel;

public class DirectSignatureValidator {

	// ************************************************
	// ************** Signature checks ****************
	// ************************************************
	

	// DTS 166, SignedData.encapContentInfo, Required
	public DetailModel validateSignedDataEncapContentInfo(String SignedDataEncapContentInfo) {
		String rfc = "RFC 5652: 5.1, 5.2;http://tools.ietf.org/html/rfc5652#section-5.1";
		if(!SignedDataEncapContentInfo.equals("")) {
			return new DetailModel("166", "SignedData.encapContentInfo", SignedDataEncapContentInfo.substring(0, 50) + "...", "SignedData.encapContentInfo (signed content + content type identifier) must be present" , rfc, Status.SUCCESS);
		} else {
			return new DetailModel("166", "SignedData.encapContentInfo", SignedDataEncapContentInfo.substring(0, 50) + "...", "SignedData.encapContentInfo (signed content + content type identifier) must be present" , rfc, Status.ERROR);
		}
		
	}

	// DTS 222, tbsCertificate.signature.algorithm, Required
	public DetailModel validateTbsCertificateSA(String tbsCertSA) {
		String rfc = "RFC 5280: 4.1.2.3;http://tools.ietf.org/html/rfc5280#section-4.1.2.3";
		if(!tbsCertSA.equals("")) {
			return new DetailModel("222", "tbsCertificate.signature.algorithm", tbsCertSA,  "tbsCertificate.signature.algorithm (name of the algorithm) must be present", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("222", "tbsCertificate.signature.algorithm", tbsCertSA,  "tbsCertificate.signature.algorithm (name of the algorithm) must be present", rfc, Status.ERROR);
		}
		
	}

	// DTS 225, tbsCertificate.subject, Required
	public DetailModel validateTbsCertificateSubject(String tbsCertSubject) {
		String rfc = "RFC 5280: 4.1.2.6, 4.1.2.4;http://tools.ietf.org/html/rfc5280#section-4.1.2.4";
		if(!tbsCertSubject.equals("")) {
			return new DetailModel("225", "tbsCertificate.subject", tbsCertSubject,  "tbsCertificate.subject (subject name) must be present", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("225", "tbsCertificate.subject", tbsCertSubject,  "tbsCertificate.subject (subject name) must be present", rfc, Status.ERROR);
		}
	}
	
	// DTS 240, Extensions.subjectAltName, Conditional
	public DetailModel validateExtensionsSubjectAltName(Collection<List<?>> ExtensionSubjectAltName) {
		String rfc = "RFC 5280: 4.1.2.6;http://tools.ietf.org/html/rfc5280#section-4.1.2.6";
		//System.out.println(ExtensionSubjectAltName);
		if(ExtensionSubjectAltName != null) {
			if (!ExtensionSubjectAltName.isEmpty()){
				if(ExtensionSubjectAltName.size() > 1) {
					return new DetailModel("240", "Extensions.subjectAltName", ExtensionSubjectAltName.toString(),  "Should be a single value", rfc, Status.WARNING);
				} else {
					return new DetailModel("240", "Extensions.subjectAltName", ExtensionSubjectAltName.toString(),  "Must be present", rfc, Status.SUCCESS);
				}
			} else {
				return new DetailModel("240", "Extensions.subjectAltName", "Not present",  "Must be present", rfc, Status.ERROR);
			}
		}
		return new DetailModel("240", "Extensions.subjectAltName", "Not present",  "Must be present", rfc, Status.ERROR);
	}


	// C4 cert/subjectAltName must contain either rfc822Name (email address without comments between parenthesis or <>) or dNSName extension
	@SuppressWarnings({ "rawtypes", "null", "unchecked" })
	public DetailModel validateExtensionsSubjectAltName2(Collection<List<?>> ExtensionSubjectAltName) {
		String rfc = "RFC 5280: 4.1.2.6;http://tools.ietf.org/html/rfc5280#section-4.1.2.6";
		//System.out.println(ExtensionSubjectAltName);
		if(ExtensionSubjectAltName != null) {
			Iterator it = null;
			// C-4 - cert/subjectAltName must contain either rfc822Name (email address without
			// comments between parenthesis or <>) or dNSName extension (IA5String - which means a
			// random string of ASCII chars including control characters, of any length except zero
			// based on rfc 5280)
			// Required


			// each List contains two items. 1st item is an integer (type of name), second item is the name itself as a String. 
			while (it.hasNext()){
				List<?> currentAltName = (List<String>) it.next();
				int currentNameType = (Integer) currentAltName.get(0);


				if (currentNameType == 1){   // integer type for rfc822Name

					if (ValidationUtils.validateEmail((String)currentAltName.get(1))){	
						// C-5 cert/subjectAltName/rfc822Name must be an email address (if present)
						return new DetailModel("C4", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName must be an email address", rfc, Status.SUCCESS);

					} else {
						return new DetailModel("C5", "Extensions.subjectAltName", currentAltName.get(1).toString(),  "cert/subjectAltName/rfc822Name must be an email address", rfc, Status.ERROR);
					}

				} else if (currentNameType == 2){   // integer type for dnsName.
					String dnsName = (String)currentAltName.get(1);

					if (ValidationUtils.isAscii(dnsName)){

						// C-7 - check 1 - cert/subjectAltName/dnsName must contain domain name (if present)
						Pattern pattern = Pattern.compile(ValidationUtils.domainNameFormat, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(dnsName);

						if (matcher.matches()){
							return new DetailModel("C7", "Extensions.subjectAltName", dnsName,  "cert/subjectAltName/dnsName must contain a domain name. (example: test.validation.com)", rfc, Status.SUCCESS);
						} else {
							return new DetailModel("C7", "Extensions.subjectAltName", dnsName,  "cert/subjectAltName/dnsName must contain a domain name. (example: test.validation.com)", rfc, Status.ERROR);
						}

						// C-7 - check 3 - cert/subjectAltName/dnsName domain name must match the domain name from cert/subject/emailAddr (if present)

					} else {
						return new DetailModel("C4", "Extensions.subjectAltName", "Not found",  "cert/subjectAltName must be an email address", rfc, Status.ERROR);
					}
				}
				
			}
		}
		return new DetailModel("C4", "Extensions.subjectAltName", "Not found",  "cert/subjectAltName must be an email address", rfc, Status.ERROR);		
	}

	// DTS-165	DigestAlgorithm	Direct Message	Required
	public DetailModel validateDigestAlgorithmDirectMessage(String digestAlgo, String micalg) {
		String rfc = "RFC 5280: 4.1.1.2;http://tools.ietf.org/html/rfc5280#section-4.1.1.2";
		String textDigestAlgo = "";
		// Convert the digest algorithm OID into a string
		if(digestAlgo.equals("1.3.14.3.2.26")) {
			textDigestAlgo = "sha1";
		} else if(digestAlgo.equals("2.16.840.1.101.3.4.2.1")) {
			textDigestAlgo = "sha256";
		}
		
		if(textDigestAlgo.contains("sha1") || textDigestAlgo.contains("sha256")) {
			micalg = micalg.replaceAll("-", "");
			micalg = micalg.replaceAll("\"", "");
			micalg = micalg.toLowerCase();
			if(textDigestAlgo.equals(micalg)) {
				return new DetailModel("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must match micalg value", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must match micalg value", rfc, Status.ERROR);
			}
		} else {
			return new DetailModel("165", "DigestAlgorithm", "Digest: " + digestAlgo + ", micalg: "+ micalg,  "Digest algorithm must contain either value \"sha1\" or \"sha256\"", rfc, Status.ERROR);
		}
		
	}

	public DetailModel validateSecondMIMEPart(boolean secondMIMEPart) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateAllNonMIMEMessageHeaders(String nonMIMEHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateContentType2(String contentType) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateDTS163(String dts163) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  DTS 164, Signed Data, Required
	 */
	public DetailModel validateSignedData(CMSProcessable cmsProcessable){
		String rfc = "RFC 5652: 5.1;http://tools.ietf.org/html/rfc5652#section-5.1";	
		if(cmsProcessable.getContent() != null) {
			return new DetailModel("164", "Signed Data", cmsProcessable.toString(),  "Must be present", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("164", "Signed Data", "Not present",  "Must be present", rfc, Status.ERROR);
		}
	}

	
	public DetailModel validateDigestAlgorithm(String digestAlgorithm) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateEncapsuledInfo2(String encapsulatedInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateEncapsuledInfo3(String encapsulatedInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateCertificates(String certificates) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateCrls(String crls) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfos(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfosSid(String signerInfosSid) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerIdentifier(String signerIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerIdentifierIssueAndSerialNumber(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfosDigestAlgorithm(String signerInfosDigestAlgorithm) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerIdentifierSubjectKeyIdentifier(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignedAttrs(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignedAttrsMessageDigest(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignedAttrsContentType(String signerInfos) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfosSignatureAlgorithm(String signerInfosSignatureAlgorithm) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfosSignature(String signerInfosSignature) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateSignerInfosUnsignedAttrs(String signerInfosUnsignedAttrs) {
		// TODO Auto-generated method stub
		return null;
	}

	public DetailModel validateBoundary(String boundary) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	public DetailModel validateSignedDataAtLeastOneCertificate(Collection c) {
		String rfc = "RFC 5652: 5.1, 10.2.3;http://tools.ietf.org/html/rfc5652#section-5.1";
		if(!c.isEmpty()) {
			return new DetailModel("167", "Signed Data", c.toString(),  "Must be present, Message with at least one certificate", rfc, Status.SUCCESS);
		} else {
			return new DetailModel("167", "Signed Data", "No signed data",  "Must be present, Message with at least one certificate", rfc, Status.ERROR);
		}
		
	}

	public DetailModel validateSignature(X509Certificate cert, SignerInformation signer, String BC) {
		String rfc = "RFC 5652: 5.1, 10.2.3;http://tools.ietf.org/html/rfc5652#section-5.1";
		try {
			if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(BC).build(cert))) {
				// C-1 - Certificate has not expired - Required
				return new DetailModel("C1", "Signature", "Signature verified",  "Signature must be verified", rfc, Status.SUCCESS);
			} else {
				return new DetailModel("C1", "Signature", "Signature verification failed",  "Signature must be verified", rfc, Status.ERROR);
			}
		} catch (OperatorCreationException e) {
			e.printStackTrace();
			return new DetailModel("C1", "Signature", "Signature verification failed " + e.getMessage(),  "Signature must be verified", rfc, Status.ERROR);
		} catch (CMSException e) {
			e.printStackTrace();
			return new DetailModel("C1", "Signature", "Signature verification failed " + e.getMessage(),  "Signature must be verified", rfc, Status.ERROR);
		}
	}
	
	// C2 - Key size <=2048
	public DetailModel validateKeySize(String key){
		//		byte[] c = null;
		//			try {
		//				c = Base64.decode(key);
		//			} catch (Base64DecodingException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		System.out.println(key.getBytes().length);
		return null;
	}

	public DetailModel validateSecondMIMEPartBody(String secondMIMEPartBody) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
