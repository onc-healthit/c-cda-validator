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
public class CCDAValidationReportImpl implements CCDAValidationReportInterface {
    
    private String filename = null;
    private String validationReport = null;

    /**
     * @return the filename
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the validationReport
     */
    @Override
    public String getValidationReport() {
        return validationReport;
    }

    /**
     * @param validationReport the validationReport to set
     */
    @Override
    public void setValidationReport(String validationReport) {
        this.validationReport = validationReport;
    }
    
    
}
