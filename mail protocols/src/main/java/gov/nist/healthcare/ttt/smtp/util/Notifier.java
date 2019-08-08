package gov.nist.healthcare.ttt.smtp.util;

import java.io.PrintStream;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Notifier
{
  static void sendmail(String subject, String email1, String email2, String email3, String email4, String email5, String email6)
    throws Exception
  {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN");
    props.setProperty("mail.smtp.ssl.trust", "*");
    
    Session session = Session.getInstance(props, null);
    
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("ttpstatus@ttpds.com"));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email1));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email2));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email3));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email4));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email5));
    message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(email6));
    message.setSubject(subject);
    message.setText("This is a message about TTP status");
    BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText("This is message body");
    
    Multipart multipart = new MimeMultipart("mixed");
    
    String file = "/opt/notify/status.txt";
    String fileName = "status.txt";
    DataSource source = new FileDataSource(file);
    messageBodyPart.setDataHandler(new DataHandler(source));
    messageBodyPart.setFileName(fileName);
    multipart.addBodyPart(messageBodyPart);
    
    message.setContent(multipart);
    
    System.setProperty("java.net.preferIPv4Stack", "true");
    MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
    CommandMap.setDefaultCommandMap(mc);
    Transport transport = session.getTransport("smtp");
    transport.connect("ttpds.sitenv.org", "ttpstatus@ttpds.sitenv.org", "smtptesting123");
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
    System.out.println("Email sent");
  }
  
  public static void main(String[] args) throws Exception
  {
   
      String subject = args[1];
      String emails = args[0];
      String[] email = emails.split(";");
      String email1 = email[0];
      String email2 = email[1];
      String email3 = email[2];
      String email4 = email[3];
      String email5 = email[4];
      String email6 = email[5];
    sendmail(subject,email1,email2,email3,email4,email5,email6);
    
  }
}