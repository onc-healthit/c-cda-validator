package messageSenderTest;

import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGenerator;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.direct.sender.DnsLookup;
import gov.nist.healthcare.ttt.model.sendDirect.SendDirectMessage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.bouncycastle.util.encoders.Base64;

public class MDNSenderTest {
public static void main(String args[]) throws Exception {
	
		String messageId = "<2089499608.1.1415378903537.JavaMail.jnp3@P854708>";
		
		SendDirectMessage messageInfo = new SendDirectMessage();
		messageInfo.setAttachmentFile("CCDA_Inpatient.xml");
		messageInfo.setEncryptionCert("src/test/java/messageSenderTest/testCert.der");
		messageInfo.setFromAddress("test@localhost");
		messageInfo.setSigningCert("");
		messageInfo.setSigningCertPassword("");
		messageInfo.setSubject("Internal Test");
		messageInfo.setTextMessage("Internal Test");
		messageInfo.setToAddress("me@localhost");
		messageInfo.setWrapped(true);
		
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

		MDNGenerator generator = new MDNGenerator();
		generator.setDisposition("automatic-action/MDN-sent-automatically;processed");
		generator.setFinal_recipient("test@localhost");
		generator.setFromAddress("me@localhost");
		generator.setOriginal_message_id(messageId);
		generator.setOriginal_recipient("me@localhost");
		generator.setReporting_UA_name("direct.nist.gov");
		generator.setReporting_UA_product("Security Agent");
		generator.setSigningCert(signingCert);
		generator.setSigningCertPassword("");
		generator.setSubject("Automatic MDN");
		generator.setText("Your message was successfully processed.");;
		generator.setToAddress("test@localhost");
		generator.setEncryptionCert(encryptionCert);
		
		MimeMessage mdn = generator.generateMDN();
		
		// To fail
//		msg.setSender(new InternetAddress("test"));

		// Log the outgoing message in the database
//		LogModel outgoingMessage = new LogModel(msg);
//		this.db.getLogFacade().addNewLog(outgoingMessage);

		DirectMessageSender sender = new DirectMessageSender();
		sender.sendMessage(12999, targetDomain, mdn, messageInfo.getToAddress(), messageInfo.getFromAddress(), false);
	}
}
