package gov.nist.healthcare.ttt.database.log;

import java.util.Collection;
import java.util.List;

/**
 * Created Jun 19, 2014 2:13:59 PM
 * @author mccaffrey
 */
public class PartImpl implements PartInterface {

    private String partID = null; // TODO: Not needed in the object?  DB only?
    private String logID = null;// TODO: Not needed in the object?  DB only?
    private String rawMessage = null;
    private String contentType = null;
    private String contentTransferEncoding = null;
    private String contentDisposition = null;
    private Boolean status = null;
    private Collection<PartInterface> children = null;
    private List<DetailInterface> details = null;
    

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
     * @return the rawMessage
     */
    @Override
    public String getRawMessage() {
        return rawMessage;
    }

    /**
     * @param rawMessage the rawMessage to set
     */
    @Override
    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    /**
     * @return the contentType
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the status
     */
    @Override
    public Boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    @Override
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * @return the contentTransferEncoding
     */
    @Override
    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    /**
     * @param contentTransferEncoding the contentTransferEncoding to set
     */
    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    /**
     * @return the contentDisposition
     */
    @Override
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * @param contentDisposition the contentDisposition to set
     */
    @Override
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    /**
     * @return the children
     */
    @Override
    public Collection<PartInterface> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    @Override
    public void setChildren(Collection<PartInterface> children) {
        this.children = children;
    }

    /**
     * @return the details
     */
    @Override
    public List<DetailInterface> getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    @Override
    public void setDetails(List<DetailInterface> details) {
        this.details = details;
    }
    
    
    
}
