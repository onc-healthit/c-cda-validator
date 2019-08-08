package gov.nist.healthcare.ttt.webapp.direct.controller

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.database.log.LogInterface;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/findmdn")
class FindMdnForDirectMessageController {
	
	static Logger logger = Logger.getLogger(FindMdnForDirectMessageController.class.getName())
	
	@Autowired
	private DatabaseInstance db
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	LogInterface getMdn(@RequestBody String messageId, HttpServletRequest request) throws IOException, DatabaseException, TTTCustomException {

		logger.debug("Getting MDN for message " + messageId)
		LogInterface log = db.getLogFacade().getLogByOriginalMessageId(messageId)
	}
}