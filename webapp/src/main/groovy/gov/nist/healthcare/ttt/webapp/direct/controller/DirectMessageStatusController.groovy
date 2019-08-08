package gov.nist.healthcare.ttt.webapp.direct.controller;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.database.log.LogInterface;
import gov.nist.healthcare.ttt.database.log.LogInterface.Status;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import gov.nist.healthcare.ttt.webapp.direct.model.messageStatus.MessageStatusDetail;
import gov.nist.healthcare.ttt.webapp.direct.model.messageStatus.MessageStatusList;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@RestController
@RequestMapping("/api/directMessageStatus")
class DirectMessageStatusController {
	
	static Logger logger = Logger.getLogger(DirectMessageStatusController.class.getName())
	
	@Autowired
	private DatabaseInstance db
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/outgoing", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Collection<MessageStatusList> getAllOutgoingLogsForUser(HttpServletRequest request) throws IOException, DatabaseException, TTTCustomException {

		getLogs(false, request)
	}
	
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/incoming", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	Collection<MessageStatusList> getAllIncomingLogsForUser(HttpServletRequest request) throws IOException, DatabaseException, TTTCustomException {
		
		getLogs(true, request)
	}
	
	Collection<MessageStatusList> getLogs(boolean incoming, HttpServletRequest request) throws TTTCustomException, DatabaseException {
		String username = ""
		Collection<MessageStatusList> logList = new ArrayList<MessageStatusList>()
		
		// Check if user is connected
		Principal principal = request.getUserPrincipal()
		if (principal != null) {
			username = principal.getName()
		} else {
			logger.info("You must be logged to acces this feature")
			throw new TTTCustomException("0x020", "You must be logged to access this feature")
		}
		
		// Iterate for each direct for the user
		Collection<String> directList = db.getDf().getDirectEmailsForUser(username)
		
		directList.each {
			if(incoming) {
				logList.add(convertLog(true, it, db.getLogFacade().getIncomingByFromLine(it)))
			} else {
				logList.add(convertLog(false, it, db.getLogFacade().getOutgoingByToLine(it)))
			}
		}
			
		logList
	}
	
	MessageStatusList convertLog(boolean incoming, String direct, Collection<LogInterface> logList) throws DatabaseException {
		Iterator<LogInterface> logIt = logList.iterator()
		
		Collection<LogInterface> tmpDetail = new ArrayList<LogInterface>()
		
		logList.each { 
			if(!it.isMdn()) {
				updateMdnStatus(it)
				tmpDetail.add(new MessageStatusDetail(it))
			}
		}
		
		def tmpList = new MessageStatusList(convertAddressType(incoming), direct, tmpDetail)
	}
	
	void updateMdnStatus(LogInterface tmpLog) {
		// Check if MDN timed out
		if(!tmpLog.getIncoming() && tmpLog.getStatus().equals(Status.WAITING)) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z (zzz)")
			DateTime dt = formatter.parseDateTime(tmpLog.getOrigDate())
			dt = dt.plusMinutes(60)
			if(dt.isBeforeNow()) {
				tmpLog.setStatus(Status.TIMEOUT)
				db.getLogFacade().updateStatus(tmpLog.getMessageId(), Status.TIMEOUT)
			}
		}
	}
	
	String convertAddressType(boolean incoming) {
		incoming ? "From" : "To"
	}
	
	String getGoodAddressType(boolean incoming, LogInterface logEntry) {
		incoming ? logEntry.getToLine().iterator().next() : logEntry.getFromLine().iterator().next()
	}
	
}
