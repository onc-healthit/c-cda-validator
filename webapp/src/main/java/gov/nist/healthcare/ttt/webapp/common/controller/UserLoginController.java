package gov.nist.healthcare.ttt.webapp.common.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import gov.nist.healthcare.ttt.webapp.common.model.login.UserLogInfo;
import gov.nist.healthcare.ttt.webapp.common.model.login.UserLogin;

@Controller
@RequestMapping("api/login")
public class UserLoginController {

	@Value("${admin.user}")
	String adminUser;

	@Autowired
	private DatabaseInstance db;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    UserLogInfo getCurrentUser(HttpServletRequest request) {
    	Principal principal = request.getUserPrincipal();
		if (principal != null) {
			if (adminUser.equals(principal.getName())) {
				return new UserLogInfo("", false);
			} else {
				return new UserLogInfo(principal.getName(), true);
			}
		} else {
        	return new UserLogInfo("", false);
        }
    }

	@RequestMapping(value="/register", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean registerNewUser(@RequestBody UserLogin newUser) throws DatabaseException, TTTCustomException {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		if(db.getDf().doesUsernameExist(newUser.getUsername())) {
			throw new TTTCustomException("0x0044", "User already exists");
		} else {
			if(db.getDf().addUsernamePassword(newUser.getUsername(), encoder.encode(newUser.getPassword())))
				return true;
			else
				return false;
		}
	}

}
