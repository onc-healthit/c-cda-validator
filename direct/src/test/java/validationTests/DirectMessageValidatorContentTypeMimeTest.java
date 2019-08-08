package validationTests;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;

public class DirectMessageValidatorContentTypeMimeTest {
	
	DirectMimeEntityValidator validator = new DirectMimeEntityValidator();

	// DTS 133-145-146, Content-Type, Required
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

	// Result: Success
	@Test
	public void testContentTypeName() {
		assertTrue(validator.validateContentType("X-test").isSuccess());
	}

}
