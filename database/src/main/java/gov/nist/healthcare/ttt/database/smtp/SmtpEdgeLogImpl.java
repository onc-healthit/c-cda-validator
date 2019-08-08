
package gov.nist.healthcare.ttt.database.smtp;

import java.util.List;

/**
 *
 * @author mccaffrey
 */
public class SmtpEdgeLogImpl implements SmtpEdgeLogInterface {

    private String smtpEdgeLogID;
    private String timestamp;
    private String transaction;
    private String testCaseNumber;
    private boolean criteriaMet;
    private String testRequestsResponse;
    private List<String> attachments;
    
    
    /**
     * @return the smtpEdgeLogID
     */
    @Override
    public String getSmtpEdgeLogID() {
        return smtpEdgeLogID;
    }

    /**
     * @param smtpEdgeLogID the smtpEdgeLogID to set
     */
    @Override
    public void setSmtpEdgeLogID(String smtpEdgeLogID) {
        this.smtpEdgeLogID = smtpEdgeLogID;
    }

    /**
     * @return the timestamp
     */
    @Override
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the testCaseNumber
     */
    @Override
    public String getTestCaseNumber() {
        return testCaseNumber;
    }

    /**
     * @param testCaseNumber the testCaseNumber to set
     */
    @Override
    public void setTestCaseNumber(String testCaseNumber) {
        this.testCaseNumber = testCaseNumber;
    }

    /**
     * @return the criteriaMet
     */
    @Override
    public boolean isCriteriaMet() {
        return criteriaMet;
    }

    /**
     * @param criteriaMet the criteriaMet to set
     */
    @Override
    public void setCriteriaMet(boolean criteriaMet) {
        this.criteriaMet = criteriaMet;
    }

    /**
     * @return the testRequestsResponse
     */
    @Override
    public String getTestRequestsResponse() {
        return testRequestsResponse;
    }

    /**
     * @param testRequestsResponse the testRequestsResponse to set
     */
    @Override
    public void setTestRequestsResponse(String testRequestsResponse) {
        this.testRequestsResponse = testRequestsResponse;
    }

    /**
     * @return the attachments
     */
    @Override
    public List<String> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    @Override
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the transaction
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

}
