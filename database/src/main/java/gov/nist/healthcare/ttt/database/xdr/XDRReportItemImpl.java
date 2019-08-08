/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.healthcare.ttt.database.xdr;

/**
 * Created Oct 10, 2014 2:34:36 PM
 * @author mccaffrey
 */
public class XDRReportItemImpl implements XDRReportItemInterface {

    
    
    private String XDRReportItemID = null;
    private String report = null;
    private ReportType reportType = null;
    
    /**
     * @return the XDRReportItemID
     */
    public String getXDRReportItemID() {
        return XDRReportItemID;
    }

    /**
     * @param XDRReportItemID the XDRReportItemID to set
     */
    public void setXDRReportItemID(String XDRReportItemID) {
        this.XDRReportItemID = XDRReportItemID;
    }

    /**
     * @return the report
     */
    @Override
    public String getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    @Override
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * @return the reportType
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * @param reportType the reportType to set
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
}
