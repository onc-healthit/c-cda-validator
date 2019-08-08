package gov.nist.healthcare.ttt.webapp.common.controller;

import gov.nist.healthcare.ttt.webapp.common.model.FileInfo.FileInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

@Controller
@RequestMapping("/api/upload")
public class TempUploadController {
	
	String tDir = System.getProperty("java.io.tmpdir");
	
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody FileInfo uploadCert(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		FileInfo fileInfo = new FileInfo();
		
		fileInfo.setAttributes(request);
		
		// Extract the file
		
		Iterator<String> itr = request.getFileNames();

        MultipartFile file = request.getFile(itr.next());
        
        File temp;
        
        // Unique uuid for filename
        UUID fileuuid = UUID.randomUUID();
        
        if(!fileInfo.getFlowFilename().equals("")) {
        	temp = new File(tDir + File.separator + fileInfo.getFlowFilename() + "-ett_" + fileuuid + "_ett");
        } else {
        	temp = File.createTempFile("tempfile", ".tmp");
        }
        
        temp.deleteOnExit();
        
        // Write the file
        file.transferTo(temp);

        fileInfo.setFlowRelativePath(temp.getAbsolutePath());
        
		return fileInfo;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody FileInfo uploadCert(@RequestParam(value = "flowFilename") String filename, HttpServletResponse response) throws IOException {
		FileInfo fileInfo = new FileInfo();
		File f = new File(tDir + File.separator + filename);
		if(f.exists()) {
			fileInfo.setFlowRelativePath(f.getAbsolutePath());
			return fileInfo;
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return fileInfo;
	}

}