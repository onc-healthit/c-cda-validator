package gov.nist.healthcare.ttt.database.jdbc;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeLogImpl;
import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeLogInterface;
import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeProfileImpl;
import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeProfileInterface;
import gov.nist.healthcare.ttt.misc.Configuration;

/**
 *
 * @author mccaffrey
 */

public class SmtpEdgeLogFacade extends DatabaseFacade {
	private static final String SMTPEDGEPROFILE_ENCRYPTKEY = getPasswdEncryKey();
    private static final String SMTPEDGEPROFILE_TABLE = "SmtpEdgeProfile";
    private static final String SMTPEDGEPROFILE_SMTPEDGEPROFILEID = "SmtpEdgeProfileID";
    private static final String SMTPEDGEPROFILE_PROFILENAME = "ProfileName";
    private static final String SMTPEDGEPROFILE_SUTSMTPADDRESS = "SUTSMTPAddress";
    private static final String SMTPEDGEPROFILE_SUTEMAILADDRESS = "SUTEmailAddress";
    private static final String SMTPEDGEPROFILE_SUTUSERNAME = "SUTUsername";
    private static final String SMTPEDGEPROFILE_SUTPASSWORD = "SUTPassword";
    private static final String SMTPEDGEPROFILE_USETLS = "useTLS";

    private static final String SMTPEDGELOG_TABLE = "SmtpEdgeLog";
    private static final String SMTPEDGELOG_SMTPEDGELOGID = "SmtpEdgeLogID";
    private static final String SMTPEDGELOG_TIMESTAMP = "Timestamp";
    private static final String SMTPEDGELOG_TRANSACTIONID = "TransactionID";
    private static final String SMTPEDGELOG_TESTCASENUMBER = "TestCaseNumber";
    private static final String SMTPEDGELOG_CRITERIAMET = "CriteriaMet";
    private static final String SMTPEDGELOG_TESTREQUESTRESPONSE = "TestRequestsResponse";
    private static final String CHECKDATA_TYPE = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'SmtpEdgeProfile' AND COLUMN_NAME = 'sutPassword'";
    private static final String ALTER_COLUMN = "ALTER TABLE SmtpEdgeProfile MODIFY SUTPassword varbinary(255)";
    private static final String DATA_TYPE = "DATA_TYPE";

    /**
     *
     * @param config
     * @throws DatabaseException
     */

    public SmtpEdgeLogFacade(Configuration config) throws DatabaseException {
        super(config);
    }

    public String addNewGroupSmtpLog(List<SmtpEdgeLogInterface> smtpLogs, String username, String profileName) throws DatabaseException {

        Iterator<SmtpEdgeLogInterface> it = smtpLogs.iterator();
        String transaction = UUID.randomUUID().toString();
        while(it.hasNext()) {
            SmtpEdgeLogInterface log = it.next();
            log.setTransaction(transaction);
            this.addNewSmtpLog(log, username, profileName);
        }
        return transaction;
    }

