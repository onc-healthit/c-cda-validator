package gov.nist.healthcare.ttt.webapp.direct.model.messageValidator;

public class DirectMessageAttachments {
	
	private String filename;
	private String rawContent;
	private String downloadLink;
	
	public DirectMessageAttachments() {
		
	}

	public DirectMessageAttachments(String filename, String rawContent,
			String downloadLink) {
		super();
		this.filename = filename;
		this.rawContent = rawContent;
		this.downloadLink = downloadLink;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getRawContent() {
		return rawContent;
	}

	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

}
