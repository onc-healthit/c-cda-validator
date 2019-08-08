/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.ttt.database.xdr;

/**
 * Created Oct 17, 2014 2:28:26 PM
 * @author mccaffrey
 */
public class XDRSimulatorImpl implements XDRSimulatorInterface {

    private String XDRSimulatorID = null;
    private String simulatorId = null;
    private String endpoint = null;
    private String endpointTLS = null;

    /**
     * @return the XDRSimulatorID
     */
    public String getXDRSimulatorID() {
        return XDRSimulatorID;
    }

    /**
     * @param XDRSimulatorID the XDRSimulatorID to set
     */
    public void setXDRSimulatorID(String XDRSimulatorID) {
        this.XDRSimulatorID = XDRSimulatorID;
    }

    /**
     * @return the simulatorId
     */
    @Override
    public String getSimulatorId() {
        return simulatorId;
    }

    /**
     * @param simulatorId the simulatorId to set
     */
    @Override
    public void setSimulatorId(String simulatorId) {
        this.simulatorId = simulatorId;
    }

    /**
     * @return the endpoint
     */
    @Override
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the endpointTLS
     */
    @Override
    public String getEndpointTLS() {
        return endpointTLS;
    }

    /**
     * @param endpointTLS the endpointTLS to set
     */
    @Override
    public void setEndpointTLS(String endpointTLS) {
        this.endpointTLS = endpointTLS;
    }

    
}
