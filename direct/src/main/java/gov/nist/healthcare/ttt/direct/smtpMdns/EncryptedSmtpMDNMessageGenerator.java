package gov.nist.healthcare.ttt.direct.smtpMdns;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import gov.nist.healthcare.ttt.direct.messageGenerator.MDNExtraSpaceDisposition;
import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGenerator;
import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGeneratorExtraLineBreaks;
import gov.nist.healthcare.ttt.direct.messageGenerator.MDNMissingDisposition;
import gov.nist.healthcare.ttt.direct.messageGenerator.MDNWhiteSpaces;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;

public class EncryptedSmtpMDNMessageGenerator {

	public enum MDNType {
		GOOD,
		EXTRA_SPACE,
		EXTRA_LINE_BREAK,
		NULL_SENDER,
		DIFF_SENDER,
		MISS_DISPOSITION,
		EXTRA_SPACE_822,
		DIFF_CASES_822,
		EXTRA_SPACE_DISPOSITION,
		DIFF_MSG_ID,
		DSN};
	
	
	
	public static void sendSmtpMDN(InputStream originalMessage, String originalMsgId, String from, String to, String type, String failure, InputStream signingCert, String signingCertPassword, MDNType mdntype) throws Exception {

		// Get the session variable
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);

		// Get the MimeMessage object
		MimeMessage msg = new MimeMessage(session, originalMessage);

		MDNGenerator generator;
		// Create MDN type
		switch (mdntype) {
        case GOOD:
        	generator = new MDNGenerator();
        	break;
        
        case EXTRA_SPACE:
        	generator = new MDNWhiteSpaces();
        	break;
        	
        case EXTRA_LINE_BREAK:
        	generator = new MDNGeneratorExtraLineBreaks();
        	break;
        	
        case EXTRA_SPACE_DISPOSITION:
        	generator = new MDNExtraSpaceDisposition();
        	break;
        	
        case MISS_DISPOSITION:
        	generator = new MDNMissingDisposition();
        	break;
        	
        default:
        	generator = new MDNGenerator();
        	break;
		}
		Properties prop = ValidationUtils.getProp();
		
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
		} else {
			generator.setText("Your message was successfully processed.");
		}
		generator.setToAddress(to);
		generator.setFailure(failure);
		// Certificates 
		generator.setSigningCert(signingCert);
		generator.setSigningCertPassword(signingCertPassword);
		generator.setEncryptionCert(generator.getEncryptionCertByDnsLookup(to));
		
		MimeMessage mdnToSend;
		// Switch for different message generator
		switch (mdntype) {
        case GOOD:
        	mdnToSend = generator.generateMDN();
        	break;
        
        case NULL_SENDER:
        	mdnToSend = generator.generateNullEnvelopeSenderMDN();
        	break;
        	
        case DIFF_SENDER:
        	mdnToSend = generator.generateDifferentSenderMDN();
        	break;
        	
        case DIFF_MSG_ID:
        	mdnToSend = generator.generateDifferentMsgIdMDN();
        	break;
        	
        case EXTRA_SPACE_822:
        	mdnToSend = generator.generate822ExtraSpaces();
        	break;
        	
        case DIFF_CASES_822:
        	mdnToSend = generator.generate822DifferentCases();
        	break;
        	
        case DSN:
        	mdnToSend = generator.generateDSN();
        	break;
        	
        default:
        	mdnToSend = generator.generateMDN();
        	break;
		}
		
		DirectMessageSender sender = new DirectMessageSender();
		
		String targetDomain = sender.getTargetDomain(to);
		
		// Send mdn
		sender.send(25, targetDomain, mdnToSend, from, to);
		
	}

}
