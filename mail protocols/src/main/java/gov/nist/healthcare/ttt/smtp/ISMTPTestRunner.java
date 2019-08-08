package gov.nist.healthcare.ttt.smtp;



/**
 * Interface for running test cases
 *
 */
public interface ISMTPTestRunner {
	ITestResult[] runTestCase(int i, TestInput ti);

	ITestResult[] runAllTests(TestInput ti);

	int[] getTestCaseIds();
}