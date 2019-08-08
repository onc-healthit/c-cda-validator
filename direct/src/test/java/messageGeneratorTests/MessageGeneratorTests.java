package messageGeneratorTests;

import gov.nist.healthcare.ttt.direct.messageGenerator.DirectMessageGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.mail.internet.MimeMessage;

public class MessageGeneratorTests {
	
	public static String privateCertPath = "src/test/java/messageGeneratorTests/testCert.p12";
	public static String publicCertPath = "src/test/java/messageGeneratorTests/testCert.pem";
	public static String attachmentPath = "src/test/java/messageGeneratorTests/CCDA_Inpatient.xml";
	
	public static void main(String args[]) {
		File privateCert = new File(privateCertPath);
		File publicCert = new File(publicCertPath);
		File attachment = new File(attachmentPath);
		
		String test = "Test Message";
		String testFromAddress = "from@test.com";
		String testToAddress = "to@test.com";
		
		DirectMessageGenerator generator;
		
		try {
			InputStream privateCertStream = new FileInputStream(privateCert);
			InputStream publicCertStream = new FileInputStream(publicCert);
			InputStream attachmentFile = new FileInputStream(attachment);
			generator = new DirectMessageGenerator(test, test, testFromAddress,
					testToAddress, attachmentFile, attachment.getName(),
					privateCertStream, "", publicCertStream, true, "SHA1withRSA");
			MimeMessage msg = generator.generateMessage();
			msg.writeTo(new FileOutputStream(new File("TestGeneratedMessage.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
