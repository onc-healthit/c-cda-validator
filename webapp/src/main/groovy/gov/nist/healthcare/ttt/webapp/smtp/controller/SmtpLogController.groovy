package gov.nist.healthcare.ttt.webapp.smtp.controller

import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeLogImpl;
import gov.nist.healthcare.ttt.database.smtp.SmtpEdgeProfileInterface;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smtpLog")
class SmtpLogController {

	@Autowired
	private DatabaseInstance db
	
	static Logger logger = Logger.getLogger(SmtpLogController.class.getName())
	
	@RequestMapping(value = "/{profile:.+}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	List<SmtpEdgeLogImpl> getLogsForProfile(@PathVariable profile, Principal principal) throws Exception {
		// Check if user is connected
		String username
		if (principal != null) {
			username = principal.getName()
		} else {
			throw new TTTCustomException("0x020", "You must be logged to access this feature")
		}
		
		db.getSmtpEdgeLogFacade().getLatestSmtpEdgeLogInterface(username, profile)
		
	}
	
	@RequestMapping(value = "/{profile:.+}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	boolean addLog(@PathVariable String profile, @RequestBody SmtpEdgeLogImpl log, Principal principal) throws Exception {
		if (principal != null) {
			if(profile != null && !profile.equals("")) {
				try {
					db.getSmtpEdgeLogFacade().addNewSmtpLog(log, principal?.getName(), profile)
				} catch(Exception e) {
					logger.info("Could not log the test: " + e.getMessage());
				}
			}
		}
	}
	
}
