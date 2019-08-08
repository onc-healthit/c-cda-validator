/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.ttt.database.xdr;

/**
 * Created Oct 17, 2014 1:47:38 PM
 * @author mccaffrey
 */
public interface XDRReportItemInterface {

    public enum ReportType {        
        REQUEST,
        RESPONSE,
        VALIDATION_REPORT,
        UNCLASSIFIED
    }
    
    
    /**
     * @return the report
     */
    String getReport();

    ReportType getReportType();
    
    /**
     * @param report the report to set
     */
    void setReport(String report);

    void setReportType(ReportType report);
    
}
