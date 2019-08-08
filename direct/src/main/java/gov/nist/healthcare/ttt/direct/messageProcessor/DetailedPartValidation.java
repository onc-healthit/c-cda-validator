package gov.nist.healthcare.ttt.direct.messageProcessor;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringEscapeUtils;

import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageHeadersValidator;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageValidator;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;
import gov.nist.healthcare.ttt.model.logging.PartModel;

public class DetailedPartValidation {
	
	private DirectMessageHeadersValidator messageHeaderValidator = new DirectMessageHeadersValidator();
	private DirectMimeEntityValidator mimeEntityValidator = new DirectMimeEntityValidator();
	private DirectMessageValidator directMessageValidator = new DirectMessageValidator();

	
	public void validateMimeEntity(PartModel m) throws Exception {
		
		// DTS 133-145-146 Validate Content Type
		m.addNewDetailLine(mimeEntityValidator.validateContentType(m.getContentType()));
		
		// DTS 191 Validate Content Type Subtype
		m.addNewDetailLine(mimeEntityValidator.validateContentTypeSubtype(m.getContentType()));
		
		// DTS 195, Validate Body
		//if(m.getContentType().contains("text/plain"))
		//	mimeEntityValidator.validateBody(m, m.getContent().toString());
		
		// DTS 192 Validate Content Type Name
		m.addNewDetailLine(mimeEntityValidator.validateContentTypeName(m.getContentType()));
		
		// DTS 193 Validate Content Type SMIMEType
		m.addNewDetailLine(mimeEntityValidator.validateContentTypeSMIMEType(m.getContentType()));
		
		// DTS 137-140 Validate Content Type Boundary
		// Find the boundary
		String boundary = "";
		String [] contentType_split = m.getContentType().split("boundary=");
		if(contentType_split.length > 1) {
			String content_split_right = contentType_split[1];
			String[] temp = content_split_right.split("\"", -2); // splits again anything that is after the boundary expression, just in case
			for (int j = 0; j<temp.length ; j++){
				if (temp[j].contains("--")) {
					boundary = temp[j];
				}
			}
		}
		m.addNewDetailLine(mimeEntityValidator.validateContentTypeBoundary(boundary));
		
		String contentTypeDisposition = "";
		if(m.getContentDisposition() != null) {
			contentTypeDisposition = m.getContentDisposition();
		}
		
		// DTS 136-148-157, Content-Transfer-Encoding, Optional
		m.addNewDetailLine(mimeEntityValidator.validateContentTransferEncodingOptional(m.getContentTransferEncoding(), m.getContentType()));
		
		// DTS 156 Validate Content Type Disposition
		m.addNewDetailLine(mimeEntityValidator.validateContentTypeDisposition(contentTypeDisposition, m.getContentType()));
		
		// DTS 195, Body, Required
		if(m.getContent().getContent() instanceof String) {
			String encoding = ValidationUtils.getSingleHeader(m.getContent(), "content-transfer-encoding");
			m.addNewDetailLine(mimeEntityValidator.validateBody(m.getContent(), (String) m.getContent().getContent(), encoding));
		}
		
		// DTS 190, All Mime Header Fields, Required
		m.addNewDetailLine(mimeEntityValidator.validateAllMimeHeaderFields(contentTypeDisposition));
		
		
		/**************************/
		/** MUST BE THE LAST ONE **/
		/**************************/
		// DTS 199 Validate All non-MIME message headers
		m.addNewDetailLine(directMessageValidator.validateMIMEEntity(m, ""));
		
		
	}
	
