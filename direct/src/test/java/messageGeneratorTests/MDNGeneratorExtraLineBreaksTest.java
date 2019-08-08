package messageGeneratorTests;

import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGeneratorExtraLineBreaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

public class MDNGeneratorExtraLineBreaksTest {

	public static String privateCertPath = "src/test/java/messageGeneratorTests/testCert.p12";
	public static String publicCertPath = "src/test/java/messageGeneratorTests/testCert.pem";

	public static void main(String[] args) throws MessagingException, Exception {
		MDNGeneratorExtraLineBreaks generator = new MDNGeneratorExtraLineBreaks();
		generator.setReporting_UA_name("direct.nist.gov");
		generator.setReporting_UA_product("Security Agent");
		generator.setDisposition("automatic-action/MDN-sent-automatically;processed");
		generator.setFinal_recipient("transport-testing.nist.gov");
		generator.setFromAddress("test@transport-testing.nist.gov");
		generator.setOriginal_message_id("<812748939.14.1386951907564.JavaMail.tomcat7@ip-10-185-147-33.ec2.internal>");
		generator.setSubject("Automatic MDN");
		generator.setText("Your message was successfully processed.");
		generator.setToAddress("test@hit-dev.nist.gov");

		InputStream signingCert = new FileInputStream(new File(privateCertPath));
		InputStream encryptionCert = new FileInputStream(new File(publicCertPath));
		generator.setSigningCert(signingCert);
		generator.setSigningCertPassword("");
		generator.setEncryptionCert(encryptionCert);


		MimeBodyPart signed = generator.generateMultipartSigned(generator.generateBodyReport(false, false, false));
		signed.writeTo(new FileOutputStream(new File("d_mdn.txt")));

		MimeMessage msg = generator.generateEncryptedMessage(signed);
		msg.writeTo(new FileOutputStream(new File("e_mdn.txt")));
	}

}
