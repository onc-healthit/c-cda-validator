package gov.nist.healthcare.ttt.webapp.direct.controller;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.ObjectWrapper.ObjWrapper;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/api/registration")
public class DirectRegistrationController {

	@Autowired
	private DatabaseInstance db;

	/**
	 * Return the list of Direct email for logged in user
	 * @param request
	 * @return
	 * @throws gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException
	 * @throws DatabaseException 
	 */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/direct", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<String> directList(HttpServletRequest request) throws TTTCustomException, DatabaseException {

		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			String username = principal.getName();
			
			List<String> listRes = new ArrayList<String>();
			Collection<String> result = db.getDf().getDirectEmailsForUser(username);
			Iterator<String> it = result.iterator();
			while(it.hasNext()) {
				listRes.add(it.next());
			}
			return listRes;
		}
		throw new TTTCustomException("0x0011", "Problem trying to get username");
	}

	/**
	 * Add a new Direct address in the database
	 * @param newDirect
	 * @param request
	 * @return
	 * @throws TTTCustomException 
	 * @throws DatabaseException 
	 */
	@RequestMapping(value = "/direct", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean addDirectAddress(
			@RequestBody String newDirect, HttpServletRequest request) throws TTTCustomException, DatabaseException {

		// Create the direct address
		db.getDf().addNewDirectEmail(newDirect);

		boolean res = false;

		// Bind address to user if logged in
		if(!db.getDf().isDirectMappedToAUsername(newDirect)) {
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				String username = principal.getName();
				res = db.getDf().addUsernameToDirectMapping(username, newDirect);
			}
		} else {
			throw new TTTCustomException("0x0014", "This direct address is already used by another user!");
		}

		return res;
	}
	
	/**
	 * Delete a specific Direct address if the User is logged in
	 * @param directToDelete
	 * @param request
	 * @return
	 * @throws TTTCustomException
	 * @throws DatabaseException 
	 */
	@RequestMapping(value = "/direct/{direct:.+}", method = RequestMethod.DELETE, produces = "application/json")
	public @ResponseBody boolean deleteDirectAddress(@PathVariable String direct, HttpServletRequest request) throws TTTCustomException, DatabaseException {

		boolean res = false;
		if(db.getDf().isDirectMappedToAUsername(direct)) {
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				String username = principal.getName();

				if(db.getDf().doesUsernameDirectMappingExist(username, direct)) {
					res = db.getDf().deleteDirectEmail(direct, username);
				} else {
					throw new TTTCustomException("0x0012", "You cannot delete a Direct address that you did not create");
				}
			} else {
				throw new TTTCustomException("0x0012", "You must be logged to delete this direct address");
			}

		} else {
			res = db.getDf().deleteDirectEmail(direct, null);
		}

		return res;
	}
	
	/**
	 * Return the list of contact email for one specific Direct email
	 * @param direct
	 * @return
	 * @throws TTTCustomException
	 * @throws DatabaseException 
	 */
	@RequestMapping(value = "/contact/{direct:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<ObjWrapper<String>> getContactList(@PathVariable String direct) throws TTTCustomException, DatabaseException {

		List<ObjWrapper<String>> listRes = new ArrayList<ObjWrapper<String>>();
		Collection<String> result = db.getDf().getContactAddresses(direct);
		Iterator<String> it = result.iterator();
		while (it.hasNext()) {
			listRes.add(new ObjWrapper<String>(it.next()));
		}
		return listRes;
	}
	
	/**
	 * Add a contact email for a specific Direct address
	 * @param direct
	 * @param newContact
	 * @return
	 * @throws TTTCustomException
	 * @throws DatabaseException 
	 */
	@RequestMapping(value = "/contact/{direct:.+}", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody boolean addContactToDirect(@PathVariable String direct, @RequestBody String newContact) throws TTTCustomException, DatabaseException {
		if(newContact == null || newContact.equals("")) {
			throw new TTTCustomException("0x0013", "Contact email must be a valid email");
		}
		boolean res = false;
		res = (db.getDf().addNewDirectAndContactEmail(direct, newContact) != null);
		
		return res;
	}
	
	/**
	 * Delete a specific contact-direct binding
	 * @param direct
	 * @param contactToDelete
	 * @return
	 * @throws TTTCustomException
	 * @throws DatabaseException 
	 */
	@RequestMapping(value = "/contact/{direct:.+}/{contact:.+}", method = RequestMethod.DELETE, produces = "application/json")
	public @ResponseBody boolean deleteContact(@PathVariable String direct, @PathVariable String contact) throws TTTCustomException, DatabaseException {
		
		boolean res = false;
		res = db.getDf().deleteSpecificContactEmail(direct, contact);
		
		return res;
	}
}
