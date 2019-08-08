package gov.nist.healthcare.ttt.webapp.common.model.FileInfo;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public class FileInfo {

	private String flowChunkNumber;
	private String flowChunkSize;
	private String flowCurrentChunkSize;
	private String flowTotalSize;
	private String flowIdentifier;
	private String flowFilename;
	private String flowRelativePath;
	private String flowTotalChunks;

	public FileInfo(String flowChunkNumber, String flowChunkSize,
			String flowCurrentChunkSize, String flowTotalSize,
			String flowIdentifier, String flowFilename,
			String flowRelativePath, String flowTotalChunks) {
		super();
		this.flowChunkNumber = flowChunkNumber;
		this.flowChunkSize = flowChunkSize;
		this.flowCurrentChunkSize = flowCurrentChunkSize;
		this.flowTotalSize = flowTotalSize;
		this.flowIdentifier = flowIdentifier;
		this.flowFilename = flowFilename;
		this.flowRelativePath = flowRelativePath;
		this.flowTotalChunks = flowTotalChunks;
	}

	public FileInfo() {
		super();
		this.flowChunkNumber = "";
		this.flowChunkSize = "";
		this.flowCurrentChunkSize = "";
		this.flowTotalSize = "";
		this.flowIdentifier = "";
		this.flowFilename = "";
		this.flowRelativePath = "";
		this.flowTotalChunks = "";
	}

	public String getFlowChunkNumber() {
		return flowChunkNumber;
	}

	public void setFlowChunkNumber(String flowChunkNumber) {
		this.flowChunkNumber = flowChunkNumber;
	}

	public String getFlowChunkSize() {
		return flowChunkSize;
	}

	public void setFlowChunkSize(String flowChunkSize) {
		this.flowChunkSize = flowChunkSize;
	}

	public String getFlowCurrentChunkSize() {
		return flowCurrentChunkSize;
	}

	public void setFlowCurrentChunkSize(String flowCurrentChunkSize) {
		this.flowCurrentChunkSize = flowCurrentChunkSize;
	}

	public String getFlowTotalSize() {
		return flowTotalSize;
	}

	public void setFlowTotalSize(String flowTotalSize) {
		this.flowTotalSize = flowTotalSize;
	}

	public String getFlowIdentifier() {
		return flowIdentifier;
	}

	public void setFlowIdentifier(String flowIdentifier) {
		this.flowIdentifier = flowIdentifier;
	}

	public String getFlowFilename() {
		return flowFilename;
	}

	public void setFlowFilename(String flowFilename) {
		this.flowFilename = flowFilename;
	}

	public String getFlowRelativePath() {
		return flowRelativePath;
	}

	public void setFlowRelativePath(String flowRelativePath) {
		this.flowRelativePath = flowRelativePath;
	}

	public String getFlowTotalChunks() {
		return flowTotalChunks;
	}

	public void setFlowTotalChunks(String flowTotalChunks) {
		this.flowTotalChunks = flowTotalChunks;
	}
	
	public void setAttributes(MultipartHttpServletRequest request) {
		this.setFlowChunkNumber(request.getParameter("flowChunkNumber"));
		this.setFlowChunkSize(request.getParameter("flowChunkSize"));
		this.setFlowCurrentChunkSize(request.getParameter("flowCurrentChunkSize"));
		this.setFlowFilename(request.getParameter("flowFilename"));
		this.setFlowIdentifier(request.getParameter("flowIdentifier"));
		this.setFlowRelativePath(request.getParameter("flowRelativePath"));
		this.setFlowTotalChunks(request.getParameter("flowTotalChunks"));
		this.setFlowTotalSize(request.getParameter("flowTotalSize"));
	}
	
	public String toString() {
		String res = "";
		res += "flowChunkNumber=" + this.flowChunkNumber + "\n";
		res += "flowChunkSize=" + this.flowChunkSize + "\n";
		res += "flowCurrentChunkSize=" + this.flowCurrentChunkSize + "\n";
		res += "flowTotalSize=" + this.flowTotalSize + "\n";
		res += "flowIdentifier=" + this.flowIdentifier + "\n";
		res += "flowFilename=" + this.flowFilename + "\n";
		res += "flowRelativePath=" + this.flowRelativePath + "\n";
		res += "flowTotalChunks=" + this.flowTotalChunks + "\n";
		return res;
	}

}
