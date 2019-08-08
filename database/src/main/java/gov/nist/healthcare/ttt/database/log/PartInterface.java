
package gov.nist.healthcare.ttt.database.log;

import java.util.Collection;
import java.util.List;

/**
 * Created Jul 25, 2014 11:07:13 AM
 * @author mccaffrey
 */
public interface PartInterface {

    /**
     * @return the children
     */
    Collection<PartInterface> getChildren();

    /**
     * @return the contentDisposition
     */
    String getContentDisposition();

    /**
     * @return the contentTransferEncoding
     */
    String getContentTransferEncoding();

    /**
     * @return the contentType
     */
    String getContentType();

    /**
     * @return the details
     */
    List<DetailInterface> getDetails();

    /**
     * @return the logID
     */
    String getLogID();

    /**
     * @return the partID
     */
    String getPartID();

    /**
     * @return the rawMessage
     */
    String getRawMessage();

    /**
     * @return the status
     */
    Boolean isStatus();

    /**
     * @param children the children to set
     */
    void setChildren(Collection<PartInterface> children);

    /**
     * @param contentDisposition the contentDisposition to set
     */
    void setContentDisposition(String contentDisposition);

    /**
     * @param contentTransferEncoding the contentTransferEncoding to set
     */
    void setContentTransferEncoding(String contentTransferEncoding);

    /**
     * @param contentType the contentType to set
     */
    void setContentType(String contentType);

    /**
     * @param details the details to set
     */
    void setDetails(List<DetailInterface> details);

    /**
     * @param logID the logID to set
     */
    void setLogID(String logID);

    /**
     * @param partID the partID to set
     */
    void setPartID(String partID);

    /**
     * @param rawMessage the rawMessage to set
     */
    void setRawMessage(String rawMessage);

    /**
     * @param status the status to set
     */
    void setStatus(Boolean status);
    
}
