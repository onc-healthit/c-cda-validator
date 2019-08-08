package gov.nist.healthcare.ttt.smtp.util;

/**
 * @author sriniadhi Request and Response
 *
 */

public class ReqRes {
	public String request;
	public String response;
	public long timeElapsedInMilliSeconds;
	public boolean isTimeOut;

	public ReqRes(String q, String s) {
		request = q;
		response = s;
	}

	public ReqRes(String q, String s, long elapsedTime) {
		request = q;
		response = s;
		timeElapsedInMilliSeconds = elapsedTime;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
