package gov.nist.healthcare.ttt.webapp.direct.direcForXdr;

class DirectMessageSenderForXdrNoLookUp extends DirectMessageSenderForXdr {

	public DirectMessageInfoForXdr sendDirectWithCCDAForXdrNoDNSLookUp(String sutSmtpAddress, int port, String encryptionCertPath) throws Exception {
		setEncryptionCertPath(encryptionCertPath)
		return sendDirectWithCCDAForXdr(sutSmtpAddress, port)
	}
	
	public DirectMessageInfoForXdr sendDirectWithXDMForXdrNoDNSLookUp(String sutSmtpAddress, int port, String encryptionCertPath) throws Exception {
		setEncryptionCertPath(encryptionCertPath)
		return sendDirectWithXDMForXdr(sutSmtpAddress, port)
	}

	public void setEncryptionCertPath(String encryptionCertPath) {
		this.setDnsLookup(false)
		super.setEncryptionCertPath(encryptionCertPath)
	}
}
