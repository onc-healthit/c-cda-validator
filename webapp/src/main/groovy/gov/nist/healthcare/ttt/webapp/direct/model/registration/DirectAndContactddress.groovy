package gov.nist.healthcare.ttt.webapp.direct.model.registration;

public class DirectAndContactddress {

    private String directAddress;
    private String contactAddress;
    
    public DirectAndContactddress() {
    	this.directAddress = "";
    	this.contactAddress = "";
    }

    public DirectAndContactddress(String directAddress, String contactAddress) {
        this.directAddress = directAddress;
        this.contactAddress = contactAddress;
    }

	public String getDirectAddress() {
		return directAddress;
	}

	public void setDirectAddress(String directAddress) {
		this.directAddress = directAddress;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

}
