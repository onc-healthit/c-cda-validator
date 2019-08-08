package gov.nist.healthcare.ttt.webapp.direct.listener;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class Emailer extends Authenticator {
	
	private Logger logger = Logger.getLogger(Emailer.class.getName());

	private EmailerModel model;

	public Emailer(EmailerModel model) {
		this.model = model;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(model.getSmtpUser(), model.getSmtpPassword());
	}


	public void sendEmail2(String to, String subject, String body) throws AddressException, MessagingException {
		//		String cc = null;
		//		String bcc = null;
		//		String url = null;
		//		String mailhost = tkprops.get("host");
		String mailer = "Emailer";
		//		String file = null;
		//		String protocol = null;
		//		String host = null;
		//		String user = null; 
		//		String password = null;
		//		String record = null;	// name of folder in which to record mail
//		boolean debug = "true".equalsIgnoreCase(tkprops.get("debug", "false")) ;
//		boolean gmailStyle = "true".equalsIgnoreCase(tkprops.get("gmail.style", "false")) ;

		/*
		 * Initialize the JavaMail Session.
		 */
		Properties props = System.getProperties();
		props.put("mail.smtp.host", model.getHost());
		props.put("mail.smtp.port", model.getSmtpPort());
		props.put("mail.smtp.auth", model.getSmtpAuth());
		props.put("mail.smtp.starttls.enable", model.getStarttls());

		props.put("mail.smtp.user", model.getSmtpUser());
		props.put("mail.smtp.password", model.getSmtpPassword());
		props.put("mail.smtp.debug", "false");
	//	props.setProperty("mail.smtp.ssl.trust", "*");
		// These lines are necesssary for connecting to gmail.com
		// Don't know about hit-testing yet
		if(model.getGmailStyle().equals("true")) {
			props.put("mail.smtp.socketFactory.port", model.getSmtpPort());
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}
			
//		if (debug) {
//			StringBuffer buf = new StringBuffer();
//			buf.append("\n");
//			for (Object oname : props.keySet()) {
//				if (oname instanceof String) {
//					String name = (String) oname;
//					String value = props.getProperty(name);
//					if (name.startsWith("mail"))
//						buf.append(name).append(" = ").append(value).append("\n");
//				}
//			}
//			logger.debug(buf.toString());
//		}

		// Get a Session object

		Session session = Session.getInstance(props, this);
		
//		if (debug)
//			session.setDebug(true);

		/*
		 * Construct the message and send it.
		 */
		Message msg = new MimeMessage(session);
		if (model.getFrom() != null)
		//	msg.setFrom(new InternetAddress(model.getFrom()));
			msg.setFrom(new InternetAddress(model.getSmtpUser()));
		else
			msg.setFrom();

		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to, false));
		//		if (cc != null)
		//			msg.setRecipients(Message.RecipientType.CC,
		//					InternetAddress.parse(cc, false));
		//		if (bcc != null)
		//			msg.setRecipients(Message.RecipientType.BCC,
		//					InternetAddress.parse(bcc, false));

		msg.setSubject(subject);
		
		msg.setContent(body, "text/html; charset=utf-8");

//		msg.setText(body);

		msg.setHeader("X-Mailer", mailer);
		msg.setSentDate(new Date());

		// send the thing off
		Transport.send(msg);

		logger.info("\nMail was sent successfully.");
	}

	//	/**
	//	 * Send a single email.
	//	 * @throws PropertyNotFoundException 
	//	 */
	//	public void sendEmail(
	//			String aFromEmailAddr, String aToEmailAddr,
	//			String aSubject, String aBody
	//			) throws PropertyNotFoundException{
	//		//Here, no Authenticator argument is used (it is null).
	//		//Authenticators are used to prompt the user for user
	//		//name and password.
	//		System.out.println("Getting session");
	//
	//		Properties sessionProps = new Properties();
	//		sessionProps.setProperty("mail.debug", props.get("debug"));
	//
	//		Session session = Session.getInstance( new Properties(), null );
	//		//		System.out.println("Setting auth");
	//		//		session.requestPasswordAuthentication(null, 25, "imap", "prompt", "bmajur");
	//		System.out.println("New MimeMessage");
	//		MimeMessage message = new MimeMessage( session );
	//		try {
	//			//the "from" address may be set in code, or set in the
	//			//config file under "mail.from" ; here, the latter style is used
	//			//message.setFrom( new InternetAddress(aFromEmailAddr) );
	//			System.out.println("Adding Recipient");
	//			message.addRecipient(
	//					Message.RecipientType.TO, new InternetAddress(aToEmailAddr)
	//					);
	//			message.setSubject( aSubject );
	//			message.setText( aBody );
	//
	//			System.out.println("Creating transport");
	//			Transport transport = session.getTransport(props.get("transport"));
	//			System.out.println("Connecting to " + props.get("host") + " as " + props.get("smtp.user"));
	//			//			transport.connect("smtp.gmail.com", "bmajur", "xxxx")	;
	//			transport.connect(props.get("host"), props.get("smtp.user"), props.get("smtp.password"))	;
	//			System.out.println("Sending");
	//			transport.sendMessage(message, message.getAllRecipients());
	//			System.out.println("Closing");
	//			transport.close();
	//			//			Transport.send( message );
	//			System.out.println("Closed");
	//		}
	//		catch (MessagingException ex){
	//			System.out.println(ExceptionUtil.exception_details(ex));
	//		}
	//	}


}