    /**
     * Method adds a new log to the database associated with a username /
     * profile name combination. If
     *
     * @param smtpLog The log to be stored.
     * @param username The username associated with the new log entry.
     * @param profileName The profile name associated with the new log entry.
     * @return The UUID of the log ID. (Only used by database.)
     * @throws DatabaseException Thrown if username / profile name not valid or
     * other database access error.
     */
    public String addNewSmtpLog(SmtpEdgeLogInterface smtpLog, String username, String profileName) throws DatabaseException {

        String profileId = this.getProfileId(username, profileName);

        if (profileId == null || "".equals(profileId)) {
            throw new DatabaseException("Invalid username / profile name combination.");
        }

        String smtpLogId = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + SMTPEDGELOG_TABLE + ' ');
        sql.append("(" + SMTPEDGELOG_SMTPEDGELOGID);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_SMTPEDGEPROFILEID);
        sql.append(", ");
        sql.append(SMTPEDGELOG_TIMESTAMP);
        sql.append(", ");
        sql.append(SMTPEDGELOG_TRANSACTIONID);
        sql.append(", ");
        sql.append(SMTPEDGELOG_TESTCASENUMBER);
        sql.append(", ");
        sql.append(SMTPEDGELOG_CRITERIAMET);
        sql.append(", ");
        sql.append(SMTPEDGELOG_TESTREQUESTRESPONSE);
        sql.append(") VALUES ('");
        sql.append(smtpLogId);
        sql.append("' , '");
        sql.append(profileId);
        sql.append("' , '");
        if (smtpLog.getTimestamp() != null) {
            sql.append(smtpLog.getTimestamp());
        } else {
            sql.append(Calendar.getInstance().getTimeInMillis());
        }
        sql.append("' ,'");
        if(smtpLog.getTransaction() != null) {
            sql.append(smtpLog.getTransaction());
        } else {
            sql.append(UUID.randomUUID().toString());
        }
        sql.append("','");
        sql.append(smtpLog.getTestCaseNumber());
        sql.append("' , '");
        if (smtpLog.isCriteriaMet()) {
            sql.append("1");
        } else {
            sql.append("0");
        }
        sql.append("' , '");
        sql.append(smtpLog.getTestRequestsResponse());
        sql.append("');");
        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        return profileId;
    }

    /**
     * Method for adding or modifying a SMTP profile to the database.
     *
     * @param profile
     * @return The UUID of the profile ID. (Only used by database.)
     * @throws DatabaseException
     */
    public String saveSmtpProfile(SmtpEdgeProfileInterface profile) throws DatabaseException {
        String existingProfileID = this.getProfileId(profile.getUsername(), profile.getProfileName());
        if(existingProfileID == null || "".equals(existingProfileID))
            return this.addNewSmtpProfile(profile);
        changeSutDataType(); // remove this line of code after DB is updated to new datatype
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE " + SMTPEDGEPROFILE_TABLE + ' ');
        sql.append("SET ");
        sql.append(SMTPEDGEPROFILE_SUTSMTPADDRESS + " = '" + profile.getSutSMTPAddress() + "', ");
        sql.append(SMTPEDGEPROFILE_SUTEMAILADDRESS + " = '" + profile.getSutEmailAddress() + "', ");
        sql.append(SMTPEDGEPROFILE_SUTUSERNAME + " = '" + profile.getSutUsername() + "', ");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD + " = AES_ENCRYPT('" + profile.getSutPassword() + "',UNHEX('"+SMTPEDGEPROFILE_ENCRYPTKEY+"') ) ,");
        sql.append(SMTPEDGEPROFILE_USETLS + " =  "+ profile.getUseTLS() +" ");
        sql.append("WHERE " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + " = '" + existingProfileID + "';");
        //System.out.println("profile.getSutUsername() ..."+profile.getSutUsername());
        //System.out.println("update sql ..."+sql.toString());
        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return existingProfileID;


    }

    private String addNewSmtpProfile(SmtpEdgeProfileInterface profile) throws DatabaseException {
    	changeSutDataType(); // remove this line of code after DB is updated to new datatype
        String smtpProfileID = UUID.randomUUID().toString();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO " + SMTPEDGEPROFILE_TABLE + ' ');
        sql.append("(" + SMTPEDGEPROFILE_SMTPEDGEPROFILEID);
        sql.append(", ");
        sql.append(USERS_USERNAME);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_PROFILENAME);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_SUTSMTPADDRESS);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_SUTEMAILADDRESS);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_SUTUSERNAME);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD);
        sql.append(", ");
        sql.append(SMTPEDGEPROFILE_USETLS);
        sql.append(") VALUES ('");
        sql.append(smtpProfileID);
        sql.append("' , '");
        sql.append(profile.getUsername());
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(profile.getProfileName()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(profile.getSutSMTPAddress()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(profile.getSutEmailAddress()));
        sql.append("' , '");
        sql.append(DatabaseConnection.makeSafe(profile.getSutUsername()));
        sql.append("' , AES_ENCRYPT('");
        sql.append(profile.getSutPassword());
        sql.append("',UNHEX('");
        sql.append(SMTPEDGEPROFILE_ENCRYPTKEY);
        sql.append("')) , ");
        sql.append(profile.getUseTLS());
        sql.append(");");
        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        return smtpProfileID;

    }

    public int removeSmtpProfile(String username, String profileName) throws DatabaseException {

        String profileId = this.getProfileId(username, profileName);
        if(profileId == null || "".equals(profileId))
            return 0;

        StringBuilder sqlRemoveLog = new StringBuilder();
        sqlRemoveLog.append("DELETE ");
        sqlRemoveLog.append("FROM " + SMTPEDGELOG_TABLE + ' ');
        sqlRemoveLog.append("WHERE " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + " = '" + profileId + "';");

        try {
            this.getConnection().executeUpdate(sqlRemoveLog.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        StringBuilder sqlRemoveProfile = new StringBuilder();
        sqlRemoveProfile.append("DELETE ");
        sqlRemoveProfile.append("FROM " + SMTPEDGEPROFILE_TABLE + ' ');
        sqlRemoveProfile.append("WHERE " + USERS_USERNAME + " = '" + username + "' AND ");
        sqlRemoveProfile.append(SMTPEDGEPROFILE_PROFILENAME + " = '" + profileName + "';");

        int result = 0;

        try {
            result = this.getConnection().executeUpdate(sqlRemoveProfile.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }

        return result;
    }

    /**
     * Given a username, profile and test case number/identifier, this method
     * returns the most recent entry in the log.
     *
     * @param username
     * @param profileName
     * @param testcasenumber
     * @return Returns the most recent applicable log entry or null if none for
     * given parameters.
     * @throws DatabaseException
     */
    public SmtpEdgeLogInterface getLatestSmtpEdgeLogInterface(String username, String profileName, String testcasenumber) throws DatabaseException {
        String profileId = this.getProfileId(username, profileName);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + SMTPEDGELOG_TABLE + ' ');
        sql.append("WHERE " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + " = '" + profileId + "' AND ");
        sql.append(SMTPEDGELOG_TESTCASENUMBER + " = '" + DatabaseConnection.makeSafe(testcasenumber) + "' ");
        sql.append("ORDER BY " + SMTPEDGELOG_TIMESTAMP + " DESC ");
        sql.append("LIMIT 1;");

        ResultSet result = null;
        SmtpEdgeLogInterface log = null;
        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                log = this.convertToLog(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return log;

    }

    /**
     * Given a username and profile name, returns one log entry for each
     * individual test case number identified. The log for each test case is
     * always the most recent.
     *
     * @param username
     * @param profileName
     * @return
     * @throws DatabaseException
     */
    public List<SmtpEdgeLogInterface> getLatestSmtpEdgeLogInterface(String username, String profileName) throws DatabaseException {

        String profileId = this.getProfileId(username, profileName);
        List<SmtpEdgeLogInterface> logs = new ArrayList<SmtpEdgeLogInterface>();

        List<String> testCaseNumbers = this.getAllTestCaseNumbers(profileId);
        Iterator<String> it = testCaseNumbers.iterator();
        while (it.hasNext()) {
            String testCaseNumber = it.next();
            SmtpEdgeLogInterface log = this.getLatestSmtpEdgeLogInterface(username, profileName, testCaseNumber);
            logs.add(log);
        }

        return logs;
    }

    private List<String> getAllTestCaseNumbers(String profileId) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT " + SMTPEDGELOG_TESTCASENUMBER + ' ');
        sql.append("FROM " + SMTPEDGELOG_TABLE + ' ');
        sql.append("WHERE " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + " = '" + profileId + "';");

        ResultSet result = null;
        List<String> testCaseNumbers = new ArrayList<String>();

        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                testCaseNumbers.add(result.getString(SMTPEDGELOG_TESTCASENUMBER));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return testCaseNumbers;
    }


    public Map<String, List<SmtpEdgeLogInterface>> getSmtpEdgeValidationReport(String username, String profileName) throws DatabaseException {

        String profileId = this.getProfileId(username, profileName);
        List<String> testCaseNumbers = this.getAllTestCaseNumbers(profileId);

        Map<String, List<SmtpEdgeLogInterface>> validation = new HashMap<String, List<SmtpEdgeLogInterface>>();
        Iterator<String> it = testCaseNumbers.iterator();
        while(it.hasNext()) {
            String testCaseNumber = it.next();
            String interactionID = this.getLatestInteractionIDByProfileAndTestCase(profileId, testCaseNumber);

            List<SmtpEdgeLogInterface> logs = this.getLogsByInteraction(interactionID);
            validation.put(testCaseNumber, logs);
        }

        return validation;

    }

    private String getLatestInteractionIDByProfileAndTestCase(String profileId, String testCaseNumber) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + SMTPEDGELOG_TRANSACTIONID + ' ');
        sql.append("FROM " + SMTPEDGELOG_TABLE + ' ');
        sql.append("WHERE " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + " = '" + profileId + "' AND ");
        sql.append(SMTPEDGELOG_TESTCASENUMBER + " = '" + testCaseNumber + "' ");
        sql.append("ORDER BY " + SMTPEDGELOG_TIMESTAMP + " DESC ");
        sql.append("LIMIT 1;");
        //System.out.println(sql.toString());
        ResultSet result = null;
        String interactionID = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                interactionID = result.getString(SMTPEDGELOG_TRANSACTIONID);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return interactionID;
    }

    private List<SmtpEdgeLogInterface> getLogsByInteraction(String interactionID) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + SMTPEDGELOG_TABLE + ' ');
        sql.append("WHERE " + SMTPEDGELOG_TRANSACTIONID + " = '" + interactionID + "';" );

        ResultSet result = null;
        List<SmtpEdgeLogInterface> logs = new ArrayList<SmtpEdgeLogInterface>();

        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                SmtpEdgeLogInterface log = this.convertToLog(result);
                logs.add(log);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return logs;
    }


    private String getProfileId(String username, String profileName) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + SMTPEDGEPROFILE_SMTPEDGEPROFILEID + ' ');
        sql.append("FROM " + SMTPEDGEPROFILE_TABLE + ' ');
        sql.append("WHERE " + USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' AND ");
        sql.append(SMTPEDGEPROFILE_PROFILENAME + " = '" + DatabaseConnection.makeSafe(profileName) + "';");

        ResultSet result = null;
        String profileID = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                profileID = result.getString(SMTPEDGEPROFILE_SMTPEDGEPROFILEID);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return profileID;

    }

    /**
     * All profile information for a specific user.
     *
     * @param username
     * @return
     * @throws DatabaseException
     */
    public List<SmtpEdgeProfileInterface> getAllProfilesByUsername(String username) throws DatabaseException {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT  ");
        sql.append(SMTPEDGEPROFILE_SMTPEDGEPROFILEID);
        sql.append(" , ");
        sql.append(SMTPEDGEPROFILE_PROFILENAME);
        sql.append(" , ");
        sql.append(SMTPEDGEPROFILE_SUTEMAILADDRESS);
        sql.append(" , ");
        sql.append(SMTPEDGEPROFILE_SUTSMTPADDRESS);
        sql.append(" , ");
        sql.append(SMTPEDGEPROFILE_SUTUSERNAME);
        sql.append(" , ");
        sql.append(USERS_USERNAME);
        sql.append(" , ");
        sql.append(SMTPEDGEPROFILE_USETLS);
        sql.append(" , ");
        sql.append(" if(CAST(AES_DECRYPT(");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD);
        sql.append(", UNHEX('");
        sql.append(SMTPEDGEPROFILE_ENCRYPTKEY);
        sql.append("')) AS CHAR(255)) IS NULL,");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD);
        sql.append(", CAST(AES_DECRYPT(");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD);
        sql.append(", UNHEX('");
        sql.append(SMTPEDGEPROFILE_ENCRYPTKEY);
        sql.append("')) AS CHAR(255))) ");
        sql.append(SMTPEDGEPROFILE_SUTPASSWORD);
        sql.append(" FROM " + SMTPEDGEPROFILE_TABLE + ' ');
        sql.append("WHERE " + USERS_USERNAME + " = '" + username + "';");

        ResultSet result = null;
        List<SmtpEdgeProfileInterface> profiles = new ArrayList<SmtpEdgeProfileInterface>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                SmtpEdgeProfileInterface profile = this.convertToProfile(result);
                profiles.add(profile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }

        return profiles;

    }

    private SmtpEdgeProfileInterface convertToProfile(ResultSet result) throws DatabaseException {
        SmtpEdgeProfileInterface profile = new SmtpEdgeProfileImpl();
        try {
            profile.setProfileName(result.getString(SMTPEDGEPROFILE_PROFILENAME));
            profile.setSmtpEdgeProfileID(result.getString(SMTPEDGEPROFILE_SMTPEDGEPROFILEID));
            profile.setSutEmailAddress(result.getString(SMTPEDGEPROFILE_SUTEMAILADDRESS));
            profile.setSutPassword(result.getString(SMTPEDGEPROFILE_SUTPASSWORD));
            profile.setSutSMTPAddress(result.getString(SMTPEDGEPROFILE_SUTSMTPADDRESS));
            profile.setSutUsername(result.getString(SMTPEDGEPROFILE_SUTUSERNAME));
            profile.setUsername(result.getString(USERS_USERNAME));
            if (result.getObject(SMTPEDGEPROFILE_USETLS) !=null){
	            profile.setUseTLS(result.getBoolean(SMTPEDGEPROFILE_USETLS));
			}
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return profile;
    }

    private SmtpEdgeLogInterface convertToLog(ResultSet result) throws DatabaseException {

        SmtpEdgeLogInterface log = new SmtpEdgeLogImpl();
        try {

            String notImpl = "TODO: Not Implemented Yet.";
            ArrayList<String> attachments = new ArrayList<String>();
            attachments.add(notImpl);
            log.setAttachments(attachments);
            log.setCriteriaMet(result.getBoolean(SMTPEDGELOG_CRITERIAMET));
            log.setSmtpEdgeLogID(result.getString(SMTPEDGELOG_SMTPEDGELOGID));
            log.setTestCaseNumber(result.getString(SMTPEDGELOG_TESTCASENUMBER));
            log.setTimestamp(result.getString(SMTPEDGELOG_TIMESTAMP));
            log.setTestRequestsResponse(result.getString(SMTPEDGELOG_TESTREQUESTRESPONSE));

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return log;

    }

    private void changeSutDataType() throws DatabaseException {
        	ResultSet result = null;
            String dataType = null;
            try {
                result = this.getConnection().executeQuery(CHECKDATA_TYPE);
                while (result.next()) {
                	dataType = result.getString(DATA_TYPE);
                }
                if (dataType !=null && dataType.equalsIgnoreCase("varchar")){
                	this.getConnection().execute(ALTER_COLUMN);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new DatabaseException(e.getMessage());
            }
    }

	private static String getPasswdEncryKey(){
		String passwdEncryKey = "F3429A0B371ED20C3";
		try{
			Properties prop = new Properties();
			String path = "./application.properties";
			FileInputStream file = new FileInputStream(path);
			prop.load(file);
			file.close();
			if (prop.getProperty("ttt.passwd.encryKey") !=null){
				passwdEncryKey = prop.getProperty("ttt.passwd.encryKey");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return passwdEncryKey;
	}
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            Configuration config = new Configuration();
            config.setDatabaseHostname("localhost");
            config.setDatabaseName("direct");
            SmtpEdgeLogFacade lf = new SmtpEdgeLogFacade(config);

            SmtpEdgeProfileInterface profile = new SmtpEdgeProfileImpl();

            profile.setProfileName("Profile1");
            profile.setUsername("Username1");
            profile.setSutEmailAddress("SUTemail1");
            profile.setSutUsername("SUTusername1");
            profile.setSutPassword("SUTpassword1");
            profile.setSutSMTPAddress("SMTPAddress1");

           //lf.saveSmtpProfile(profile);
            SmtpEdgeLogInterface log = new SmtpEdgeLogImpl();
            log.setCriteriaMet(true);
            log.setTestCaseNumber("SMTP2");
            log.setTestRequestsResponse("OOOOO OOOOOO");

            SmtpEdgeLogInterface log2 = new SmtpEdgeLogImpl();
            log2.setCriteriaMet(true);
            log2.setTestCaseNumber("SMTP2");
            log2.setTestRequestsResponse("XXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXX");

            SmtpEdgeLogInterface log3 = new SmtpEdgeLogImpl();
            log3.setCriteriaMet(true);
            log3.setTestCaseNumber("SMTP2");
            log3.setTestRequestsResponse("RRRRRRRRRRRRRRR THING");


            List<SmtpEdgeLogInterface> logs = new ArrayList<SmtpEdgeLogInterface>();
            logs.add(log);
            logs.add(log2);
            logs.add(log3);
           // lf.addNewGroupSmtpLog(logs, "Username", "Profile1");
            long begin = Calendar.getInstance().getTimeInMillis();
          //  for(int i = 0; i <= 100; i++) {

                lf.addNewGroupSmtpLog(logs, "Username1", "Profile1");
                lf.addNewSmtpLog(log, "Username", "Profile1");

               Map<String, List<SmtpEdgeLogInterface>> report = lf.getSmtpEdgeValidationReport("Username1", "Profile1");
             //   System.out.println(report.get("SMTP2").get(0).getTestRequestsResponse());

//            }
                long end = Calendar.getInstance().getTimeInMillis();


                System.out.println("Time elasped (milliseconds): " + (end - begin));
          //  Map<String, List<SmtpEdgeLogInterface>> report = lf.getSmtpEdgeValidationReport("Username", "Profile1");

         //   System.out.println(report.get("SMTP2").size());
            // lf.addNewSmtpLog(log, "Username", "Profile1");
        //    SmtpEdgeLogInterface log2 = lf.getLatestSmtpEdgeLogInterface("Username", "Profile1", "SMTP1");
           // System.out.println(log2.getSmtpEdgeLogID());
            //  System.out.println(lf.getLatestSmtpEdgeLogInterface("Username", "Profile1", "SMTP1").getTestRequestsResponse());

            //  lf.getLatestSmtpEdgeLogInterface(USERS_TABLE, USERS_TABLE)
          //  List<SmtpEdgeLogInterface> logs = lf.getLatestSmtpEdgeLogInterface("Username", "Profile1");
            //Iterator<SmtpEdgeLogInterface> it = logs.iterator();
//            while (it.hasNext()) {
  //              SmtpEdgeLogInterface readLog = it.next();
    //            System.out.println(readLog.getTestCaseNumber() + " " + readLog.getTestRequestsResponse());
      //      }


        //    lf.removeSmtpProfile("Username", "Profile1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
