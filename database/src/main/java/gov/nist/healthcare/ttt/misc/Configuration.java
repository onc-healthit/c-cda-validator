
package gov.nist.healthcare.ttt.misc;

/**
 * Created Apr 25, 2014 4:06:05 PM
 * @author mccaffrey
 */
public class Configuration {

    private String databaseHostname = null;
    private String databaseName = null;
    private String databaseUsername = null;
    private String databasePassword = null;

    /**
     * @return the databaseHostname
     */
    public String getDatabaseHostname() {
        return databaseHostname;
    }

    /**
     * @param databaseHostname the databaseHostname to set
     */
    public void setDatabaseHostname(String databaseHostname) {
        this.databaseHostname = databaseHostname;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the databaseUsername
     */
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    /**
     * @param databaseUsername the databaseUsername to set
     */
    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    /**
     * @return the databasePassword
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * @param databasePassword the databasePassword to set
     */
    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}
