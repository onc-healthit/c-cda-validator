/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nist.healthcare.ttt.database.log;

/**
 *
 * @author mccaffrey
 */
public interface CCDAValidationReportInterface {

    /**
     * @return the filename
     */
    String getFilename();

    /**
     * @return the validationReport
     */
    String getValidationReport();

    /**
     * @param filename the filename to set
     */
    void setFilename(String filename);

    /**
     * @param validationReport the validationReport to set
     */
    void setValidationReport(String validationReport);
    
}
