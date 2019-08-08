package gov.nist.healthcare.ttt.database.smtp;

import java.util.List;

/**
 *
 * @author mccaffrey
 */
public interface SmtpEdgeLogInterface {

    /**
     * @return the smtpEdgeLogID
     */
    String getSmtpEdgeLogID();

    /**
     * @return the testCaseNumber
     */
    String getTestCaseNumber();

    /**
     * @return the testRequestsResponse
     */
    String getTestRequestsResponse();

    /**
     * @return the timestamp
     */
    String getTimestamp();

    /**
     * @return the username
     */
   // String getUsername();

    /**
     * @return the criteriaMet
     */
    boolean isCriteriaMet();

    /**
     * @param criteriaMet the criteriaMet to set
     */
    void setCriteriaMet(boolean criteriaMet);

    /**
     * @param smtpEdgeLogID the smtpEdgeLogID to set
     */
    void setSmtpEdgeLogID(String smtpEdgeLogID);

    /**
     * @param testCaseNumber the testCaseNumber to set
     */
    void setTestCaseNumber(String testCaseNumber);

    /**
     * @param testRequestsResponse the testRequestsResponse to set
     */
    void setTestRequestsResponse(String testRequestsResponse);

    /**
     * @param timestamp the timestamp to set
     */
    void setTimestamp(String timestamp);

    /**
     * @param username the username to set
     */
   // void setUsername(String username);

    List<String> getAttachments();

    /**
     * @param attachments the attachments to set
     */
    void setAttachments(List<String> attachments);

    String getTransaction();
    
    void setTransaction(String transaction);
    
}
