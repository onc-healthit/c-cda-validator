/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nist.healthcare.ttt.database.jdbc;

import gov.nist.healthcare.ttt.misc.Configuration;
import java.util.UUID;

/**
 *
 * @author mccaffrey
 */
public class DatabaseLoadTestingRetrieve implements Runnable  {

    @Override
    public void run() {
        
        Configuration config = new Configuration();
        config.setDatabaseHostname("localhost");
        config.setDatabaseName("direct");

        try {
            DatabaseFacade df;
            df = new DatabaseFacade(config);
            
            String salt = UUID.randomUUID().toString();
            
            
            Thread.currentThread().getId();
            for(int i = 0; i < 1000; i++) {                
                //df.addNewDirectAndContactEmail(i + "-" + salt + "@fakedirect.com",  i + "-" + salt + "@fakecontact.com");                
                df.doesDirectAndContactExist(salt, salt);
            }
                        
        } catch (Exception ex) {
            ex.printStackTrace();
            //  Logger.getLogger(DatabaseFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
