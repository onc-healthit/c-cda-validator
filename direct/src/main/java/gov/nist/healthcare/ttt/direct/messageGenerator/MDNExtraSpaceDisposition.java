package gov.nist.healthcare.ttt.direct.messageGenerator;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;

import com.sun.mail.dsn.DispositionNotification;
import com.sun.mail.dsn.MultipartReport;

public class MDNExtraSpaceDisposition extends MDNGenerator {
	public MultipartReport create() throws MessagingException {

		// Create InterHeaders
		InternetHeaders notification = new InternetHeaders();
		if(!isNullorEmpty(reporting_UA_name) || !isNullorEmpty(reporting_UA_product)) {
			notification.addHeader("Reporting-UA", reporting_UA_name + ";" + reporting_UA_product);			
		}
		if(!isNullorEmpty(final_recipient))
			notification.addHeader("Final-Recipient", "rfc822; " + final_recipient);
		if(!isNullorEmpty(original_recipient))
			notification.addHeader("Original-Recipient", "rfc822; " + original_recipient);
		if(!isNullorEmpty(original_message_id))
			notification.addHeader("Original-Message-ID", original_message_id);
		if(!isNullorEmpty(disposition))
			if(disposition.contains(";")) {
				disposition.replace(";", " ; ");
			}
			notification.addHeader("Disposition", disposition);
		if(!isNullorEmpty(failure))
			notification.addHeader("Failure", failure);


		// Create disposition/notification
		DispositionNotification dispositionNotification = new DispositionNotification();
		dispositionNotification.setNotifications(notification);

		// Create the message parts. According to RFC 2298, there are two
		// compulsory parts and one optional part...
		MultipartReport multiPartReport = new MultipartReport(text, dispositionNotification);

		// Part 3: The optional third part, the original message is omitted.
		// We don't want to propogate over-sized, virus infected or
		// other undesirable mail!
		// There is the option of adding a Text/RFC822-Headers part, which
		// includes only the RFC 822 headers of the failed message. This is
		// described in RFC 1892. It would be a useful addition!
		return multiPartReport;
	}
}
