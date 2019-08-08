/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nist.healthcare.ttt.database.xdr;

/**
 *
 * @author mccaffrey
 */
public interface XDRVanillaInterface {
    
    /*
    private String request = null;
    private String response = null;
    private String samlReport = null;
    private String simId = null;
    private String timestamp = null;
*/
    /**
     * @return the request
     */
    public String getRequest();
    

    /**
     * @param request the request to set
     */
    void setRequest(String request);

    /**
     * @return the response
     */
    public String getResponse();

    /**
     * @param response the response to set
     */
    public void setResponse(String response);

    /**
     * @return the samlReport
     */
    public String getSamlReport();

    /**
     * @param samlReport the samlReport to set
     */
    public void setSamlReport(String samlReport);

    /**
     * @return the simId
     */
    public String getSimId();

    /**
     * @param simId the simId to set
     */
    public void setSimId(String simId);

    /**
     * @return the timestamp
     */
    public String getTimestamp();

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp);
    
}
