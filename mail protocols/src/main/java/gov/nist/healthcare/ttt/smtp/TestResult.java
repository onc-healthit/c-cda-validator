package gov.nist.healthcare.ttt.smtp;

import gov.nist.healthcare.ttt.smtp.server.AbstractSMTPSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

/*
 * See rfc2821 for the basic specification of SMTP; see also rfc1123 for important additional information. 

 See rfc1893 and rfc2034 for information about enhanced status codes.
 http://www.greenend.org.uk/rjk/tech/smtpreplies.html

 --------------------------------------------------------------------
 Reply codes
 ---------------------------------------------------------------------
 200 (nonstandard success response, see rfc876) 
 211 System status, or system help reply 
 214 Help message 
 220 <domain> Service ready 
 221 <domain> Service closing transmission channel 
 250 Requested mail action okay, completed 
 251 User not local; will forward to <forward-path> 
 252 Cannot VRFY user, but will accept message and attempt delivery 
 354 Start mail input; end with <CRLF>.<CRLF> 
 421 <domain> Service not available, closing transmission channel 
 450 Requested mail action not taken: mailbox unavailable 
 451 Requested action aborted: local error in processing 
 452 Requested action not taken: insufficient system storage 
 500 Syntax error, command unrecognised 
 501 Syntax error in parameters or arguments 
 502 Command not implemented 
 503 Bad sequence of commands 
 504 Command parameter not implemented 
 521 <domain> does not accept mail (see rfc1846) 
 530 Access denied (???a Sendmailism) 
 550 Requested action not taken: mailbox unavailable 
 551 User not local; please try <forward-path> 
 552 Requested mail action aborted: exceeded storage allocation 
 553 Requested action not taken: mailbox name not allowed 
 554 Transaction failed 
 */

public class TestResult implements ITestResult {

	// Interface implementations
	@Override
	public LinkedHashMap<String, String> getTestRequestResponses() {
		return reqres;
	}
	
	@Override
	public String getTestCaseDesc() {
		return desc;
	}

	@Override
	public int getTestCaseId() {
		return id;
	}

	@Override
	public boolean isProctored() {
		return proctored;
	}

	/*@Override
	public boolean isCriteriaMet() {
		return criteriaMet;
	}*/

	// Data Members
	// Stores all the results and responses for a given test case
	LinkedHashMap<String, String> reqres = new LinkedHashMap<String, String>();
	String desc; //
	int id;


	public void setCriteriamet(CriteriaStatus criteriamet) {
		this.criteriamet = criteriamet;
	}

	public enum CriteriaStatus {TRUE, FALSE, MANUAL, STEP2, RETRY}
	CriteriaStatus criteriamet;
	boolean didRequestTimeOut = false;
	long timeElapsedInSeconds = 0L;
	boolean proctored = false;
	public LinkedHashMap<String, String> attachments = new LinkedHashMap<String, String>();
	public LinkedHashMap<String, JsonNode> CCDAValidationReports = new LinkedHashMap<String, JsonNode>();
	public String  MessageId;
	public String fetchType;
	public String searchType;
	public String startTime;
	
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getFetchType() {
		return fetchType;
	}

	public void setFetchType(String fetchType) {
		this.fetchType = fetchType;
	}
	
	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String messageId) {
		MessageId = messageId;
	}

	public LinkedHashMap<String, JsonNode> getCCDAValidationReports() {
		return CCDAValidationReports;
	}

	public void setCCDAValidationReports(
			LinkedHashMap<String, JsonNode> cCDAValidationReports) {
		CCDAValidationReports = cCDAValidationReports;
	}

	public LinkedHashMap<String, String> getAttachments() {
		return attachments;
	}

	public void setAttachments(LinkedHashMap<String, String> attachments) {
		this.attachments = attachments;
	}

	// Methods
	/*public void isCriteriaMet(boolean f) {
		criteriaMet = f;
	}*/

	public int getLastTestResultStatus() {
		return AbstractSMTPSender.getStatus(getLastTestResponse());
	}

	public String getLastTestResponse() {
		return getTestResult(-1);
	}

	public String getTestResult(int n) {
		int sz = reqres.size();

		// negative value and large value result in the last message
		if (n < 0 || n >= sz)
			n = sz - 1;

		if (sz > 0) {
			@SuppressWarnings("unchecked")
			Entry<String, String>[] es = new Entry[sz];
			reqres.entrySet().toArray(es);
			String lastMessage = es[n].getValue();
			return lastMessage;
		}
		return null;
	}

	public void setTestCaseId(int _id) {
		id = _id;
	}

	public void setTestCaseDesc(String desc) {
		this.desc = desc;
	}

	public boolean isDidRequestTimeOut() {
		return didRequestTimeOut;
	}

	public void setDidRequestTimeOut(boolean didRequestTimeOut) {
		this.didRequestTimeOut = didRequestTimeOut;
	}

	public long getTimeElapsedInSeconds() {
		return timeElapsedInSeconds;
	}

	public void setTimeElapsedInSeconds(long timeoutInSeconds) {
		this.timeElapsedInSeconds = timeoutInSeconds;
	}

	public void setProctored(boolean proctored) {
		this.proctored = proctored;
	}

	@Override
	public CriteriaStatus getCriteriaMet() {
		// TODO Auto-generated method stub
		return this.criteriamet;
	}

	

	

}

