package validationTests;

import static org.junit.Assert.assertTrue;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageHeadersValidator;
import org.junit.Test;

public class DirectMessageValidatorResentFields {
	
	DirectMessageHeadersValidator validator = new DirectMessageHeadersValidator();
	
	// DTS 197, Resent-fields, Required
	// Result: Success
	@Test
	public void testResentFields() {
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "resent-to", "content-type", "content-disposition"};
		assertTrue(validator.validateResentFields(resentFields, false).isSuccess());
	}
	
	// Result: Fail
	@Test
	public void testResentFields2() {
		String[] resentFields = {"from", "to", "date", "resent-date", "resent-from", "content-type", "resent-to", "content-disposition"};
		assertTrue(!validator.validateResentFields(resentFields, false).isSuccess());
	}
	
}
