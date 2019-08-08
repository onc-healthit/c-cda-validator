package gov.nist.healthcare.ttt.webapp.direct.listener;

import gov.nist.healthcare.ttt.database.jdbc.DatabaseException;
import gov.nist.healthcare.ttt.database.log.LogInterface.Status;
import gov.nist.healthcare.ttt.direct.messageGenerator.MDNGenerator;
import gov.nist.healthcare.ttt.direct.messageProcessor.DirectMessageProcessor;
import gov.nist.healthcare.ttt.direct.sender.DirectMessageSender;
import gov.nist.healthcare.ttt.direct.sender.DnsLookup;
import gov.nist.healthcare.ttt.direct.smtpMdns.EncryptedSmtpMDNMessageGenerator
import gov.nist.healthcare.ttt.direct.smtpMdns.EncryptedSmtpMDNMessageGenerator.MDNType
import gov.nist.healthcare.ttt.direct.smtpMdns.SmtpMDNMessageGenerator;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xbill.DNS.TextParseException;

import javax.mail.Header
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class ListenerProcessor implements Runnable {
	Socket server;
	String directFrom = null; // Direct from address reported in SMTP protocol
	List<String> directTo = new ArrayList<String>();
	boolean logInputs = true;

	// Message and cert streams
	StringBuffer message = new StringBuffer();
	InputStream messageStream = null;
	InputStream certStream = null;
	BufferedReader inReader = null;
	BufferedOutputStream outStream = null;

	String logFilePath = ""

	Collection<String> contactAddr = null;
	String logHostname = "";
	static final String CRLF = "\r\n";

	String domainName

	String servletName

	int port

	int listenerPort = 0

	String certificatesPath

	String certPassword

	Emailer emailer

	DirectMessageProcessor processor;

	String smtpHost

	String startTlsAdress

	private DatabaseInstance db;

	private static Logger logger = Logger.getLogger(ListenerProcessor.class.getName());

	ListenerProcessor(Socket server, DatabaseInstance db, String mdhtR1Endpoint, String mdhtR2Endpoint, String toolkitUrl) throws DatabaseException, SQLException {
		this.server = server;
		this.db = db;
		this.processor = new DirectMessageProcessor(mdhtR1Endpoint, mdhtR2Endpoint, toolkitUrl);
	}

	/**
	 * For unit testing only
	 */
	public ListenerProcessor() {

	}

	public void run() {

		if (readSMTPMessage() == false)
			return;
		logger.info("Processing message from " + directFrom);

		// Get inputstream message
		byte[] messageBytes = message.toString().getBytes();
		this.messageStream = new ByteArrayInputStream(messageBytes);

		// Need to know if this is a message for MDN answer: one of those addresses
		// processedonly5
		// processeddispatched6
		// processdelayeddispatch7
		// nomdn8
		// noaddressfailure9
		def smtpAddressList = ['processedonly5', 'processeddispatched6', 'processdelayeddispatch7', 'nomdn8', 
			'noaddressfailure9', 'goodaddress-plain',
			'processedonly5-plain', 'processdelayeddispatch30min', 'processeddispatched6-plain', 'noaddressfailure9-plain', 'dispatchedonly-plain',
			'white_space_mdn', 'extra_line_break_mdn', 'extra_space_disposition', 'missing_disposition', 'null_sender',
			'different_sender', 'different_msgid', 'white_space_822', 'different_cases_822', 'dsn', 'processedfailuretest', 'processedfailure','processedtimeoutfailure']

		String smtpFrom = directTo?.get(0)
		logger.info("To " + smtpFrom)
		smtpFrom = smtpFrom.split("@")[0]
		if(smtpAddressList.contains(smtpFrom)) {
			logger.info("MDN address found $smtpFrom sending back appropriate MDN")
			manageMDNAddresses(smtpFrom, directTo?.get(0), directFrom, messageStream)
			return
		} else if(smtpFrom.startsWith("delaydispatched")) {
			logger.info("MDN address found $smtpFrom sending back appropriate MDN")
			manageMDNAddressesTimeout(smtpFrom, directTo?.get(0), directFrom, messageStream)
			return
		} else if(smtpFrom.equals("wellformed1")) {
			// Get the session variable
			Properties props = System.getProperties();
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage forward = new MimeMessage(session, this.messageStream);
			DirectMessageSender sender = new DirectMessageSender();
			logger.info("Forwarding message to " + this.startTlsAdress);
			sender.sendMessage(25, this.smtpHost, forward, forward.getFrom()[0].toString(), this.startTlsAdress, false);
			return
		}

		logger.info("Mime Message parsing successful");

		// Valid Direct (From) addr?
		try {
			if (!db.getDf().doesDirectExist(directFrom)) {
				// looks like spam - unregistered direct (from) Addr
				logger.error("Throw away message from " + directFrom
						+ " not a registered Direct (From) email account name");
				return;
			}
		} catch (DatabaseException e1) {
			logger.error("Cannot connect to database: " + e1.getMessage());
		}

		try {
			contactAddr = getContactAddr(directFrom);
		} catch (DatabaseException e1) {
			logger.error("Cannot connect to database: " + e1.getMessage());
		}

		if (contactAddr == null || contactAddr.isEmpty()) {
			logger.error("No contact address listed for Direct (From) address "
					+ directFrom);
		} else {
			logger.info("Direct addr (From) " + directFrom
					+ " is registered and has contact email of " + contactAddr);
		}

		/****************************************************************************************
		 * 
		 * All errors detected after this can be reported back to the user via
		 * their Contact Addr.
		 * 
		 ****************************************************************************************/

		// Reporting enclosure - throw ReportException to flush messages and
		// continue
		try {

			this.certStream = getSigningPrivateCert();

			if (this.certStream == null) {
				logger.error("Cannot load private decryption key");
			}

			// TkPropsServer ccdaProps =
			// reportingProps.withPrefixRemoved("ccdatype");
			if (directTo.size() > 1) {
				String msg = "Multiple TO addresses pulled from SMTP protocol headers - cannot determine which CCDA validator to run - CCDA validation will be skipped";
				logger.warn(msg);
			} else if (directTo.size() == 0) {
				String msg = "No TO addresses pulled from SMTP protocol headers - cannot determine which CCDA validator to run - CCDA validation will be skipped";
				logger.warn(msg);
				// else {
				// String to = directTo.get(0);
				// for (int i=1; i<500; i++) {
				// String en = Integer.toString(i);
				// String type = ccdaProps.get("type" + en, null);
				// String ccdaTo = ccdaProps.get("directTo" + en, null);
				// if (type == null || ccdaTo == null)
				// break;
				// if (ccdaTo.equals(to)) {
				// ccdaType = type;
				// break;
				// }
				// }
				// }
			} else if (directTo[0].startsWith("r2_")) {
				logger.warn("r2 validation endpoints not supported from 2.1.1")
				return
			}

			// ccdaType tells us the document type to validate against

			// Validate
			// yadda, yadda, yadda

			logger.info("Message Validation Begins");

			processor.setDirectMessage(messageStream);
			processor.setCertificate(certStream);
			processor.setCertificatePassword(certPassword);
			processor.processDirectMessage();

			// Log in the database
			try {
				logger.info("Logging message validation in database: "
						+ processor.getLogModel().getMessageId());
				db.getLogFacade().addNewLog(processor.getLogModel());
				db.getLogFacade().addNewPart(
						processor.getLogModel().getMessageId(),
						processor.getMainPart());
				if(processor.hasCCDAReport()) {
					processor.getCcdaReport().each {
						db.getLogFacade().addNewCCDAValidationReport(processor.getLogModel().getMessageId(), it);
					}
				}
			} catch (DatabaseException e) {
				logger.error("Error trying to log in the database "
						+ e.getMessage());
				e.printStackTrace();
			}

			// If it's an MDN update the status to MDN RECEIVED
			if(processor.isMdn()) {
				// Check if message not already timed out
				if(db.getLogFacade().getLogByMessageId(processor.getOriginalMessageId()).getStatus().equals(Status.WAITING)) {
					logger.info("Updating MDN status to MDN_RECEIVED for message " + processor.getOriginalMessageId());
					db.getLogFacade().updateStatus(processor.getOriginalMessageId(), Status.MDN_RECEIVED);
				} else {
					logger.info("Not Updating MDN status because message already timed out " + processor.getOriginalMessageId());
				}
			}

			logger.info("Message Validation Complete");

			// Log line for the stats
			if (directTo != null) {
				String docType;
				if(processor.isMdn()) {
					docType = "mdn";
				} else {
					docType = getCcdaType(directTo.get(0));
				}

				try {
					FileWriter fw = new FileWriter(this.logFilePath, true); //the true will append the new data

					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z"); // Date format
					String statLog = "| ${logHostname}  - - [${dateFormat.format(new Date())}] \"POST /ttt/listener/${docType} HTTP/1.1\" 200 75"
					logger.info(statLog);

					// Write in listener log
					fw.write(statLog +"\n");
					fw.close();

				} catch(Exception e) {
					logger.info(e.getMessage())
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Message Validation Error: " + e.getMessage());
		}

		logger.info("Starting report generation");

		// Generate validation report URL
		// String reportId = new DirectActorFactory().getNewId();
	//	String url = "https://${domainName}:${port}${servletName}/#/direct/report/${this.processor.getLogModel().getMessageId()}"
	//	String url = "https://${domainName}${servletName}/#/direct/report/${this.processor.getLogModel().getMessageId()}"
		String urlappend = "${this.processor.getLogModel().getMessageId()}";
		String url = "https://${domainName}${servletName}/#/direct/report/" + URLEncoder.encode(urlappend,"UTF-8");
		// Generate report template
		String announcement = "<h2>Direct Validation Report</h2>Validation from ${new Date()}<p>Report link: <a href=\"${url}\">${url}</p>"

		logger.debug("Announcement is:\n" + announcement);

		logger.info("Send report");

		try {

			logger.info("Sending report to " + contactAddr);
			if (contactAddr == null) {
				throw new Exception(
				"Internal Error: no Contact email Address found");
			}

			// Send report

			logger.info("Sending report from " + emailer.model.getFrom()
					+ "   to " + contactAddr);
			Iterator<String> it = contactAddr.iterator();
			while (it.hasNext()) {
				emailer.sendEmail2(it.next(),
						"Direct Message Validation Report", announcement);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Cannot send email (" + e.getClass().getName() + ". "
					+ e.getMessage());
		}

		logger.info("Done");

		// Send MDN if it is Direct Message
		if (!processor.isMdn()) {
			String toMDN = "";
			if(processor.getLogModel().getReplyTo() != null) {
				toMDN = processor.getLogModel().getReplyTo().iterator().next();
			} else if(this.processor.getLogModel().getFromLine() != null) {
				toMDN = this.processor.getLogModel().getFromLine().iterator().next();
			}
			String fromMDN = this.processor.getLogModel().getToLine().iterator().next();

			try {
				logger.info("Generating MDN for message " + processor.getLogModel().getMessageId());
				MimeMessage mdn = generateMDN(fromMDN, toMDN, this.processor.getLogModel().getMessageId(), getSigningPrivateCert(),
						this.certPassword, true);
				DirectMessageSender sender = new DirectMessageSender();
				logger.info("Sending MDN to " + toMDN);
				sender.send(listenerPort, sender.getTargetDomain(toMDN), mdn, fromMDN, toMDN);
			} catch (Exception e) {
				logger.info("Cannot send MDN to " + toMDN + ": " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public String getDirectTo() {
		if (directTo.size() == 0 || directTo.size() > 1)
			return null;
		return directTo.get(0);
	}

	public Collection<String> getContactAddr(String directFrom)
	throws DatabaseException {
		directFrom = stripBrackets(directFrom);

		return db.getDf().getContactAddresses(directFrom);
	}

	public boolean readSMTPMessage() {
		InetAddress ia = server.getInetAddress();
		String stars = "**********************************************************";
		logger.info(stars + "\n" + "Connection from " + ia.getHostName() + " ("
				+ ia.getHostAddress() + ")");

		// Get the hostname for the stats log
		logHostname = ia.getHostName();

		try {
			// smtpFrom set as a side-effect. This is the from address from the
			// SMTP protocol elements
			// replies should go here as well as this addr should be used to
			// lookup contact addr.
			String m = readIncomingSMTPMessage(server, this.domainName);
			message.append(m);
		} catch (EOFException e) {
			logger.warn("IOException on socket listen: " + e);
			return true;
		} catch (IOException ioe) {
			logger.error("IOException on socket listen: " + ioe);
			return false;
		} catch (Exception e) {
			logger.error("Protocol error on socket listen: " + e);
			return false;
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		return true;
	}

	class RSETException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RSETException() {

		}

	}

	public String readIncomingSMTPMessage(Socket socket, String domainname)
	throws IOException, Exception {
		this.domainName = domainname;
		String buf = null;

		inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outStream = new BufferedOutputStream(socket.getOutputStream());

		try {
			send("220 " + domainname + " SMTP Exim");

			buf = rcvStateMachine();

			logger.trace("MESSAGE: \n" + buf);

		} catch (RSETException e) {
			// Reset all buffers
			resetAllBuff();
			buf = rcvStateMachine();
		} catch (IOException e) {
			return "";
		} finally {
			inReader.close();
			outStream.close();
		}

		return buf;
	}

	void send(String cmd) throws IOException {
		logger.debug("SMTP SEND: " + cmd);
		cmd = cmd + CRLF;
		outStream.write(cmd.getBytes());
		outStream.flush();
	}

	String rcvStateMachine() throws IOException, RSETException, Exception {
		return rcvStateMachine(false);
	}

	String rcvStateMachine(boolean reportError) throws IOException,
	RSETException, Exception {
		StringBuffer buf = new StringBuffer();
		String msg;
		boolean isFirstMessage = true;
		while (true) {
			msg = rcv().trim();
			msg = msg.toLowerCase();
			if (msg.startsWith("rcpt to:")) {
				String to = msg.substring(msg.indexOf(':') + 1);
				if(isSpam(to)) {
					throw new Exception("Spam throwing away message");
				}
				if (to != null && !to.equals(""))
					directTo.add(stripBrackets(to));
				send("250 OK");
				continue;
			}
			if (msg.startsWith("data")) {
				send("354 Enter message, ending with '.' on a line by itself");
				msg = "";
				logInputs = false;
				while (true) {
					msg = rcv();
					if (".".equals(msg.trim())) {
						isFirstMessage = false;
						break;
					}
					buf.append(msg).append(CRLF);
				}
				logInputs = true;

				send("250 OK");
				continue;
			}
			if (msg.startsWith("helo")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("ehlo")) {
				send("502 ehlo not supported - use helo");
				continue;
			}
			if (msg.startsWith("mail from:")) {
				directFrom = stripBrackets(msg.substring(msg.indexOf(':') + 1));
				send("250 OK");
				continue;
			}
			if (msg.startsWith("rset")) {
				if(!isFirstMessage) {
					send("421 Ony one message per connection allowed");
					return buf.toString();
				} else {
					send("250 OK");
					throw new RSETException();
				}
			}
			if (msg.startsWith("vrfy")) {
				send("250 OK");
				continue;
			}
			if (msg.startsWith("noop")) {
				if(!isFirstMessage) {
					send("421 Ony one message per connection allowed");
					return buf.toString();
				} else {
					send("250 OK");
				}
				continue;
			}
			if (msg.startsWith("quit")) {
				send("221 " + domainName + " closing connection");
				// if (error)
				// return "";
				return buf.toString();
			}
			send("503 bad sequence of commands - received " + msg);
			throw new Exception("503 bad sequence of commands - received "
			+ msg);
		}
	}

	String rcv(String expect) throws IOException, RSETException, Exception {
		String msg = rcv().trim().toLowerCase();
		expect = expect.toLowerCase();
		if (expect != null && !msg.startsWith(expect)) {
			send("503 bad sequence of commands");
			return rcvStateMachine(true);
		}
		return msg;
	}

	String rcv() throws IOException, RSETException {
		String msg = inReader.readLine();
		if (logInputs)
			logger.debug("SMTP RCV: " + msg);
		return (msg == null) ? "" : msg;
	}

	void resetAllBuff() {
		this.directFrom = null;
		this.directTo = new ArrayList<String>();
	}

	String ccdaValidationType(String toAddr) {
		if (toAddr == null)
			return null;
		// TkPropsServer props = tps.withPrefixRemoved("ccdatype");
		// for (int i=1; i<50; i++) {
		// String key = "directTo" + String.valueOf(i);
		// String to = props.get(key, null);
		// if (to == null)
		// return null;
		// if (toAddr.equals(to)) {
		// key = "type" + String.valueOf(i);
		// return props.get(key, null);
		// }
		// }
		return null;
	}

	String simpleEmailAddr(String addr) throws Exception {
		addr = addr.trim();
		int fromi = addr.indexOf('@');
		if (fromi == -1)
			throw new Exception("Cannot parse Direct Address " + addr);
		int toi = fromi;
		for (int i = fromi; i >= 0; i--) {
			if (i == 0) {
				fromi = 0;
				break;
			}
			char c = addr.charAt(i);
			if (c == '"' || c == '<' || c == ' ' || c == '\t' || c == ':') {
				fromi = i + 1;
				break;
			}
		}
		for (int i = toi; i < addr.length(); i++) {
			char c = addr.charAt(i);
			if (c == '"' || c == '>') {
				String sim = addr.substring(fromi, i);
				return sim.trim();
			}
			if (i == addr.length() - 1) {
				return addr.substring(fromi).trim();
			}
		}
		throw new Exception("Cannot parse Direct Address " + addr);
	}

	String header(List<String> headers, String name) {
		String prefix = (name + ":").toLowerCase();
		for (String h : headers) {
			String hl = h.toLowerCase();
			if (hl.startsWith(prefix))
				return h;
		}
		return "";
	}

	List<String> headers(StringBuffer b) {
		List<String> headers = new ArrayList<String>();
		String CR = "\n";

		int start = 0;
		int end = b.indexOf(CR);
		int length = end - start;

		while (end != -1) {
			if (length > 0) {
				String header = b.substring(start, end).trim();
				if (header.length() == 0)
					break;
				logger.info("Header (" + header.length() + ") : " + header);
				headers.add(header);
				start = end;
				end = b.indexOf(CR, start + 1);
			}
		}

		logger.info("Found " + headers.size() + " headers");

		return headers;
	}

	/**
	 * Strip surrounding < > brackets if present
	 * 
	 * @param input
	 * @return
	 */
	public String stripBrackets(String input) {
		if (input == null || input.length() == 0)
			return input;
		input = input.trim();
		int openI = input.indexOf('<');
		while (openI > -1) {
			input = input.substring(1);
			openI = input.indexOf('<');
		}

		if (input.length() == 0)
			return input;

		int closeI = input.indexOf('>');
		if (closeI > 0)
			input = input.substring(0, closeI);

		return input;
	}

	public MimeMessage generateMDN(String from, String to, String messageId, InputStream signingCert, String signingCertPassword, boolean wrapped) throws MessagingException, Exception {

		// Generate the MDN
		MDNGenerator generator = new MDNGenerator();
		generator.setDisposition("automatic-action/MDN-sent-automatically;processed");
		generator.setFinal_recipient(to);
		generator.setFromAddress(from);
		generator.setOriginal_message_id(messageId);
		generator.setOriginal_recipient(from);
		generator.setReporting_UA_name("direct.nist.gov");
		generator.setReporting_UA_product("Security Agent");
		generator.setSigningCert(signingCert);
		generator.setSigningCertPassword(signingCertPassword);
		generator.setSubject("Automatic MDN");
		generator.setText("Your message was successfully processed.");
		generator.setToAddress(to);
		generator.setEncryptionCert(generator.getEncryptionCertByDnsLookup(to));
		generator.setWrapped(wrapped);

		return generator.generateMDN();

	}

	// this only applies to certificates in byte[] format
	boolean isEmpty(byte[] b) {
		if (b == null)
			return true;
		if (b.length < 10)
			return true;
		return false;
	}

	String getDirectServerName(String domainName) {
		String directServerName = null;
		try {
			directServerName = new DnsLookup().getMxRecord(domainName);
		} catch (TextParseException e) {
			logger.error("    Error parsing MX record from DNS - for domain "
					+ domainName);
		}

		if (directServerName != null && !directServerName.equals(""))
			return directServerName;

		logger.error("    MX record lookup in DNS did not provide a mail handler hostname for domain "
				+ domainName);
		directServerName = "smtp." + domainName;
		logger.error("    Guessing at mail server name - " + directServerName);
		return directServerName;
	}

	public static String getCcdaType(String directAddress) {
		String res = null;
		if (directAddress != null) {
			if (directAddress.contains("@")) {
				res = directAddress.split("@")[0];
				res = res.toLowerCase();
				if (!res.equals("direct-clinical-summary")
				&& !res.equals("direct-ambulatory2")
				&& !res.equals("direct-ambulatory7")
				&& !res.equals("direct-ambulatory1")
				&& !res.equals("direct-inpatient2")
				&& !res.equals("direct-inpatient7")
				&& !res.equals("direct-inpatient1")
				&& !res.equals("direct-vdt-inpatient")
				&& !res.equals("direct-vdt-ambulatory")) {
					res = "non-specific";
				}
			}
		}
		return res;
	}

	public InputStream getClasspathPrivateCert(String path, String extension) throws IOException {
		InputStream input = getClass().getResourceAsStream(path);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = rdr.readLine()) != null) {
			if(line.endsWith(extension)) {
				logger.info("Loading private key (decryption) from classpath " + path + line);
				return getClass().getResourceAsStream(path + line);
			}
		}
		rdr.close();
		return null;
	}

	public InputStream getSigningCertFromFolder(String path, String extension) throws Exception {
		FileInputStream res = null
		File folder = new File(path)
		if(folder.isDirectory()) {
			folder.listFiles().each {
				if(it.getName().endsWith(extension)) {
					logger.info("Getting signing certificates " + it.getAbsolutePath())
					res = new FileInputStream(it)
				}
			}
		}
		if(res == null) {
			throw new Exception("Cannot find certificate in path " + path)
		} else {
			return res
		}
	}

	public boolean isSpam(String to) {
		String[] notAllowed = ["@yahoo.com.tw", "@123.com", "@163.com",
			"@ms35.hinet.net", "@ms46.hinet.net", "@pchome.com.tw", "@ms49.hinet.net",
			"@ms14.hinet.net", "@hotmail.com", "@ms77.hinet.net", "@ms27.hinet.net",
			"@msa.hinet.net", "@seed.net.tw", "ms76.hinet.net"];
		notAllowed = notAllowed.collect {
			/(.*)<(.*)${it}>(.*)/
		}
		def test = notAllowed.any {
			to ==~ it
		}
		return test;
	}

	public InputStream getSigningPrivateCert(String type = 'good') {
		try {
			InputStream res = getSigningCertFromFolder(this.certificatesPath + type, ".p12")
			return res
		} catch(Exception e) {
			logger.info("Cannot get certificate from configured location " + this.certificatesPath + type)
			logger.info(e.getMessage())
			this.certPassword = ""
			return getClasspathPrivateCert("/signing-certificates/good/", ".p12")
		}
	}
	public void manageMDNAddressesTimeout(String smtpFrom, String from, String to, InputStream message) {
		// Get the session variable
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		// Get the MimeMessage object
		MimeMessage msg = new MimeMessage(session, message);
		String originalMsgId = msg.getMessageID()

		String t = smtpFrom.replaceAll("[^0-9]", ""); //get time in minutes from address
		int timeout = Integer.parseInt(t)
		int sleepTime = timeout*60000;
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				logger.info("Thread will sleep for $timeout minutes and send dispatched mdn")
				this.sleep(sleepTime);
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				
		}
	
	public void manageMDNAddresses(String smtpFrom, String from, String to, InputStream message) {
		// Get the session variable
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props, null);
		int flag = 0;
		// Get the MimeMessage object
		MimeMessage msg = new MimeMessage(session, message);
		String originalMsgId = msg.getMessageID()

		switch(smtpFrom) {
			case 'processedonly5':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break

			case 'processeddispatched6':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break

			case 'processedfailuretest':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, ' failed', 'Failure MDN', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break
				
			case 'processdelayeddispatch7':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				logger.info("Thread will sleep for 1 hour 5 minutes and send dispatched mdn")
				this.sleep(3900000);
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break
				
			case 'processdelayeddispatch30min':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				logger.info("Thread will sleep for 30 minutes and send dispatched mdn")
				this.sleep(1800000);
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break
				
			case 'nomdn8':
				logger.info("Found address $smtpFrom so no mdn sent")
				break
			
			case 'goodaddress-plain':
				logger.info("Found address $smtpFrom so no mdn sent")
				break
				
			case 'processedfailure':
				logger.info("Found address $smtpFrom for XDR. Sending processed MDN and failure MDN")
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'failed', 'Failure MDN', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break

			case 'processedtimeoutfailure':
				logger.info("Found address $smtpFrom for XDR. Sending processed MDN and failure MDN")
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				this.sleep(3900000);
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'failed', 'Failure MDN', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break

			case 'noaddressfailure9':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'failed', 'Failure MDN', getSigningPrivateCert(), this.certPassword, MDNType.GOOD)
				break

			case 'processedonly5-plain':
				logger.info("Found address $smtpFrom for XDR. Sending processed MDN and failure MDN")
				SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword)
				break

			case 'processeddispatched6-plain':
				logger.info("Found address $smtpFrom for XDR. Sending processed MDN and failure MDN")
				SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword)
				SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword)
				break

			case 'dispatchedonly-plain':
			/*logger.info("Found address $smtpFrom for XDR. Sending dispatched MDN")
			 SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, true)
			 break*/
				Enumeration headers = msg.getAllHeaders();
				while (headers.hasMoreElements()) {
					Header h = (Header) headers.nextElement();
					String header = h.getName();

					if (header.contains("Disposition-Notification-Options")){
						flag = 1;
					}
				}

				if (flag == 1){
					logger.info("Found address $smtpFrom for XDR. Found Header. Sending dispatched MDN")
					SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'dispatched', '', getSigningPrivateCert(), this.certPassword, true)
				}
				break
			case 'noaddressfailure9-plain':
				SmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'failed', 'Failure MDN', getSigningPrivateCert(), this.certPassword, true)
				break

			case 'white_space_mdn':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.EXTRA_SPACE)
				break

			case 'extra_line_break_mdn':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.EXTRA_LINE_BREAK)
				break

			case 'extra_space_disposition':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.EXTRA_SPACE_DISPOSITION)
				break

			case 'missing_disposition':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.MISS_DISPOSITION)
				break

			case 'null_sender':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.NULL_SENDER)
				break

			case 'different_sender':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.DIFF_SENDER)
				break

			case 'different_msgid':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.DIFF_MSG_ID)
				break

			case 'white_space_822':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.EXTRA_SPACE_822)
				break

			case 'different_cases_822':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.DIFF_CASES_822)
				break

			/*case 'dsn':
				EncryptedSmtpMDNMessageGenerator.sendSmtpMDN(message, originalMsgId, from, to, 'processed', '', getSigningPrivateCert(), this.certPassword, MDNType.DSN)
				break*/

			default:
				logger.info("Could not intepret the address $smtpFrom")
				break
		}
	}

}
