package gov.nist.healthcare.ttt.jamesext;

import org.apache.james.protocols.api.Request;
import org.apache.james.protocols.api.Response;
import org.apache.james.protocols.smtp.SMTPResponse;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.core.esmtp.AuthCmdHandler;

public class RequireTLSAuthCmdHander extends AuthCmdHandler {

	private static final Response TLS_REQUIRED = new SMTPResponse("530",
			"5.7.0 Must issue a STARTTLS command first").immutable();

	public Response onCommand(SMTPSession session, Request request) {
		if (session.isTLSStarted())
			return super.onCommand(session, request);

		return TLS_REQUIRED;
	}
}
