package gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON;

public class TTTCustomException extends Exception {
	
	private static final long serialVersionUID = -3332292346834265371L;
	
	private String code;
	
	public TTTCustomException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
