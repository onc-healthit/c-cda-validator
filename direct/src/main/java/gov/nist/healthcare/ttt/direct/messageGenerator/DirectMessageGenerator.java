package gov.nist.healthcare.ttt.direct.messageGenerator;

import gov.nist.healthcare.ttt.direct.certificates.PrivateCertificateLoader;
import gov.nist.healthcare.ttt.direct.certificates.PublicCertLoader;
import gov.nist.healthcare.ttt.direct.sender.DnsLookup;
import gov.nist.healthcare.ttt.direct.sender.LdapDnslookUp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.xbill.DNS.TextParseException;

public class DirectMessageGenerator {

	private static Logger logger = Logger.getLogger(DirectMessageGenerator.class.getName());

	protected String textMessage;
	protected String subject;
	protected String fromAddress;
	protected String toAddress;
	protected InputStream attachmentFile;
	protected String attachmentFileName;
	// Signing certificate
	protected InputStream signingCert;
	protected String signingCertPassword;
	// Encryption cert
	protected InputStream encryptionCert;
	// Wrapped or Unwrapped message
	protected boolean isWrapped;
	// Convert to Address
	protected Address from;
	protected Address to;

	protected String wrappedMessageID;

	protected String digestAlgo;

	public DirectMessageGenerator() {

	}

	public DirectMessageGenerator(String textMessage, String subject,
			String fromAddress, String toAddress, InputStream attachmentFile,
			String attachmentFileName, InputStream signingCert,
			String signingCertPassword, InputStream encryptionCert,
			boolean isWrapped, String digestAlgo) throws Exception {
		super();
		this.textMessage = textMessage;
		this.subject = subject;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.attachmentFile = attachmentFile;
		this.attachmentFileName = attachmentFileName;
		this.signingCert = signingCert;
		this.signingCertPassword = signingCertPassword;
		this.encryptionCert = encryptionCert;
		this.isWrapped = isWrapped;
		this.from = new InternetAddress(
				new SMTPAddress().properEmailAddr(this.fromAddress));
		this.to = new InternetAddress(
				new SMTPAddress().properEmailAddr(this.toAddress));
		this.digestAlgo = digestAlgo;
	}

	public MimeMessage generateMessage() throws Exception {

		Security.addProvider(new BouncyCastleProvider());
		MimeBodyPart attachments;
		if (isWrapped) {
			attachments = generateWrappedMultipart();
		} else {
			attachments = generateUnWrappedMultipart();
		}
		MimeBodyPart signed = generateMultipartSigned(attachments);

		return generateEncryptedMessage(signed);
	}

	/**
	 * Generate the Unwrapped attachment part of the message
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public MimeBodyPart generateUnWrappedMultipart() throws MessagingException,
	IOException {

		MimeBodyPart m = new MimeBodyPart();

		if(this.attachmentFile != null && !this.attachmentFileName.equals("")) {
			m.setContent(addAttachments());
		} else {
			m = MessageContentGenerator.addTextPart(this.textMessage);
		}

		return m;
	}

	/**
	 * Generate the Wrapped attachment part of the message
	 * @return
	 * @throws Exception
	 */
	public MimeBodyPart generateWrappedMultipart() throws Exception {


		InternetHeaders rfc822Headers = new InternetHeaders();
		rfc822Headers.addHeaderLine("Content-Type: message/rfc822");
		rfc822Headers.addHeader("To", to.toString());
		rfc822Headers.addHeader("From", from.toString());
		rfc822Headers.addHeader("Subject", subject);
		rfc822Headers.addHeader("Date", new Date().toString());

		MimeMessage message2 = new MimeMessage(
				Session.getDefaultInstance(new Properties()));
		message2.setFrom(from);
		message2.setRecipient(Message.RecipientType.TO, to);
		message2.setSentDate(new Date());
		message2.setSubject(this.subject);

		// Add Multipart if attachment file is not null otherwise add a MimeBodyPart
		if(this.attachmentFile != null && !this.attachmentFileName.equals("")) {
			MimeMultipart mp = addAttachments();
			message2.setContent(mp, mp.getContentType());
		} else {
			message2.setText(this.textMessage);
		}

		message2.saveChanges();

		MimeBodyPart m = new MimeBodyPart();
		m.setContent(message2, "message/rfc822");

		// Set messageID variable
		this.wrappedMessageID = message2.getMessageID();

		return m;
	}

