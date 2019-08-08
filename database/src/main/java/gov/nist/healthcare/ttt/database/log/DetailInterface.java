package gov.nist.healthcare.ttt.database.log;

/**
 * Created Jul 25, 2014 11:08:07 AM
 * @author mccaffrey
 */
public interface DetailInterface {

    public enum Status {        
        SUCCESS,
        ERROR,
        WARNING,
        INFO                
    }
    
    /**
     * @return the counter
     */
    int getCounter();

    /**
     * @return the detailID
     */
    String getDetailID();

    /**
     * @return the dts
     */
    String getDts();

    /**
     * @return the expected
     */
    String getExpected();

    /**
     * @return the found
     */
    String getFound();

    /**
     * @return the logID
     */
    String getLogID();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the partID
     */
    String getPartID();

    /**
     * @return the rfc
     */
    String getRfc();

    /**
     * @return the status
     */
    Status getStatus();

    /**
     * @param counter the counter to set
     */
    void setCounter(int counter);

    /**
     * @param detailID the detailID to set
     */
    void setDetailID(String detailID);

    /**
     * @param dts the dts to set
     */
    void setDts(String dts);

    /**
     * @param expected the expected to set
     */
    void setExpected(String expected);

    /**
     * @param found the found to set
     */
    void setFound(String found);

    /**
     * @param logID the logID to set
     */
    void setLogID(String logID);

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param partID the partID to set
     */
    void setPartID(String partID);

    /**
     * @param rfc the rfc to set
     */
    void setRfc(String rfc);

    /**
     * @param status the status to set
     */
    void setStatus(Status status);
    
}
