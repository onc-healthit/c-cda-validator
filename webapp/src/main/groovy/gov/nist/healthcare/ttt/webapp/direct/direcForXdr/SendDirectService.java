package gov.nist.healthcare.ttt.webapp.direct.direcForXdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.nist.healthcare.ttt.direct.messageGenerator.DirectMessageGenerator;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.model.sendDirect.FetchGitHubData;
import gov.nist.healthcare.ttt.model.sendDirect.SendDirectMessage;
import gov.nist.healthcare.ttt.webapp.direct.listener.ListenerProcessor;

@Component
public class SendDirectService {

	private static Logger logger = Logger.getLogger(SendDirectService.class.getName());

	private ListenerProcessor listener = new ListenerProcessor();
	private DirectMessageSender sender = new DirectMessageSender();

	@Value("${direct.certificates.repository.path}")
	String certificatesPath;

	@Value("${direct.certificates.password}")
	String certPassword;

	public SendDirectService() {
		
	}

	public boolean sendDirect(SendDirectMessage messageInfo) throws Exception {
		return sendDirect(messageInfo, false);
	}
	
	public boolean sendDirect(SendDirectMessage messageInfo, boolean usegithub) throws Exception {
		// Set certificates values
		listener.setCertificatesPath(this.certificatesPath);
		listener.setCertPassword(this.certPassword);
		
		FetchGitHubData f = new FetchGitHubData();

		if (messageInfo.isValidSendEmail()) {
			InputStream attachmentFile = null;
			if (messageInfo.getOwnCcdaAttachment() != null && !messageInfo.getOwnCcdaAttachment().equals("")) {
				File ownCcda = new File(messageInfo.getOwnCcdaAttachment());
				messageInfo.setAttachmentFile(ownCcda.getName());
				attachmentFile = new FileInputStream(ownCcda);
			} else if (!messageInfo.getAttachmentFile().equals("")) {
				if (!usegithub)
					attachmentFile = getClass().getResourceAsStream("/cda-samples/" + messageInfo.getAttachmentFile());
				else {
					attachmentFile = f.fetch(messageInfo.getAttachmentFile());
					logger.info("Data pulled from GITHUB");
				}
							
			}
			if (messageInfo.getSigningCert().toLowerCase().equals("")) {
				messageInfo.setSigningCert("good");
			}
			InputStream signingCert = listener.getSigningPrivateCert(messageInfo.getSigningCert().toLowerCase());
			
			String fname = messageInfo.getAttachmentFile();
			
			if (fname != null && fname.endsWith("_ett")) {
				fname = fname.substring(0, fname.lastIndexOf("-ett_"));
			}
	
			DirectMessageGenerator messageGenerator = new DirectMessageGenerator(messageInfo.getTextMessage(),
					messageInfo.getSubject(), messageInfo.getFromAddress(), messageInfo.getToAddress(), attachmentFile,
					fname, signingCert, messageInfo.getSigningCertPassword(), null,
					messageInfo.isWrapped(), "SHA1withRSA");

			// Get encryption cert
			InputStream encryptionCert = null;
			if (!messageInfo.getEncryptionCert().equals("")) {
				encryptionCert = new FileInputStream(new File(messageInfo.getEncryptionCert()));
			} else {
				logger.debug("Trying to fetch encryption cert for " + messageInfo.getToAddress());
				encryptionCert = messageGenerator.getEncryptionCertByDnsLookup(messageInfo.getToAddress());
			}

			messageGenerator.setEncryptionCert(encryptionCert);

			// Check if we want invalid digest
			MimeMessage msg;
			if (messageInfo.isInvalidDigest()) {
				msg = messageGenerator.generateAlteredDirectMessage();
			} else {
				msg = messageGenerator.generateMessage();
			}

			return sender.send(25, messageGenerator.getTargetDomain(messageInfo.getToAddress()), msg,
					messageInfo.getFromAddress(), messageInfo.getToAddress());
		}
		return false;
	}
}
