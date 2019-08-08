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
Author: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */

package validationTests;


import static org.junit.Assert.*;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageHeadersValidator;

import org.junit.Test;

public class DirectMessageValidatorMessageIdTest {
	
	DirectMessageHeadersValidator validator = new DirectMessageHeadersValidator();
	
	// DTS 121, Message-Id, Required
	// Result: Success
	@Test
	public void testMessageId() {	
		assertTrue(validator.validateMessageId("<4EC2C25A.8090002@ssa-w0066.acct04.us.lmco.com>", false).isSuccess());
	}
			
	// Result: Fail
	@Test
	public void testMessageId2() {
		assertTrue(!validator.validateMessageId("4EC2C25A.8090002@ssa-w0066.acct04.us.lmco.com", false).isSuccess());   // Not a valid name
	}
}
