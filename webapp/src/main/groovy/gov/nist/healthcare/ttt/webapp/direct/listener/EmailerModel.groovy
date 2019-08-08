package gov.nist.healthcare.ttt.webapp.direct.listener;

public class EmailerModel {
	
	private String smtpUser = "";
	private String smtpPassword = "";
	private String from = "";
	private String host = "";
	private String smtpPort = "";
	private String smtpAuth = "";
	private String starttls = "";
	private String gmailStyle = "";
	
	public EmailerModel() {
		super();
	}

	public EmailerModel(String smtpUser, String smtpPassword, String from,
			String host, String smtpPort, String smtpAuth, String starttls) {
		super();
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.from = from;
		this.host = host;
		this.smtpPort = smtpPort;
		this.smtpAuth = smtpAuth;
		this.starttls = starttls;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public String getStarttls() {
		return starttls;
	}

	public void setStarttls(String starttls) {
		this.starttls = starttls;
	}

	public String getGmailStyle() {
		return gmailStyle;
	}

	public void setGmailStyle(String gmailStyle) {
		this.gmailStyle = gmailStyle;
	}
}
