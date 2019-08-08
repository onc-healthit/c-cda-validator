package gov.nist.healthcare.ttt.direct.sender;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsLookup {
	Record[] certRecord = null;
	String type = "";
	String cert = "";
	String algo = "";
	String finalCert = "";
	
	public InputStream convertCertToInputStream(String encCertString) {
		return new ByteArrayInputStream(org.bouncycastle.util.encoders.Base64.decode(encCertString.getBytes()));
	}
	
	public String getMxRecord(String domainname) throws TextParseException {
		Lookup dnsLookup = new Lookup(domainname, Type.MX);
		Record[] records = dnsLookup.run();
		if (records == null || records.length == 0)
			return null;
		String[] d = records[0].rdataToString().split(" ");
		if (d.length < 2)
			return null;
		String value = d[1];
		if (value.endsWith("."))
			value = value.substring(0, value.length() - 1);
		return value;
	}

	public String getCertRecord(String domainname) throws TextParseException, CertificateException {
		Date date = new Date();
		Lookup dnsLookup = new Lookup(domainname, Type.CERT);
		certRecord = dnsLookup.run();
		if (dnsLookup.getResult() != Lookup.SUCCESSFUL)
			return null;
		if (certRecord == null || certRecord.length == 0)
			return null;
		System.out.println("Total Cert found :" + certRecord.length);
		
		for (int x = 0; x < certRecord.length; x++){ // returns the first valid cert
			String[] dig = certRecord[x].rdataToString().split(" ");
			if(dig.length < 4)
				finalCert = null;
			
				InputStream is = convertCertToInputStream(dig[3]);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate cert =  (X509Certificate)cf.generateCertificate(is);
				if(cert.getNotAfter().compareTo(date) >= 0){ //check validity
					finalCert = dig[3];
					break;
			}
				else {
					System.out.println("Certificate Expired on :" + cert.getNotAfter());
				}
		}
		return finalCert;
	}

}
