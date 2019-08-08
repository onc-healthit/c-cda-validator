package gov.nist.healthcare.ttt.smtp.server;

import java.io.IOException;
import java.net.UnknownHostException;

import gov.nist.healthcare.ttt.smtp.TestResult;
import gov.nist.healthcare.ttt.smtp.util.ReqRes;

/**
 * AbstractSMTPSender
 * 
 * This class has specifies three sets of functions:
 * 
 * 1. getResponseForRequest(String req)
 * 
 * 2. startupServer/resetServer/shutdownServer - for connection level
 * 
 * 3. sndMsgXXXX invokes 1 with specific commands.
 * 
 * @author sriniadhi
 *
 */
public abstract class AbstractSMTPSender {
	public String crlf = "\r\n";

	public static final String HELO = "HELO %s\r\n";
	public static final String EHLO = "EHLO %s\r\n";
	public static final String MAILFROM = "MAIL FROM:<%s>\r\n";
	public static final String RCPTTO = "RCPT TO:<%s>\r\n";
	public static final String DATA = "DATA \r\n%s\r\n.\r\n";
	public static final String QUIT = "QUIT\r\n";
	public static final String VRFY = "VRFY %s\r\n";
	public static final String EXPN = "EXPN %s\r\n";
	public static final String STARTTLS = "STARTTLS\r\n";
	public static final String AUTHLOGIN = "AUTH LOGIN\r\n";
	public static final String AUTHPLAIN = "AUTH PLAIN %s\r\n";

	// Invalid commands
	public static final String DATA_LFCR = "DATA \f\r%s\f\r.\f\r";
	public static final String DATA_NOCRLF = "DATA \r\n%s";
	public static final String DATA_NO_DOT = "DATA \r\n%s\r\n";
	public static final String STARTTLS_PARAM = "STARTTLS abcd\r\n";
	public static final int[] SUCCESSCODES = new int[] { 200, 220, 250 };
	public static final int[] FAILURECODES = new int[] { 221, 421, 450, 451, 452, 500, 501, 504, 521, 530, 550, 551, 552, 553, 554};

	protected String smtphostname;
	protected int smtpport;

	/**
	 * getResponseForRequest:
	 * 
	 * @param req
	 * @return Response from the server
	 */
	public abstract ReqRes getResponseForRequest(String req);

	/**
	 * setupServer: Opens a connection to the url:port
	 * 
	 * @param url
	 * @param port
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect(String url, int port) throws UnknownHostException, IOException {
		smtphostname = url;
		smtpport = port;
		connect();
	}

	public abstract void startTLS(); // upgrades the plain to TLS

	protected abstract void connect() throws UnknownHostException, IOException;

	/**
	 * resetServer
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void resetServer() throws UnknownHostException, IOException {
		close();
		connect(smtphostname, smtpport);
	}

	/**
	 * 
	 * @param url
	 * @param port
	 * @param auth
	 * @param starttls
	 * @param uname
	 * @param pwd
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */

	public void connect(String url, int port, boolean auth, boolean starttls,
			String uname, String pwd) throws UnknownHostException, IOException {
		connect(url, port);
	}

	/**
	 * shutdownServer
	 */
	public abstract void close();

	/**
	 * sndMsgXXX
	 * 
	 * @param?
	 * @return responseFromServer
	 */
	public ReqRes sndMsgEHLO(String domain) {
		return getResponseForRequest(String.format(EHLO, domain));
	}

	public ReqRes sndMsgHELO(String domain) {
		return getResponseForRequest(String.format(HELO, domain));
	}
	
	/**
	 * @param reverseAddress
	 * @return
	 */
	public ReqRes sndMsgMAILFROM(String reverseAddress) {
		return getResponseForRequest(String.format(MAILFROM, reverseAddress));
	}

	/**
	 * @param address
	 * @return
	 */
	public ReqRes sndMsgRCPTTO(String address) {
		return getResponseForRequest(String.format(RCPTTO, address));
	}

	/**
	 * @param msg
	 * @return
	 */
	public ReqRes sndMsgDATA(String msg) {
		return getResponseForRequest(String.format(DATA, msg));
	}

	/**
	 * @param address
	 * @return
	 */
	public ReqRes sndMsgVRFY(String address) {
		return getResponseForRequest(String.format(VRFY, address));
	}

	/**
	 * @param address
	 * @return
	 */
	public ReqRes sndMsgEXPN(String address) {
		return getResponseForRequest(String.format(EXPN, address));
	}

	/**
	 * @param address
	 * @return
	 */
	public ReqRes sndMsgSTARTTLS() {
		return getResponseForRequest(STARTTLS);
	}

	/**
	 * @param address
	 * @return
	 */
	public ReqRes sndMsgSTARTTLS_PARAM() {
		return getResponseForRequest(STARTTLS_PARAM);
	}

	/**
	 * @return
	 */
	public ReqRes sndMsgQUIT() {
		return getResponseForRequest(QUIT);
	}

	/**
	 * @return
	 */
	public ReqRes sndAUTHLOGIN() {
		return getResponseForRequest(AUTHLOGIN);
	}
	
	public ReqRes sndAUTHPLAIN() {
		return getResponseForRequest(AUTHPLAIN);
	}
	
	public ReqRes sndAUTHPLAIN(String userpass) {
		return getResponseForRequest(String.format(AUTHPLAIN, userpass));
	}

	/**
	 * @return
	 */
	public ReqRes sndTxt(String txt) {
		return getResponseForRequest(txt + "\r\n");
	}
	
	public abstract void AUTHLOGIN(String user, String password) throws Exception;
	
	public abstract void AUTHPLAIN(String user, String password) throws Exception;
	
	public abstract void setTimeOut(int nInSeconds);

	public abstract int getTimeOut();

	public abstract void restoreTimeOut();

	public static boolean isThereNextLine(String msg) {
		return (msg != null && msg.length() > 4 && msg.trim().substring(3, 4)
				.equals("-"));
	}

	public static int getStatus(String msg) {
		try {
			if (msg != null && msg.length() > 4
					&& msg.trim().substring(3, 4).equals(" ")) {
				return Integer.parseInt(msg.trim().substring(0, 3));
			}
		} catch (NumberFormatException n) {
		}
		return -1;
	}

	public static boolean isSuccess(int code) {
		for (int a : SUCCESSCODES) {
			if (code == a)
				return true;
		}
		return false;
	}

	public static boolean IsFailure(int code) {
		for (int a : FAILURECODES) {
			if (code == a)
				return true;
		}
		return false;		
	}


}
