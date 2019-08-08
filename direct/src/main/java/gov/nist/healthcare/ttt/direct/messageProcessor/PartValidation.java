package gov.nist.healthcare.ttt.direct.messageProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.util.Store;
import org.json.JSONObject;

import gov.nist.healthcare.ttt.database.log.CCDAValidationReportImpl;
import gov.nist.healthcare.ttt.database.log.CCDAValidationReportInterface;
import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.database.log.PartInterface;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageValidator;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;
import gov.nist.healthcare.ttt.direct.directValidator.DirectSignatureValidator;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.DetailModel;
import gov.nist.healthcare.ttt.model.logging.PartModel;

public class PartValidation {

	private Logger logger = Logger.getLogger(PartValidation.class.getName());

	private DetailedPartValidation detailedpartValidation = new DetailedPartValidation();
	private DirectMessageValidator directMessageValidator = new DirectMessageValidator();
	private DirectMimeEntityValidator mimeEntityValidator = new DirectMimeEntityValidator();
	private DirectSignatureValidator signatureValidator = new DirectSignatureValidator();

	private boolean wrapped;
	private boolean encrypted;
	private boolean signed;
	private boolean hasError;

	// MDHT Endpoint
	private String mdhtR1Endpoint;
	private String mdhtR2Endpoint;

	// Toolkit endpoint
	private String toolkitUrl;

	private String ccdaType;
	private String ccdaR2Type;
	private String ccdaR2ReferenceFilename;
	private List<CCDAValidationReportInterface> ccdaReport = new ArrayList<CCDAValidationReportInterface>();

	public PartValidation(boolean encrypted, boolean signed, boolean wrapped, String mdhtR1Endpoint, String mdhtR2Endpoint, String toolkitUrl) {
		this.encrypted = encrypted;
		this.signed = signed;
		this.wrapped = wrapped;
		this.hasError = false;
		this.mdhtR1Endpoint = mdhtR1Endpoint;
		this.mdhtR2Endpoint = mdhtR2Endpoint;
		this.toolkitUrl = toolkitUrl;
	}

