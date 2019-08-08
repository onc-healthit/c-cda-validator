package messageSenderTest;

import gov.nist.healthcare.ttt.direct.messageGenerator.DirectMessageGenerator;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.direct.sender.DnsLookup;
import gov.nist.healthcare.ttt.model.sendDirect.SendDirectMessage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.bouncycastle.util.encoders.Base64;

public class MessageSenderTest {

	public static void main(String args[]) throws Exception {
		
		SendDirectMessage messageInfo = new SendDirectMessage();
		messageInfo.setAttachmentFile("CCDA_Inpatient.xml");
		messageInfo.setEncryptionCert("src/test/java/messageSenderTest/testCert.der");
		messageInfo.setFromAddress("\"Perugini, Julien C.\" <julien@hit-testing.nist.gov>");
		messageInfo.setSigningCert("");
		messageInfo.setSigningCertPassword("");
		messageInfo.setSubject("Internal Test");
		messageInfo.setTextMessage("Internal Test");
		messageInfo.setToAddress("julien@localhost");
		messageInfo.setWrapped(true);
		

		InputStream attachmentFile = null;
		if(!messageInfo.getAttachmentFile().equals("")) {
			attachmentFile = new FileInputStream(new File("src/test/java/messageSenderTest/CCDA_Inpatient.xml"));
		}
		InputStream signingCert = new FileInputStream(new File("src/test/java/messageSenderTest/testCert.p12"));

		// Get the target domain
		String targetDomain = "";
		if(messageInfo.getToAddress().contains("@")) {
			targetDomain = messageInfo.getToAddress().split("@")[1];
		} else {
			signingCert.close();
			throw new Exception("Not a valid To Email Address");
		}

		InputStream encryptionCert = null;
		if(!messageInfo.getEncryptionCert().equals("")) {
			encryptionCert = new FileInputStream(new File(messageInfo.getEncryptionCert()));
		} else {
			// Certificate was not uploaded. Try fetching from DNS.
			DnsLookup dl = new DnsLookup();
			String encCertString = dl.getCertRecord(targetDomain);
			if (encCertString != null)
				encryptionCert = new ByteArrayInputStream(Base64.decode(encCertString.getBytes()));
			if (encryptionCert != null) {
				System.out.println("Encryption certificate pulled from DNS");
			} else {
				System.out.println("Cannot pull encryption certificate from DNS");
				signingCert.close();
				throw new Exception("Cannot pull encryption certificate from DNS");
			}
		}

		DirectMessageGenerator messageGenerator = new DirectMessageGenerator(
				messageInfo.getTextMessage(), messageInfo.getSubject(),
				messageInfo.getFromAddress(), messageInfo.getToAddress(),
				attachmentFile, messageInfo.getAttachmentFile(),
				signingCert, messageInfo.getSigningCertPassword(),
				encryptionCert, messageInfo.isWrapped(), "SHA1withRSA");

		MimeMessage msg = messageGenerator.generateMessage();
		
		// To fail
//		msg.setSender(new InternetAddress("test"));

		// Log the outgoing message in the database
//		LogModel outgoingMessage = new LogModel(msg);
//		this.db.getLogFacade().addNewLog(outgoingMessage);

		DirectMessageSender sender = new DirectMessageSender();
		sender.sendMessage(12999, targetDomain, msg, messageInfo.getFromAddress(), messageInfo.getToAddress(), false);
	}
}
