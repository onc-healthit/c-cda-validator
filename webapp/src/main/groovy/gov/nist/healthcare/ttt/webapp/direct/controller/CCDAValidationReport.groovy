package gov.nist.healthcare.ttt.webapp.direct.controller;

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import gov.nist.healthcare.ttt.database.log.CCDAValidationReportInterface;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.direct.model.ccdaReport.CCDAReport;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonParser;

@Controller
@RequestMapping("/api/ccdaReport")
public class CCDAValidationReport {
	private static Logger logger = Logger.getLogger(CCDAValidationReport.class.getName());
	
	@Autowired
	private DatabaseInstance db;

	@RequestMapping(value = "/{messageId:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<CCDAReport> getCCDAForMessageId(@PathVariable String messageId, HttpServletRequest request) throws Exception {
		List<CCDAReport> ccdaReports = new ArrayList<CCDAReport>() 
		db.getLogFacade().getCCDAValidationReportByMessageId(messageId).each { 
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
				String report = it.getValidationReport();
				// Escape new lines
				if(report.contains("\n")) {
					report = report.replace("\n", "\\n");
					report = report.replaceAll("\\p{Cc}", "")
				}
				JsonNode jsonObject = mapper.readTree(report)	
				ccdaReports.add(new CCDAReport(it.getFilename(), jsonObject))
		}
		return ccdaReports
	}
}
