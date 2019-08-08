package gov.nist.healthcare.ttt.smtp.listener;

import java.io.IOException;

public abstract class AbstractSMTPListener {
	public abstract void listen(int port, int behavior) throws IOException;
}