	public void validateMessageHeader(PartModel m, boolean wrapped) throws IOException, MessagingException {
		
		MimeMessage msg = (MimeMessage) m.getContent();
		
		// DTS 196, Validate All Headers
		String[] header = ValidationUtils.getHeadersAndContent(msg).get(0);
		String[] headerContent =ValidationUtils.getHeadersAndContent(msg).get(1);
		m.addNewDetailLine(messageHeaderValidator.validateAllHeaders(header, headerContent, wrapped));
		
		// DTS 114 Validate Orig Date
		m.addNewDetailLine(messageHeaderValidator.validateOrigDate(ValidationUtils.getSingleHeader(msg, "date"), wrapped));

		// DTS 115 Validate From
		String from = "";
		if(msg.getFrom() != null) {
			for(int i=0;i<msg.getFrom().length;i++) {
				from = msg.getFrom()[i].toString();
				m.addNewDetailLine(messageHeaderValidator.validateFrom(from, wrapped));
				from = StringEscapeUtils.escapeHtml4(from);
			}
		}
		
		// DTS 118 Validate To
		String to = "";
		if(msg.getRecipients(Message.RecipientType.TO) != null) {
			to = msg.getRecipients(Message.RecipientType.TO)[0].toString();
		}
		m.addNewDetailLine(messageHeaderValidator.validateTo(to, wrapped));
		to = StringEscapeUtils.escapeHtml4(to);
		
		// DTS 121, Validate Message-Id
		String messageID = ValidationUtils.getSingleHeader(msg, "message-id");
		m.addNewDetailLine(messageHeaderValidator.validateMessageId(messageID, wrapped));
		
		// DTS 102b Validate Mime Version
		// Searching for Mime Version Header and Value
		m.addNewDetailLine(messageHeaderValidator.validateMIMEVersion(ValidationUtils.getSingleHeader(msg, "mime-version"), wrapped));
		
		// DTS 103-105 Validate Return Path
		String returnPath = ValidationUtils.getSingleHeader(msg, "return-path");
		m.addNewDetailLine(messageHeaderValidator.validateReturnPath(returnPath, wrapped));
		
		// DTS 104-106 Validate Received
		String[] searchRes = ValidationUtils.getMultipleHeader(msg, "received");
		String received = "";
		for(int i=0;i<searchRes.length;i++) {
			received = searchRes[i];
			m.addNewDetailLine(messageHeaderValidator.validateReceived(received, wrapped));
		}
		
		// DTS 107 Validate Resent-Date
		m.addNewDetailLine(messageHeaderValidator.validateResentDate(ValidationUtils.getSingleHeader(msg, "resent-date"), wrapped));
		
		// DTS 108 Validate Resent-From
		m.addNewDetailLine(messageHeaderValidator.validateResentFrom(ValidationUtils.getSingleHeader(msg, "resent-from"), wrapped));
		
		// DTS 109 Validate Resent-Sender
		m.addNewDetailLine(messageHeaderValidator.validateResentSender(ValidationUtils.getSingleHeader(msg, "resent-sender"), ValidationUtils.getSingleHeader(msg, "resent-from"), wrapped));

		// DTS 113 Validate Resent-Msg-Id
		m.addNewDetailLine(messageHeaderValidator.validateResentMsgId(ValidationUtils.getSingleHeader(msg, "resent-msg-id"), wrapped));
		
		// DTS 116 Validate Sender
		if(msg.getFrom() != null) {
			m.addNewDetailLine(messageHeaderValidator.validateSender(ValidationUtils.getSingleHeader(msg, "sender"), msg.getFrom(), wrapped));
		}
		
		// DTS 117 Validate Reply To
		if(msg.getReplyTo() != null) {
			m.addNewDetailLine(messageHeaderValidator.validateReplyTo(msg.getReplyTo()[0].toString(), wrapped));
		}
		
		// DTS 110 Validate Resent-To
		m.addNewDetailLine(messageHeaderValidator.validateResentTo(ValidationUtils.getSingleHeader(msg, "resent-to"), wrapped));
		
		// DTS 111 Validate Resent-Cc
		m.addNewDetailLine(messageHeaderValidator.validateResentCc(ValidationUtils.getSingleHeader(msg, "resent-cc"), wrapped));
		
		// DTS 112 Validate Resent-Bcc
		m.addNewDetailLine(messageHeaderValidator.validateResentBcc(ValidationUtils.getSingleHeader(msg, "resent-bcc"), wrapped));
		
		// DTS 197 Validate Resent Fields
		String[] resentField = null;
		resentField = ValidationUtils.getHeadersAndContent(msg).get(0);
		m.addNewDetailLine(messageHeaderValidator.validateResentFields(resentField, wrapped));
		
		// DTS 119 Validate Cc
		searchRes = ValidationUtils.getMultipleHeader(msg, "cc");
		String cc = "";
		for(int i=0;i<searchRes.length;i++) {
			cc = searchRes[i];
			cc = cc.replaceAll("\\s", "");
		}
		m.addNewDetailLine(messageHeaderValidator.validateCc(cc, wrapped));
		
		// DTS 120 Validate Bcc
		m.addNewDetailLine(messageHeaderValidator.validateBcc(ValidationUtils.getSingleHeader(msg, "bcc"), wrapped));
		
		// DTS 122 Validate In-Reply-To
		m.addNewDetailLine(messageHeaderValidator.validateInReplyTo(ValidationUtils.getSingleHeader(msg, "in-reply-to"), ValidationUtils.getSingleHeader(msg, "date"), wrapped));
		
		// DTS 123 Validate Reference
		m.addNewDetailLine(messageHeaderValidator.validateReferences(ValidationUtils.getSingleHeader(msg, "references"), wrapped));
		
		// DTS 124 Validate Subject
		m.addNewDetailLine(messageHeaderValidator.validateSubject(msg.getSubject(), msg.getContentType(), wrapped));
		
		// DTS 125 Validate Comment
		m.addNewDetailLine(messageHeaderValidator.validateComments(ValidationUtils.getSingleHeader(msg, "comments"), wrapped));
		
		// DTS 126 Validate Keywords
		m.addNewDetailLine(messageHeaderValidator.validateKeywords(ValidationUtils.getSingleHeader(msg, "keywords"), wrapped));

		// DTS 127 Validate Optional Fields
		m.addNewDetailLine(messageHeaderValidator.validateOptionalField(ValidationUtils.getSingleHeader(msg, "optional-field"), wrapped));
		
		// DTS 128 Validate Disposition-Notification-To
		m.addNewDetailLine(messageHeaderValidator.validateDispositionNotificationTo(ValidationUtils.getSingleHeader(msg, "disposition-notification-to"), wrapped));
		
		/**************************/
		/** MUST BE THE LAST ONE **/
		/**************************/
		// DTS 199 Validate All non-MIME message headers
		m.addNewDetailLine(directMessageValidator.validateNonMIMEMessageHeaders(m, ""));

		
	}
	
