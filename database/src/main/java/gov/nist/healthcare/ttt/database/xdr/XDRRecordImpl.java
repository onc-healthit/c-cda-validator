package gov.nist.healthcare.ttt.database.xdr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created Oct 10, 2014 1:55:29 PM
 *
 * @author mccaffrey
 */
public class XDRRecordImpl implements XDRRecordInterface {

    private String xdrRecordDatabaseId = null;
    private String username = null;
    private String testCaseNumber = null;
    private String timestamp = null;
    private Status criteriaMet = null;
    private String mDHTValidationReport = null;
    private List<XDRTestStepInterface> testSteps = null;

    public XDRRecordImpl() {
        this.setTestSteps(new ArrayList<XDRTestStepInterface>());
    }

    /**
     * @return the xdrRecordDatabaseId
     */
    public String getXdrRecordDatabaseId() {
        return xdrRecordDatabaseId;
    }

    /**
     * @param xdrRecordID the xdrRecordDatabaseId to set
     */
    public void setXdrRecordDatabaseId(String xdrRecordID) {
        this.xdrRecordDatabaseId = xdrRecordID;
    }

    /**
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
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
     * @return the criteriaMet
     */
    @Override
    public Status getCriteriaMet() {
        return criteriaMet;
    }

    /**
     * @param status the criteriaMet to set
     */
    @Override
    public void setStatus(Status status) {
        this.criteriaMet = status;
    }

    /**
     * @return the testSteps
     */
    @Override
    public List<XDRTestStepInterface> getTestSteps() {
        return testSteps;
    }

    /**
     * @param testSteps the testSteps to set
     */
    @Override
    public void setTestSteps(List<XDRTestStepInterface> testSteps) {
        this.testSteps = testSteps;
    }

    /**
     * @return the mDHTValidationReport
     */
    public String getMDHTValidationReport() {
        return mDHTValidationReport;
    }

    /**
     * @param mDHTValidationReport the mDHTValidationReport to set
     */
    public void setMDHTValidationReport(String mDHTValidationReport) {
        this.mDHTValidationReport = mDHTValidationReport;
    }

    @Override
    public Status getStatus() {
        return criteriaMet;
    }

}
