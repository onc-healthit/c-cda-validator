package gov.nist.healthcare.ttt.webapp.common.controller;

import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.ExceptionJSONInfo;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionJSONInfo handleGenericException(HttpServletRequest request, Exception ex){
		
		// Get username for logging
		String username = "anonymous";
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			username = principal.getName();
		}
		
		// Log the error first
		ex.printStackTrace();
    	logger.error(username + ": " + ex.getMessage());
    	
		ExceptionJSONInfo response = new ExceptionJSONInfo();
	    response.setUrl(request.getRequestURL().toString());
	    
	    // Get the exception message and code
	    String code = "No Code";
	    String message = ex.getMessage();
	    response.setMessage(message);
	    response.setCode(code);
	     
	    return response;
    }
     
    @ExceptionHandler(TTTCustomException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionJSONInfo handleTTTCustomException(HttpServletRequest request, TTTCustomException ex){
    	
    	// Get username for logging
    	String username = "anonymous";
    	Principal principal = request.getUserPrincipal();
    	if (principal != null) {
    		username = principal.getName();
    	}

    	// Log the error first
    	logger.error(username + ": " + ex.getMessage());

    	ExceptionJSONInfo response = new ExceptionJSONInfo();
	    response.setUrl(request.getRequestURL().toString());
	    
	    // Get the exception message and code
	    response.setMessage(ex.getMessage());
	    response.setCode(ex.getCode());
	     
	    return response;
    }

}