	/**
	 * Generate the attachments multipart
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public MimeMultipart addAttachments() throws MessagingException, IOException {

		MimeMultipart mp = new MimeMultipart();

		// Add the text part
		mp.addBodyPart(MessageContentGenerator.addTextPart(this.textMessage));

		// Add the CDA file if the filename is not empty
		if (!this.attachmentFileName.equals("") && this.attachmentFile != null) {
			mp.addBodyPart(MessageContentGenerator.addAttachement(
					this.attachmentFile, this.attachmentFileName));
		}

		return mp;
	}

	/**
	 * Generate the multipart/signed
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public MimeBodyPart generateMultipartSigned(MimeBodyPart m)
			throws Exception {
		PrivateCertificateLoader loader = new PrivateCertificateLoader(
				this.signingCert, this.signingCertPassword, this.digestAlgo);
		SMIMESignedGenerator gen = loader.getSMIMESignedGenerator();

		MimeMultipart mm = gen.generate(m);

		return generateBodyPartfromMultipart(mm, mm.getContentType());
	}

	/**
	 * Generate the multipart/signed
	 * @param mul
	 * @return
	 * @throws Exception
	 */
	public MimeBodyPart generateBodyPartfromMultipart(MimeMultipart mul, String contentType) {
		MimeBodyPart body = new MimeBodyPart();
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		try {
			mul.writeTo(oStream);
			oStream.flush();
			InternetHeaders headers = new InternetHeaders();
			headers.addHeader("Content-Type", contentType);

			body = new MimeBodyPart(headers, oStream.toByteArray());
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new RuntimeException(ex);
		}

		return body;
	}

