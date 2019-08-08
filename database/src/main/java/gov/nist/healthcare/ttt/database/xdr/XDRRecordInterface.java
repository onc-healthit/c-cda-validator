
package gov.nist.healthcare.ttt.database.xdr;


import java.util.List;

/**
 * Created Oct 17, 2014 1:47:19 PM
 * @author mccaffrey
 */
public interface XDRRecordInterface {

    String getXdrRecordDatabaseId();
    
    /**
     * @return the testCaseNumber
     */
    String getTestCaseNumber();

    /**
     * @return the testSteps
     */
    List<XDRTestStepInterface> getTestSteps();

    /**
     * @return the timestamp
     */
    String getTimestamp();

    /**
     * @return the username
     */
    String getUsername();

    /**
     * @return the criteriaMet
     */
    Status getCriteriaMet();
    
    String getMDHTValidationReport();
            
    
    void setXdrRecordDatabaseId(String databaseId);
    
    /**
     * @param status the status to set
     */
    void setStatus(Status status);
    
    Status getStatus();

    /**
     * @param testCaseNumber the testCaseNumber to set
     */
    void setTestCaseNumber(String testCaseNumber);

    /**
     * @param testSteps the testSteps to set
     */
    void setTestSteps(List<XDRTestStepInterface> testSteps);

    /**
     * @param timestamp the timestamp to set
     */
    void setTimestamp(String timestamp);

    /**
     * @param username the username to set
     */
    void setUsername(String username);

    void setMDHTValidationReport(String validationReport);
}
