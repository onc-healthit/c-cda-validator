package gov.nist.healthcare.ttt.webapp.common.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;

@Service
public class UserAuth implements UserDetailsService {

	private static Logger logger = Logger.getLogger(UserAuth.class.getName());

	@Value("${admin.user}")
	String adminUser;

	@Value("${admin.password}")
	String adminPassword;

	@Autowired
	private DatabaseInstance db;

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("user"));
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		boolean isAdminLogin = (attr.getRequest().getParameter("AdminLogin") != null) ? true : false;

		try {
			if (isAdminLogin) {
				if (adminUser.equals(username)) {
					return new User(username, encoder.encode(adminPassword), authorities);
				}
			} else if (db.getDf().doesUsernameExist(username)) {
				return new User(username, db.getDf().getPasswordForUsername(username), authorities);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		logger.log(Level.WARNING, "User " + username + " not found.");
		throw new UsernameNotFoundException("User " + username + " not found.");
	}

}