	/**
	 * Generate the encrypted message
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public MimeMessage generateEncryptedMessage(MimeBodyPart body) throws Exception {
		// Get session to create message
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		MimeBodyPart encryptedPart = generateEncryptedPart(body);

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(new SMTPAddress().properEmailAddr(this.fromAddress)));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(new SMTPAddress().properEmailAddr(this.toAddress)));
		msg.setSentDate(new Date());
		msg.setContent(encryptedPart.getContent(),
				encryptedPart.getContentType());
		msg.setDisposition("attachment");
		msg.setFileName("smime.p7m");
		if (!isWrapped) {
			msg.setSubject(subject);
		}
		msg.saveChanges();
		if (isWrapped) {
			msg.setHeader("Message-ID", this.wrappedMessageID);
		}

		return msg;
	}
	
	/**
	 * Generate unencrypted message
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public MimeMessage generateUnencryptedMessage(MimeMultipart body) throws Exception {
		// Get session to create message
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(new SMTPAddress().properEmailAddr(this.fromAddress)));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(new SMTPAddress().properEmailAddr(this.toAddress)));
		msg.setSentDate(new Date());
		msg.setContent(body);
		msg.setDisposition("attachment");
		msg.setFileName("smime.p7m");
		if (!isWrapped) {
			msg.setSubject(subject);
		}
		msg.saveChanges();
		if (isWrapped) {
			msg.setHeader("Message-ID", this.wrappedMessageID);
		}

		return msg;
	}

	public MimeMessage generateEncryptedMessageDifferentMsgId(MimeBodyPart body) throws Exception {
		// Get session to create message
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		MimeBodyPart encryptedPart = generateEncryptedPart(body);

		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(new SMTPAddress().properEmailAddr(this.fromAddress)));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(new SMTPAddress().properEmailAddr(this.toAddress)));
		msg.setSentDate(new Date());
		msg.setContent(encryptedPart.getContent(),
				encryptedPart.getContentType());
		msg.setDisposition("attachment");
		msg.setFileName("smime.p7m");
		if (!isWrapped) {
			msg.setSubject(subject);
		}
		msg.saveChanges();
		return msg;
	}

	public MimeBodyPart generateEncryptedPart(MimeBodyPart body) throws Exception {
		// Encryption cert
		PublicCertLoader publicLoader = new PublicCertLoader(
				this.encryptionCert);
		X509Certificate encCert = publicLoader.getCertificate();

		/* Create the encrypter */
		SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
		try {
			encrypter
			.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(
					encCert).setProvider("BC"));
		} catch (Exception e1) {
			logger.warn("Error loading encryption cert - must be in X.509 format" + e1.getMessage());
			throw new Exception("Error loading encryption cert - must be in X.509 format" + e1.getMessage());
		}
		/* Encrypt the message */
		MimeBodyPart encryptedPart = encrypter.generate(body,
				// RC2_CBC
				new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
				.setProvider("BC").build());

		return encryptedPart;
	}

	/**
	 * Alter message to get invalid digest
	 * @throws Exception 
	 */
	public MimeMessage generateAlteredDirectMessage() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		MimeBodyPart attachments;
		if (isWrapped) {
			attachments = generateWrappedMultipart();
		} else {
			attachments = generateUnWrappedMultipart();
		}
		MimeBodyPart signed = generateMultipartSigned(attachments);
		String contentType = signed.getContentType();

		// Add header to alter message
		BodyPart firstContent = ((MimeMultipart) signed.getContent()).getBodyPart(0);
		firstContent.addHeader("NewHeader", "Message altered");
		BodyPart secondContent = ((MimeMultipart) signed.getContent()).getBodyPart(1);

		MimeMultipart signedMultipart = new MimeMultipart();
		signedMultipart.addBodyPart(firstContent, 0);
		signedMultipart.addBodyPart(secondContent, 1);

		String boundary = signedMultipart.getContentType().split("boundary=\"")[1].split("\"")[0];

		contentType = contentType.replaceAll("boundary=\\\"(.+)\\\"", boundary);

		MimeBodyPart messageToEncrypt = generateBodyPartfromMultipart(signedMultipart, contentType);

		return generateEncryptedMessage(messageToEncrypt);
	}

	/**
	 * 
	 * Faulty Message generator
	 * 
	 */
	public MimeMessage generateEncryptedMessageWithDifferentSettings(MimeBodyPart body, boolean nullSender, boolean differentSender) throws Exception {

		// Get session to create message
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		MimeBodyPart encryptedPart = generateEncryptedPart(body);

		MimeMessage msg = new MimeMessage(session);
		if(!nullSender) {
			msg.setFrom(new InternetAddress(new SMTPAddress().properEmailAddr(this.fromAddress)));
		}
		if(differentSender) {
			msg.setHeader("Sender", "anotheraddress@ttt.gov");
		}
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(new SMTPAddress().properEmailAddr(this.toAddress)));
		msg.setSentDate(new Date());
		msg.setContent(encryptedPart.getContent(),
				encryptedPart.getContentType());
		msg.setDisposition("attachment");
		msg.setFileName("smime.p7m");
		if (!isWrapped) {
			msg.setSubject(subject);
		}
		msg.saveChanges();
		if (isWrapped) {
			msg.setHeader("Message-ID", this.wrappedMessageID);
		}

		return msg;

	}

	public InputStream getEncryptionCertByDnsLookup(String targetedTo) throws TextParseException, Exception {

		// Certificate was not uploaded. Try fetching from DNS.
		DnsLookup dl = new DnsLookup();
		LdapDnslookUp ldapDl = new LdapDnslookUp();

		// 1st try Ldap address bound cert
		try {
			InputStream certStream = ldapDl.getLdapCert(targetedTo);
			if(certStream != null) {
				logger.info("Address bound encryption certificate pulled from LDAP");
				return certStream;
			} else {
				logger.warn("Cannot pull address bound encryption certificate from LDAP");
			}
		} catch(Exception e) {
			logger.warn("Cannot pull address bound encryption certificate from LDAP");
		}

		// 2nd try DNS address bound cert
		try {
			String encCertAddressString = dl.getCertRecord(this.getAddressBoundDomain(targetedTo));
			if (encCertAddressString != null) {
				logger.info("Address bound encryption certificate pulled from DNS");
				return convertCertToInputStream(encCertAddressString);
			} else {
				logger.warn("Cannot pull address bound encryption certificate from DNS");
			}
		} catch(Exception e) {
			logger.warn("Cannot pull address bound encryption certificate from DNS");
		}

		// 3rd try LDAP domain bound cert
		try {
			InputStream certStream2 = ldapDl.getLdapCert(getTargetDomain(targetedTo));
			if(certStream2 != null) {
				logger.info("Domain bound encryption certificate pulled from LDAP");
				return certStream2;
			} else {
				logger.warn("Cannot pull domain bound encryption certificate from LDAP");
			}
		} catch(Exception e) {
			logger.warn("Cannot pull domain bound encryption certificate from LDAP");
		}

		// 4th try DNS domain bound cert
		try{
			String encCertDomainString = dl.getCertRecord(this.getTargetDomain(targetedTo));
			if (encCertDomainString != null) {
				logger.info("Domain bound encryption certificate pulled from DNS");
				return convertCertToInputStream(encCertDomainString);
			} else {
				logger.warn("Cannot pull domain bound encryption certificate from DNS");
			}
		} catch(Exception e) {
			logger.warn("Cannot pull domain bound encryption certificate from DNS");
		}

		throw new Exception("Cannot pull encryption certificate from DNS");
	}

	public InputStream convertCertToInputStream(String encCertString) {
		return new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(encCertString.getBytes()));
	}

	public String getTargetDomain(String targetedTo) {
		// Get the targeted domain
		String targetDomain = "";
		if(targetedTo.contains("@")) {
			targetDomain = targetedTo.split("@", 2)[1];
		}
		return targetDomain;
	}

	public String getAddressBoundDomain(String targetTo) {
		return targetTo.replace("@", ".");
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
		try {
			this.from = new InternetAddress(new SMTPAddress().properEmailAddr(this.fromAddress));
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
		try {
			this.to = new InternetAddress(new SMTPAddress().properEmailAddr(this.toAddress));
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public InputStream getAttachmentFile() {
		return attachmentFile;
	}

	public void setAttachmentFile(InputStream attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public InputStream getSigningCert() {
		return signingCert;
	}

	public void setSigningCert(InputStream signingCert) {
		this.signingCert = signingCert;
	}

	public String getSigningCertPassword() {
		return signingCertPassword;
	}

	public void setSigningCertPassword(String signingCertPassword) {
		this.signingCertPassword = signingCertPassword;
	}

	public InputStream getEncryptionCert() {
		return encryptionCert;
	}

	public void setEncryptionCert(InputStream encryptionCert) {
		this.encryptionCert = encryptionCert;
	}

	public boolean isWrapped() {
		return isWrapped;
	}

	public void setWrapped(boolean isWrapped) {
		this.isWrapped = isWrapped;
	}

	public String getWrappedMessageID() {
		return wrappedMessageID;
	}

	public void setWrappedMessageID(String wrappedMessageID) {
		this.wrappedMessageID = wrappedMessageID;
	}

	public String getDigestAlgo() {
		return digestAlgo;
	}

	public void setDigestAlgo(String digestAlgo) {
		this.digestAlgo = digestAlgo;
	}
}
