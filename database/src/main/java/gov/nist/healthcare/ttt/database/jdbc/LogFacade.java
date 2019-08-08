package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.database.log.CCDAValidationReportImpl;
import gov.nist.healthcare.ttt.database.log.CCDAValidationReportInterface;
import gov.nist.healthcare.ttt.database.log.DetailImpl;
import gov.nist.healthcare.ttt.database.log.DetailInterface;
import gov.nist.healthcare.ttt.database.log.LogImpl;
import gov.nist.healthcare.ttt.database.log.LogInterface;
import gov.nist.healthcare.ttt.database.log.LogInterface.Status;
import gov.nist.healthcare.ttt.database.log.PartImpl;
import gov.nist.healthcare.ttt.database.log.PartInterface;
import gov.nist.healthcare.ttt.misc.Configuration;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created Jul 10, 2014 4:07:24 PM
 *
 * @author mccaffrey
 */
public class LogFacade extends DatabaseFacade {

    public final static String LOG_TABLE = "Log";
    public final static String LOG_LOGID = "LogID";
    public final static String LOG_INCOMING = "Incoming";
    public final static String LOG_TIMESTAMP = "Timestamp";
    public final static String LOG_STATUS = "Status";
    public final static String LOG_MDN = "Mdn";
    public final static String LOG_ORIGDATE = "OrigDate";
    public final static String LOG_MESSAGEID = "MessageId";
    public final static String LOG_ORIGINALMESSAGEID = "OriginalMessageId";
    public final static String LOG_MIMEVERSION = "MIMEVersion";
    public final static String LOG_SUBJECT = "Subject";
    public final static String LOG_CONTENTTYPE = "ContentType";
    public final static String LOG_CONTENTDISPOSITION = "ContentDisposition";

    public final static String FROMLINE_TABLE = "FromLine";
    public final static String FROMLINE_FROMLINEID = "FromLineID";
    public final static String FROMLINE_FROMLINE = "FromLine";

    public final static String TOLINE_TABLE = "ToLine";
    public final static String TOLINE_TOLINEID = "ToLineID";
    public final static String TOLINE_TOLINE = "ToLine";

    public final static String RECEIVED_TABLE = "Received";
    public final static String RECEIVED_RECEIVEDID = "ReceivedId";
    public final static String RECEIVED_RECEIVED = "Received";

    public final static String REPLYTO_TABLE = "ReplyTo";
    public final static String REPLYTO_REPLYTOID = "ReplyToID";
    public final static String REPLYTO_REPLYTO = "ReplyTo";

    public final static String PART_TABLE = "Part";
    public final static String PART_PARTID = "PartID";
    public final static String PART_LOGID = "LogID";
    public final static String PART_RAWMESSAGE = "RawMessage";
    public final static String PART_CONTENTTYPE = "ContentType";
    public final static String PART_CONTENTTRANSFERENCODING = "ContentTransferEncoding";
    public final static String PART_CONTENTDISPOSITION = "ContentDisposition";
    public final static String PART_STATUS = "Status";

    public final static String CCDAVALIDATIONREPORT_TABLE = "CCDAValidationReport";
    public final static String CCDAVALIDATIONREPORT_CCDAVALIDATIONREPORTID = "CCDAValidationReportID";
    public final static String CCDAVALIDATIONREPORT_FILENAME = "Filename";
    public final static String CCDAVALIDATIONREPORT_VALIDATIONREPORT = "ValidationReport";

    public final static String PARTRELATIONSHIP_TABLE = "PartRelationship";
    public final static String PARTRELATIONSHIP_PARTRELATIONSHIPID = "PartRelationshipID";
    public final static String PARTRELATIONSHIP_PARENTID = "ParentID";
    public final static String PARTRELATIONSHIP_CHILDID = "ChildID";

    public final static String DETAIL_TABLE = "Detail";
    public final static String DETAIL_DETAILID = "DetailID";
    public final static String DETAIL_LOGID = "LogID";
    public final static String DETAIL_PARTID = "PartID";
    public final static String DETAIL_COUNTER = "Counter";
    public final static String DETAIL_NAME = "Name";
    public final static String DETAIL_STATUS = "Status";
    public final static String DETAIL_DTS = "DTS";
    public final static String DETAIL_FOUND = "Found";
    public final static String DETAIL_EXPECTED = "Expected";
    public final static String DETAIL_RFC = "RFC";

