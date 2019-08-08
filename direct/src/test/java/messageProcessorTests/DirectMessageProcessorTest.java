package messageProcessorTests;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.database.jdbc.LogFacade;
import gov.nist.healthcare.ttt.database.log.CCDAValidationReportInterface;
import gov.nist.healthcare.ttt.direct.messageProcessor.DirectMessageProcessor;
import gov.nist.healthcare.ttt.misc.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.mail.MessagingException;

public class DirectMessageProcessorTest {
	
	public static String privateCertPath = "src/test/java/messageProcessorTests/testCert.p12";
	public static String messagePath = "src/test/java/messageProcessorTests/ccdar2.txt";
	public static String password = "";
	public static String mdhtR1Endpoint = "http://devccda.sitenv.org/CCDAValidatorServices/r1.1/";
	public static String mdhtR2Endpoint = "http://hit-dev.nist.gov:11080/referenceccdaservice/";
	public static String toolkitUrl = "http://hit-dev.nist.gov:11080/xdstools2";
	
	public static void main(String args[]) {
		File privateCert = new File(privateCertPath);
		File message = new File(messagePath);
		
		InputStream privateCertStream = null;
		InputStream messageStream = null;
		
		 Configuration config = new Configuration();
		 config.setDatabaseHostname("localhost");
		 config.setDatabaseUsername("direct");
		 config.setDatabaseName("direct"); 
		LogFacade db = null;
		try {
			db = new LogFacade(config);
		} catch (DatabaseException e1) {
			e1.printStackTrace();
		}
		
		try {
			privateCertStream = new FileInputStream(privateCert);
			messageStream = new FileInputStream(message);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DirectMessageProcessor processor = null;
		try {
			processor = new DirectMessageProcessor(messageStream, privateCertStream, password, mdhtR1Endpoint, mdhtR2Endpoint, toolkitUrl);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			processor.processDirectMessage();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(processor.getLogModel());
		System.out.println(processor.getMainPart());
		
		
		try {
			db.addNewLog(processor.getLogModel());
			db.addNewPart(processor.getLogModel().getMessageId(), processor.getMainPart());
			for(CCDAValidationReportInterface report : processor.getCcdaReport()) {
				db.addNewCCDAValidationReport(processor.getLogModel().getMessageId(), report);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
