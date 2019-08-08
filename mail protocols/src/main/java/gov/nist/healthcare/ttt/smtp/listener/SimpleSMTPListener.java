package gov.nist.healthcare.ttt.smtp.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class SimpleSMTPListener extends AbstractSMTPListener {
	private static Logger log = Logger.getLogger(SimpleSMTPListener.class);

	public static void main(String[] args) {
		System.out.println("Running Chameleon Server on Port: "
				+ (args.length <= 0 ? 8000 : Integer.parseInt(args[0])));
		try {
			new SimpleSMTPListener().listen(
					args.length <= 0 ? 8000 : Integer.parseInt(args[0]), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int behavior = 0;

	public void listen(int port, final int _behavior) throws IOException {
		ServerSocket sock = null;
		behavior = _behavior;
		try {
			sock = new ServerSocket(port);
			sock.setSoTimeout(0);
			while (true) {
				System.out
						.println("Waiting for a socket connection.....................");
				final Socket smtpSocket = sock.accept();
				System.out
						.println("Accepted socket connection...................."
								+ behavior);
				smtpSocket.setSoTimeout(0);
				Thread client = new Thread(new Runnable() {
					public void run() {
						_listen(smtpSocket, behavior);
					}
				});
				client.start();
			}

		} catch (Exception e) {
			log.error(e);
		} finally {
			if (sock != null)
				sock.close();
		}
	}

	public void _listen(Socket smtpSocket, int _behavior) {
		try {
			// ServerSocket sock = new ServerSocket(port);
			// Socket smtpSocket = sock.accept();
			behavior = _behavior;
			DataOutputStream os = new DataOutputStream(
					smtpSocket.getOutputStream());
			BufferedReader is = new BufferedReader(new InputStreamReader(
					smtpSocket.getInputStream()));
			String s;
			for (int ii = 0; ii < behaviordesc.length; ii++) {
				os.writeBytes("00" + ii + "- " + behaviordesc[ii]);
			}
			os.writeBytes("EX:- BEHAVE n   // changes the existing behavior to n\r\n");
			os.writeBytes("220 <Chameleon> Service ready \r\n");
			os.flush();

			while ((s = is.readLine()) != null) {
				System.out.println("Read: " + s);
				if (s.startsWith("BEHAVE")) {
					s = s.replaceAll("[^0-9]*", "");
					behavior = Integer.parseInt(s);
					os.writeBytes("Changing behavior to " + behavior + "\r\n");
					behave(behavior, "BEHAVE", os, is);
					continue;
				}
				if (s.toUpperCase().startsWith("QUIT")) {
					os.writeBytes("Bye!\n");
					smtpSocket.close();
					break;
				}

				// System.out.println("Setting up the behavior to be: " +
				// behavior);
				behave(behavior, s, os, is);
				if (behavior == 3) {
					os.writeBytes("Timed out \n");
					smtpSocket.close();
					break;
				}
			}			
		} catch (Exception e) {
			log.error("SMTP socket encountered a problem: "
					+ e.getLocalizedMessage());
		}
	}

	void behave(int behavior, String current, DataOutputStream os,
			BufferedReader b) throws IOException {
		switch (behavior) {
		case 0:
		default:
			behaveOK(current, os, b);
			break;
		case 1:
			behaveNotOK(current, os, b);
			break;
		case 2:
			behaveSMTP(current, os, b);
			break;
		case 3:
			behaveTimeout(current, os, b);
			break;
		case 4:
			behaveStartTLS(current, os, b);
		}
	}


	String[] behaviordesc = { "This behavior returns 220 to all requests!\r\n",
			"This behavior returns 501 to all requests!\r\n",
			"This behavior returns reasonably proper responses to all requests!\r\n",
			"This server times out after the n seconds you specify next",
			"STARTTLS\r\n",
			};

	void behaveOK(String current, DataOutputStream os, BufferedReader b)
			throws IOException {
		if (current.startsWith("BEHAVE")) {
			os.writeBytes("Welcome! " + behaviordesc[0]);
			os.flush();
			return;
		}

		os.writeBytes("220 <domain> Service ready \r\n");
		os.flush();
	}

	void behaveNotOK(String current, DataOutputStream os, BufferedReader b)
			throws IOException {
		if (current.startsWith("BEHAVE")) {
			os.writeBytes("Welcome!" + behaviordesc[1]);
			os.flush();
		}

		os.writeBytes("501 Syntax\r\n");
		os.flush();
	}

	static HashMap<String, String> validSmtpReqRes = new HashMap<String, String>();
	static {
		validSmtpReqRes.put("HELO", "HELO <domain>\r\n250 Ok");
		validSmtpReqRes.put("EHLO", "EHLO <domain>\r\n250 Ok");
		validSmtpReqRes.put("AUTH LOGIN", "235 Authentication successful.");
		validSmtpReqRes.put("MAIL FROM", "250 Ok");
		validSmtpReqRes.put("RCPT TO", "250 Ok");
		validSmtpReqRes.put("DATA", "354 End data with <CR><LF>.<CR><LF>");
		validSmtpReqRes.put(".\r\n", "250 Ok");
		validSmtpReqRes.put("NOP", "250 Ok");
		validSmtpReqRes.put("VRFY", "250 Ok");
		validSmtpReqRes.put("RSET", "250 Ok");
		validSmtpReqRes.put("QUIT", "221 Bye");
	}

	void behaveSMTP(String current, DataOutputStream os, BufferedReader b)
			throws IOException {
		if (current.startsWith("BEHAVE")) {
			os.writeBytes("Welcome! " + behaviordesc[2]);
			os.flush();
			return;
		}

		for (String k : validSmtpReqRes.keySet()) {
			if (current.toUpperCase().startsWith(k)) {
				os.writeBytes(validSmtpReqRes.get(k) + "\r\n");
				os.flush();
				return;
			}
		}
		os.writeBytes("501 Syntax\r\n");
		os.flush();
	}

	void behaveTimeout(String current, DataOutputStream os, BufferedReader b)
			throws IOException {
		if (current.startsWith("BEHAVE")) {
			os.writeBytes("Welcome! " + behaviordesc[3]);
			os.flush();
			return;
		}		
		try {
			log.info("Sleeping for : " + current);
		    Thread.sleep(Integer.parseInt(current));                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	private void behaveStartTLS(String current, DataOutputStream os,
			BufferedReader b) throws IOException {
		
		if (current.startsWith("BEHAVE")) {
			os.writeBytes("Welcome! " + behaviordesc[4]);
			os.flush();
			return;
		}		
	}
}
