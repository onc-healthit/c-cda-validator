package validationTests;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;


public class DirectMessageValidatorContentTypeSubtypeTest {

	DirectMimeEntityValidator validator = new DirectMimeEntityValidator();
	
	// DTS 191, Content-Type Subtype, Required
	// Result: Success
	@Test
	public void testContentType() {
		assertTrue(validator.validateContentType("plain/text").isSuccess());
	}

	// Result: Fail
	@Test
	public void testContentType2() {
		assertTrue(!validator.validateContentType("text").isSuccess());
	}

}
