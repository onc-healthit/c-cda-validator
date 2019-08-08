package gov.nist.healthcare.ttt.direct.messageGenerator;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.DispositionNotification;
import com.sun.mail.dsn.MultipartReport;

public class MDNGenerator extends DirectMessageGenerator {

	protected String reporting_UA_name;
	protected String reporting_UA_product;
	protected String original_recipient;
	protected String final_recipient;
	protected String original_message_id;
	protected String disposition;
	protected String failure;
	protected String text;
	protected boolean wrapped;

	public MDNGenerator() {
		this.reporting_UA_name = "";
		this.reporting_UA_product = "";
		this.original_recipient = "";
		this.original_message_id = "";
		this.final_recipient = "";
		this.failure = "";
		this.disposition = "";
		this.wrapped = true;
	}

	public MDNGenerator(String reporting_UA_name, String reporting_UA_product,
			String original_recipient, String final_recipient,
			String original_message_id, String disposition, String failure, String text, boolean wrapped) {
		super();
		this.reporting_UA_name = reporting_UA_name;
		this.reporting_UA_product = reporting_UA_product;
		this.original_recipient = original_recipient;
		this.final_recipient = final_recipient;
		this.original_message_id = original_message_id;
		this.disposition = disposition;
		this.failure = failure;
		this.text = text;
		this.wrapped = wrapped;
	}

	public MultipartReport create() throws MessagingException {
		
		// Create the message parts. According to RFC 2298, there are two
		// compulsory parts and one optional part...
		MultipartReport multiPartReport = new MultipartReport(text, createMDN());

		// Part 3: The optional third part, the original message is omitted.
		// We don't want to propogate over-sized, virus infected or
		// other undesirable mail!
		// There is the option of adding a Text/RFC822-Headers part, which
		// includes only the RFC 822 headers of the failed message. This is
		// described in RFC 1892. It would be a useful addition!
		return multiPartReport;
	}

	public MultipartReport createReportDSN() throws MessagingException {

		// Create the message parts. According to RFC 2298, there are two
		// compulsory parts and one optional part...
		MultipartReport multiPartReport = new MultipartReport(text, createDSN());

		// Part 3: The optional third part, the original message is omitted.
		// We don't want to propogate over-sized, virus infected or
		// other undesirable mail!
		// There is the option of adding a Text/RFC822-Headers part, which
		// includes only the RFC 822 headers of the failed message. This is
		// described in RFC 1892. It would be a useful addition!
		return multiPartReport;
	}

	public DispositionNotification createMDN() throws MessagingException {
		// Create InterHeaders
		InternetHeaders notification = new InternetHeaders();
		if(!isNullorEmpty(reporting_UA_name) || !isNullorEmpty(reporting_UA_product)) {
			notification.addHeader("Reporting-UA", reporting_UA_name + "; " + reporting_UA_product);			
		}
		if(!isNullorEmpty(final_recipient))
			notification.addHeader("Final-Recipient", "rfc822; " + final_recipient);
		if(!isNullorEmpty(original_recipient))
			notification.addHeader("Original-Recipient", "rfc822; " + original_recipient);
		if(!isNullorEmpty(original_message_id))
			notification.addHeader("Original-Message-ID", original_message_id);
		if(!isNullorEmpty(disposition)) {
			// Add X-DIRECT-FINAL-DESTINATION-DELIVERY if it is dispatched MDN
			if(disposition.contains("dispatched")) {
				notification.addHeader("X-DIRECT-FINAL-DESTINATION-DELIVERY", "");		
			}
		
			notification.addHeader("Disposition", disposition);
		}
		
		if(!isNullorEmpty(failure))
			notification.addHeader("Failure", failure);


		// Create disposition/notification
		DispositionNotification dispositionNotification = new DispositionNotification();
		dispositionNotification.setNotifications(notification);
		
		return dispositionNotification;
	}
	
	public DeliveryStatus createDSN() throws MessagingException {
		// Create InterHeaders
		InternetHeaders dsn = new InternetHeaders();
		if(!isNullorEmpty(reporting_UA_name) || !isNullorEmpty(reporting_UA_product)) {
			dsn.addHeader("Reporting-MTA", "dsn; mail.nist.gov");			
		}
		if(!isNullorEmpty(original_message_id))
			dsn.addHeader("Original-Envelope-ID", original_message_id);
		if(!isNullorEmpty(original_recipient))
			dsn.addHeader("Original-Recipient", "rfc822; " + original_recipient);
		if(!isNullorEmpty(final_recipient))
			dsn.addHeader("Final-Recipient", "rfc822; " + final_recipient);
		
		dsn.addHeader("Action", "delivered");
		dsn.addHeader("Status", "2.0.0");


		// Create disposition/notification
		DeliveryStatus deliveryStatus = new DeliveryStatus();
		deliveryStatus.setMessageDSN(dsn);

		return deliveryStatus;
	}

