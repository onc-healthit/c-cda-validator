package gov.nist.healthcare.ttt.database.log;

import java.util.Collection;

/**
 * Created Jun 17, 2014 3:57:08 PM
 *
 * @author mccaffrey
 */
public class LogImpl implements LogInterface {

    private String logID = null;  // TODO: Not needed in the object?  DB only?
    private Boolean incoming = null;
    private String timestamp = null;
    private Status status = null;
    private String origDate = null;
    private Collection<String> fromLine = null;
    private Collection<String> toLine = null;
    private String messageId = null;
    private String originalMessageId = null;
    private String mimeVersion = null;
    private Collection<String> received = null;
    private Collection<String> replyTo = null;
    private String subject = null;
    private String contentType = null;
    private String contentDisposition = null;
    private Boolean mdn = null;
    
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
     * @return the origDate
     */
    @Override
    public String getOrigDate() {
        return origDate;
    }

    /**
     * @param origDate the origDate to set
     */
    @Override
    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    /**
     * @return the fromLine
     */
    @Override
    public Collection<String> getFromLine() {
        return fromLine;
    }

    /**
     * @param fromLine the fromLine to set
     */
    @Override
    public void setFromLine(Collection<String> fromLine) {
        this.fromLine = fromLine;
    }

    /**
     * @return the toLine
     */
    @Override
    public Collection<String> getToLine() {
        return toLine;
    }

    /**
     * @param toLine the toLine to set
     */
    @Override
    public void setToLine(Collection<String> toLine) {
        this.toLine = toLine;
    }

    /**
     * @return the messageId
     */
    @Override
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    @Override
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the mimeVersion
     */
    @Override
    public String getMimeVersion() {
        return mimeVersion;
    }

    /**
     * @param mimeVersion the mimeVersion to set
     */
    @Override
    public void setMimeVersion(String mimeVersion) {
        this.mimeVersion = mimeVersion;
    }

    /**
     * @return the recieved
     */
    @Override
    public Collection<String> getReceived() {
        return received;
    }

    /**
     * @param received the recieved to set
     */
    @Override
    public void setReceived(Collection<String> received) {
        this.received = received;
    }

    /**
     * @return the replyTo
     */
    @Override
    public Collection<String> getReplyTo() {
        return replyTo;
    }

    /**
     * @param replyTo the replyTo to set
     */
    @Override
    public void setReplyTo(Collection<String> replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * @return the subject
     */
    @Override
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    @Override
    public void setSubject(String subject) {
        this.subject = subject;
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
     * @return the incoming
     */
    @Override
    public Boolean getIncoming() {
        return incoming;
    }

    /**
     * @param incoming the incoming to set
     */
    @Override
    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
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

    public static Status intToStatus(int i) {
        switch (i) {
            case 1:
                return Status.SUCCESS;
            case 0:
                return Status.ERROR;
            case -1:
                return Status.WAITING;
            case 2:
                return Status.MDN_RECEIVED;
            case -2:
                return Status.TIMEOUT;
            default:
                return null;
        }
    }

    public static int statusToInt(Status status) {
        if(status == null)
            return -1;
        switch (status) {
            case SUCCESS:
                return 1;
            case ERROR:
                return 0;
            case WAITING:
                return -1;
            case MDN_RECEIVED:
                return 2;
            case TIMEOUT:
                return -2;
            default:
                // undefined
                return 5;
                                
        }
    }

    /**
     * @return the mdn
     */
    @Override
    public Boolean isMdn() {
        return mdn;
    }

    /**
     * @param mdn the mdn to set
     */
    @Override
    public void setMdn(Boolean mdn) {
        this.mdn = mdn;
    }

    /**
     * @return the originalMessageId
     */
    @Override
    public String getOriginalMessageId() {
        return originalMessageId;
    }

    /**
     * @param originalMessageId the originalMessageId to set
     */
    @Override
    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }
    
}
