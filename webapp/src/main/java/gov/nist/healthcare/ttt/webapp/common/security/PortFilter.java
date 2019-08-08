package gov.nist.healthcare.ttt.webapp.common.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.web.filter.OncePerRequestFilter;

public class PortFilter extends OncePerRequestFilter {
	
	private int serverPort;
	
	public PortFilter(int port) {
		this.serverPort = port;
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof RequestFacade) {
            RequestFacade requestFacade = (RequestFacade) request;
            if (requestFacade.getServerPort() != this.serverPort && !isWhiteList(requestFacade.getRequestURI())) {
                // only allow unsecured requests to access whitelisted endpoints
            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Use HTTPS port");
            }
        }
        filterChain.doFilter(request, response);
    }

    private static HashSet<String> uriWhitelist = new HashSet<>(4);
    static {
        // static website content
        uriWhitelist.add("api/xdrNotification/.*");
        uriWhitelist.add("api/xdrvalidator/receive/.*");
        
        // public APIs
        // uriWhitelist.add("/public");
    }
    
    private static boolean isWhiteList(String uri) {
    	for(String pattern : uriWhitelist) {
    		Pattern r = Pattern.compile(pattern);
    		Matcher m = r.matcher(uri);
    	      if (m.find()) {
    	    	  return true;
    	      }
    	}
    	return false;
    }
	
}