	public void validateDirectMessageInnerDecryptedMessage(PartModel m) throws Exception {
		// DTS 133b, Validate Content-Type
		m.addNewDetailLine(directMessageValidator.validateMessageContentTypeB(m.getContentType()));
		
		// DTS 160, Validate Content-Type micalg
		// Find micalg
		String contentTypeMicalg = "";
		String[] contentType_split = m.getContentType().split("micalg=");
		if(contentType_split.length > 1) {
			String content_split_right = contentType_split[1];
			String[] temp = content_split_right.split(";", -2);
			contentTypeMicalg = temp[0];
			contentTypeMicalg = contentTypeMicalg.toLowerCase();
		}
		m.addNewDetailLine(directMessageValidator.validateContentTypeMicalg(contentTypeMicalg));
		
		// DTS 205, Validate Content-Type protocol
		// Find protocol
		String contentTypeProtocol = "";
		contentType_split = m.getContentType().split("protocol=");
		if(contentType_split.length > 1) {
			String content_split_right = contentType_split[1];
			String[] temp = content_split_right.split(";", -2);
			contentTypeProtocol = temp[0];
		}
		m.addNewDetailLine(directMessageValidator.validateContentTypeProtocol(contentTypeProtocol));
		
		// DTS ??? - Mime Entity body - Required
		if(m.getContent().getContent() instanceof MimeMultipart) {
			MimeMultipart mimeEntityBody = (MimeMultipart) m.getContent().getContent();
			m.addNewDetailLine(directMessageValidator.validateMIMEEntityBody(mimeEntityBody.getCount()));
		}
        
        // DTS 204, MIME Entity
		m.addNewDetailLine(directMessageValidator.validateMIMEEntity2(m.isStatus()));
		
	}
	
	public void validateSignaturePart(PartModel m) throws MessagingException {
		// DTS 206, Validate Content-Transfer-Encoding
		m.addNewDetailLine(directMessageValidator.validateContentTransferEncoding(ValidationUtils.getSingleHeader(m.getContent(), "content-transfer-encoding")));
	}
	
}
