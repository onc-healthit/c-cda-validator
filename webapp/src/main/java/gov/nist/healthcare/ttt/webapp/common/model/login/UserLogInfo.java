package gov.nist.healthcare.ttt.webapp.common.model.login;

public class UserLogInfo {
	
	private String username;
	private boolean isLogged;
	
	public UserLogInfo(String username, boolean isLogged) {
		this.username = username;
		this.isLogged = isLogged;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isLogged() {
		return isLogged;
	}

	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}
	
	

}
