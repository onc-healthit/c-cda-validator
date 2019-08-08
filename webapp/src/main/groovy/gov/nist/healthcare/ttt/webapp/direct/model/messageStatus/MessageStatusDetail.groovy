package gov.nist.healthcare.ttt.webapp.direct.model.messageStatus;

import gov.nist.healthcare.ttt.database.log.LogImpl;
import gov.nist.healthcare.ttt.database.log.LogInterface;

public class MessageStatusDetail extends LogImpl implements LogInterface {
	
	private String a_address;
	private String b_messageID;
	private String c_time;
	private String d_status;
	
	public MessageStatusDetail(String a_address, String b_messageID,
			String c_time, String d_status) {
		super();
		this.a_address = a_address;
		this.b_messageID = b_messageID;
		this.c_time = c_time;
		this.d_status = d_status;
	}
	
	public MessageStatusDetail(LogInterface log) {
		if(log.getIncoming()) {
			this.a_address = log.getToLine().iterator().next();
		} else {
			this.a_address = log.getFromLine().iterator().next();
		}
		this.b_messageID = log.getMessageId();
		this.c_time = log.getOrigDate();
		this.d_status = log.getStatus().toString();
		this.setContentDisposition(log.getContentDisposition());
		this.setFromLine(log.getFromLine());
		this.setIncoming(log.getIncoming());
		this.setLogID(log.getLogID());
		this.setMdn(log.isMdn());
		this.setMessageId(log.getMessageId());
		this.setMimeVersion(log.getMimeVersion());
		this.setOrigDate(log.getOrigDate());
		this.setOriginalMessageId(log.getOriginalMessageId());
		this.setReceived(log.getReceived());
		this.setReplyTo(log.getReplyTo());
		this.setStatus(log.getStatus());
		this.setSubject(log.getSubject());
		this.setTimestamp(log.getTimestamp());
		this.setToLine(log.getToLine());
	}

	public String getA_from() {
		return a_address;
	}

	public void setA_from(String a_from) {
		this.a_address = a_from;
	}

	public String getB_messageID() {
		return b_messageID;
	}

	public void setB_messageID(String b_messageID) {
		this.b_messageID = b_messageID;
	}

	public String getC_time() {
		return c_time;
	}

	public void setC_time(String c_time) {
		this.c_time = c_time;
	}

	public String getD_status() {
		return d_status;
	}

	public void setD_status(String d_status) {
		this.d_status = d_status;
	}

}
