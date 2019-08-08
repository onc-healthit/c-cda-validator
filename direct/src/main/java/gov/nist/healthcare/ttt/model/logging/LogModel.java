package gov.nist.healthcare.ttt.model.logging;

import gov.nist.healthcare.ttt.database.log.LogImpl;
import gov.nist.healthcare.ttt.database.log.LogInterface;
import gov.nist.healthcare.ttt.direct.utils.ValidationUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class LogModel extends LogImpl implements LogInterface {

	public LogModel() {
		this.setIncoming(true);
		this.setMessageId("");
		this.setOrigDate("");
		this.setFromLine(new ArrayList<String>());
		this.setToLine(new ArrayList<String>());
		this.setReceived(new ArrayList<String>());
		this.setReplyTo(new ArrayList<String>());
		this.setMimeVersion("");
		this.setSubject("");
		this.setContentType("");
		this.setContentDisposition("");
		this.setTimestamp(new Timestamp(new Date().getTime()).toString());
	}
	
	public LogModel(MimeMessage msg) {
		try {
			this.setIncoming(false);
			this.setMessageId(msg.getMessageID());
			this.setOrigDate(ValidationUtils.getSingleHeader(msg, "date"));
			this.setFromLine(ValidationUtils.fillArrayLog(msg.getFrom()));
			this.setToLine(ValidationUtils.fillArrayLog(msg.getRecipients(Message.RecipientType.TO)));
			this.setReceived(ValidationUtils.fillArrayLog(ValidationUtils.getMultipleHeader(msg, "received")));
			this.setReplyTo(ValidationUtils.fillArrayLog(msg.getReplyTo()));
			this.setMimeVersion(ValidationUtils.getSingleHeader(msg, "mime-version"));
			this.setSubject(msg.getSubject());
			this.setContentType(msg.getContentType());
			this.setContentDisposition(ValidationUtils.getSingleHeader(msg, "content-disposition"));
			this.setTimestamp(new Timestamp(new Date().getTime()).toString());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "LogModel [msgId=" + this.getMessageId() + ", origDate=" + this.getOrigDate()
				+ ", from=" + this.getFromLine() + ", to=" + this.getToLine() + ", received=" + this.getReceived()
				+ ", replyTo=" + this.getReplyTo() + ", mimeVersion=" + this.getMimeVersion()
				+ ", subject=" + this.getSubject() + ", contentType=" + this.getContentType()
				+ ", contentDisposition=" + this.getContentDisposition() + "\n]";
	}

}
