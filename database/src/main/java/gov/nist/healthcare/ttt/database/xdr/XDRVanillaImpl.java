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
public class XDRVanillaImpl implements XDRVanillaInterface {
    
    private String request = null;
    private String response = null;
    private String samlReport = null;
    private String simId = null;
    private String timestamp = null;

    /**
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * @return the samlReport
     */
    public String getSamlReport() {
        return samlReport;
    }

    /**
     * @param samlReport the samlReport to set
     */
    public void setSamlReport(String samlReport) {
        this.samlReport = samlReport;
    }

    /**
     * @return the simId
     */
    public String getSimId() {
        return simId;
    }

    /**
     * @param simId the simId to set
     */
    public void setSimId(String simId) {
        this.simId = simId;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
}
