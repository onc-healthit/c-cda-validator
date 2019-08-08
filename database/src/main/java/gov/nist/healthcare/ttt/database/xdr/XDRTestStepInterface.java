/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.ttt.database.xdr;

import java.util.List;

/**
 * Created Oct 17, 2014 1:50:13 PM
 * @author mccaffrey
 */
public interface XDRTestStepInterface {

    /**
     * @return the messageId
     */
    String getMessageId();

    String getName();
    
    /**
     * @return the reportItems
     */
    List<XDRReportItemInterface> getXdrReportItems();


    /**
     * @return the timestamp
     */
    String getTimestamp();

    XDRSimulatorInterface getXdrSimulator();
    
    Status getStatus();
    
    String getHostname();
    
    String getDirectFrom();
    
    /**
     * @param messageId the messageId to set
     */
    void setMessageId(String messageId);

    void setName(String name);
    
    /**
     * @param reportItems the reportItems to set
     */
    void setXdrReportItems(List<XDRReportItemInterface> reportItems);


    /**
     * @param timestamp the timestamp to set
     */
    void setTimestamp(String timestamp);

    
    void setXdrSimulator(XDRSimulatorInterface xdrSimulator);
    
    void setStatus(Status status);
    
    void setHostname(String hostname);
    
    void setDirectFrom(String directFrom);
}
