package gov.nist.healthcare.ttt.webapp.common.model.login;

public class ChangePasswordModel {
	
	private String oldPassword;
	private String newPassword;
	
	
	public ChangePasswordModel() {
		super();
	}

	public ChangePasswordModel(String oldPassword, String newPassword) {
		super();
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
