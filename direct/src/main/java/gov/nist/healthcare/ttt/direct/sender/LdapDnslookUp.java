package gov.nist.healthcare.ttt.direct.sender;

import org.apache.log4j.Logger;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapDnslookUp {

	private static Logger logger = Logger.getLogger(LdapDnslookUp.class.getName());
	
	public InputStream getLdapCert(String email) {
		ArrayList<String> domains = getLDAPServer(getTargetDomain(email));
        Iterator<String> it = domains.iterator();
        InputStream cert = null;
        while(cert==null && it.hasNext()) {
            cert = getCert(it.next(), email);
        }
        return cert;
	}
	
	
	public ArrayList<String> getLDAPServer(String domain) {

		ArrayList<String> res = new ArrayList<>();
		String query = "_ldap._tcp." + domain;

		try {
			Record[] records = new Lookup(query, Type.SRV).run();

			if (records != null) {
				for (Record record : records) {
					SRVRecord srv = (SRVRecord) record;

					String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
					int port = srv.getPort();

					logger.info("DNS SRV query found LDAP at " + hostname + ":" + port);
					res.add(hostname + ":" + port);
				}
			}
		} catch (TextParseException e) {
			logger.info("Error trying to get Ldap certificate " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.info("Error trying to get Ldap certificate " + e.getMessage());
			e.printStackTrace();
		}

		return res;
	}

	private String getBaseDnSearchBase(DirContext ctx) throws NamingException {
        Attributes attrs = ctx.getAttributes("", new String[]{"namingContexts"});
        //System.out.println(" attrs1 = " + attrs.toString());
        
        Attribute baseAttr = attrs.get("namingContexts");
        
        String searchBase = "";
       
        if (baseAttr != null) {
	        NamingEnumeration<?>  ids = baseAttr.getAll();
	        while(ids.hasMoreElements()){
	            Object obj = ids.next();
	            //System.out.println(obj.toString());
	            
	            searchBase +=  (", " + obj.toString());
	        }
	        
	        if (searchBase.startsWith(", ")) {
	        	searchBase = searchBase.substring(2);
	        }
        }
        
        return searchBase;
	}
	
	@SuppressWarnings("rawtypes")
	public InputStream getCert(String domain, String email) {
		InputStream cert = null;

        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://"+ domain);
        //env.put(Context.REFERRAL, "follow");
        String searchBase = "";
        DirContext ctx = null;
        NamingEnumeration results = null;
        try {
            ctx = new InitialDirContext(env);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        	String filterString = "(|(mail=" + email + ")(mail=" + getTargetDomain(email) +"))";
            try {
            	results = ctx.search(searchBase, filterString, controls);
            } catch (NamingException e) {
            	searchBase = getBaseDnSearchBase(ctx);
            	logger.info("Empty Base Dn search failed with " + e.getMessage() + ". Trying with searchbase - " + searchBase);
            	results = ctx.search(searchBase, filterString, controls);
            	logger.info("search results with entries  = " + results != null ? results.toString() : 0);
            	System.out.println("search results with entries  = " + results != null ? results.toString() : 0);
            }
            
            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("cn");
                String cn = (String) attr.get();
                //System.out.println(" Person Common Name = " + cn);

                Attribute certAttribute = null;
                for (NamingEnumeration ae = attributes.getAll(); ae.hasMore();) {
                    Attribute attributesL = (Attribute) ae.next();
                    System.out.println("attribute: " + attributesL.getID());
                    if (attributesL.getID().startsWith("userCertificate")) {
                    	certAttribute = attributes.get(attributesL.getID());
                    	break;
                    }
                    /* print each value 
                    for (NamingEnumeration e = certAttribute.getAll(); e.hasMore(); System.out
                        .println("value: " + e.next()))*/
                      ;
                  }
                
                certAttribute = attributes.get("userCertificate");
                if (certAttribute == null) {
                	certAttribute = attributes.get("userCertificate;binary");
                }

                try {
                    cert = new ByteArrayInputStream((byte[]) certAttribute.get());
                    logger.info("Found certificate for " + email + " at " + domain);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return cert;
    }
	
	public static String getTargetDomain(String targetedTo) {
		// Get the targeted domain
		String targetDomain = targetedTo;
		if(targetedTo.contains("@")) {
			targetDomain = targetedTo.split("@", 2)[1];
		}
		return targetDomain;
	}

}
