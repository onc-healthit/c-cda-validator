package gov.nist.healthcare.ttt.smtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

// Test input class
public class TestInput {
	public String sutSmtpAddress;

	public int sutSmtpPort;

	public int tttSmtpPort;

	public String sutEmailAddress;

	public String tttEmailAddress;

	public boolean useTLS;

	public int sutCommandTimeoutInSeconds;

	public String sutUserName;

	public String sutPassword;

	public String tttUserName;

	public String tttPassword;

	public String tttSmtpAddress;

	public int startTlsPort;

    public String ccdaReferenceFilename;

	public String ccdaValidationObjective;

	public TestResult tr;

	public byte[] certificate;

	public LinkedHashMap<String, byte[]> attachments = new LinkedHashMap<String, byte[]>();

	public TestInput(String _sutSmtpAddress,String _tttSmtpAddress,int _sutSmtpPort, int _tttSmtpPort,
			String _sutEmailAddress, String _tttEmailAddress, boolean _useTLS,
			String _sutUserName, String _sutPassword,String _tttUserName, String _tttPassword, int _starTtlsPort,
			int _sutCommandTimeoutInSeconds,
			LinkedHashMap<String, byte[]> _attachments, String _ccdaReferenceFilename, String _ccdaValidationObjective, byte[] _certificate) {
		sutSmtpAddress = _sutSmtpAddress;
		tttSmtpAddress = _tttSmtpAddress;
		sutSmtpPort = _sutSmtpPort;
		tttSmtpPort = _tttSmtpPort;
		sutEmailAddress = _sutEmailAddress;
		tttEmailAddress = _tttEmailAddress;
		useTLS = _useTLS;
		sutUserName = _sutUserName;
		sutPassword = _sutPassword;
		tttUserName = _tttUserName;
		tttPassword = _tttPassword;
		startTlsPort = _starTtlsPort;
		sutCommandTimeoutInSeconds = _sutCommandTimeoutInSeconds;
		attachments = _attachments;
		ccdaReferenceFilename = _ccdaReferenceFilename;
		ccdaValidationObjective = _ccdaValidationObjective;
		certificate = _certificate;
	}

	public TestInput(String _sutSmtpAddress, int _sutSmtpPort,
			String _sutEmailAddress, String _tttEmailAddress, boolean _useTLS) {
		sutSmtpAddress = _sutSmtpAddress;
		sutSmtpPort = _sutSmtpPort;
		sutEmailAddress = _sutEmailAddress;
		tttEmailAddress = _tttEmailAddress;
		useTLS = _useTLS;
	}

	public TestInput(byte[] cert) {
		this.certificate = cert;
	}

	public String getSutSmtpAddress() {
		return sutSmtpAddress;
	}

	public void setSutSmtpAddress(String sutSmtpAddress) {
		this.sutSmtpAddress = sutSmtpAddress;
	}

	public int getSutSmtpPort() {
		return sutSmtpPort;
	}

	public void setSutSmtpPort(int sutSmtpPort) {
		this.sutSmtpPort = sutSmtpPort;
	}

	public int getTttSmtpPort() {
		return tttSmtpPort;
	}

	public void setTttSmtpPort(int tttSmtpPort) {
		this.tttSmtpPort = tttSmtpPort;
	}

	public String getSutEmailAddress() {
		return sutEmailAddress;
	}

	public void setSutEmailAddress(String sutEmailAddress) {
		this.sutEmailAddress = sutEmailAddress;
	}

	public String getTttEmailAddress() {
		return tttEmailAddress;
	}

	public void setTttEmailAddress(String tttEmailAddress) {
		this.tttEmailAddress = tttEmailAddress;
	}

	public String userName() {
		return sutUserName;
	}

	public void userName(String userName) {
		this.sutUserName = userName;
	}

	public String passWord() {
		return sutPassword;
	}

	public void passWord(String passWord) {
		this.sutPassword = passWord;
	}

	public int starTtlsPort() {
		return startTlsPort;
	}

	public void setStarTtlsPort(int starTtlsPort) {
		this.startTlsPort = starTtlsPort;
	}

	public LinkedHashMap<String, byte[]> getAttachments() {
		return attachments;
	}

	public void setAttachments(LinkedHashMap<String, byte[]> attachments) {
		this.attachments = attachments;
	}

	public String getTttUserName() {
		return tttUserName;
	}

	public void setTttUserName(String tttUserName) {
		this.tttUserName = tttUserName;
	}

	public String getTttPassword() {
		return tttPassword;
	}

	public void setTttPassword(String tttPassword) {
		this.tttPassword = tttPassword;
	}

	public String getTttSmtpAddress() {
		return tttSmtpAddress;
	}

	public void setTttSmtpAddress(String tttSmtpAddress) {
		this.tttSmtpAddress = tttSmtpAddress;
	}

	public String getCcdaValidationObjective() {
		return ccdaValidationObjective;
	}

	public void setCcdaValidationObjective(String ccdaValidationObjective) {
		this.ccdaValidationObjective = ccdaValidationObjective;
	}

	public String getCcdaReferenceFilename() {
		return ccdaReferenceFilename;
	}

	public void setCcdaReferenceFilename(String ccdaReferenceFilename) {
		this.ccdaReferenceFilename = ccdaReferenceFilename;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public TestResult getTr() {
		return tr;
	}

	public void setTr(TestResult tr) {
		this.tr = tr;
	}

}