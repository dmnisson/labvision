package io.github.dmnisson.labvision.reportdocs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.entities.FilesystemReportDocument;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.repositories.FilesystemReportDocumentRepository;

@Controller
@RequestMapping("/reportdocs")
public class ReportDocumentController {

	@Autowired
	private FilesystemReportDocumentRepository filesystemReportDocumentRepository;
	
	@Autowired
	private ReportDocumentService reportDocumentService;
	
	@GetMapping("/{studentId}/{experimentId}/{filename}")
	public void getReportDocument(
			@PathVariable Integer studentId, @PathVariable Integer experimentId, @PathVariable String filename, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, HttpServletResponse response) throws IOException {
		
		Path filesystemPath = reportDocumentService.buildFilesystemPath(experimentId, studentId, filename);
		
		FilesystemReportDocument filesystemReportDocument = 
				filesystemReportDocumentRepository.findByFilesystemPath(filesystemPath.normalize().toString())
				.orElseThrow(() -> new ResourceNotFoundException(FilesystemReportDocument.class, filename));
		
		File file = filesystemReportDocument.getReportDocumentFile();
		
		response.setContentType(filesystemReportDocument.getContentType());
		response.setContentLengthLong(file.length());
		response.setDateHeader("Date", Instant.now().toEpochMilli());
		response.setDateHeader("Last-Modified", file.lastModified());
		
		// TODO consider processing this asynchronously
		ServletOutputStream out = response.getOutputStream();
		Files.copy(filesystemPath, out);
		out.flush();
	}
	
}