    protected static LogFacade logInstance = null;

    public LogFacade(Configuration config) throws DatabaseException {
        super(config);
    }
    /*
     static public LogFacade getInstance(Configuration config) throws DatabaseException {
     if (logInstance == null) {
     logInstance = new LogFacade(config);
     }
     return logInstance;
     }

     static public LogFacade getNewInstance(Configuration config) throws DatabaseException {
     if (logInstance != null) {
     logInstance.closeConnection();
     }
     logInstance = new LogFacade(config);
     return logInstance;
     }
     */

    public String addNewLog(LogInterface log) throws DatabaseException {

        Boolean incoming = log.getIncoming();
        if (incoming == null) {
            throw new DatabaseException("Incoming / outgoing message type MUST be set.");
        }
        String timestamp = log.getTimestamp();
        if (timestamp == null || timestamp.trim().equalsIgnoreCase("")) {
            throw new DatabaseException("Timestamp MUST be set.");
        }

        String logID = UUID.randomUUID().toString();

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + LOG_TABLE);
        sql.append(" ( ");
        sql.append(LOG_LOGID);
        sql.append(", ");
        sql.append(LOG_INCOMING);
        sql.append(", ");
        sql.append(LOG_MDN);
        sql.append(", ");
        sql.append(LOG_TIMESTAMP);
        sql.append(", ");
        sql.append(LOG_STATUS);
        sql.append(", ");
        sql.append(LOG_ORIGDATE);
        sql.append(", ");
        sql.append(LOG_MESSAGEID);
        sql.append(", ");
        sql.append(LOG_ORIGINALMESSAGEID);
        sql.append(", ");
        sql.append(LOG_MIMEVERSION);
        sql.append(", ");
        sql.append(LOG_SUBJECT);
        sql.append(", ");
        sql.append(LOG_CONTENTTYPE);
        sql.append(", ");
        sql.append(LOG_CONTENTDISPOSITION);
        sql.append(") VALUES ('");
        sql.append(logID);
        sql.append("' , '");
        if (log.getIncoming()) {
            sql.append("1");
        } else {
            sql.append("0");
        }
        sql.append("' , '");
        if (log.isMdn() != null && log.isMdn()) {
            sql.append("1");
        } else {
            sql.append("0");
        }
        sql.append("' , '");
        sql.append(log.getTimestamp());
        sql.append("' , '");

        sql.append(LogImpl.statusToInt(log.getStatus()));

