package gov.nist.healthcare.ttt.smtp.util;


import java.util.Properties;
import java.util.Scanner;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;


public class HeaderTest {

	public static void main(String args[]){

		System.setProperty("java.net.preferIPv4Stack", "true");


		try{

			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter hostname: ");
			String hostname = reader.next();

			System.out.println("Enter Username[john.doe@hostname.com]: ");
			String username = reader.next();

			System.out.println("Enter Password: ");
			String password = reader.next();

			System.out.println("Enter Destination email address: ");
			String recipient = reader.next();

			reader.close();


			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.starttls.required", "true");
			props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN");
			props.setProperty("mail.smtp.ssl.trust", "*");

			Session session = Session.getInstance(props, null);

			for (int i = 0; i < 3; i++) {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipient));
				message.setSubject("Message "+ i);
				message.setText("This is a message to test!");
				message.addHeader("Disposition-Notification-Options", "X-DIRECT-FINAL-DESTINATION-DELIVERY=optional,true");
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("This is message body");
				


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





