package gov.nist.healthcare.ttt.smtp.testcases;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMWriter;

import gov.nist.healthcare.ttt.smtp.TestInput;
import gov.nist.healthcare.ttt.smtp.TestResult;
import gov.nist.healthcare.ttt.smtp.TestResult.CriteriaStatus;

public class MU2SenderTests {

	public static Logger log = Logger.getLogger(MU2SenderTests.class.getName());
	
	public Properties getProps(TestInput ti) {
		Properties props = new Properties();
		if(ti.useTLS){
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.starttls.required", "true");
			props.setProperty("mail.smtp.ssl.trust", "*");
		}
		
		if(ti.sutUserName.equals("red") && ti.sutPassword.equals("red")){
			props.put("mail.smtp.auth", "false");
		}

		else {
			props.put("mail.smtp.auth", "false");
		}

		return props;

	}
	
	/**
	 * Implements  a Testcase to send an email to a Bad Address. Authenticates with SUT and sends a mail from SUT Server to a end point using STARTTLS.
	 * 
	 * @return
	 */
	public TestResult testBadAddress(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();
		//	String dsn = "SUCCESS,FAILURE,DELAY,ORCPT=rfc1891";

		Properties props = getProps(ti);
		tr.setFetchType("imap");
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();

			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("bad.address")));
		//	InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			
			if (header){
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
            message.addHeader("Disposition-Notification-To", fromAddress);
			}

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			message.saveChanges();
			System.out.println(message.getHeader("Message-ID")[0]);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
		//	tr.setMessageId(message.getHeader("Message-ID")[0]);
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("bad.address")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS" + prop.getProperty("bad.address")+ "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());


		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}


	public TestResult testMu2Two(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
	//	props.put("mail.smtp.from", prop.getProperty("not.published"));
		tr.setFetchType("imap");
		tr.setSearchType("fail");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.trusted")));
				//	InternetAddress.parse(prop.getProperty("bad.address")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.trusted")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS " + prop.getProperty("not.trusted")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2Three(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
	//	props.put("mail.smtp.from", prop.getProperty("not.published"));
		tr.setFetchType("imap");
		tr.setSearchType("fail");

		Session session = Session.getInstance(props, null);
	

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.published")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.published")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS " + prop.getProperty("not.published")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2Four(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
	//	props.put("mail.smtp.from", prop.getProperty("not.published"));
		tr.setFetchType("imap");
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("no.processedmdn")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("no.processedmdn")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS " + prop.getProperty("no.processedmdn")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}

	public TestResult testBadAddressPop(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();
		//	String dsn = "SUCCESS,FAILURE,DELAY,ORCPT=rfc1891";

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName +"@"+ ti.sutSmtpAddress;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("bad.address")));
		//	InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			message.saveChanges();
			System.out.println(message.getHeader("Message-ID")[0]);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("bad.address")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS" + prop.getProperty("bad.address")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}


	public TestResult testMu2TwoPop(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("fail");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.trusted")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.trusted")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS" + prop.getProperty("not.trusted")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());


		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2ThreePop(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("fail");

		Session session = Session.getInstance(props, null);
	

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.published")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.published")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS "+prop.getProperty("not.published")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2FourPop(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("no.processedmdn")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("no.processedmdn")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS "+prop.getProperty("no.processedmdn") +"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	public TestResult testBadAddressSmtp(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();
		//	String dsn = "SUCCESS,FAILURE,DELAY,ORCPT=rfc1891";

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("bad.address")));
		//	InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			message.saveChanges();
			System.out.println(message.getHeader("Message-ID")[0]);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("bad.address")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS " +prop.getProperty("bad.address")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());


		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}


	public TestResult testMu2TwoSmtp(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("fail");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.trusted")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.trusted")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS "+prop.getProperty("not.trusted")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
			


		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2ThreeSmtp(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("not.published")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("not.published")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO BAD ADDRESS "+prop.getProperty("not.published")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} 

		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}
		return tr;
	}

	public TestResult testMu2FourSmtp(TestInput ti, boolean header) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("no.processedmdn")));
			message.setSubject("Testing sending mail to BadAddress!");
			message.setText("This is a message to a badAddress!");
			if (header){
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				message.addHeader("Disposition-Notification-To", fromAddress);
				}
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("no.processedmdn")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS "+prop.getProperty("no.processedmdn")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} 
		
		catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR :", "Address cannot be null");
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST BAD ADDRESS: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}

	public TestResult testMu2TwoSeven(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap");
		tr.setSearchType("timeout");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			  
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	public TestResult testMu2TwoEight(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap");
		tr.setSearchType("timeout28");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			  
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}

	public TestResult testMu2TwoSevenSmtp(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("timeout");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	public TestResult testMu2TwoEightSmtp(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("timeout28");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	
	public TestResult testMu2TwoSevenPop(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("timeout");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	public TestResult testMu2TwoEightPop(TestInput ti, String Address) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("timeout28");

		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Address));
			message.setSubject("Testing sending mail to " + Address);
			message.setText("This is a message to "+ Address);
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + Address+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO  "+ Address + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in MU2 -27/28");
			result.put("ERROR", "Cannot send message to " + Address + ": " +  e.getLocalizedMessage());
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	// to add custom message-id
	static class MyMessage extends MimeMessage {
		MyMessage(Session session) { super(session); }

		@Override
		protected void updateMessageID() throws MessagingException {
			setHeader("Message-ID", "<123456>");		
		}
	}
	
	

	public TestResult testDispositionNotification(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap");
		tr.setSearchType("dispatched");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Disposition Notification Header (Test Case MU2-21)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH MESSAGE DISPOSITION NOTIFICATION HEADER TO "+prop.getProperty("processed.dispatched")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}

	public TestResult testDispositionNotificationPop(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("dispatched");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Disposition Notification Header (Test Case MU2-21)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH MESSAGE DISPOSITION NOTIFICATION HEADER TO "+prop.getProperty("processed.dispatched")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	
	public TestResult testDispositionNotificationSmtp(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("dispatched");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Disposition Notification Header (Test Case MU2-21)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", fromAddress);

			
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH MESSAGE DISPOSITION NOTIFICATION HEADER TO "+prop.getProperty("processed.dispatched")+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (Exception e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}

		return tr;
	}
	
	
	public TestResult testDispositionNotificationSutReceiver(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap1");
		tr.setSearchType("both");
		Session session = Session.getInstance(props, null);

		try {
			
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("dir.username")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Testing sending mail with Disposition Notification Header!");
			message.setText("This is a message to a SUT");
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", prop.getProperty("dir.username"));

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			String aName = "";

			Multipart multipart = new MimeMultipart();

			// Adding attachments
			for (Map.Entry<String, byte[]> e : ti.getAttachments().entrySet()) {

				DataSource source = new ByteArrayDataSource(e.getValue(),
						"text/html");
				messageBodyPart.setDataHandler(new DataHandler(source));
				if (e.getKey().contains("zip")){
					DataSource source1 = new ByteArrayDataSource(e.getValue(),
							"application/zip");
					messageBodyPart.setDataHandler(new DataHandler(source1));
				}
				
				messageBodyPart.setFileName(e.getKey());
				aName += e.getKey();
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				message.setContent(multipart);
			}
			
			
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(prop.getProperty("dir.hostname"), ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort,prop.getProperty("dir.username"), prop.getProperty("dir.password"));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + ti.sutEmailAddress);
			result.put("1","SENDING EMAIL WITH MESSAGE DISPOSITION NOTIFICATION HEADER TO "+ti.sutEmailAddress+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}	catch (NullPointerException e) {
				log.info("Error in testBadAddress");
				result.put("ERROR " ,"Please enter 'Vendor Email Address'");
				e.printStackTrace();
				tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	public TestResult uploadCertificate(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setCriteriamet(CriteriaStatus.TRUE);

		try {
			
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			// Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = prop.getProperty("dir.soap");
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(ti.getCertificate()), url);

            // Process the SOAP Response
          //  printSOAPResponse(soapResponse);

            soapConnection.close();
            System.out.println("Upload Successful");
		
		} catch (Exception e) {
			tr.setCriteriamet(CriteriaStatus.FALSE);
			e.printStackTrace();
			
		}

		return tr;
	}
	
	private static SOAPMessage createSOAPRequest(byte[] cert) throws Exception {
		
		Properties prop = new Properties();
		String path = "./application.properties";
		FileInputStream file = new FileInputStream(path);
		prop.load(file);
		file.close();
		byte[] finalCert;
		
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://nhind.org/config";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ns1", serverURI);

        
        /*<?xml version="1.0" encoding="UTF-8"?>
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="http://nhind.org/config">
         <SOAP-ENV:Body>
           <ns1:addAnchor>
             <anchor>
               <certificateId>0</certificateId>
               <createTime>2016-06-22T20:44:05.517Z</createTime>
               <data>MIIFQzCCBCugAwIJ9WLf/jtz1Zlg==</data>
               <id>0</id>
               <incoming>true</incoming>
               <outgoing>true</outgoing>
               <owner>hit-testing.nist.gov</owner>
               <status></status>
               <thumbprint>193298f2b2b8e83c316fa512c1e481bf7ed37a9a</thumbprint>
               <validEndDate>2026-03-16T17:30:06Z</validEndDate>
               <validStartDate>2016-03-18T17:30:06Z</validStartDate>
             </anchor>
           </ns1:addAnchor>
         </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>*/
        
        if (new String(cert).contains("BEGIN CERTIFICATE")){
        	finalCert = cert;
        }
        
        else {
        	InputStream is = new ByteArrayInputStream(cert);
        	CertificateFactory f = CertificateFactory.getInstance("X.509");
        	X509Certificate certificate = (X509Certificate)f.generateCertificate(is);
        	finalCert = convertCertToPem(certificate);
        }
        
    	InputStream is = new ByteArrayInputStream(cert);
    	CertificateFactory f = CertificateFactory.getInstance("X.509");
    	X509Certificate certificate = (X509Certificate)f.generateCertificate(is);
    	String thumbPrint = DigestUtils.sha1Hex(certificate.getEncoded());
    	
    	
		String s = new String(finalCert);
		String s1 = s.replace("-----BEGIN CERTIFICATE-----","");
		String s2 = s1.replace("-----END CERTIFICATE-----","");
		String s3 = s2.replaceAll("\\s+","");
		

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("addAnchor", "ns1");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("anchor");
        
        SOAPElement soapBodyElem2 = soapBodyElem1.addChildElement("certificateId");
        soapBodyElem2.addTextNode("0");
        
        SOAPElement soapBodyElem3 = soapBodyElem1.addChildElement("createTime");
        soapBodyElem3.addTextNode("2016-06-22T20:44:05.517Z");
        
        SOAPElement soapBodyElem4 = soapBodyElem1.addChildElement("data");
        soapBodyElem4.addTextNode(s3);
        
        SOAPElement soapBodyElem5 = soapBodyElem1.addChildElement("id");
        soapBodyElem5.addTextNode("0");
        
        SOAPElement soapBodyElem6 = soapBodyElem1.addChildElement("incoming");
        soapBodyElem6.addTextNode("true");
        
        SOAPElement soapBodyElem7 = soapBodyElem1.addChildElement("outgoing");
        soapBodyElem7.addTextNode("true");
        
        SOAPElement soapBodyElem8 = soapBodyElem1.addChildElement("owner");
        soapBodyElem8.addTextNode(prop.getProperty("dir.hostname"));
        
        SOAPElement soapBodyElem9 = soapBodyElem1.addChildElement("status");
        soapBodyElem9.addTextNode("ENABLED");
        
        SOAPElement soapBodyElem10 = soapBodyElem1.addChildElement("thumbprint");
        soapBodyElem10.addTextNode(thumbPrint);
        
        SOAPElement soapBodyElem11 = soapBodyElem1.addChildElement("validEndDate");
        soapBodyElem11.addTextNode("2026-03-16T18:30:06Z");
        
        SOAPElement soapBodyElem12 = soapBodyElem1.addChildElement("validStartDate");
        soapBodyElem12.addTextNode("2016-03-18T17:30:06Z");


        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "addDomain");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
	
	//converts cert to pem to bytearray
	private static byte[] convertCertToPem(X509Certificate certificate) throws Exception {
		StringWriter sw = new StringWriter();
		try (PEMWriter pw = new PEMWriter(sw)) {
			pw.writeObject(certificate);
		}
		return sw.toString().getBytes();
		
	}
	
	public TestResult testBadDispositionNotification(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap");
		tr.setSearchType("pass");
		Session session = Session.getInstance(props, null);
		try {
			
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Bad Disposition Notification Header (Test Case MU2-22)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-XXXX-FINAL-X-DELXXXX=optioXXX,tXX");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH BAD DISPOSITION NOTIFICATION HEADER TO "+ti.sutEmailAddress+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}catch (Exception e) {
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	public TestResult testBadDispositionNotificationSutReceiver(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap1");
		tr.setSearchType("either");
		Session session = Session.getInstance(props, null);
		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("dir.username")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Testing sending mail with Bad Disposition Notification Header");
			message.setText("This is a message to a SUT!");
			message.addHeader("Disposition-Notification-Options", "X-XXXX-FINAL-X-DELXXXX=optioXXX,tXX");
			message.addHeader("Disposition-Notification-To", prop.getProperty("dir.username"));

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			String aName = "";

			Multipart multipart = new MimeMultipart();

			// Adding attachments
			for (Map.Entry<String, byte[]> e : ti.getAttachments().entrySet()) {

				DataSource source = new ByteArrayDataSource(e.getValue(),
						"text/html");
				messageBodyPart.setDataHandler(new DataHandler(source));
				if (e.getKey().contains("zip")){
					DataSource source1 = new ByteArrayDataSource(e.getValue(),
							"application/zip");
					messageBodyPart.setDataHandler(new DataHandler(source1));
				}
				
				messageBodyPart.setFileName(e.getKey());
				aName += e.getKey();
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				message.setContent(multipart);
			}
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(prop.getProperty("dir.hostname"), ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort,prop.getProperty("dir.username"), prop.getProperty("dir.password"));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + ti.sutEmailAddress);
			result.put("1","SENDING EMAIL WITH BAD DISPOSITION NOTIFICATION HEADER TO "+ti.sutEmailAddress+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}	catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR " ,"Please enter 'Vendor Email Address'");
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	
	public TestResult testBadAddressSutReceiverTimeout(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap1");
		tr.setSearchType("fail");
		Session session = Session.getInstance(props, null);
		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("dir.username")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Mail to receivng HISP");
			message.setText("This is a message to a SUT!");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(prop.getProperty("dir.hostname"), ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort,prop.getProperty("dir.username"), prop.getProperty("dir.password"));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + ti.sutEmailAddress);
			result.put("1","Sending email\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}	catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR " ,"Please enter 'Vendor Email Address'");
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	public TestResult testBadAddressSutReceiver(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("imap1");
		tr.setSearchType("processedandfailure");
		Session session = Session.getInstance(props, null);
		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("dir.username")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(ti.sutEmailAddress));
			message.setSubject("Mail to receivng HISP");
			message.setText("This is a message to a SUT!");
			message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
			message.addHeader("Disposition-Notification-To", prop.getProperty("dir.username"));
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			/*String aName = "";

			Multipart multipart = new MimeMultipart();

			// Adding attachments
			for (Map.Entry<String, byte[]> e : ti.getAttachments().entrySet()) {

				DataSource source = new ByteArrayDataSource(e.getValue(),
						"text/html");
				messageBodyPart.setDataHandler(new DataHandler(source));
				if (e.getKey().contains("zip")){
					DataSource source1 = new ByteArrayDataSource(e.getValue(),
							"application/zip");
					messageBodyPart.setDataHandler(new DataHandler(source1));
				}
				
				messageBodyPart.setFileName(e.getKey());
				aName += e.getKey();
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				message.setContent(multipart);
			}*/
			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(prop.getProperty("dir.hostname"), ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort,prop.getProperty("dir.username"), prop.getProperty("dir.password"));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + ti.sutEmailAddress);
			result.put("1","Sending email to "+ti.sutEmailAddress+"\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		}	catch (NullPointerException e) {
			log.info("Error in testBadAddress");
			result.put("ERROR " ,"Please enter 'Vendor Email Address'");
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	public TestResult testBadDispositionNotificationPop(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();

		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("pass");
		Session session = Session.getInstance(props, null);
		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Bad Disposition Notification Header (Test Case MU2-22)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-XXXX-FINAL-X-DELXXXX=optioXXX,tXX");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH BAD DISPOSITION NOTIFICATION HEADER\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);

			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());
		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error" + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	public TestResult testBadDispositionNotificationSmtp(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();
		

		Properties props = getProps(ti);
		tr.setFetchType("smtp");;
		tr.setSearchType("pass");

		Session session = Session.getInstance(props, null);
		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail with Bad Disposition Notification Header (Test Case MU2-22)!");
			message.setText("This is a message to a Address 6!");
			message.addHeader("Disposition-Notification-Options", "X-XXXX-FINAL-X-DELXXXX=optioXXX,tXX");

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL WITH BAD DISPOSITION NOTIFICATION HEADER\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST MESSAGE BAD DISPOSITION NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}
		

		return tr;
	}
	
	public TestResult testPositiveDeliveryNotification(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();
		
		Properties props = getProps(ti);
		//	props.put("mail.smtp.dsn.ret", "HDRS");
		//	props.put("mail.smtp.notify", dsn);
		tr.setFetchType("imap");
		tr.setSearchType("dispatched");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail to receive Positive Delivery Notification (Test Case MU2-29)!");
			message.setText("This is a message to a badAddress!");
			
			//	message.addHeader("Disposition-Notification-To", prop.getProperty("not.published"));

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");
			

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS  " + prop.getProperty("processed.dispatched") + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST POSITIVE DELIVERY NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}

	public TestResult testPositiveDeliveryNotificationPop(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.STEP2);
		HashMap<String, String> result = tr.getTestRequestResponses();
		
		Properties props = getProps(ti);
		tr.setFetchType("pop");
		tr.setSearchType("dispatched");
		Session session = Session.getInstance(props, null);

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail to receive Positive Delivery Notification (Test Case MU2-29)!");
			message.setText("This is a message to a badAddress!");
			
			//	message.addHeader("Disposition-Notification-To", prop.getProperty("not.published"));

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");
			

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS  " + prop.getProperty("processed.dispatched") + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST POSITIVE DELIVERY NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
	
	public TestResult testPositiveDeliveryNotificationSmtp(TestInput ti) {
		TestResult tr = new TestResult();
		tr.setProctored(true);
		tr.setCriteriamet(CriteriaStatus.MANUAL);
		HashMap<String, String> result = tr.getTestRequestResponses();
		
		Properties props = getProps(ti);
		Session session = Session.getInstance(props, null);
		tr.setFetchType("smtp");;
		tr.setSearchType("dispatched");

		try {
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			
			String fromAddress = "";
			if (ti.sutUserName.contains("@")){
				fromAddress = ti.sutUserName;
			}

			else {
				fromAddress = ti.sutUserName + "@" + ti.sutSmtpAddress;;
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(prop.getProperty("processed.dispatched")));
			message.setSubject("Testing sending mail to receive Positive Delivery Notification (Test Case MU2-29)!");
			message.setText("This is a message to a badAddress!");
			
			//	message.addHeader("Disposition-Notification-To", prop.getProperty("not.published"));

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("This is message body");
			

			log.info("Sending Message");
			System.setProperty("java.net.preferIPv4Stack", "true");
			

			Transport transport = session.getTransport("smtp");
			transport.connect(ti.sutSmtpAddress, ti.useTLS ? ti.startTlsPort
					: ti.sutSmtpPort, ti.sutUserName, ti.sutPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			String MessageId = message.getHeader("Message-ID")[0];
			System.out.println("Done");
			log.info("Message Sent with ID " + MessageId +" to " + prop.getProperty("processed.dispatched")+" from "+fromAddress);
			result.put("1","SENDING EMAIL TO ADDRESS  " + prop.getProperty("processed.dispatched") + "\n");
			result.put("2","Email sent Successfully\n");
			result.put("3", "Message-ID of the email sent: " + MessageId);
			
			tr.setMessageId(MessageId);
			tr.setStartTime(ZonedDateTime.now().toString());

		} catch (MessagingException e) {
			log.info("Error in testBadAddress");
			result.put("1", "Error in TEST POSITIVE DELIVERY NOTIFICATION: " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
		} catch (Exception e) {
			result.put("1", "Error " + e.getLocalizedMessage());
			// throw new RuntimeException(e);
			e.printStackTrace();
			tr.setCriteriamet(CriteriaStatus.FALSE);
			
		}

		return tr;
	}
}