/*
 * ------------------------------------------------------------- Grouped by
 * command --------------------------------------------------------------
 * 
 * 
 * Command
 * 
 * Code
 * 
 * Description
 * 
 * 
 * connect 220 <domain> Service ready 421 <domain> Service not available,
 * closing transmission channel HELO 250 Requested mail action okay, completed
 * 500 Syntax error, command unrecognised 501 Syntax error in parameters or
 * arguments 504 Command parameter not implemented 521 <domain> does not accept
 * mail [rfc1846] 421 <domain> Service not available, closing transmission
 * channel EHLO 250 Requested mail action okay, completed 550 Not implemented
 * 500 Syntax error, command unrecognised 501 Syntax error in parameters or
 * arguments 504 Command parameter not implemented 421 <domain> Service not
 * available, closing transmission channel MAIL 250 Requested mail action okay,
 * completed 552 Requested mail action aborted: exceeded storage allocation 451
 * Requested action aborted: local error in processing 452 Requested action not
 * taken: insufficient system storage 500 Syntax error, command unrecognised 501
 * Syntax error in parameters or arguments 421 <domain> Service not available,
 * closing transmission channel RCPT 250 Requested mail action okay, completed
 * 251 User not local; will forward to <forward-path> 550 Requested action not
 * taken: mailbox unavailable 551 User not local; please try <forward-path> 552
 * Requested mail action aborted: exceeded storage allocation 553 Requested
 * action not taken: mailbox name not allowed 450 Requested mail action not
 * taken: mailbox unavailable 451 Requested action aborted: local error in
 * processing 452 Requested action not taken: insufficient system storage 500
 * Syntax error, command unrecognised 501 Syntax error in parameters or
 * arguments 503 Bad sequence of commands 521 <domain> does not accept mail
 * [rfc1846] 421 <domain> Service not available, closing transmission channel
 * DATA 354 Start mail input; end with <CRLF>.<CRLF> 451 Requested action
 * aborted: local error in processing 554 Transaction failed 500 Syntax error,
 * command unrecognised 501 Syntax error in parameters or arguments 503 Bad
 * sequence of commands 421 <domain> Service not available, closing transmission
 * channel received data 250 Requested mail action okay, completed 552 Requested
 * mail action aborted: exceeded storage allocation 554 Transaction failed 451
 * Requested action aborted: local error in processing 452 Requested action not
 * taken: insufficient system storage RSET 200 (nonstandard success response,
 * see rfc876) 250 Requested mail action okay, completed 500 Syntax error,
 * command unrecognised 501 Syntax error in parameters or arguments 504 Command
 * parameter not implemented 421 <domain> Service not available, closing
 * transmission channel SEND 250 Requested mail action okay, completed 552
 * Requested mail action aborted: exceeded storage allocation 451 Requested
 * action aborted: local error in processing 452 Requested action not taken:
 * insufficient system storage 500 Syntax error, command unrecognised 501 Syntax
 * error in parameters or arguments 502 Command not implemented 421 <domain>
 * Service not available, closing transmission channel SOML 250 Requested mail
 * action okay, completed 552 Requested mail action aborted: exceeded storage
 * allocation 451 Requested action aborted: local error in processing 452
 * Requested action not taken: insufficient system storage 500 Syntax error,
 * command unrecognised 501 Syntax error in parameters or arguments 502 Command
 * not implemented 421 <domain> Service not available, closing transmission
 * channel SAML 250 Requested mail action okay, completed 552 Requested mail
 * action aborted: exceeded storage allocation 451 Requested action aborted:
 * local error in processing 452 Requested action not taken: insufficient system
 * storage 500 Syntax error, command unrecognised 501 Syntax error in parameters
 * or arguments 502 Command not implemented 421 <domain> Service not available,
 * closing transmission channel VRFY 250 Requested mail action okay, completed
 * 251 User not local; will forward to <forward-path> 252 Cannot VRFY user, but
 * will accept message and attempt delivery 550 Requested action not taken:
 * mailbox unavailable 551 User not local; please try <forward-path> 553
 * Requested action not taken: mailbox name not allowed 500 Syntax error,
 * command unrecognised 501 Syntax error in parameters or arguments 502 Command
 * not implemented 504 Command parameter not implemented 421 <domain> Service
 * not available, closing transmission channel EXPN 250 Requested mail action
 * okay, completed 550 Requested action not taken: mailbox unavailable 500
 * Syntax error, command unrecognised 501 Syntax error in parameters or
 * arguments 502 Command not implemented 504 Command parameter not implemented
 * 421 <domain> Service not available, closing transmission channel HELP 211
 * System status, or system help reply 214 Help message 500 Syntax error,
 * command unrecognised 501 Syntax error in parameters or arguments 502 Command
 * not implemented 504 Command parameter not implemented 421 <domain> Service
 * not available, closing transmission channel NOOP 200 (nonstandard success
 * response, see rfc876) 250 Requested mail action okay, completed 500 Syntax
 * error, command unrecognised 421 <domain> Service not available, closing
 * transmission channel QUIT 221 <domain> Service closing transmission channel
 * 500 Syntax error, command unrecognised TURN 250 Requested mail action okay,
 * completed 502 Command not implemented 500 Syntax error, command unrecognised
 * 503 Bad sequence of commands
 */