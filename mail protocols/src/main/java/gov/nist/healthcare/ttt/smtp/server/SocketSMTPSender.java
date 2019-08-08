package gov.nist.healthcare.ttt.smtp.server;

import gov.nist.healthcare.ttt.smtp.util.ReqRes;
import gov.nist.healthcare.ttt.smtp.util.ServerSocketNullException;
import gov.nist.healthcare.ttt.smtp.util.TestData;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

public class SocketSMTPSender extends AbstractSMTPSender {
	static Logger log = Logger.getLogger(SocketSMTPSender.class);

	protected Socket smtpSocket;
	protected DataOutputStream os;
	protected BufferedReader is;

	protected int defaultServerTimeout = 3; // seconds
	protected int serverTimeOut = defaultServerTimeout;

	// TODO: return?
	@Override
	protected void connect() throws UnknownHostException, IOException {
		//try {
		// Open port to server
		// not explicitly setting this IPv4 preference seems to create
		// connection issues in some systems.
		// http://tinyurl.com/pr8qn6n
		System.setProperty("java.net.preferIPv4Stack", "true");
		smtpSocket = new Socket(smtphostname, smtpport);
		smtpSocket.setSoTimeout(serverTimeOut * 1000); // temporarily to
		// solve the last
		// read
		// that is blocking
		os = new DataOutputStream(smtpSocket.getOutputStream());
		is = new BufferedReader(new InputStreamReader(
				smtpSocket.getInputStream()));

		if (smtpSocket != null && os != null && is != null) {
			log.info("Successfully setup the server. "
					+ getResponseForRequest("")); // empty the greetings
		} else {

		}

		/*} catch (Exception e) {
			log.error("Host " + smtphostname + ":" + smtpport + " has problem: "
					+ e.getLocalizedMessage());
		}*/
	}

	@Override
	public void close() {
		sndMsgQUIT();
		try {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
			if (smtpSocket != null) {
				if (!smtpSocket.isClosed())
					smtpSocket.close();
				smtpSocket = null;
			}
		} catch (Exception e) {
			log.error("Host: " + smtphostname + " closing exception; Error: "
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public void resetServer() throws UnknownHostException, IOException {
		close();
		connect(smtphostname, smtpport);
	}

	@Override
	public ReqRes getResponseForRequest(String req) {
		String result = "";
		long startTime = System.nanoTime();
		try {
			if (smtpSocket != null && os != null && is != null) {
				if (!req.isEmpty()) {
					log.info("REQUEST: " + req + "\n");
					os.writeBytes(req);
					os.flush();
				}

				String responseline;
				while ((responseline = is.readLine()) != null) {
					result += (responseline + "\r\n");
					if (!isThereNextLine(responseline))
						break;
					log.info("RESPONSE READ: " + responseline + "\n");
				}
				if (responseline == null)
					throw new ServerSocketNullException(req
							+ " resulted in null response!");
				log.info("--> LAST RESPONSE READ: " + responseline + "\n");
			} else {
				log.info("Socket/stream is null!");
			}
		} catch (SocketTimeoutException e) {

			if (result == "") { // if we have partial output we ignore the
				// timeout
				log.error("getResponseForRequest SocketTimeoutException: Host "
						+ smtphostname + " has problems connecting;"
						+ e.getLocalizedMessage());
				result = TestData.TTT_TIMEOUT_MSG;
			}
		} catch (ServerSocketNullException e) {

			if (result == "") { // if we have partial output we ignore the
				// timeout
				log.error("getResponseForRequest ServerSocketNullException: Host "
						+ smtphostname
						+ " has problems connecting;"
						+ e.getLocalizedMessage());
				result = TestData.SUT_TIMEOUT_MSG;
			}
		} catch (Exception e) {
			log.error("getResponseForRequest: Host " + smtphostname
					+ " has problems connecting;" + e.getLocalizedMessage());
		}

		long timeElapsed = (System.nanoTime() - startTime)
				/ (1000 * 1000 * 1000);

		return new ReqRes(req, result, timeElapsed);
	}

	@Override
	public void connect(String url, int port, boolean auth, boolean starttls,
			String uname, String pwd) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		connect(url, port);

	}

	@Override
	public void setTimeOut(int nInSeconds) {
		serverTimeOut = nInSeconds;
		if (smtpSocket != null)
			try {
				smtpSocket.setSoTimeout(serverTimeOut * 1000);
			} catch (SocketException e) {
				log.error("SetTimeOut " + serverTimeOut + " caused exception ",
						e);
				e.printStackTrace();
			}
	}

	@Override
	public int getTimeOut() {
		return serverTimeOut;
	}

	public void restoreTimeOut() {
		serverTimeOut = defaultServerTimeout;
	}

	public void startTLS() {
		ReqRes r = sndMsgSTARTTLS();

		try {
			log.info("Starting handshake; Response:" + r.response);
			if (r.response.contains("220 ") || r.response.contains("670 ")) {
				log.info("Proceeding with the handshake");
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs,
							String authType) {
					}

					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs,
							String authType) {
					}
				} }, new java.security.SecureRandom());
				SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) sc
						.getSocketFactory());
				SSLSocket sslSocket = (SSLSocket) sslSocketFactory
						.createSocket(smtpSocket, smtpSocket.getInetAddress()
								.getHostAddress(), smtpSocket.getPort(), true);
				sslSocket.startHandshake();
				smtpSocket = sslSocket;
				os = new DataOutputStream(smtpSocket.getOutputStream());
				is = new BufferedReader(new InputStreamReader(
						smtpSocket.getInputStream()));
			}
		} 
		catch (Exception e) {
			log.error("TLS failed with " + e.getLocalizedMessage());
		}

	}


	public void AUTHLOGIN(String user, String password) throws Exception {
		ReqRes r = sndAUTHLOGIN();
		if (r.response.contains("334 ")) {
			r = sndTxt(new String(Base64.encodeBase64(user.getBytes())));
			if (r.response.contains("334 "))
				r = sndTxt(new String(Base64.encodeBase64(password.getBytes())));
			if (!r.response.contains("235 "))
				throw new Exception("Authentication failed with : " + r.response);

		}
	}

	public void AUTHPLAIN(String user, String password) throws Exception {
		String userpass = new String(Base64.encodeBase64(("\0" + user + "\0" + password).getBytes()));
		ReqRes r = sndAUTHPLAIN(userpass);
		if (!r.response.contains("235 ")) {
			log.info("AUTH PLAIN generated " + r.response + ";Trying AUTH LOGIN...");
			AUTHLOGIN(user, password);
		}
	}
}
