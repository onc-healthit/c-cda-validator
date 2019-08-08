package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.misc.Configuration;
import java.util.UUID;

/**
 *
 * @author mccaffrey
 */
public class DatabaseLoadTesting implements Runnable {

    @Override
    public void run() {

        Configuration config = new Configuration();
        config.setDatabaseHostname("localhost");
        config.setDatabaseName("direct");

        try {
            DatabaseFacade df;
            df = new DatabaseFacade(config);
            
            String salt = UUID.randomUUID().toString();
            
            for(int i = 0; i < 100; i++) {                
                df.addNewDirectAndContactEmail(i + "-" + salt + "@fakedirect.com",  i + "-" + salt + "@fakecontact.com");                
            }
                        
        } catch (Exception ex) {
            ex.printStackTrace();
            //  Logger.getLogger(DatabaseFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        
        (new Thread(new DatabaseLoadTesting())).start();
      //  (new Thread(new DatabaseLoadTesting())).start();
        
        
    }
    
    
}
