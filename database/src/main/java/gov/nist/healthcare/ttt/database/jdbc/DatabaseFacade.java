/*
 * DatabaseFacade.java
 *
 * Created on April 25, 2014, 4:03 PM
 */
package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.misc.Configuration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author mccaffrey
 */
public class DatabaseFacade {

    public static final int MAX_CALLS = 100;

    private DatabaseConnection connection = null;
    private static int currentNumberOfCalls = 0;
    private Configuration config = null;
    protected static DatabaseFacade instance = null;

    /**
     * Table name.
     */
    public final static String DIRECT_EMAIL_TABLE = "DirectEmail";

    /**
     * Column name.
     */
    public final static String DIRECT_EMAIL_ID_COLUMN = "DirectEmailID";

    /**
     * Column name.
     */
    public final static String DIRECT_EMAIL_COLUMN = "DirectEmail";

    /**
     * Table name.
     */
    public final static String CONTACT_EMAIL_TABLE = "ContactEmail";

    /**
     * Column name.
     */
    public final static String CONTACT_EMAIL_ID_COLUMN = "ContactEmailID";
    //public final static String DIRECT_EMAIL_ID_COLUMN = "DirectEmailID";

    /**
     * Column name.
     */
    public final static String CONTACT_EMAIL_COLUMN = "ContactEmail";

    public final static String USERS_TABLE = "Users";
    public final static String USERS_USERNAME = "Username";
    public final static String USERS_PASSWORD = "Password";

    public final static String USER_DIRECT_TABLE = "UserDirect";

    /**
     * Constructor.
     *
     * @param config The configuration information (includes database
     * information).
     * @throws SQLException
     */
    public DatabaseFacade(Configuration config) throws DatabaseException {
        this.setConfig(config);
        //   this.setConnection(new DatabaseConnection(config));
    }

