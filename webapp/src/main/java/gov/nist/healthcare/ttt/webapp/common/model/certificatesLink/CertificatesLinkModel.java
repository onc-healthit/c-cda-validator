package gov.nist.healthcare.ttt.webapp.common.model.certificatesLink;

public class CertificatesLinkModel {

	private String name;
	private String icon;
	private String description;
	private String link;
	private String hoverText;

	public CertificatesLinkModel(String name, String icon, String description, String link,String hoverText) {
		super();
		this.name = name;
		this.icon = icon;
		this.description = description;
		this.link = link;
		this.hoverText = hoverText;
	}

	public CertificatesLinkModel() {
		super();
		this.name = "";
		this.icon = "";
		this.description = "";
		this.link = "";
		this.hoverText = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getHoverText() {
		return hoverText;
	}

	public void setHoverText(String hoverText) {
		this.hoverText = hoverText;
	}
}
