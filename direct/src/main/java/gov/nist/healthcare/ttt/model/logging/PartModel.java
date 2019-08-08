package gov.nist.healthcare.ttt.model.logging;

import gov.nist.healthcare.ttt.database.log.DetailInterface;
import gov.nist.healthcare.ttt.database.log.DetailInterface.Status;
import gov.nist.healthcare.ttt.database.log.PartImpl;
import gov.nist.healthcare.ttt.database.log.PartInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.Part;

import org.apache.commons.lang3.StringUtils;

public class PartModel extends PartImpl implements PartInterface {
	
	private String contentType;
	private String contentTransferEncoding;
	private String contentDisposition;
	private Part content;
	private String rawMessage;
	private Boolean status;
	private PartInterface parent;
	private Collection<PartInterface> children;
	private List<DetailInterface> details;
	
	private boolean isQuotedPrintable = false;

	public PartModel(String contentType, String contentTransferEncoding,
			String contentDisposition, Part content, boolean status,
			PartInterface parent) {
		super();
		this.contentType = contentType;
		this.contentTransferEncoding = contentTransferEncoding;
		this.contentDisposition = contentDisposition;
		this.content = content;
		this.status = status;
		this.parent = parent;
		this.rawMessage = "";
		this.children = new ArrayList<PartInterface>();
		this.details = new ArrayList<DetailInterface>();
	}

	public PartModel() {
		this.status = true;
		this.rawMessage = "";
		this.parent = null;
		this.children = new ArrayList<PartInterface>();
		this.details = new ArrayList<DetailInterface>();
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentTransferEncoding() {
		return contentTransferEncoding;
	}

	public void setContentTransferEncoding(String contentTransferEncoding) {
		this.contentTransferEncoding = contentTransferEncoding;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public Part getContent() {
		return content;
	}

	public void setContent(Part content) {
		this.content = content;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public Boolean isStatus() {
		return this.status;
	}
	
	public List<DetailInterface> getDetails() {
		return details;
	}

	public void setDetails(List<DetailInterface> details) {
		this.details = details;
	}

	public void addNewDetailLine(DetailInterface detail) {
		this.details.add(detail);
		if(detail.getStatus().equals(Status.ERROR)) {
			this.status = false;
		}
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public PartInterface getParent() {
		return parent;
	}

	public void setParent(PartInterface parent) {
		this.parent = parent;
	}
	
	public boolean hasParent() {
		return (this.parent != null);
	}
	
	public Collection<PartInterface> getChildren() {
		return children;
	}

	public void setChildren(Collection<PartInterface> children) {
		this.children = children;
	}

	public void addChild(PartInterface child) {
		this.children.add(child);
	}
	
	public boolean hasChild() {
		return !this.children.isEmpty();
	}

	@Override
	public String toString() {
		String res;
		if(!children.isEmpty()) {
		res = "\n" + StringUtils.repeat("\t", this.getOffset()) + "--[contentType=" + contentType.trim().replace("\r\n", "")
//				+ ", contentTransferEncoding=" + contentTransferEncoding
//				+ ", contentDisposition=" + contentDisposition 
//				+ ", content=" + content 
//				+ ", status=" + status 
//				+ ", parent=" + parent
				+ ", detailedList=" + details 
				+ ", children=[" + children
				+ "\n" + StringUtils.repeat("\t", this.getOffset()) + "]";
		} else {
			res = "\n" + StringUtils.repeat("\t", this.getOffset()) + "--[contentType=" + contentType.trim().replace("\r\n", "")
//					+ ", contentTransferEncoding=" + contentTransferEncoding
//					+ ", contentDisposition=" + contentDisposition 
//					+ ", content=" + content 
//					+ ", status=" + status 
//					+ ", parent=" + parent
					+ ", leaf"
//					+ ", children=[\n" + StringUtils.repeat("\t", this.getOffset()) + "--" + children
					+ ", detailedList=" + details 
					+ "\n" + StringUtils.repeat("\t", this.getOffset()) + "]";
		}
		return res;
	}

	public int getOffset() {
		int off = 0;
		PartModel it = this;
		while(it.hasParent()) {
			off++;
			it = (PartModel) it.getParent();
		}
		return off;
	}

	public boolean isQuotedPrintable() {
		return isQuotedPrintable;
	}

	public void setQuotedPrintable(boolean isQuotedPrintable) {
		this.isQuotedPrintable = isQuotedPrintable;
	}

}
