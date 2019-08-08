package gov.nist.healthcare.ttt.direct.messageGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;

public class MessageContentGenerator {
	
	public static MimeBodyPart addTextPart(String textMessage) throws MessagingException {
		MimeBodyPart    msg1 = new MimeBodyPart();
		msg1.setText(textMessage);
		return msg1;
	}
	
	public static MimeBodyPart addAttachement(InputStream attachmentFile, String filename) throws IOException, MessagingException {
		byte[] fileContent = IOUtils.toByteArray(attachmentFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream base64OutputStream = MimeUtility.encode(baos, "base64");
        base64OutputStream.write(fileContent);
        base64OutputStream.close();
        
        byte[] content = baos.toByteArray();
        
        InternetHeaders partHeaders = new InternetHeaders();
        if(filename.contains(".xml")) {
        	partHeaders.addHeader("Content-Type", "text/xml; name="+filename);
        } else if(filename.contains(".zip")) {
        	partHeaders.addHeader("Content-Type", "application/zip; name="+filename);
        } else {
        	partHeaders.addHeader("Content-Type", "application/octet-stream; name="+filename);
        }
        partHeaders.addHeader("Content-Transfer-Encoding", "base64");
        partHeaders.addHeader("Content-Disposition", "attachment; filename="+filename);

        MimeBodyPart ccda = new MimeBodyPart(partHeaders, content);
        return ccda;
	}
}
