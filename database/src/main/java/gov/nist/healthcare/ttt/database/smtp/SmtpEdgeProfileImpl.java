
package gov.nist.healthcare.ttt.database.smtp;

/**
 *
 * @author mccaffrey
 */
public class SmtpEdgeProfileImpl implements SmtpEdgeProfileInterface {

    private String smtpEdgeProfileID;
    private String profileName;
    private String sutSMTPAddress;
    private String sutEmailAddress;
    private String sutUsername;
    private String sutPassword;
    private String username;
    private boolean useTLS;

    /**
     * @return the smtpEdgeProfileID
     */
    @Override
    public String getSmtpEdgeProfileID() {
        return smtpEdgeProfileID;
    }

    /**
     * @param smtpEdgeProfileID the smtpEdgeProfileID to set
     */
    @Override
    public void setSmtpEdgeProfileID(String smtpEdgeProfileID) {
        this.smtpEdgeProfileID = smtpEdgeProfileID;
    }

    /**
     * @return the profileName
     */
    @Override
    public String getProfileName() {
        return profileName;
    }

    /**
     * @param profileName the profileName to set
     */
    @Override
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    /**
     * @return the sutSMTPAddress
     */
    @Override
    public String getSutSMTPAddress() {
        return sutSMTPAddress;
    }

    /**
     * @param sutSMTPAddress the sutSMTPAddress to set
     */
    @Override
    public void setSutSMTPAddress(String sutSMTPAddress) {
        this.sutSMTPAddress = sutSMTPAddress;
    }

    /**
     * @return the sutEmailAddress
     */
    @Override
    public String getSutEmailAddress() {
        return sutEmailAddress;
    }

    /**
     * @param sutEmailAddress the sutEmailAddress to set
     */
    @Override
    public void setSutEmailAddress(String sutEmailAddress) {
        this.sutEmailAddress = sutEmailAddress;
    }

    /**
     * @return the sutUsername
     */
    @Override
    public String getSutUsername() {
        return sutUsername;
    }

    /**
     * @param sutUsername the sutUsername to set
     */
    @Override
    public void setSutUsername(String sutUsername) {
        this.sutUsername = sutUsername;
    }

    /**
     * @return the sutPassword
     */
    @Override
    public String getSutPassword() {
        return sutPassword;
    }

    /**
     * @param sutPassword the sutPassword to set
     */
    @Override
    public void setSutPassword(String sutPassword) {
        this.sutPassword = sutPassword;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the username
     */
    public boolean getUseTLS() {
        return useTLS;
    }

    /**
     * @param username the username to set
     */
    public void setUseTLS(boolean useTLS) {
        this.useTLS = useTLS;
    }
}
