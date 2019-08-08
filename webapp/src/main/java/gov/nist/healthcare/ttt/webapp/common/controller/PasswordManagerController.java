package gov.nist.healthcare.ttt.webapp.common.controller;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import gov.nist.healthcare.ttt.webapp.common.model.login.ChangePasswordModel;
import gov.nist.healthcare.ttt.webapp.direct.listener.Emailer;
import gov.nist.healthcare.ttt.webapp.direct.listener.EmailerModel;

import java.security.Principal;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/passwordManager")
public class PasswordManagerController {

	private static Logger logger = Logger.getLogger(PasswordManagerController.class.getName());

	@Autowired
	private DatabaseInstance db;

	// Emailer settings
	@Autowired
	private EmailerModel emailerModel;

	@RequestMapping(value="/change", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean changePassword(@RequestBody ChangePasswordModel changePassword, Principal principal) throws DatabaseException, TTTCustomException {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		if (principal == null) {
			throw new TTTCustomException("0x0035", "You must be logged in to save profile");
		} else {
			String username = principal.getName();
			if(encoder.matches(changePassword.getOldPassword(), db.getDf().getPasswordForUsername(username))) {
				return db.getDf().changePassword(username, encoder.encode(changePassword.getNewPassword()));
			} else {
				throw new TTTCustomException("0x0043", "Old password is not correct");
			}
		}
	}

	@RequestMapping(value="/forgot", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody boolean forgotPassword(@RequestBody String username) throws DatabaseException, TTTCustomException {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		if(db.getDf().doesUsernameExist(username)) {
			String uuid = UUID.randomUUID().toString();
			if(db.getDf().changePassword(username, encoder.encode(uuid))) {
				logger.info("New password for user: " + username + " password: " + uuid);
				Emailer emailer = new Emailer(emailerModel);
				try {
					emailer.sendEmail2(username, "TTP forgot password", "Your password for user: " + username + " is: " + uuid);
				} catch (Exception e) {
					e.printStackTrace();
					throw new TTTCustomException("0x0045", "Password changed but could not send email. Contact us.");
				}
				return true;
			} else {
				throw new TTTCustomException("0x0045", "Problem trying to generate new password");
			}
		} else {
			throw new TTTCustomException("0x0044", "This username does not exist");
		}
	}

}
