package gov.nist.healthcare.ttt.webapp.common.model.logViewModel;

public class LogViewModel {

	private String logs;
	private String grep;
	private int grepAContext;
	private int grepBContext;
	
	public LogViewModel() {
		this.logs = "";
		this.grep = "";
		this.grepAContext = 0;
		this.grepBContext = 0;
	}

	public LogViewModel(String logs, String grep, int grepAContext, int grepBContext) {
		super();
		this.logs = logs;
		this.grep = grep;
		this.grepAContext = grepAContext;
		this.grepBContext = grepBContext;
	}

	public String getLogs() {
		return logs;
	}
	

	public void setLogs(String logs) {
		this.logs = logs;
	}
	

	public String getGrep() {
		return grep;
	}
	

	public void setGrep(String grep) {
		this.grep = grep;
	}
	

	public int getGrepAContext() {
		return grepAContext;
	}
	

	public void setGrepAContext(int grepAContext) {
		this.grepAContext = grepAContext;
	}
	

	public int getGrepBContext() {
		return grepBContext;
	}
	

	public void setGrepBContext(int grepBContext) {
		this.grepBContext = grepBContext;
	}
}
