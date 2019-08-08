package gov.nist.healthcare.ttt.webapp.direct.model.messageValidator;

public class MessageValidator {
	
	private String messageFilePath;
	private String certFilePath;
	private String certPassword;
	
	public MessageValidator(String messageFilePath, String certFilePath, String certPassword) {
		super();
		this.messageFilePath = messageFilePath;
		this.certFilePath = certFilePath;
		this.certPassword = certPassword;
	}
	
	public MessageValidator() {
		super();
		this.messageFilePath = "";
		this.certFilePath = "";
		this.certPassword = "";
	}
	
	public String getMessageFilePath() {
		return messageFilePath;
	}
	public void setMessageFilePath(String messageFilePath) {
		this.messageFilePath = messageFilePath;
	}
	public String getCertFilePath() {
		return certFilePath;
	}
	public void setCertFilePath(String certFilePath) {
		this.certFilePath = certFilePath;
	}
	public String getCertPassword() {
		return certPassword;
	}
	public void setCertPassword(String certPassword) {
		this.certPassword = certPassword;
	}

}
