package gov.nist.healthcare.ttt.database.log;

/**
 * Created Jun 19, 2014 2:16:46 PM
 * @author mccaffrey
 */
public class DetailImpl implements DetailInterface {

    private String detailID = null; // TODO: Not needed in the object?  DB only?
    private String logID = null;// TODO: Not needed in the object?  DB only?
    private String partID = null;// TODO: Not needed in the object?  DB only?
    private int counter = 0;
    private String name = null;
    private Status status = null;
    private String dts = null;
    private String found = null;
    private String expected = null;
    private String rfc = null;
    
    public static Status intToStatus(int i) {
        switch(i) {
            case 0:
                return Status.SUCCESS;
            case 1:
                return Status.ERROR;
            case 2:
                return Status.WARNING;
            case 3:
                return Status.INFO;
            default:
                return null;                        
        }
    }
    
    public static int statusToInt(Status s) {
        if(s == null)
            return -1;
        switch(s) {
            case SUCCESS:
                return 0;
            case ERROR:
                return 1;
            case WARNING:
                return 2;
            case INFO:
                return 3;
            default:
                return -1;
        }
    }
    
    /**
     * @return the detailID
     */
    @Override
    public String getDetailID() {
        return detailID;
    }

    /**
     * @param detailID the detailID to set
     */
    @Override
    public void setDetailID(String detailID) {
        this.detailID = detailID;
    }

    /**
     * @return the logID
     */
    @Override
    public String getLogID() {
        return logID;
    }

    /**
     * @param logID the logID to set
     */
    @Override
    public void setLogID(String logID) {
        this.logID = logID;
    }

    /**
     * @return the partID
     */
    @Override
    public String getPartID() {
        return partID;
    }

    /**
     * @param partID the partID to set
     */
    @Override
    public void setPartID(String partID) {
        this.partID = partID;
    }

    /**
     * @return the counter
     */
    @Override
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    @Override
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the dts
     */
    @Override
    public String getDts() {
        return dts;
    }

    /**
     * @param dts the dts to set
     */
    @Override
    public void setDts(String dts) {
        this.dts = dts;
    }

    /**
     * @return the found
     */
    @Override
    public String getFound() {
        return found;
    }

    /**
     * @param found the found to set
     */
    @Override
    public void setFound(String found) {
        this.found = found;
    }

    /**
     * @return the expected
     */
    @Override
    public String getExpected() {
        return expected;
    }

    /**
     * @param expected the expected to set
     */
    @Override
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     * @return the rfc
     */
    @Override
    public String getRfc() {
        return rfc;
    }

    /**
     * @param rfc the rfc to set
     */
    @Override
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }
    
    
    
}
