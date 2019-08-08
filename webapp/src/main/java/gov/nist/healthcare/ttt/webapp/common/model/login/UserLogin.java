package gov.nist.healthcare.ttt.webapp.common.model.login;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="UserLogin")
@XmlType(propOrder={"username", "password"})
public class UserLogin {
	
	// Decalaration of account type enum
	public enum ACCOUNT_TYPE {
		ANNONYMOUS,
		USER,
		ADMIN
	}
	
	private String username;
	private String password;
	private ACCOUNT_TYPE type;
	
	
	public UserLogin() {
		this.username = "";
		this.password = "";
		this.type = ACCOUNT_TYPE.ANNONYMOUS;
	}

	public UserLogin(String username, String password) {
		this.username = username;
		this.password = password;
		this.type = ACCOUNT_TYPE.USER;
	}

	@XmlElement(name="type")
	public ACCOUNT_TYPE getType() {
		return type;
	}

	public void setType(ACCOUNT_TYPE type) {
		this.type = type;
	}

	@XmlElement(name="username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name="password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
