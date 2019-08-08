/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */

package validationTests;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageValidator;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMimeEntityValidator;


public class DirectMessageValidatorContentTypeTest {
	
	DirectMimeEntityValidator validator = new DirectMimeEntityValidator();
	DirectMessageValidator validator2 = new DirectMessageValidator();

	// DTS 133a, Content-Type, Required
	// Result: Success
	@Test
	public void testContentTypeName() {
		assertTrue(validator.validateContentType("application/pkcs7-mime").isSuccess());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeName2() {
		assertTrue(!validator.validateContentType("application").isSuccess());             // Not a valid name
	}
		
		
	// DTS 133b, Content-Type, Required
	// Result: Success
	@Test
	public void testContentTypeName3() {
		assertTrue(validator2.validateContentType2("multipart/signed").isSuccess());
	}
		
	// Result: Fail
	@Test
	public void testContentTypeName4() {
		assertTrue(!validator2.validateContentType2("multipart").isSuccess());          // Not valid
	}
	
	// DTS 160, Content Type Miclag, Required
	// Result: Success
	@Test
	public void testContentTypeMicalg() {
		assertTrue(validator2.validateContentTypeMicalg("sha-1").isSuccess());
	}
	
	// Result: Fail
	@Test
	public void testContentTypeMicalg2() {	
		assertTrue(!validator2.validateContentTypeMicalg("sha-2").isSuccess());  // Not valid
	}
	
	// DTS 205, Content Type Protocol, Required
	// Result: Success
	@Test
	public void testContentTypeProtocol() {
		assertTrue(validator2.validateContentTypeProtocol("\"application/pkcs7-signature\"").isSuccess());
	}
					
	// Result: Fail
	@Test
	public void testContentTypeProtocol2() {
		assertTrue(!validator2.validateContentTypeProtocol("application").isSuccess());  // Not valid
	}
	
	// DTS 206, Content-Transfer-Encoding, Required
	// Result: Success
	@Test
	public void testContentTransferEncoding() {
		assertTrue(validator2.validateContentTransferEncoding("base64").isSuccess());
	}
					
	// Result: Fail
	@Test
	public void testContentTransferEncoding2() {
		assertTrue(!validator2.validateContentTransferEncoding("base").isSuccess());
	}
}
