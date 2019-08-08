package gov.nist.healthcare.ttt.direct.smtpMdns;

import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGenerator;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SmtpMDNMessageGenerator {

	public static void sendSmtpMDN(InputStream originalMessage, String originalMsgId, String from, String to, String type, String failure, InputStream signingCert, String signingCertPassword) throws Exception {
		sendSmtpMDN(originalMessage, originalMsgId, from, to, type, failure, signingCert, signingCertPassword, false);
	}


	public static void sendSmtpMDN(InputStream originalMessage, String originalMsgId, String from, String to, String type, String failure, InputStream signingCert, String signingCertPassword, boolean useStartTLS) throws Exception {

		// Get the session variable
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		// Get the MimeMessage object
		MimeMessage msg = new MimeMessage(session, originalMessage);

		Properties prop = ValidationUtils.getProp();
		
		MDNGenerator generator = new MDNGenerator();
		generator.setDisposition("automatic-action/MDN-sent-automatically;" + type);
		generator.setFinal_recipient(from);
		generator.setFromAddress(from);
		generator.setOriginal_message_id(originalMsgId);
		generator.setOriginal_recipient(from);
		generator.setReporting_UA_name(prop.getProperty("direct.reporting.ua"));
		generator.setReporting_UA_product("Security Agent");
		generator.setSubject("Automatic MDN");
		if(type.equals("dispatched")) {
			generator.setText("Your message was successfully dispatched.");
		} else if (type.equals("failure")){
			generator.setText("The message delivery failed.");
		}
		else {
			generator.setText("Your message was successfully processed.");
		}
		generator.setToAddress(to);
		generator.setFailure(failure);
		// Certificates 
		generator.setSigningCert(signingCert);
		generator.setSigningCertPassword(signingCertPassword);
		//		generator.setEncryptionCert(generator.getEncryptionCertByDnsLookup(to));

		MimeMessage mdnToSend = generator.generateSmtpMDN();

		DirectMessageSender sender = new DirectMessageSender();

		String targetDomain = sender.getTargetDomain(to);

		// Send mdn
		sender.send(25, targetDomain, mdnToSend, from, to, useStartTLS);

	}

}