	public MimeBodyPart generateBodyReport(boolean isDsn, boolean extraWhiteSpace, boolean diffCases) throws MessagingException {
		MimeBodyPart m = new MimeBodyPart();
		
		// Create mdn report or dsn report
		MultipartReport report;
		if(isDsn) {
			report = createReportDSN();
		} else {
			report = create();
		}
		
		String extraSpace = "";
		if(extraWhiteSpace) {
			extraSpace = " ";
		}
		
		if(this.wrapped) {
			InternetHeaders rfc822Headers = new InternetHeaders();
			rfc822Headers.addHeaderLine(getHeaderNormalOrDifferentCases("Content-Type" + extraSpace + ": message/rfc822", diffCases));
			rfc822Headers.addHeader(getHeaderNormalOrDifferentCases("To" + extraSpace, diffCases), this.to.toString());
			rfc822Headers.addHeader(getHeaderNormalOrDifferentCases("From" + extraSpace, diffCases), this.from.toString());
			rfc822Headers.addHeader(getHeaderNormalOrDifferentCases("Subject" + extraSpace, diffCases), this.subject);
			rfc822Headers.addHeader(getHeaderNormalOrDifferentCases("Date" + extraSpace, diffCases), new Date().toString());

			MimeMessage message2 = new MimeMessage(
					Session.getDefaultInstance(new Properties()));
			message2.setFrom(this.from);
			message2.setRecipient(Message.RecipientType.TO, this.to);
			message2.setSentDate(new Date());
			message2.setSubject(this.subject);
			
			// Attach the mdn report
			message2.setContent(report, report.getContentType());

			message2.saveChanges();

			m.setContent(message2, "message/rfc822");

			// Set messageID variable
			this.wrappedMessageID = message2.getMessageID();
			
		} else {
			m.setContent(report);
		}
			return m;
	}
	
	public MimeBodyPart signMDN(MimeBodyPart mdnPart) throws MessagingException, Exception {
		return generateMultipartSigned(mdnPart);
	}
	
	public MimeMessage encryptMDN(MimeBodyPart body) throws Exception {
		return generateEncryptedMessage(body);
	}
	
	public MimeMessage generateMDN() throws MessagingException, Exception {
		return encryptMDN(signMDN(generateBodyReport(false, false, false)));
	}
	
	/**
	 * SMTP Only MDN
	 */
	public MimeMessage generateSmtpMDN() throws MessagingException, Exception {
		return generateUnencryptedMessage(create());
	}
	
	/**
	 * 
	 * Faulty MDN generation
	 * 
	 */
	
	// Generate DSN instead of MDN
	public MimeMessage generateDSN() throws MessagingException, Exception {
		return generateEncryptedMessageWithDifferentSettings(signMDN(generateBodyReport(true, false, false)), true, false);
	}

	// Null Envelope Sender
	public MimeMessage generateNullEnvelopeSenderMDN() throws MessagingException, Exception {
		return generateEncryptedMessageWithDifferentSettings(signMDN(generateBodyReport(false, false, false)), true, false);
	}
	
	// Different Sender
	public MimeMessage generateDifferentSenderMDN() throws MessagingException, Exception {
		return generateEncryptedMessageWithDifferentSettings(signMDN(generateBodyReport(false, false, false)), false, true);
	}
	
	// Different Msg Id
	public MimeMessage generateDifferentMsgIdMDN() throws MessagingException, Exception {
		return generateEncryptedMessageDifferentMsgId(signMDN(generateBodyReport(false, false, false)));
	}
	
	// RFC 822 Extra White Space
	public MimeMessage generate822ExtraSpaces() throws MessagingException, Exception {
		return generateEncryptedMessageWithDifferentSettings(signMDN(generateBodyReport(false, true, false)), true, false);
	}
	
	// RFC 822 Different Cases
	public MimeMessage generate822DifferentCases() throws MessagingException, Exception {
		return generateEncryptedMessageWithDifferentSettings(signMDN(generateBodyReport(false, false, true)), true, false);
	}
	
	public boolean isNullorEmpty(String value) {
		if(value == null) {
			return true;
		}
		if(value.equals("")) {
			return true;
		}
		return false;
	}
	
	public String getHeaderNormalOrDifferentCases(String header, boolean diffCases) {
		if(diffCases) {
			return header.toLowerCase();
		} else {
			return header;
		}
	}

	public String getReporting_UA_name() {
		return reporting_UA_name;
	}

	public void setReporting_UA_name(String reporting_UA_name) {
		this.reporting_UA_name = reporting_UA_name;
	}

	public String getReporting_UA_product() {
		return reporting_UA_product;
	}

	public void setReporting_UA_product(String reporting_UA_product) {
		this.reporting_UA_product = reporting_UA_product;
	}

	public String getOriginal_recipient() {
		return original_recipient;
	}

	public void setOriginal_recipient(String original_recipient) {
		this.original_recipient = original_recipient;
	}

	public String getFinal_recipient() {
		return final_recipient;
	}

	public void setFinal_recipient(String final_recipient) {
		this.final_recipient = final_recipient;
	}

	public String getOriginal_message_id() {
		return original_message_id;
	}

	public void setOriginal_message_id(String original_message_id) {
		this.original_message_id = original_message_id;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isWrapped() {
		return wrapped;
	}

	public void setWrapped(boolean wrapped) {
		this.wrapped = wrapped;
	}

}
