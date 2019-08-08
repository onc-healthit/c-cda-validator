

package gov.nist.healthcare.ttt.database.smtp;

/**
 *
 * @author mccaffrey
 */
public interface SmtpEdgeProfileInterface {

    /**
     * @return the profileName
     */
    String getProfileName();

    /**
     * @return the smtpEdgeProfileID
     */
    String getSmtpEdgeProfileID();

    /**
     * @return the sutEmailAddress
     */
    String getSutEmailAddress();

    /**
     * @return the sutPassword
     */
    String getSutPassword();

    /**
     * @return the sutSMTPAddress
     */
    String getSutSMTPAddress();

    /**
     * @return the sutUsername
     */
    String getSutUsername();

    String getUsername();

    boolean getUseTLS();


    /**
     * @param profileName the profileName to set
     */
    void setProfileName(String profileName);

    /**
     * @param smtpEdgeProfileID the smtpEdgeProfileID to set
     */
    void setSmtpEdgeProfileID(String smtpEdgeProfileID);

    /**
     * @param sutEmailAddress the sutEmailAddress to set
     */
    void setSutEmailAddress(String sutEmailAddress);

    /**
     * @param sutPassword the sutPassword to set
     */
    void setSutPassword(String sutPassword);

    /**
     * @param sutSMTPAddress the sutSMTPAddress to set
     */
    void setSutSMTPAddress(String sutSMTPAddress);

    /**
     * @param sutUsername the sutUsername to set
     */
    void setSutUsername(String sutUsername);

    void setUsername(String username);

    void setUseTLS(boolean useTls);
}
