package gov.nist.healthcare.ttt.direct.sender;

import gov.nist.healthcare.ttt.direct.messageGenerator.SMTPAddress;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DirectMessageSender {
	
	private static Logger logger = Logger.getLogger(DirectMessageSender.class.getName());

	public static final String CRLF = "\r\n";
	public BufferedReader in = null;
	public BufferedOutputStream out = null;

	
	public boolean send(int port, String mailerHostname, MimeMessage msg, String fromAddress, String toAddress) throws Exception {
		return send(port, mailerHostname, msg, fromAddress, toAddress, false);
	}
	
	
	public boolean send(int port, String mailerHostname, MimeMessage msg, String fromAddress, String toAddress, boolean startTLS) throws Exception {
		DnsLookup lookup = new DnsLookup();
		String mxMailerHostname = lookup.getMxRecord(mailerHostname);
		if(mxMailerHostname != null) {
			mailerHostname = mxMailerHostname;
		}
		return sendMessage(port, mailerHostname, msg, fromAddress, toAddress, startTLS);
	}
	
	public boolean sendMessage(int mailerPort, String mailerHostname, MimeMessage msg, String fromAddress, String toAddress, boolean startTLS) throws Exception {
		logger.info("Opening socket to Direct system on " + mailerHostname + ":" + mailerPort + "...");
		Socket socket;
		try {
			socket = new Socket(mailerHostname, mailerPort);
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
			throw new Exception(e.getMessage());
		}
		logger.info("\t...Success");
		
		try {
			smtpProtocol(socket, msg, mailerHostname, fromAddress, toAddress, startTLS);
		} catch (Exception ex) {
			logger.info("Exception: " + ex.getMessage());
			throw new Exception(ex.getMessage());
		} finally {
			socket.close();
		}
		
		return true;
	}
	
	public void smtpProtocol(Socket socket, MimeMessage mmsg, String domainname, String from, String to) throws Exception {
		smtpProtocol(socket, mmsg, domainname, from, to, false);
	}
	
	
	public void smtpProtocol(Socket socket, MimeMessage mmsg, String domainname, String from, String to, boolean startTLS) throws Exception {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedOutputStream(socket.getOutputStream());

		try {
			from = new SMTPAddress().properEmailAddr(from);
			to = new SMTPAddress().properEmailAddr(to);

			rcv("220");

			send("HELO " + domainname);

			rcv("250"); 
			
			if (startTLS) {
				logger.info("smtpProtocol: issuing STARTTLS");
				send("STARTTLS");
				rcv("220"); 
				socket = startTLS(socket);
				out = new BufferedOutputStream(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				
			}

			send("MAIL FROM:" + from);

			rcv("250"); 

			send("RCPT TO:" + to);

			rcv("250");

			send("DATA");

			rcv("354"); 

			send("Subject: " + mmsg.getSubject());

			mmsg.writeTo(out);

			send(CRLF + ".");

			rcv("250");

			send("QUIT");

			rcv("221"); 
		} catch (Exception e) {
			logger.info("Protocol error: " + e.getMessage());
			throw new Exception("Protocol error: " + e.getMessage());
		} finally {
			in.close();
			out.close();
			in = null;
			out = null;
		}
	}
	
	
	public Socket startTLS(Socket smtpSocket) {
		try {
				logger.info("startTLS: upgrading the regular socket to startTLS");
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
				
				return smtpSocket;
			
		} 
		catch (Exception e) {
			logger.info("startTLS threw an error: " + e.getMessage());
		}
		
		logger.info("Unable to upgrade to startTLS: returning plain socket!");
		return smtpSocket;

	}


	public void send(String cmd) throws IOException {
		logger.info("SMTP SEND: " + cmd);
		cmd = cmd + CRLF;
		out.write(cmd.getBytes());
		out.flush();
	}

	public String rcv(String expect) throws Exception {
		String msg;
		msg = in.readLine();
		logger.info("SMTP RCV: " + msg);
		if (expect != null && !msg.startsWith(expect))
			throw new Exception("Error: expecting " + expect + ", got <" + msg + "> instead");
		return msg;
	}

	public String getTargetDomain(String targetedFrom) {
		// Get the targeted domain
		String targetDomain = "";
		if(targetedFrom.contains("@")) {
			targetDomain = targetedFrom.split("@", 2)[1];
		}
		return targetDomain;
	}
}
