package gov.nist.healthcare.ttt.webapp.direct.model.ccdaReport;

import com.fasterxml.jackson.databind.JsonNode

public class CCDAReport {
	
	private String filename
	private JsonNode ccdaReport
	public CCDAReport(String filename, JsonNode ccdaReport) {
		super();
		this.filename = filename;
		this.ccdaReport = ccdaReport;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public JsonNode getCcdaReport() {
		return ccdaReport;
	}
	public void setCcdaReport(JsonNode ccdaReport) {
		this.ccdaReport = ccdaReport;
	}

}
