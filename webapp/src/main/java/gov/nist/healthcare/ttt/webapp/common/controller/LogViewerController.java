package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.unix4j.Unix4j;
import org.unix4j.builder.Unix4jCommandBuilder;
import org.unix4j.unix.grep.GrepOption;

import gov.nist.healthcare.ttt.webapp.common.model.logViewModel.LogLevelModel;
import gov.nist.healthcare.ttt.webapp.common.model.logViewModel.LogLevelModel.LogLevel;
import gov.nist.healthcare.ttt.webapp.common.model.logViewModel.LogViewModel;

@Controller
@RequestMapping("/api/logview")
public class LogViewerController {

	@Value("${server.tomcat.basedir}")
	String logsBasedir;

	private static Logger logger = Logger.getLogger(LogViewerController.class.getName());

	private final String datePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2} ";

	private Unix4jCommandBuilder unix4j = Unix4j.builder();

	@RequestMapping(value = "/{file:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody HashMap<String, String> getLogs(@PathVariable String file) throws Exception {

		HashMap<String, String> res = new HashMap<String, String>();

		String logsPath = this.logsBasedir + File.separator + "logs" + File.separator + file;
		String logsString = "";
		try {
//			logsString = IOUtils.toString(new FileInputStream(new File(logsPath)), Charsets.UTF_8);
			logsString = unix4j.tail(10000, logsPath).toStringResult();
		} catch (Exception e) {
			logger.error("Could not read logs file: " + e.getMessage());
			throw e;
		}

		res.put("file", logsString);

		return res;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<String> getAllLogFiles() throws Exception {
		
		String logsPath = this.logsBasedir + File.separator + "logs" + File.separator;
		
		return unix4j.ls(logsPath).toStringList();
	}

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody HashMap<String, String> getGrepedLogs(@RequestBody LogViewModel logModel) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();

		String resLogs = unix4j.fromString(logModel.getLogs()).grep(logModel.getGrep()).toStringResult();

		res.put("file", resLogs);

		return res;
	}

	@RequestMapping(value = "/level", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody HashMap<String, String> getLevelLogs(@RequestBody LogLevelModel logLevel) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();

		String pattern = datePattern + "(";

		String resLogs = "";
		
		if(!logLevel.isNoStacktrace()) {
			List<LogLevel> allLvlEnums = new ArrayList<LogLevel>(EnumSet.allOf(LogLevel.class));
			pattern += StringUtils.join(CollectionUtils.subtract(allLvlEnums, logLevel.getLevels()), "|") + ")";
			resLogs = unix4j.fromString(logLevel.getLogs()).grep(GrepOption.invertMatch, pattern).toStringResult();
		} else {
			pattern += StringUtils.join(logLevel.getLevels(), "|") + ")";
			resLogs = unix4j.fromString(logLevel.getLogs()).grep(pattern).toStringResult();
		}

		res.put("file", resLogs);

		return res;
	}

}
