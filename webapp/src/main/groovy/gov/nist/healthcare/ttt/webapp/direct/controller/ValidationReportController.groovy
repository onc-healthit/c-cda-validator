package gov.nist.healthcare.ttt.webapp.direct.controller;

import gov.nist.healthcare.ttt.database.log.PartInterface;
import gov.nist.healthcare.ttt.webapp.common.db.DatabaseInstance;
import gov.nist.healthcare.ttt.webapp.common.model.exceptionJSON.TTTCustomException;
import gov.nist.healthcare.ttt.webapp.direct.model.messageValidator.DirectMessageAttachments;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeBodyPart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

@Controller
@RequestMapping("/api/validationReport")
public class ValidationReportController {

	private static Logger logger = Logger.getLogger(ValidationReportController.class.getName());
	
	private int attachmentNumber = 0;

	@Autowired
	private DatabaseInstance db;

	@RequestMapping(value = "/{messageId:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
    PartInterface validateDirectMessage(@PathVariable String messageId, HttpServletRequest request) throws Exception {
		
		logger.debug("Getting validation report for message id: " + messageId);
		
		PartInterface partRes = db.getLogFacade().getPartByMessageId(messageId);
		if(partRes == null) {
			throw new TTTCustomException("0x0029", "This validation report does not exist");
		}
		return partRes;
	}
	
	@RequestMapping(value = "/rawContent/{messageId:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Collection<DirectMessageAttachments> getPartsRawContent(@PathVariable String messageId, HttpServletRequest request) throws Exception {
		
		logger.debug("Getting attachments for message id: " + messageId);
		
		Collection<DirectMessageAttachments> res = new ArrayList<DirectMessageAttachments>();
		
		PartInterface partRes = db.getLogFacade().getPartByMessageId(messageId);
		if(partRes == null) {
			throw new TTTCustomException("0x0029", "This validation report does not exist");
		}
		
		res = getPartContentTable(res, partRes);
		
		// Reset attachment number
		this.attachmentNumber = 0;
		
		return res;
	}
	
	@RequestMapping(value = "/download/{partId:.+}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody void downloadContent(@PathVariable String partId, HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			PartInterface partRes = db.getLogFacade().getPart(partId);
			String rawContent = partRes.getRawMessage();
			InputStream contentStream;
			
			if(partRes.getContentType().contains("application/zip") ||
			partRes.getContentType().contains("application/x-zip-compressed") ||
			partRes.getContentType().contains("application/octet-stream") ||
			partRes.getContentType().contains("application/pdf")) {
				InputStream tmpZip = new ByteArrayInputStream(rawContent.getBytes(StandardCharsets.UTF_8));
				MimeBodyPart zipPart = new MimeBodyPart(tmpZip);
				contentStream = zipPart.getInputStream();
			} else if(partRes.getContentType().contains("application/xml")) {
				contentStream = new MimeBodyPart(new ByteArrayInputStream(rawContent.getBytes(StandardCharsets.UTF_8))).getInputStream();
			} else {
				contentStream = new ByteArrayInputStream(rawContent.getBytes(StandardCharsets.UTF_8));
			}

			// Create response
			response.setContentType(partRes.getContentType());
			response.setContentLength(contentStream.available());
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", getFilename(partRes));
			if(hasFilename(partRes)) {
				headerValue = String.format(partRes.getContentDisposition());
			}
			response.setHeader(headerKey, headerValue);
	
			// writes the file to the client
			IOUtils.copy(contentStream, response.getOutputStream());

			contentStream.close();
			response.flushBuffer();

		} catch (Exception ex) {
			ex.printStackTrace();
			response.getWriter().print("Error: " + ex.getMessage());
		}
	}
	
	public Collection<DirectMessageAttachments> getPartContentTable(Collection<DirectMessageAttachments> res, PartInterface part) {
		if(!part.getRawMessage().equals("")) {
			res.add(new DirectMessageAttachments(getFilename(part), part.getRawMessage(), saveAttachmentAndGetLink(part)));
		}
		for(PartInterface child : part.getChildren()) {
			getPartContentTable(res, child);
		}
		return res;
	}

	public String getFilename(PartInterface part) {
		String contentDisposition = part.getContentDisposition();
		String res = "No Filename " + this.attachmentNumber;
		if(part.getContentType().contains("pkcs7-mime")) {
			return "encrypted-message.txt";
		} else if(part.getContentType().contains("multipart/signed")) {
			return "decrypted-message.txt";
		} else {
			if(contentDisposition != null) {
				if(contentDisposition.contains("filename")) {
					res = contentDisposition.split("filename=")[1];
					if(res.contains(";")) {
						res = res.split(";")[0];
					}
				} else {
					res = "attachment-" + this.attachmentNumber;
					this.attachmentNumber++;
				}
			}
			return res;
		}
	}
	
	public boolean hasFilename(PartInterface part) {
		String contentDisposition = part.getContentDisposition();
		if(contentDisposition != null) {
			if(contentDisposition.contains("filename")) {
				return true;
			}
		}
		return false;
	}
	
	public String saveAttachmentAndGetLink(PartInterface part) {
		return part.getPartID();
	}
	
}