    /**
     * Returns an instance of the DatabaseFacade.
     *
     * @param config The configuration information (includes database
     * information).
     * @return
     * @throws SQLException
     */
    /*
     static public DatabaseFacade getInstance(Configuration config) throws DatabaseException {
     if (instance == null) {
     instance = new DatabaseFacade(config);
     }
     return instance;
     }
     */
    /**
     * If a current instance exists, close it and return a new one.
     *
     * @param config The configuration information (includes database
     * information).
     * @return
     * @throws SQLException
     */
    /*
     static public DatabaseFacade getNewInstance(Configuration config) throws DatabaseException {
     if (instance != null) {
     instance.closeConnection();
     }
     instance = new DatabaseFacade(config);
     return instance;
     }
     */
    /**
     * Returns all the direct addresses associated with the given contact email
     * address.
     *
     * @param contactEmail The contact address to search on.
     * @return All direct addresses associated with the contact address. Returns
     * an empty Collection if none found. Returns null if an SQL/DB problem is
     * encountered.
     */
    public Collection<String> getDirectAddresses(String contactEmail) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT de." + DatabaseFacade.DIRECT_EMAIL_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ce , " + DatabaseFacade.DIRECT_EMAIL_TABLE + " de ");
        sql.append("WHERE ce." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = de." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " ");
        sql.append("AND ce." + DatabaseFacade.CONTACT_EMAIL_COLUMN + " = '" + contactEmail + "';");

        ResultSet result = null;
        Collection<String> directAddresses = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                directAddresses.add(result.getString(DatabaseFacade.DIRECT_EMAIL_COLUMN));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return directAddresses;

    }

    /**
     * Returns all the contact addresses associated with the given direct email
     * address.
     *
     * @param directEmail The direct address to search on.
     * @return All contact addresses associated with the contact address.
     * Returns an empty Collection if none found. Returns null if an SQL/DB
     * problem is encountered.
     */
    public Collection<String> getContactAddresses(String directEmail) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ce." + DatabaseFacade.CONTACT_EMAIL_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ce , " + DatabaseFacade.DIRECT_EMAIL_TABLE + " de ");
        sql.append("WHERE ce." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = de." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " ");
        sql.append("AND de." + DatabaseFacade.DIRECT_EMAIL_COLUMN + " = '" + directEmail + "';");

        ResultSet result = null;
        Collection<String> contactAddresses = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                contactAddresses.add(result.getString(DatabaseFacade.CONTACT_EMAIL_COLUMN));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return contactAddresses;
    }

    /**
     * Provides a list of all distinct contact addresses in the database.
     *
     * @return A collection of strings of all contact addresses in the database.
     */
    public Collection<String> getAllContactAddresses() throws DatabaseException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT " + DatabaseFacade.CONTACT_EMAIL_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE);

        ResultSet result = null;
        Collection<String> contactAddresses = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                contactAddresses.add(result.getString(DatabaseFacade.CONTACT_EMAIL_COLUMN));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return contactAddresses;
    }

    /**
     * Provides a list of all distinct direct addresses in the database.
     *
     * @return A collection of strings of all direct addresses in the database.
     */
    public Collection<String> getAllDirectAddresses() throws DatabaseException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT " + DatabaseFacade.DIRECT_EMAIL_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.DIRECT_EMAIL_TABLE);

        ResultSet result = null;
        Collection<String> contactAddresses = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                contactAddresses.add(result.getString(DatabaseFacade.DIRECT_EMAIL_COLUMN));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return contactAddresses;
    }

    /**
     * Given a direct email address, return the ID of the record in the
     * database.
     *
     * @param directEmail The direct address to search on.
     * @return
     */
    private String getIdOfDirect(String directEmail) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.DIRECT_EMAIL_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.DIRECT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(directEmail) + "'");

        ResultSet result = null;
        String id = null;

        try {

            DatabaseConnection connection = this.getConnection();
            result = connection.executeQuery(sql.toString());

            if (result.next()) {
                id = result.getString(DatabaseFacade.DIRECT_EMAIL_ID_COLUMN);
            }
            //connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return id;
    }

    /**
     * Add a new direct email address to the database and return the new ID for
     * the record. If it already exists, a new one is not added; the existing ID
     * is returned.
     *
     * @param directEmail
     * @return
     */
    public String addNewDirectEmail(String directEmail) throws DatabaseException {
        // check if it already exists
        String directEmailId = this.getIdOfDirect(directEmail);

        // only create entry if it doesn't exist
        if (directEmailId == null) {
            directEmailId = UUID.randomUUID().toString();

            StringBuilder sqlDirect = new StringBuilder();

            sqlDirect.append("INSERT INTO ");
            sqlDirect.append(DatabaseFacade.DIRECT_EMAIL_TABLE);
            sqlDirect.append(" ");
            sqlDirect.append("(");
            sqlDirect.append(DatabaseFacade.DIRECT_EMAIL_ID_COLUMN);
            sqlDirect.append(", ");
            sqlDirect.append(DatabaseFacade.DIRECT_EMAIL_COLUMN);
            sqlDirect.append(") VALUES ('");
            sqlDirect.append(directEmailId);
            sqlDirect.append("','");
            sqlDirect.append(DatabaseConnection.makeSafe(directEmail));
            sqlDirect.append("');");

            try {
                this.getConnection().executeUpdate(sqlDirect.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException(ex.getMessage());
            }

        }
        return directEmailId;
    }

    public boolean doesDirectExist(String directEmail) throws DatabaseException {

        String directEmailId = this.getIdOfDirect(directEmail);
        if (directEmailId == null) {
            return false;
        }
        return true;

    }

    /**
     * Checks whether a direct / contact pairing already exists.
     *
     * @param directEmail The direct half of the pair to check.
     * @param contactEmail The contact half of the pair to check.
     * @return 'true' if pair exists; 'false' if it does not.
     */
    public boolean doesDirectAndContactExist(String directEmail, String contactEmail) throws DatabaseException {

        String directEmailId = this.getIdOfDirect(directEmail);
        if (directEmailId == null) {
            return false;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + DatabaseFacade.DIRECT_EMAIL_COLUMN + " de,  " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ce ");
        sql.append("WHERE de." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = ce." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " AND ");
        sql.append("de." + DatabaseFacade.DIRECT_EMAIL_COLUMN + " ='" + DatabaseConnection.makeSafe(directEmail) + "' AND ");
        sql.append("ce." + DatabaseFacade.CONTACT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(contactEmail) + "';");

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

    public boolean doesUsernameDirectMappingExist(String username, String directEmail) throws DatabaseException {
        String directID = this.getIdOfDirect(directEmail);
        return this.doesUsernameDirectIdMappingExist(username, directID);
    }

    private boolean doesUsernameDirectIdMappingExist(String username, String directEmailId) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + DatabaseFacade.USER_DIRECT_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' ");
        sql.append("AND " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = '" + directEmailId + "';");

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

    private boolean deleteUsernameDirectIdMapping(String username, String directEmailId) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE ");
        sql.append("FROM " + DatabaseFacade.USER_DIRECT_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' ");
        sql.append("AND " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = '" + directEmailId + "';");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    public boolean isDirectMappedToAUsername(String directEmail) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + DatabaseFacade.USER_DIRECT_TABLE + " ud ," + DatabaseFacade.DIRECT_EMAIL_TABLE + " de ");
        sql.append("WHERE de." + DatabaseFacade.DIRECT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(directEmail) + "' ");
        sql.append("AND ud." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = de." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN);

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

    public boolean doesUsernameExist(String username) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT username ");
        sql.append("FROM " + DatabaseFacade.USERS_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "';");

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

    public boolean addUsernamePassword(String username, String password) throws DatabaseException {
        if (doesUsernameExist(username)) {
            return false;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(DatabaseFacade.USERS_TABLE);
        sql.append(" (");
        sql.append(DatabaseFacade.USERS_USERNAME + " , " + DatabaseFacade.USERS_PASSWORD);
        sql.append(" ) VALUES ( '" + DatabaseConnection.makeSafe(username) + "','" + DatabaseConnection.makeSafe(password) + "');");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;
    }

    public boolean isValidUsernamePassword(String username, String password) throws DatabaseException {
        if (!doesUsernameExist(username)) {
            return false;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM " + DatabaseFacade.USERS_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' ");
        sql.append("AND " + DatabaseFacade.USERS_PASSWORD + " = '" + DatabaseConnection.makeSafe(password) + "';");

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

    public String getPasswordForUsername(String username) throws DatabaseException {
        if (!doesUsernameExist(username)) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT " + DatabaseFacade.USERS_PASSWORD + " ");
        sql.append("FROM " + DatabaseFacade.USERS_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "';");

        ResultSet result = null;
        String password = null;

        try {
            result = this.getConnection().executeQuery(sql.toString());
            if (result.next()) {
                password = result.getString(DatabaseFacade.USERS_PASSWORD);
                return password;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return null;
    }

    public boolean changePassword(String username, String newPassword) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE " + DatabaseFacade.USERS_TABLE + " ");
        sql.append("SET " + DatabaseFacade.USERS_PASSWORD + " = '" + DatabaseConnection.makeSafe(newPassword) + "' ");
        sql.append("WHERE " + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' ");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;
    }

    public Collection<String> getDirectEmailsForUser(String username) throws DatabaseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT de." + DatabaseFacade.DIRECT_EMAIL_COLUMN + " ");
        sql.append("FROM " + DatabaseFacade.DIRECT_EMAIL_TABLE + " de, " + DatabaseFacade.USER_DIRECT_TABLE + " ud ");
        sql.append("WHERE ud." + DatabaseFacade.USERS_USERNAME + " = '" + DatabaseConnection.makeSafe(username) + "' ");
        sql.append("AND ud." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = de." + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + ";");

        ResultSet result = null;
        Collection<String> directAddresses = new ArrayList<String>();
        try {
            result = this.getConnection().executeQuery(sql.toString());
            while (result.next()) {
                directAddresses.add(result.getString(DatabaseFacade.DIRECT_EMAIL_COLUMN));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        }
        return directAddresses;
    }

    public boolean addUsernameToDirectMapping(String username, String directEmail) throws DatabaseException {

        if (!this.doesUsernameExist(username)) {
            return false;
        }
        String directEmailId = this.getIdOfDirect(directEmail);
        if (this.doesUsernameDirectIdMappingExist(username, directEmailId)) {
            return true;
        }
        String directId = this.getIdOfDirect(directEmail);
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(DatabaseFacade.USER_DIRECT_TABLE);
        sql.append(" (" + DatabaseFacade.USERS_USERNAME + ", " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + ") ");
        sql.append("VALUES ");
        sql.append("('" + DatabaseConnection.makeSafe(username) + "', '" + directId + "');");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    /**
     * Adds new direct / contact pair and returns the ID of the direct record.
     * If the direct address already exists, a new one is not created; the ID of
     * the existing record is returned. If the pairing already exists, it is not
     * added again.
     *
     * @param directEmail The direct address to add.
     * @param contactEmail The contact address to add.
     * @return The ID of the direct record.
     */
    public String addNewDirectAndContactEmail(String directEmail, String contactEmail) throws DatabaseException {
        String directEmailId = this.addNewDirectEmail(directEmail);

        String contactEmailId = UUID.randomUUID().toString();

        if (this.doesDirectAndContactExist(directEmail, contactEmail)) {
            return directEmailId;
        }
        StringBuilder sqlContact = new StringBuilder();
        sqlContact.append("INSERT INTO ");
        sqlContact.append(DatabaseFacade.CONTACT_EMAIL_TABLE);
        sqlContact.append(" ");
        sqlContact.append("(");
        sqlContact.append(DatabaseFacade.CONTACT_EMAIL_ID_COLUMN);
        sqlContact.append(", ");
        sqlContact.append(DatabaseFacade.DIRECT_EMAIL_ID_COLUMN);
        sqlContact.append(", ");
        sqlContact.append(DatabaseFacade.CONTACT_EMAIL_COLUMN);
        sqlContact.append(") VALUES ('");
        sqlContact.append(contactEmailId);
        sqlContact.append("','");
        sqlContact.append(directEmailId);
        sqlContact.append("','");
        sqlContact.append(DatabaseConnection.makeSafe(contactEmail));
        sqlContact.append("');");

        try {
            this.getConnection().executeUpdate(sqlContact.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return directEmailId;
    }

    // removing direct email also removes any contact emails associated with it
    /**
     * Removes the selected direct address from the database. As a consequence,
     * any pairing between that direct address and a contact address is also
     * removed.
     *
     * @param directEmail The direct email to remove.
     * @return 'true' if success; 'false' if failure.
     */
    public boolean deleteDirectEmail(String directEmail, String username) throws DatabaseException {

        String directId = this.getIdOfDirect(directEmail);
        if (directId == null) {
            return true;
        }
        boolean successfulDeleteContact = this.deleteContactEmailByDirectId(directId);
        if (successfulDeleteContact == false) {
            return false;
        }
        this.deleteUsernameDirectIdMapping(username, directId);

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM " + DatabaseFacade.DIRECT_EMAIL_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.DIRECT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(directEmail) + "';");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    /**
     * Deletes a specific pairing between a direct address and a contact
     * address.
     *
     * @param directEmail The direct half of the pairing.
     * @param contactEmail The contact half of the pairing.
     * @return 'true' if success; 'false' if failure.
     */
    public boolean deleteSpecificContactEmail(String directEmail, String contactEmail) throws DatabaseException {

        String directId = this.getIdOfDirect(directEmail);
        if (directId == null) {
            return false;
        }
        StringBuilder sql = new StringBuilder();

        sql.append("DELETE FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = '" + directId + "' AND ");
        sql.append(DatabaseFacade.CONTACT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(contactEmail) + "';");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    /**
     * Given an ID of a direct record, remove all contact emails associated with
     * that direct record.
     *
     * @param directId The direct ID to use to remove contact addresses.
     * @return 'true' if success; 'false' if failure.
     */
    public boolean deleteContactEmailByDirectId(String directId) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.DIRECT_EMAIL_ID_COLUMN + " = '" + DatabaseConnection.makeSafe(directId) + "';");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    /**
     * Remove all contact records matching the contact email supplied. Note that
     * this will not remove any information from the direct table.
     *
     * @param contactEmail The contact email to remove.
     * @return 'true' if success; 'false' if failure.
     */
    public boolean deleteContactEmail(String contactEmail) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM " + DatabaseFacade.CONTACT_EMAIL_TABLE + " ");
        sql.append("WHERE " + DatabaseFacade.CONTACT_EMAIL_COLUMN + " = '" + DatabaseConnection.makeSafe(contactEmail) + "';");

        try {
            this.getConnection().executeUpdate(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
        return true;

    }

    /**
     * Determines whether a batch update was completely successful. TODO: Should
     * this be moved to DatabaseConnection?
     *
     * @param result An array of results from an SQL batch update.
     * @return 'true' if fully successful; 'false' if at least one failure.
     *
     */
    public static boolean allBatchGood(int[] result) {

        for (int a = 0; a > result.length; a++) {
            if (result[a] != 0) {
                return false;
            }
        }
        return true;

    }

    /**
     * Closes the connection to the database.
     */
    public void closeConnection() throws DatabaseException {
        try {
            this.getConnection().close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Getter for property this.getConnection().
     *
     * @return Value of property this.getConnection().
     */
    public DatabaseConnection getConnection() throws DatabaseException {

//        return new DatabaseConnection(config);
        if (connection == null) {
            connection = new DatabaseConnection(config);

            currentNumberOfCalls = 0;
            return connection;
        }

        try {
            if (!connection.isAlive()) {
                connection.close();
                connection = new DatabaseConnection(config);
                currentNumberOfCalls = 0;
            }
        } catch (SQLException sqle) {
            // Don't throw exception.  Try to restart connection gracefully.
            sqle.printStackTrace();
            connection = new DatabaseConnection(config);
            currentNumberOfCalls = 0;
            return connection;
        }

        currentNumberOfCalls++;
        if (currentNumberOfCalls >= MAX_CALLS) {
            try {
                connection.close();
            } catch (SQLException sqle) {
                // Don't throw exception.  Try to restart connection gracefully.
                sqle.printStackTrace();
            }
            connection = new DatabaseConnection(config);
            currentNumberOfCalls = 0;
        }

        return connection;
    }
    /*
     public DatabaseConnection getResetConnection() throws DatabaseException {
     try {
     connection.close();
     } catch (SQLException sqle) {
     sqle.printStackTrace();
     // no need to throw this further up the stack
     }
     connection = new DatabaseConnection(config);
     return connection;
     }
     */

    /**
     * Setter for property this.getConnection().
     *
     * @param connection
     */
    /*
     public void setConnection(DatabaseConnection connection) {
     //  this.connection = connection;
     }
     */
    /**
     * Get the configuration information stored in this object.
     *
     * @return The configuration information stored in this object.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Set the configuration information in this object.
     *
     * @param config The configuration information.
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    public static String getCurrentTimestamp() {

        return new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();

        /*
         StringBuilder timestamp = new StringBuilder();
         Calendar now = Calendar.getInstance();
         timestamp.append(now.get(Calendar.YEAR));
         timestamp.append(now.get(Calendar.MONTH));
         timestamp.append(now.get(Calendar.DATE));
         timestamp.append(now.get(Calendar.HOUR_OF_DAY));
         timestamp.append(now.get(Calendar.MINUTE));
         timestamp.append(now.get(Calendar.SECOND));
         timestamp.append(now.get(Calendar.MILLISECOND));
        
         return timestamp.toString();
         */
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {

        Configuration config = new Configuration();
        config.setDatabaseHostname("localhost");
        config.setDatabaseName("direct");

        Collection<String> contacts = new ArrayList<String>();
        contacts.add("contact1@nist.gov");
        contacts.add("contact2@nist.gov");

        try {
            DatabaseFacade df;
            df = new DatabaseFacade(config); // .getInstance(config);
            df.addNewDirectEmail("guy@example.com");
            df.addUsernamePassword("guy", "dontcare");
            df.addUsernameToDirectMapping("guy", "guy@example.com");

            df.deleteDirectEmail("guy@example.com", "guy");
         //   System.out.println(df.doesUsernameDirectMappingExist("guy","guy@example.com"));
/*
             System.out.println(Calendar.getInstance().getTime().toString());
             for(int i = 0; i < 100; i++) {
                
             df.addNewDirectAndContactEmail("1direct" + i + "@fake.com", "1contact" + i + "@gmail.com");
             }
             System.out.println(Calendar.getInstance().getTime().toString());
             */

            //   df.deleteDirectEmail("direct32@fake.com");
        } catch (Exception ex) {
            //  Logger.getLogger(DatabaseFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
