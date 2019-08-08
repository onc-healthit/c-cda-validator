
package gov.nist.healthcare.ttt.database.log;

import java.util.Collection;

/**
 * Created Jul 25, 2014 11:11:16 AM
 * @author mccaffrey
 */
public interface LogInterface {

    public enum Status {
        SUCCESS,
        ERROR,
        WAITING,
        TIMEOUT,
        MDN_RECEIVED
    }
    
    
    /**
     * @return the contentDisposition
     */
    String getContentDisposition();

    /**
     * @return the contentType
     */
    String getContentType();

    /**
     * @return the fromLine
     */
    Collection<String> getFromLine();

    /**
     * @return the incoming
     */
    Boolean getIncoming();

    /**
     * @return the logID
     */
    String getLogID();

    /**
     * @return the messageId
     */
    String getMessageId();

    String getOriginalMessageId();
    
    /**
     * @return the mimeVersion
     */
    String getMimeVersion();

    /**
     * @return the origDate
     */
    String getOrigDate();

    /**
     * @return the recieved
     */
    Collection<String> getReceived();

    /**
     * @return the replyTo
     */
    Collection<String> getReplyTo();

    /**
     * @return the subject
     */
    String getSubject();

    /**
     * @return the status
     */
    Status getStatus();

    /**
     * @return the timestamp
     */
    String getTimestamp();

    /**
     * @return the toLine
     */
    Collection<String> getToLine();
    
    Boolean isMdn();

    /**
     * @param contentDisposition the contentDisposition to set
     */
    void setContentDisposition(String contentDisposition);

    /**
     * @param contentType the contentType to set
     */
    void setContentType(String contentType);

    /**
     * @param fromLine the fromLine to set
     */
    void setFromLine(Collection<String> fromLine);

    /**
     * @param incoming the incoming to set
     */
    void setIncoming(Boolean incoming);

    /**
     * @param logID the logID to set
     */
    void setLogID(String logID);

    /**
     * @param messageId the messageId to set
     */
    void setMessageId(String messageId);

    void setOriginalMessageId(String originalMessageId);
    
    /**
     * @param mimeVersion the mimeVersion to set
     */
    void setMimeVersion(String mimeVersion);

    /**
     * @param origDate the origDate to set
     */
    void setOrigDate(String origDate);

    /**
     * @param recieved the recieved to set
     */
    void setReceived(Collection<String> received);

    /**
     * @param replyTo the replyTo to set
     */
    void setReplyTo(Collection<String> replyTo);

    /**
     * @param subject the subject to set
     */
    void setSubject(String subject);

    /**
     * @param status the status to set
     */
    void setStatus(Status success);

    /**
     * @param timestamp the timestamp to set
     */
    void setTimestamp(String timestamp);

    /**
     * @param toLine the toLine to set
     */
    void setToLine(Collection<String> toLine);
    
    void setMdn(Boolean mdn);
    
    
}
