package gov.nist.healthcare.ttt.model.sendDirect;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SendDirectMessage {
	
	private static Logger logger = Logger.getLogger(SendDirectMessage.class.getName());
	
	private String textMessage;
	private String subject;
	private String fromAddress;
	private String toAddress;
	private String attachmentFile;
	private String ownCcdaAttachment;
	// Signing certificate
	private String signingCert;
	private String signingCertPassword;
	// Encryption cert
	private String encryptionCert;
	// Wrapped or Unwrapped message
	private boolean isWrapped;
	private boolean invalidDigest;
	private String digestAlgo;
	
	
	public SendDirectMessage(String textMessage, String subject,
			String fromAddress, String toAddress, String attachmentFile,
			String signingCert, String signingCertPassword,
			String encryptionCert, boolean isWrapped, boolean invalidDigest) {
		super();
		this.textMessage = textMessage;
		this.subject = subject;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.attachmentFile = attachmentFile;
		this.signingCert = signingCert;
		this.signingCertPassword = signingCertPassword;
		this.encryptionCert = encryptionCert;
		this.isWrapped = isWrapped;
		this.invalidDigest = invalidDigest;
	}
	
	public SendDirectMessage() {
		super();
		this.textMessage = "";
		this.subject = "";
		this.fromAddress = "";
		this.toAddress = "";
		this.attachmentFile = "";
		this.signingCert = "";
		this.signingCertPassword = "";
		this.encryptionCert = "";
		this.isWrapped = true;
		this.invalidDigest = false;
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
	}


	public String getToAddress() {
		return toAddress;
	}


	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}


	public String getAttachmentFile() {
		return attachmentFile;
	}


	public void setAttachmentFile(String attachmentFile) {
		this.attachmentFile = attachmentFile;
	}


	public String getSigningCert() {
		return signingCert;
	}


	public void setSigningCert(String signingCert) {
		this.signingCert = signingCert;
	}


	public String getSigningCertPassword() {
		return signingCertPassword;
	}


	public void setSigningCertPassword(String signingCertPassword) {
		this.signingCertPassword = signingCertPassword;
	}


	public String getEncryptionCert() {
		return encryptionCert;
	}


	public void setEncryptionCert(String encryptionCert) {
		this.encryptionCert = encryptionCert;
	}


	public boolean isWrapped() {
		return isWrapped;
	}


	public void setWrapped(boolean isWrapped) {
		this.isWrapped = isWrapped;
	}
	
	public String getOwnCcdaAttachment() {
		return ownCcdaAttachment;
	}

	public void setOwnCcdaAttachment(String ownCcdaAttachment) {
		this.ownCcdaAttachment = ownCcdaAttachment;
	}

	public boolean isInvalidDigest() {
		return invalidDigest;
	}

	public void setInvalidDigest(boolean invalidDigest) {
		this.invalidDigest = invalidDigest;
	}

	public boolean isValidSendEmail() throws Exception {
		if(this.fromAddress.equals("")) {
			logger.log(Level.WARNING, "From address can't be null");
			throw new Exception("From address can't be null");
		}
		if(this.toAddress.equals("")) {
			logger.log(Level.WARNING, "To address can't be null");
			throw new Exception("To address can't be null");
		}
		if(this.signingCert.equals("")) {
			logger.log(Level.WARNING, "Signing cert is null using good cert");
		}
		return true;
	}

	public String getDigestAlgo() {
		return digestAlgo;
	}

	public void setDigestAlgo(String digestAlgo) {
		this.digestAlgo = digestAlgo;
	}
}
