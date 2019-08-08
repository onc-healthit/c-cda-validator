package gov.nist.healthcare.ttt.webapp.direct.model.messageStatus;

import gov.nist.healthcare.ttt.database.log.LogInterface;

import java.util.Collection;

public class MessageStatusList {
	
	private String type;
	private String directAddress;
	private Collection<LogInterface> logList;
	
	
	public MessageStatusList(String type, String directAddress,
			Collection<LogInterface> logList) {
		super();
		this.type = type;
		this.directAddress = directAddress;
		this.logList = logList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDirectAddress() {
		return directAddress;
	}


	public void setDirectAddress(String directAddress) {
		this.directAddress = directAddress;
	}

	public Collection<LogInterface> getLogList() {
		return logList;
	}


	public void setLogList(Collection<LogInterface> logList) {
		this.logList = logList;
	}

}
