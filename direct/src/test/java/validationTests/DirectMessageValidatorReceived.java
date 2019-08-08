package validationTests;

import static org.junit.Assert.assertTrue;
import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.direct.directValidator.DirectMessageHeadersValidator;

import org.junit.Test;

public class DirectMessageValidatorReceived {

DirectMessageHeadersValidator validator = new DirectMessageHeadersValidator();
	
	// DTS 121, Message-Id, Required
	// Result: Success
	@Test
	public void testReceived() {
		String received = "from VERONA-EXCH-2.epic.com (2620:72:0:8e27::13) by verona-exch-2.epic.com (2620:72:0:8e27::13) with Microsoft SMTP Server (TLS) id 15.0.995.29; Wed, 18 Feb 2015 17:17:55 -0600";
		assertTrue(validator.validateReceived(received, false).getStatus().equals(Status.WARNING));
	}
	
	@Test
	public void testReceived2() {
		String received = "from PHILIPW-PC (c-73-213-26-177.hsd1.md.comcast.net [73.213.26.177]) (authenticated bits=0) by has.com (8.14.5/8.14.4) with ESMTP id t0GLSfbF009835 for <direct-clinical-summary@hit-dev.nist.gov>; Fri, 16 Jan 2015 21:28:43 GMT";
		assertTrue(validator.validateReceived(received, false).getStatus().equals(Status.WARNING));
	}
	
	@Test
	public void testReceived3() {
		String received = "from zeus.med-web.com (zeus-old.med-web.com [192.168.240.12]) by hermes.med-web.com (Postfix) with ESMTP id 8E01E688A78 for ; Fri, 19 Dec 2014 15:26:31 -0500 (EST)";
		assertTrue(validator.validateReceived(received, false).isSuccess());
	}
}
