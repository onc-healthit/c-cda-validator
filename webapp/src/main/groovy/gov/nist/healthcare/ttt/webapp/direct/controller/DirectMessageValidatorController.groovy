package gov.nist.healthcare.ttt.webapp.direct.controller;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.direct.messageProcessor.DirectMessageProcessor;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import gov.nist.healthcare.ttt.model.logging.LogModel;
import gov.nist.healthcare.ttt.webapp.direct.model.messageValidator.MessageValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("/api/directValidator")
public class DirectMessageValidatorController {
	
private static Logger logger = Logger.getLogger(DirectMessageValidatorController.class.getName());
	
	@Autowired
	private DatabaseInstance db;
	
	@Value('${ett.mdht.r2.url}')
	String mdhtR2Url;
	
	@Value('${ett.mdht.r1.url}')
	String mdhtR1Url;
	
	@Value('${toolkit.url}')
	String toolkitUrl;
	
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
    LogModel validateDirectMessage(@RequestBody MessageValidator validator, HttpServletRequest request) throws Exception {
		FileInputStream messageFile;
		FileInputStream certFile;
		
		try {
			messageFile = new FileInputStream(new File(validator.getMessageFilePath()));
		} catch(FileNotFoundException e) {
			throw new TTTCustomException("0x0028", "You need to upload a message file");
		}
		try {
			certFile = new FileInputStream(new File(validator.getCertFilePath()));
		} catch(FileNotFoundException e) {
			messageFile.close();
			throw new TTTCustomException("0x0028", "You need to upload a private certificate");
		}
		
		// Validate the message
		logger.debug("Started validation of message");
		DirectMessageProcessor processor = new DirectMessageProcessor(messageFile, certFile, validator.getCertPassword(), mdhtR1Url, mdhtR2Url, toolkitUrl);
		processor.processDirectMessage();
		logger.info("Validating message" + processor.getLogModel().getMessageId() + " done");
		
		try {
			db.getLogFacade().addNewLog(processor.getLogModel());
			db.getLogFacade().addNewPart(processor.getLogModel().getMessageId(), processor.getMainPart());
			if(processor.hasCCDAReport()) {
				processor.getCcdaReport().each {
					db.getLogFacade().addNewCCDAValidationReport(processor.getLogModel().getMessageId(), it);					
				}
			}
		} catch(DatabaseException e) {
			e.printStackTrace();
			return processor.getLogModel();
		}
		
		return processor.getLogModel();
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public void validateDirectMessage(HttpServletRequest request) throws Exception {
		
	}
}