	public void processMainPart(PartModel part) throws Exception {
		Part p = part.getContent();

		// MIME Entity Validation
		detailedpartValidation.validateMimeEntity(part);

		// If this is a message part (outer envelope or message/rfc822)
		if(p instanceof MimeMessage) {
			try {
				Address[] toAddr = ((Message) p).getRecipients(Message.RecipientType.TO);
				if(toAddr != null) {
					this.ccdaType = getCCDAType(ValidationUtils.fillArrayLog(toAddr).get(0));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			this.processEnvelope(part);
		} else if(!part.hasParent()) {
			logger.error("File is not a Direct Message");
			part.addNewDetailLine(new DetailModel("No DTS", "Unexpected Error", "File is not a Direct Message", "",	"-", Status.ERROR));
		}

		if(part.hasParent()) {
			if(p.isMimeType("multipart/signed")) {    // If this is multipart
				convertContentToBlob(part, p);
				this.validateMultipartSigned(part);
			} else if(p.isMimeType("multipart/*")) {
				this.processMultipart(part);
			} else if(p.isMimeType("message/rfc822")) {
				// DTS 151, Validate First MIME Part Body
				part.addNewDetailLine(directMessageValidator.validateFirstMIMEPartBody(true));
			} else if(p.isMimeType("application/pkcs7-signature") || p.isMimeType("application/x-pkcs7-signature")) {
				// Validate signature Part
				detailedpartValidation.validateSignaturePart(part);
				this.processLeafPart(part);
			} else {
				this.processLeafPart(part);
				convertLeafContentToBlob(part, p);
			}
		}

		if(!part.isStatus()) {
			this.hasError = true;
		}

		if(part.hasChild()) {
			Iterator<PartInterface> it = part.getChildren().iterator();
			while(it.hasNext()) {
				this.processMainPart((PartModel) it.next());
			}
		}
	}

	/**
	 * Validates the envelope of the message
	 * @throws Exception
	 * */
	public void processEnvelope(PartModel part) throws Exception {
		Part p = part.getContent();

		try {
			// Validation Message Headers
			detailedpartValidation.validateMessageHeader(part, this.wrapped);

		} catch (Exception e) {
			logger.error(e.getMessage());
			part.addNewDetailLine(new DetailModel("No DTS", "Unexpected Error", "Problem parsing message file", "", "", Status.ERROR));
			throw e;
		}

		// Message is not encrypted: Issue an error
		if(!this.encrypted && !part.hasParent()) {
			part.addNewDetailLine(new DetailModel("No DTS", "Unexpected Error", "Message is not encrypted", "Should be encrypted", "", Status.ERROR));
		}

		// Message is not signed: Issue an error
		if(!this.signed && !part.hasParent()) {
			part.addNewDetailLine(new DetailModel("No DTS", "Unexpected Error", "Message is not signed", "Should be signed", "", Status.ERROR));
		}

		// Message is not wrapped: Issue an error
		if(!this.wrapped && !part.hasParent()) {
			part.addNewDetailLine(new DetailModel("No DTS", "Unexpected Error", "Message is not wrapped", "Should be wrapped", "", Status.ERROR));
		}

		// Check the Encryption algorithm
		if(!part.hasParent()) {
			try {
				SMIMEEnveloped env = new SMIMEEnveloped((MimeMessage) p);
				String OID = env.getEncryptionAlgOID();
				part.addNewDetailLine(new DetailModel("No DTS", "Encryption Algorithm used", OID, "AES 128 or AES 256 (or better)", "-", Status.INFO));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		if(p.isMimeType("application/pkcs7-mime") || p.isMimeType("application/x-pkcs7-mime")) {

			// DTS 133a, Content-Type
			part.addNewDetailLine(directMessageValidator.validateMessageContentTypeA(part.getContentType()));

			// DTS 201, Content-Type Name
			part.addNewDetailLine(directMessageValidator.validateContentTypeNameOptional(part.getContentType()));

			// DTS 202, Content-Type S/MIME Type
			part.addNewDetailLine(directMessageValidator.validateContentTypeSMIMETypeOptional(part.getContentType()));

			// DTS 203, Content-Disposition
			part.addNewDetailLine(directMessageValidator.validateContentDispositionOptional(part.getContentDisposition()));

			// DTS 161-194 Validate Content-Disposition Filename
			part.addNewDetailLine(mimeEntityValidator.validateContentDispositionFilename(getFilename(part.getContentDisposition())));

		}

		// Write the blob in the part if it is not message/rfc822
		if(!p.isMimeType("message/rfc822") && !p.isMimeType("multipart/*")) {
			convertContentToBlob(part, p);
		}
	}

	public void processMultipart(PartModel part) throws Exception {
		// Part p = part.getContent();

		// DTS 151, Validate First MIME Part Body
		part.addNewDetailLine(directMessageValidator.validateFirstMIMEPart(true));

		// DTS 152, Validate Second MIME Part
		part.addNewDetailLine(directMessageValidator.validateSecondMIMEPart(true));

		// DTS 155, Validate Content-Type
		// part.addNewDetailLine(directMessageValidator.validateContentType2(p.getContentType()));
	}

	public void validateMultipartSigned(PartModel part) throws Exception {
		Part p = part.getContent();

		// DTS 129, Message Body
		part.addNewDetailLine(directMessageValidator.validateMessageBody(true));

		// Validate inner message
		detailedpartValidation.validateDirectMessageInnerDecryptedMessage(part);

		SMIMESigned s = new SMIMESigned((MimeMultipart)p.getContent());

		// Find micalg
		String micalg = "";
		if(p.getContentType().contains("micalg=")) {
			micalg = p.getContentType().split("micalg=")[1];
			if(micalg.contains(";")) {
				micalg = micalg.split(";")[0];
			}
		}

		// verify signature
		this.verifySignature(part, s, micalg);
	}

	public void processLeafPart(PartModel part) throws Exception {
		Part p = part.getContent();

		// Validate CCDA
		if(p.isMimeType("text/xml") || p.isMimeType("application/xml")) {
			validateCCDAwithMDHT(part);
		}

		if(p.isMimeType("application/zip") || p.isMimeType("application/octet-stream") || p.isMimeType("application/x-zip-compressed")) {
			validateXDM(part);
		}
	}

	public String validateCCDAwithMDHT(PartModel part) throws Exception {
		Part p = part.getContent();
		String ccdaFilename = "";
		File ccdaFile = null;
		if(p.getFileName() != null) {
			ccdaFilename = getGoodFilename(p.getFileName());
			if(part.isQuotedPrintable()) {
				ccdaFile = getCCDAFile(DirectMessageProcessor.decodeQPStream(p.getInputStream()), p.getFileName());
			} else {
				ccdaFile = getCCDAFile(p.getInputStream(), p.getFileName());
			}
		} else {
			ccdaFilename = UUID.randomUUID().toString();
		}

		if(this.ccdaType.equals("r2")) {
			return validateCCDA_R2(ccdaFile, ccdaFilename);
		} else {
			return validateCCDA_R1(ccdaFile, ccdaFilename);
		}
	}

	public String validateXDM(PartModel part) throws Exception {
		Part p = part.getContent();
		String xdmFilename = "";
		File xdmFile = null;
		if(p.getFileName() != null) {
			xdmFilename = getGoodFilename(p.getFileName());
			if(part.isQuotedPrintable()) {
				xdmFile = getCCDAFile(DirectMessageProcessor.decodeQPStream(p.getInputStream()), p.getFileName());
			} else {
				xdmFile = getCCDAFile(p.getInputStream(), p.getFileName());
			}
		} else {
			xdmFilename = UUID.randomUUID().toString();
		}

		xdmFilename = "XDM_" + xdmFilename;

		byte[] xdmByte = IOUtils.toByteArray(new FileInputStream(xdmFile));

		// Query toolkit endpoint
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(this.toolkitUrl + "/rest/simulators/xdmValidation");
		//
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		String reqString = Base64.getEncoder().encodeToString(xdmByte);

		JSONObject json = new JSONObject();
		json.put("zip", reqString);
		StringEntity params = null;
		try {
			params = new StringEntity(json.toString());
		} catch (UnsupportedEncodingException e1) {
			logger.error(e1.getMessage());
		}

		post.addHeader("content-type", "application/json");
		post.setEntity(params);

		String result = "";
		try {
			HttpResponse response = client.execute(post);
			// CONVERT RESPONSE TO STRING
			result = EntityUtils.toString(response.getEntity());
		} catch(Exception e) {
			logger.error(e.getMessage());
		}

		CCDAValidationReportImpl report = new CCDAValidationReportImpl();
		report.setFilename(xdmFilename);
		report.setValidationReport(result);

		ccdaReport.add(report);

		return result;
	}

	/**
	 * verify the signature (assuming the cert is contained in the message)
	 */
	@SuppressWarnings("rawtypes")
	private void verifySignature(PartModel part, SMIMESigned s, String contentTypeMicalg) throws Exception{

		// DTS-164, SignedData exists for the message
		part.addNewDetailLine(signatureValidator.validateSignedData(s.getSignedContent()));

		//
		// extract the information to verify the signatures.
		//

		//
		// certificates and crls passed in the signature
		//
		Store certs = s.getCertificates();

		//
		// SignerInfo blocks which contain the signatures
		//
		SignerInformationStore  signers = s.getSignerInfos();

		Collection c = signers.getSigners();
		Iterator it = c.iterator();

		String digestAlgOID = "";

		// DTS 167, SignedData.certificates must contain at least one certificate
		part.addNewDetailLine(signatureValidator.validateSignedDataAtLeastOneCertificate(c));

		//
		// check each signer
		//
		while (it.hasNext())
		{
			SignerInformation   signer = (SignerInformation)it.next();
			// Get digest Algorihm OID
			digestAlgOID = signer.getDigestAlgOID();
			Collection certCollection = certs.getMatches(signer.getSID());

			Iterator certIt = certCollection.iterator();
			X509Certificate cert = null;
			try {
				cert = new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate((X509CertificateHolder)certIt.next());
			} catch (Exception e) {
				part.addNewDetailLine(new DetailModel("No DTS", "Certificate File", "Cannot extract the signing certificate", "", "-", Status.ERROR));
				throw e;
			}

			// DTS 158, Second MIME Part Body
			part.addNewDetailLine(directMessageValidator.validateSecondMIMEPartBody(""));

			// DTS 165, AlgorithmIdentifier.algorithm
			part.addNewDetailLine(new DetailModel("No DTS", "Encryption asymmetric algorithm", signer.getEncryptionAlgOID(), "AES 128 or AES 256 (or better)", "-", Status.INFO));
			part.addNewDetailLine(signatureValidator.validateDigestAlgorithmDirectMessage(digestAlgOID, contentTypeMicalg));

			// DTS 166, SignedData.encapContentInfo
			part.addNewDetailLine(signatureValidator.validateSignedDataEncapContentInfo(new String(cert.getSignature())));

			// DTS 222, tbsCertificate.signature.algorithm
			part.addNewDetailLine(signatureValidator.validateTbsCertificateSA(cert.getSigAlgName()));
			// needs signer.getDigestAlgorithmID(); and compare the two (needs to be the same)

			// DTS 225, tbsCertificate.subject
			part.addNewDetailLine(signatureValidator.validateTbsCertificateSubject(cert.getSubjectDN().toString()));

			// DTS 240, Extensions.subjectAltName
			// C-4 - cert/subjectAltName must contain either rfc822Name or dNSName extension
			// C-5 cert/subjectAltName/rfc822Name must be an email address - Conditional
			part.addNewDetailLine(signatureValidator.validateExtensionsSubjectAltName(cert.getSubjectAlternativeNames()));

			// C-2 - Key size <=2048
			//msgValidator.validateKeySize(er, new String(cert.getPublicKey()));


			// -------how to get other extension fields:
			//-------  cert.getExtensionValue("2.5.29.17")

			// verify that the sig is valid and that it was generated
			// when the certificate was current
			part.addNewDetailLine(signatureValidator.validateSignature(cert, signer, BouncyCastleProvider.PROVIDER_NAME));

			//verify and get the digests
			Attribute digAttr = signer.getSignedAttributes().get(CMSAttributes.messageDigest);
			ASN1Primitive hashObj = digAttr.getAttrValues().getObjectAt(0).toASN1Primitive();
			byte[] signedDigest = ((ASN1OctetString)hashObj).getOctets();
			String signedDigestHex = org.apache.commons.codec.binary.Hex.encodeHexString(signedDigest);
			String digestHex = "";
			// System.out.println("\r\nSigned Message Digest: " + signedDigestHex);

			try {
				signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(cert));
			} catch (Exception e) {
				logger.error("Signature failed to verify: " + e.getMessage());
			}
			// should have the computed digest now
			try {
				byte[] digest = signer.getContentDigest();
				digestHex = org.apache.commons.codec.binary.Hex.encodeHexString(digest);
			} catch(Exception e) {
				logger.error("Failed to get the computed digest: " + e.getMessage());
			}
			// System.out.println("\r\nComputed Message Digest: " + digestHex);
			part.addNewDetailLine(new DetailModel("No DTS", "Signature digest", "Signed Message Digest: " + signedDigestHex, "Computed Message Digest: " + digestHex, "-", Status.INFO));

		}
	}

	public void convertContentToBlob(PartModel part, Part p) throws IOException, MessagingException {
		// Convert content
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		p.writeTo(out);
		String contentString = "";
		contentString = out.toString();
		part.setRawMessage(contentString);
	}

	public void convertLeafContentToBlob(PartModel part, Part p) throws IOException, MessagingException {
		if(p.getContent() instanceof String) {
			String content = (String) p.getContent();
			part.setRawMessage(content);
		} else {
			convertContentToBlob(part, p);
		}
	}

	public String getFilename(String contentDisposition) {
		String res = contentDisposition;
		if(contentDisposition.contains("filename=")) {
			res = contentDisposition.split("filename=")[1];
			res = res.split(";")[0];
			res = res.replace("\"", "");
		}
		return res;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public File getCCDAFile(InputStream stream, String filename) {

		String tDir = System.getProperty("java.io.tmpdir");
		OutputStream outputStream = null;

		File temp;
		if(filename != null) {
			temp = new File(tDir + File.separator + filename);
		} else {
			temp = new File(tDir + File.separator + "tempCCDA.xml");
		}

		try {

			// write the inputStream to a FileOutputStream
			outputStream = new FileOutputStream(temp);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = stream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return temp;
	}

	public String getCCDAType(String to) {
		String trimmedTo = to.split("@")[0];

		HashMap<String, String> types = new HashMap<String, String>();
		types.put("direct-clinical-summary", "ClinicalOfficeVisitSummary");
		types.put("direct-ambulatory2", "TransitionsOfCareAmbulatorySummary");
		types.put("direct-ambulatory7", "TransitionsOfCareAmbulatorySummary");
		types.put("direct-ambulatory1", "TransitionsOfCareAmbulatorySummary");
		types.put("direct-inpatient2", "TransitionsOfCareInpatientSummary");
		types.put("direct-inpatient7", "TransitionsOfCareInpatientSummary");
		types.put("direct-inpatient1", "TransitionsOfCareInpatientSummary");
		types.put("direct-vdt-ambulatory", "VDTAmbulatorySummary");
		types.put("direct-vdt-inpatient", "VDTInpatientSummary");
		types.put("ccda", "NonSpecificCCDA");

		if(types.containsKey(trimmedTo)) {
			logger.info("CCDA R1 type: " + types.get(trimmedTo));
			return types.get(trimmedTo);
		} else if(trimmedTo.startsWith("r2_") || trimmedTo.startsWith("testing")) {
			logger.info("To address start with r2_ this should contain R2 CCDA");
			// Get ccdar2 types
			getCCDAR2Type(trimmedTo);
			return "r2";
		} else {
			return "NonSpecificCCDA";
		}
	}

	public void getCCDAR2Type(String to) {
		// Initialize ccda r2 type variables
		this.ccdaR2Type = "NonSpecificCCDA";
		this.ccdaR2ReferenceFilename = "";

		// Get type and reference filename from direct to address
		/*
		this.ccdaR2ReferenceFilename = to.substring(3);

		if(this.ccdaR2ReferenceFilename.contains("_sample")) {
			this.ccdaR2Type = this.ccdaR2ReferenceFilename.split("_sample")[0];
		} else {
			this.ccdaR2Type = this.ccdaR2ReferenceFilename;
			this.ccdaR2ReferenceFilename = "";
		}
		*/
		
		logger.info("CCDA R2 validation params: Type " + this.ccdaR2Type + " Ref filename " + this.ccdaR2ReferenceFilename);
	}

	public List<CCDAValidationReportInterface> getCcdaReport() {
		return ccdaReport;
	}

	public void setCcdaReport(List<CCDAValidationReportInterface> ccdaReport) {
		this.ccdaReport = ccdaReport;
	}

	public String getGoodFilename(String filename) {
		filename = filename.replace("\\", "");
		filename = filename.replace("/", "");
		filename = filename.replace(":", "");
		filename = filename.replace("*", "");
		filename = filename.replace("?", "");
		filename = filename.replace("\"", "");
		filename = filename.replace("<", "");
		filename = filename.replace(">", "");
		filename = filename.replace("|", "");
		return filename;
	}

	public String validateCCDA_R1(File ccdaFile, String ccdaFilename) {
		try {
			logger.info("Trying CCDA validation at: " + this.mdhtR1Endpoint);
			int timeout = 5;
			int sotimeout = 10;
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(timeout * 1000)
					.setConnectionRequestTimeout(timeout * 1000)
					.setSocketTimeout(sotimeout * 1000).build();
			CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpPost post = new HttpPost(this.mdhtR1Endpoint);
			FileBody fileBody = new FileBody(ccdaFile);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		    builder.addTextBody("validationObjective", this.ccdaType);
		    builder.addTextBody("referenceFileName", ccdaFilename);
		    builder.addPart("ccdaFile", fileBody);

			//builder.addPart("file", fileBody);
			//builder.addTextBody("type_val", this.ccdaType);
			HttpEntity entity = builder.build();

			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			// CONVERT RESPONSE TO STRING
			String result = EntityUtils.toString(response.getEntity());
			JSONObject jsonObj = new JSONObject(result);
			JSONObject resultsMetaData = jsonObj.getJSONObject("resultsMetaData");
			String ccdaversion = (String)resultsMetaData.get("ccdaVersion");
			  jsonObj.put("ccdaRType", ccdaversion != null && ccdaversion.startsWith("R2") ? "r2" : "r1");
			result = jsonObj.toString();

			CCDAValidationReportImpl report = new CCDAValidationReportImpl();
			report.setFilename(ccdaFilename);
			report.setValidationReport(result);

			this.ccdaReport.add(report);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public String validateCCDA_R2(File ccdaFile, String ccdaFilename) {
		logger.info("Validating CCDA " + ccdaFilename + " with validation objective " + this.ccdaR2Type + " and reference filename " + this.ccdaR2ReferenceFilename);

		// Query MDHT war endpoint
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(this.mdhtR2Endpoint);
		FileBody fileBody = new FileBody(ccdaFile);
		//
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("validationObjective", this.ccdaR2Type);
		builder.addTextBody("referenceFileName", this.ccdaR2ReferenceFilename);
		builder.addPart("ccdaFile", fileBody);
		HttpEntity entity = builder.build();
		//
		post.setEntity(entity);
		String result = "";
		try {
			HttpResponse response = client.execute(post);
			// CONVERT RESPONSE TO STRING
			result = EntityUtils.toString(response.getEntity());
			  JSONObject jsonObj = new JSONObject(result);
			JSONObject resultsMetaData = jsonObj.getJSONObject("resultsMetaData");
			String ccdaversion = (String)resultsMetaData.get("ccdaVersion");
				  jsonObj.put("ccdaRType", ccdaversion != null && ccdaversion.startsWith("R1") ? "r1" : "r2");
			  //jsonObj.put("ccdaRType", "r2");
			result = jsonObj.toString();
		} catch(Exception e) {
			logger.error("Error validation CCDA " + e.getMessage());
			e.printStackTrace();
		}

		CCDAValidationReportImpl report = new CCDAValidationReportImpl();
		report.setFilename(ccdaFilename);
		report.setValidationReport(result);

		ccdaReport.add(report);

		return result;




	}

}
