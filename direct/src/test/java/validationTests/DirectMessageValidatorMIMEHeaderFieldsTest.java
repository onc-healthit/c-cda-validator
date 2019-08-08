package validationTests;

import static org.junit.Assert.*;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;
import org.junit.Test;

public class DirectMessageValidatorMIMEHeaderFieldsTest {

	DirectMimeEntityValidator validator = new DirectMimeEntityValidator();
	
	// DTS 190, All Mime Header Fields, Required
	// Result: Success
	@Test
	public void testAllMimeHeaderFields() {
		assertTrue(validator.validateAllMimeHeaderFields("attachment; filename=smime.p7m").isSuccess());
	}
	
	// Result: Success
	@Test
	public void testAllMimeHeaderFields2() {
		assertTrue(!validator.validateAllMimeHeaderFields("attachment; comment:\"test comment (comment)\"; filename=smime.p7m").isSuccess());
	}

	// Result: Fail
	@Test
	public void testAllMimeHeaderFields3() {
		assertTrue(!validator.validateAllMimeHeaderFields("attachment(comment); filename=smime.p7m").isSuccess());
	}

	// Result: Fail
	@Test
	public void testAllMimeHeaderFields4() {
		assertTrue(!validator.validateAllMimeHeaderFields("attachment; filename=smime.p7m (comment)").isSuccess());
	}

}