        /*
         if (log.getSuccess() != null && log.getSuccess()) {
         sql.append("1");
         } else {
         sql.append("0");
         }*/
        sql.append("' , '");
        if (log.getOrigDate() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getOrigDate()));
        }
        sql.append("' , '");
        if (log.getMessageId() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getMessageId()));
        }
        sql.append("' , '");
        if (log.getOriginalMessageId() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getOriginalMessageId()));
        }

        sql.append("' , '");
        if (log.getMimeVersion() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getMimeVersion()));
        }
        sql.append("' , '");
        if (log.getSubject() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getSubject()));
        }
        sql.append("' , '");
        if (log.getContentType() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getContentType()));
        }
        sql.append("' , '");
        if (log.getContentDisposition() != null) {
            sql.append(DatabaseConnection.makeSafe(log.getContentDisposition()));
        }
        sql.append("');");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        if (log.getFromLine() != null) {
            this.addNewFromLines(logID, log.getFromLine());
        }
        if (log.getToLine() != null) {
            this.addNewToLines(logID, log.getToLine());
        }
        if (log.getReceived() != null) {
            this.addNewReceived(logID, log.getReceived());
        }
        if (log.getReplyTo() != null) {
            this.addNewReplyTo(logID, log.getReplyTo());
        }

        return logID;
    }

    public Collection<String> addNewFromLines(String logID, Collection<String> fromLines) throws DatabaseException {

        Collection<String> fromLineIds = new ArrayList<String>();
        if (fromLines == null) {
            return fromLineIds;
        }
        Iterator<String> it = fromLines.iterator();
        while (it.hasNext()) {

            String fromLineId = UUID.randomUUID().toString();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO " + FROMLINE_TABLE + ' ');
            sql.append("( ");
            sql.append(FROMLINE_FROMLINEID);
            sql.append(", ");
            sql.append(LOG_LOGID);
            sql.append(", ");
            sql.append(FROMLINE_FROMLINE);
            sql.append(") VALUES (");
            sql.append("'");
            sql.append(fromLineId);
            sql.append("','");
            sql.append(logID);
            sql.append("','");
            sql.append(it.next());
            sql.append("');");

            try {
                this.getConnection().executeUpdate(sql.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException(ex.getMessage());
            }
            fromLineIds.add(fromLineId);
        }
        return fromLineIds;
    }

    public Collection<String> getFromLines(String logID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + FROMLINE_FROMLINE + " ");
        sql.append("FROM " + FROMLINE_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logID + "';");
        Collection<String> fromLines = new ArrayList<String>();
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                fromLines.add(result.getString(FROMLINE_FROMLINE));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return fromLines;
    }

    public Collection<String> addNewToLines(String logID, Collection<String> toLines) throws DatabaseException {

        Collection<String> toLineIds = new ArrayList<String>();
        if (toLines == null) {
            return toLineIds;
        }
        Iterator<String> it = toLines.iterator();
        while (it.hasNext()) {

            String toLineId = UUID.randomUUID().toString();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO " + TOLINE_TABLE + ' ');
            sql.append("( ");
            sql.append(TOLINE_TOLINEID);
            sql.append(", ");
            sql.append(LOG_LOGID);
            sql.append(", ");
            sql.append(TOLINE_TOLINE);
            sql.append(") VALUES (");
            sql.append("'");
            sql.append(toLineId);
            sql.append("','");
            sql.append(logID);
            sql.append("','");
            sql.append(it.next());
            sql.append("');");

            try {
                this.getConnection().executeUpdate(sql.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException(ex.getMessage());
            }

            toLineIds.add(toLineId);
        }

        return toLineIds;
    }

    public Collection<String> getToLines(String logID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + TOLINE_TOLINE + " ");
        sql.append("FROM " + TOLINE_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logID + "';");
        Collection<String> toLines = new ArrayList<String>();
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                toLines.add(result.getString(TOLINE_TOLINE));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return toLines;
    }

    public Collection<String> addNewReceived(String logID, Collection<String> received) throws DatabaseException {

        Collection<String> receivedIds = new ArrayList<String>();
        if (received == null) {
            return receivedIds;
        }
        Iterator<String> it = received.iterator();
        while (it.hasNext()) {

            String receivedId = UUID.randomUUID().toString();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO " + RECEIVED_TABLE + ' ');
            sql.append("( ");
            sql.append(RECEIVED_RECEIVEDID);
            sql.append(", ");
            sql.append(LOG_LOGID);
            sql.append(", ");
            sql.append(RECEIVED_RECEIVED);
            sql.append(") VALUES (");
            sql.append("'");
            sql.append(receivedId);
            sql.append("','");
            sql.append(logID);
            sql.append("','");
            sql.append(it.next());
            sql.append("');");

            try {
                this.getConnection().executeUpdate(sql.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException(ex.getMessage());
            }

            receivedIds.add(receivedId);
        }

        return receivedIds;
    }

    public Collection<String> getReceived(String logID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + RECEIVED_RECEIVED + " ");
        sql.append("FROM " + RECEIVED_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logID + "';");
        Collection<String> receiveds = new ArrayList<String>();
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                receiveds.add(result.getString(RECEIVED_RECEIVED));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return receiveds;
    }

    public Collection<String> addNewReplyTo(String logID, Collection<String> replyTo) throws DatabaseException {

        Collection<String> replyToIds = new ArrayList<String>();
        if (replyTo == null) {
            return replyToIds;
        }
        Iterator<String> it = replyTo.iterator();
        while (it.hasNext()) {

            String replyToId = UUID.randomUUID().toString();
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO " + REPLYTO_TABLE + ' ');
            sql.append("( ");
            sql.append(REPLYTO_REPLYTOID);
            sql.append(", ");
            sql.append(LOG_LOGID);
            sql.append(", ");
            sql.append(REPLYTO_REPLYTO);
            sql.append(") VALUES (");
            sql.append("'");
            sql.append(replyToId);
            sql.append("','");
            sql.append(logID);
            sql.append("','");
            sql.append(it.next());
            sql.append("');");

            try {
                this.getConnection().executeUpdate(sql.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException(ex.getMessage());
            }

            replyToIds.add(replyToId);
        }

        return replyToIds;
    }

    public Collection<String> getReplyTo(String logID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + REPLYTO_REPLYTO + " ");
        sql.append("FROM " + REPLYTO_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logID + "';");
        Collection<String> replyTos = new ArrayList<String>();
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                replyTos.add(result.getString(REPLYTO_REPLYTO));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return replyTos;
    }

    /*
     public String addNewPart(String messageId, PartImpl part) {

     }
     */
    public Collection<LogInterface> getIncomingByFromLine(String from) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT lg." + LOG_LOGID + ' ');
        sql.append("FROM " + LOG_TABLE + " lg, ");
        sql.append(' ' + FROMLINE_TABLE + " fl ");
        sql.append("WHERE fl." + FROMLINE_FROMLINE + " = '" + DatabaseConnection.makeSafe(from) + "' ");
        sql.append("AND lg." + LOG_INCOMING + " = '1' ");
        sql.append("AND lg." + LOG_LOGID + " =  fl." + LOG_LOGID + ';');

        ResultSet result = null;
        Collection<LogInterface> logs = new ArrayList<LogInterface>();
        Collection<String> logIds = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                String logId = result.getString(LOG_LOGID);
                logIds.add(logId);
            }

            Iterator<String> it = logIds.iterator();

            while (it.hasNext()) {
                LogInterface log = this.getLogByLogId(it.next());
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return logs;
    }

    public Collection<LogInterface> getOutgoingByToLine(String to) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT lg. " + LOG_LOGID + " ");
        sql.append("FROM " + LOG_TABLE + " lg, ");
        sql.append(' ' + TOLINE_TABLE + " tl ");
        sql.append("WHERE tl." + TOLINE_TOLINE + " = '" + DatabaseConnection.makeSafe(to) + "' ");
        sql.append("AND lg." + LOG_INCOMING + " = '0' ");
        sql.append("AND lg." + LOG_LOGID + " = tl." + LOG_LOGID + ';');

        ResultSet result = null;
        Collection<LogInterface> logs = new ArrayList<LogInterface>();
        Collection<String> logIds = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                String logId = result.getString(LOG_LOGID);
                logIds.add(logId);
            }

            Iterator<String> it = logIds.iterator();

            while (it.hasNext()) {
                LogInterface log = this.getLogByLogId(it.next());
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return logs;
    }

    private LogInterface getLogByLogId(String logId) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + LOG_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logId + "';");

        ResultSet result = null;
        LogInterface log = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                log = this.convertDatabaseRecordToLog(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return log;

    }

    public LogInterface getLogByMessageId(String messageId) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + LOG_TABLE + ' ');
        sql.append("WHERE " + LOG_MESSAGEID + " = '" + messageId + "';");

        ResultSet result = null;
        LogInterface log = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                log = this.convertDatabaseRecordToLog(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return log;
    }

    public LogInterface getLogByOriginalMessageId(String originalMessageId) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + LOG_TABLE + ' ');
        sql.append("WHERE " + LOG_ORIGINALMESSAGEID + " = '" + originalMessageId + "';");

        ResultSet result = null;
        LogInterface log = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                log = this.convertDatabaseRecordToLog(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return log;
    }

    public String getLogIDByMessageId(String messageId) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + LOG_LOGID + ' ');
        sql.append("FROM " + LOG_TABLE + ' ');
        sql.append("WHERE " + LOG_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "';");

        String logID = null;
        ResultSet result = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                logID = result.getString(LOG_LOGID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return logID;
    }

    public String addNewCCDAValidationReport(String messageId, CCDAValidationReportInterface validationReport) throws DatabaseException {

        String logID = this.getLogIDByMessageId(messageId);
        if (logID == null) {
            return null;
        }

        // Because of JSON escape issues with double quote character.
        String validationReportString = validationReport.getValidationReport();
        validationReportString = validationReportString.replace("\\\"", "&quot;");

        String ccdaValidationReportID = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + CCDAVALIDATIONREPORT_TABLE + ' ');
        sql.append("(" + CCDAVALIDATIONREPORT_CCDAVALIDATIONREPORTID);
        sql.append(", ");
        sql.append(LOG_LOGID);
        sql.append(", ");
        sql.append(CCDAVALIDATIONREPORT_FILENAME);
        sql.append(", ");
        sql.append(CCDAVALIDATIONREPORT_VALIDATIONREPORT);
        sql.append(") VALUES ('");
        sql.append(ccdaValidationReportID);
        sql.append("', '");
        sql.append(logID);
        sql.append("', '");
        sql.append(DatabaseConnection.makeSafe(validationReport.getFilename()));
        sql.append("', '");
        sql.append(DatabaseConnection.makeSafe(validationReportString));
        sql.append("');");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return ccdaValidationReportID;
    }

    private String addNewPart(String messageId, PartInterface part, String parentID) throws DatabaseException {
        String logID = this.getLogIDByMessageId(messageId);
        if (logID == null) {
            return null;
        }
        String partID = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();
        String rawMessage = part.getRawMessage();
        rawMessage = rawMessage.replace("'", "&#039;");

        sql.append("INSERT INTO " + PART_TABLE + ' ');
        sql.append("(" + PART_PARTID);
        sql.append(", ");
        sql.append(LOG_LOGID);
        sql.append(", ");
        sql.append(PART_RAWMESSAGE);
        sql.append(", ");
        sql.append(PART_CONTENTTYPE);
        sql.append(", ");
        sql.append(PART_CONTENTTRANSFERENCODING);
        sql.append(", ");
        sql.append(PART_CONTENTDISPOSITION);
        sql.append(", ");
        sql.append(PART_STATUS);
        sql.append(") VALUES ('");
        sql.append(partID);
        sql.append("' , '");
        sql.append(logID);
        sql.append("' , '");
        sql.append(rawMessage);
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(part.getContentType()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(part.getContentTransferEncoding()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(part.getContentDisposition()));
        sql.append("' , '");
        if (part.isStatus() != null && part.isStatus()) {
            sql.append("1");
        } else {
            sql.append("0");
        }
        sql.append("');");


        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        Collection<DetailInterface> details = part.getDetails();
        if (details != null) {
            int i = 0;
            Iterator<DetailInterface> it = details.iterator();
            while (it.hasNext()) {
                DetailImpl detail = (DetailImpl) it.next();
                detail.setCounter(++i);
                this.addNewDetail(partID, detail);
            }
        }

        Collection<PartInterface> children = part.getChildren();
        if (children != null) {
            Iterator<PartInterface> it = children.iterator();
            while (it.hasNext()) {
                PartImpl child = (PartImpl) it.next();
                this.addNewPart(messageId, child, partID);
            }
        }
        this.addNewPartRelationship(parentID, partID);
        return partID;

    }

    public Collection<CCDAValidationReportInterface> getCCDAValidationReportByMessageId(String messageId) throws DatabaseException {

        String logID = this.getLogIDByMessageId(messageId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + CCDAVALIDATIONREPORT_TABLE + ' ');
        sql.append("WHERE " + LOG_LOGID + " = '" + logID + "';");

        Collection<CCDAValidationReportInterface> reports = new ArrayList<CCDAValidationReportInterface>();
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                CCDAValidationReportInterface report = new CCDAValidationReportImpl();
                report.setFilename(result.getString(CCDAVALIDATIONREPORT_FILENAME));

                String validationReportString = result.getString(CCDAVALIDATIONREPORT_VALIDATIONREPORT);
                validationReportString = validationReportString.replace("&quot;", "\\\"");

                report.setValidationReport(validationReportString);
                reports.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return reports;

    }

    public PartInterface getPart(String partID) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + PART_TABLE + " ");
        sql.append("WHERE " + PART_PARTID + " = '" + partID + "';");

        PartInterface part = null;
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                part = this.convertDatabaseRecordToPart(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        part.setDetails(this.getDetailsForPart(partID));
        Collection<String> childIDs = this.getImmediateChildIDs(partID);
        Collection<PartInterface> children = new ArrayList<PartInterface>();
        Iterator<String> it = childIDs.iterator();
        while (it.hasNext()) {
            PartInterface child = this.getPart(it.next());
            children.add(child);
        }
        part.setChildren(children);

        return part;

    }

    private Collection<String> getImmediateChildIDs(String partID) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + PARTRELATIONSHIP_CHILDID + " ");
        sql.append("FROM " + PARTRELATIONSHIP_TABLE + " ");
        sql.append("WHERE " + PARTRELATIONSHIP_PARENTID + " = '" + partID + "';");

        ResultSet result = null;
        Collection<String> childIDs = new ArrayList<String>();

        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                String childID = result.getString(PARTRELATIONSHIP_CHILDID);
                childIDs.add(childID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return childIDs;
    }

    public List<DetailInterface> getDetailsForPart(String partID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + DETAIL_TABLE + " ");
        sql.append("WHERE " + PART_PARTID + " = '" + partID + "' ");
        sql.append("ORDER BY " + DETAIL_COUNTER);

        ResultSet result = null;
        List<DetailInterface> details = new ArrayList<DetailInterface>();

        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                DetailInterface detail = this.convertDatabaseRecordtoDetail(result);
                details.add(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return details;
    }

    public PartInterface getPartByMessageId(String messageID) throws DatabaseException {

        String partID = this.getTopLevelPartID(messageID);
        return this.getPart(partID);

    }

    public String getTopLevelPartID(String messageId) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p." + PART_PARTID + " ");
        sql.append("FROM " + PART_TABLE + " p, " + PARTRELATIONSHIP_TABLE + " pr, " + LOG_TABLE + " l ");
        sql.append("WHERE l." + LOG_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "' ");
        sql.append("AND p." + LOG_LOGID + " = l." + LOG_LOGID + " ");
        sql.append("AND pr." + PARTRELATIONSHIP_CHILDID + " = p." + PART_PARTID + " ");
        sql.append("AND pr." + PARTRELATIONSHIP_PARENTID + " IS NULL;");

        String partID = null;
        ResultSet result = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                partID = result.getString(PART_PARTID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return partID;
    }

    public String addNewPart(String messageId, PartInterface part) throws DatabaseException {
        return addNewPart(messageId, part, null);
    }

    public boolean isPartAChild(String partID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + PARTRELATIONSHIP_PARTRELATIONSHIPID + " ");
        sql.append("FROM " + PARTRELATIONSHIP_TABLE + " ");
        sql.append("WHERE " + PARTRELATIONSHIP_CHILDID + " = '" + partID + "';");

        ResultSet result = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return false;
    }

    public boolean isPartAParent(String partID) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + PARTRELATIONSHIP_PARTRELATIONSHIPID + " ");
        sql.append("FROM " + PARTRELATIONSHIP_TABLE + " ");
        sql.append("WHERE " + PARTRELATIONSHIP_PARENTID + " = '" + partID + "';");

        ResultSet result = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return false;
    }

    public String addNewPartRelationship(String parentID, String childID) throws DatabaseException {

        String partRelationshipID = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO " + PARTRELATIONSHIP_TABLE + ' ');
        sql.append("(");
        sql.append(PARTRELATIONSHIP_PARTRELATIONSHIPID);
        sql.append(",");
        sql.append(PARTRELATIONSHIP_PARENTID);
        sql.append(",");
        sql.append(PARTRELATIONSHIP_CHILDID);
        sql.append(") VALUES('");
        sql.append(partRelationshipID);
        sql.append("',");
        if (parentID == null || parentID.trim().equalsIgnoreCase("")) {
            sql.append("NULL");
        } else {
            sql.append("'" + parentID + "'");
        }
        sql.append(",'");
        sql.append(childID);
        sql.append("');");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return partRelationshipID;

    }

    public String addNewDetail(String partID, DetailInterface detail) throws DatabaseException {
        String detailID = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + DETAIL_TABLE + ' ');
        sql.append("(" + DETAIL_DETAILID);
        sql.append(", ");
        sql.append(PART_PARTID);
        sql.append(", ");
        sql.append(DETAIL_COUNTER);
        sql.append(", ");
        sql.append(DETAIL_NAME);
        sql.append(", ");
        sql.append(DETAIL_STATUS);
        sql.append(", ");
        sql.append(DETAIL_DTS);
        sql.append(", ");
        sql.append(DETAIL_FOUND);
        sql.append(", ");
        sql.append(DETAIL_EXPECTED);
        sql.append(", ");
        sql.append(DETAIL_RFC);
        sql.append(") VALUES ('");
        sql.append(detailID);
        sql.append("' , '");
        sql.append(partID);
        sql.append("' , '");
        sql.append(detail.getCounter());
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(detail.getName()));
        sql.append("' , '");
        sql.append(DetailImpl.statusToInt(detail.getStatus()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(detail.getDts()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(detail.getFound()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(detail.getExpected()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(detail.getRfc()));
        sql.append("');");
        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        return detailID;

    }

    // This depends on messageId being unique (enforced by DB constraint)
    public boolean updateStatus(String messageId, Status status) throws DatabaseException {
        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE " + LOG_TABLE + " ");
        sql.append("SET " + LOG_STATUS + " = '" + LogImpl.statusToInt(status) + "' ");
        sql.append("WHERE " + LOG_MESSAGEID + " = '" + DatabaseConnection.makeSafe(messageId) + "';");
        int i = 0;

        try {
            i = this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        if (i == 0) {
            return false;
        }

        return true;
    }

    private PartInterface convertDatabaseRecordToPart(ResultSet result) throws DatabaseException {
        PartImpl part = new PartImpl();

        try {
            part.setPartID(result.getString(PART_PARTID));
            part.setLogID(result.getString(LOG_LOGID));
            String rawMessage = result.getString(PART_RAWMESSAGE);
            rawMessage = rawMessage.replace("&#039;", "'");
            part.setRawMessage(rawMessage);
            part.setContentType(result.getString(PART_CONTENTTYPE));
            part.setContentTransferEncoding(result.getString(PART_CONTENTTRANSFERENCODING));
            part.setContentDisposition(result.getString(PART_CONTENTDISPOSITION));
            part.setStatus(result.getBoolean(PART_STATUS));

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return part;
    }

    private DetailInterface convertDatabaseRecordtoDetail(ResultSet result) throws DatabaseException {
        DetailImpl detail = new DetailImpl();

        try {
            detail.setDetailID(result.getString(DETAIL_DETAILID));
            detail.setPartID(result.getString(PART_PARTID));
            detail.setCounter(result.getInt(DETAIL_COUNTER));
            detail.setName(result.getString(DETAIL_NAME));
            detail.setStatus(DetailImpl.intToStatus(result.getInt(DETAIL_STATUS)));
            detail.setDts(result.getString(DETAIL_DTS));
            detail.setFound(result.getString(DETAIL_FOUND));
            detail.setExpected(result.getString(DETAIL_EXPECTED));
            detail.setRfc(result.getString(DETAIL_RFC));
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
        return detail;

    }

    // TODO: Re-write this.  To many unneeded calls to database.
    private LogInterface convertDatabaseRecordToLog(ResultSet result) throws DatabaseException {

        LogImpl log = new LogImpl();
        try {
            log.setLogID(result.getString(LOG_LOGID));
            log.setIncoming(result.getBoolean(LOG_INCOMING));
            log.setMdn(result.getBoolean(LOG_MDN));
            log.setTimestamp(result.getString(LOG_TIMESTAMP));
            log.setStatus(LogImpl.intToStatus(result.getInt(LOG_STATUS)));
            // log.setSuccess(result.getBoolean(LOG_SUCCESS));
            log.setOrigDate(result.getString(LOG_ORIGDATE));
//        log.setFromLine(result.getString(LOG_FROMLINE));
//        log.setToLine(result.getString(LOG_TOLINE));
            log.setMessageId(result.getString(LOG_MESSAGEID));
            log.setOriginalMessageId(result.getString(LOG_ORIGINALMESSAGEID));
            log.setMimeVersion(result.getString(LOG_MIMEVERSION));
//        log.setReceived(result.getString(LOG_RECEIVED));
//        log.setReplyTo(result.getString(LOG_REPLYTO));
            log.setSubject(result.getString(LOG_SUBJECT));
            log.setContentType(result.getString(LOG_CONTENTTYPE));
            log.setContentDisposition(result.getString(LOG_CONTENTDISPOSITION));
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

//        LogFacade lf = new LogFacade(this.getConfig());
        log.setFromLine(this.getFromLines(log.getLogID()));
        log.setToLine(this.getToLines(log.getLogID()));
        log.setReceived(this.getReceived(log.getLogID()));
        log.setReplyTo(this.getReplyTo(log.getLogID()));

        return log;

    }

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setDatabaseHostname("localhost");
        config.setDatabaseName("direct");

        try {

            LogFacade lf;
            lf = new LogFacade(config);

            CCDAValidationReportInterface report = new CCDAValidationReportImpl();
            report.setFilename("filename.txt");


             Collection<CCDAValidationReportInterface> reports = lf.getCCDAValidationReportByMessageId("1073cb15-5de5-458a-998b-e36c173abbad");

             Iterator<CCDAValidationReportInterface> it = reports.iterator();

             while(it.hasNext()) {
             CCDAValidationReportInterface report1 = it.next();
            // System.out.println(report1.getFilename());
             System.out.println("after  = " + report1.getValidationReport());
			}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
