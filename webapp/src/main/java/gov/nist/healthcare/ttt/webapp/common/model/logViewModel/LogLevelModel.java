package gov.nist.healthcare.ttt.webapp.common.model.logViewModel;

import java.util.ArrayList;
import java.util.List;

public class LogLevelModel {
	
	public enum LogLevel {
		ALL, INFO, WARNING, ERROR, DEBUG
	}
	
	private List<LogLevel> levels;
	private String logs;
	private boolean noStacktrace;
	
	public LogLevelModel() {
		this.levels = new ArrayList<LogLevelModel.LogLevel>();
		this.logs = "";
		this.noStacktrace = false;
	}
	
	public LogLevelModel(List<LogLevel> levels, String logs, boolean noStacktrace) {
		super();
		this.levels = levels;
		this.logs = logs;
		this.noStacktrace = noStacktrace;
	}

	public List<LogLevel> getLevels() {
		return levels;
	}
	

	public void setLevels(List<LogLevel> levels) {
		this.levels = levels;
	}
	

	public String getLogs() {
		return logs;
	}
	

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public boolean isNoStacktrace() {
		return noStacktrace;
	}
	

	public void setNoStacktrace(boolean noStacktrace) {
		this.noStacktrace = noStacktrace;
	}
}
