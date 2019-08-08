/*
 * DatabaseConnection.java
 *
 * Created on April 25, 2014, 4:02 PM
 */
package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.misc.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author mccaffrey
 */
public class DatabaseConnection {

    private Configuration config = null;
    private static Connection con;
    private static Statement stmt;
    private boolean successfulConnection = false;

    /*
     public DatabaseConnection(Configuration config) throws SQLException {
     this.setHostname("localhost");
     this.setConfig(config);
     this.initialize();
     }
     */
    /**
     * Creates a new instance of JdbcConnection
     *
     * @param config
     * @throws
     */
    public DatabaseConnection(Configuration config) throws DatabaseException {
        this.setConfig(config);
        this.initialize();
    }

    private void initialize() throws DatabaseException {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = null;
            url = "jdbc:mysql://" + this.getConfig().getDatabaseHostname() + "/" + this.getConfig().getDatabaseName() + "?autoReconnect=true&amp;useSSL=false";

            // System.out.println("Connecting to mysql on url " + url);

            if (this.getConfig().getDatabaseUsername() != null) {
                con = DriverManager.getConnection(url, this.getConfig().getDatabaseUsername(), this.getConfig().getDatabasePassword());
            } else {
                con = DriverManager.getConnection(url);
            }
            stmt = con.createStatement();
            successfulConnection = true;

        } catch (Exception e) {
            e.printStackTrace();

            throw new DatabaseException(e.getMessage());

        }
    }

    /**
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        con.close();
    }

    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet result = null;
        result = stmt.executeQuery(sql);
        return result;
    }

    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        int i = 0;
        i = stmt.executeUpdate(sql);
        return i;
    }

    /**
    *
    * @param sql
    * @return
    * @throws SQLException
    */
   public boolean execute(String sql) throws SQLException {
       boolean bo = false;
       bo = stmt.execute(sql);
       return bo;
   }
    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int[] executeBatchUpdate(Collection<String> sql) throws SQLException {
        int i[] = {0};
        Iterator<String> it = sql.iterator();
        while (it.hasNext()) {
            stmt.addBatch(it.next());
        }
        i = stmt.executeBatch();

        return i;
    }

    /**
     *
     * @return @throws SQLException
     */
    public boolean isAlive() throws SQLException {
        return !con.isClosed();
    }

    /**
     *
     * @param input
     * @return
     */
    public static String makeSafe(String input) {
        if (input == null) {
            return null;
        }
        // String output = input.replaceAll("\\" , "\\\\");
        String output = input.replaceAll("'", "''");

//        output = output.replaceAll("<", "&lt;");
        return output;
    }

    /**
     *
     * @return
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     *
     * @param config
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    public static void main(String[] args) {


        String test = "hello ' world";
        System.out.println(makeSafe(test));
    }

}
