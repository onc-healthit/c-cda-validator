/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.ttt.database.xdr;

/**
 * Created Oct 20, 2014 11:45:10 AM
 * @author mccaffrey
 */
public interface XDRSimulatorInterface {

    /**
     * @return the endpoint
     */
    String getEndpoint();

    /**
     * @return the endpointTLS
     */
    String getEndpointTLS();

    /**
     * @return the simulatorId
     */
    String getSimulatorId();

    /**
     * @param endpoint the endpoint to set
     */
    void setEndpoint(String endpoint);

    /**
     * @param endpointTLS the endpointTLS to set
     */
    void setEndpointTLS(String endpointTLS);

    /**
     * @param simulatorId the simulatorId to set
     */
    void setSimulatorId(String simulatorId);

}
