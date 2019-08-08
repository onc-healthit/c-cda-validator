package gov.nist.healthcare.ttt.smtp;

import gov.nist.healthcare.ttt.smtp.TestResult.CriteriaStatus;

import java.util.LinkedHashMap;

/**
 * Each runTestCase returns this interface
 *
 */
public interface ITestResult {
	public LinkedHashMap<String, String> getTestRequestResponses(); // The trail
																	// of all
																	// the
																	// interactions
																	// for this
																	// test case

	public CriteriaStatus getCriteriaMet(); // if this is false, then the test did not
									// get the expected results.

	

	public String getTestCaseDesc();

	public int getTestCaseId();

	public boolean isProctored();
}
