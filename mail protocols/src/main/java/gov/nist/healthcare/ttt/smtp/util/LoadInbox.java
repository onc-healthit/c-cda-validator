package gov.nist.healthcare.ttt.smtp.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;


public class LoadInbox {

	public static void main(String args[]){

		System.setProperty("java.net.preferIPv4Stack", "true");


		try{

			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter folder path: ");
			String path = reader.next();

			System.out.println("Enter hostname: ");
			String hostname = reader.next();

			System.out.println("Enter Username[john.doe@hostname.com]: ");
			String username = reader.next();

			System.out.println("Enter Password: ");
			String password = reader.next();

			System.out.println("Enter Destination email address: ");
			String recipient = reader.next();

			reader.close();

			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.starttls.required", "true");
			props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN");
			props.setProperty("mail.smtp.ssl.trust", "*");

			Session session = Session.getInstance(props, null);

			for (int i = 0; i < listOfFiles.length; i++) {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipient));
				message.setSubject(listOfFiles[i].getName());
				message.setText("This is a message to test!");

				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("This is message body");
				Multipart multipart = new MimeMultipart();

				// Adding attachments
				FileInputStream input;
				input = new FileInputStream(listOfFiles[i]);
				byte[] b =  IOUtils.toByteArray(input);

				DataSource source = new ByteArrayDataSource(b,"application/xml");
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(listOfFiles[i].getName());
				multipart.addBodyPart(messageBodyPart);

				// Send the complete message parts
				message.setContent(multipart);


				System.setProperty("java.net.preferIPv4Stack", "true");

				Transport transport = session.getTransport("smtp");
				transport.connect(hostname,username, password);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
				int j = i+1;
				System.out.println("Email sent " + j);


			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}




