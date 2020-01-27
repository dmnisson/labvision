package io.github.dmnisson.labvision.reportdocs;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.entities.FileType;
import io.github.dmnisson.labvision.entities.FilesystemReportDocument;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;

@Service
public class ReportDocumentService {
	
	@Value("${app.reports.upload-file-path:reports}")
	private String reportUploadFilePath;
	
	@Autowired
	ReportedResultRepository reportedResultRepository;
	
	public String buildReportDocumentUrl(Integer reportId) throws MalformedURLException, UnsupportedEncodingException {
		return reportedResultRepository.findById(reportId).get()
				.getReportDocument()
				.getReportDocumentURL(MvcUriComponentsBuilder.fromController(ReportDocumentController.class)
						.build().toUri().toURL()).toString();
	}
	
	public void updateFilesystemReportDocumentEntity(Integer experimentId, MultipartFile filesystemDocumentFile,
			Student student, FilesystemReportDocument filesystemReportDocument) throws IOException {
		String originalFilename = filesystemDocumentFile.getOriginalFilename();
		filesystemReportDocument.setFilename(originalFilename);
		
		// set file type from MIME type
		FileType fileType = FileType.ofContentType(filesystemDocumentFile.getContentType());
		if (Objects.isNull(fileType)) {
			// set file type from extension if content type not available or not specific enough
			fileType = FileType.ofExtension(
					originalFilename.substring(originalFilename.lastIndexOf('.'))
					);
		}
		
		filesystemReportDocument.setFileType(fileType);
		
		// save the file into the configured path
		Path filesystemPath = buildFilesystemPath(experimentId, student.getId(), originalFilename);
		
		filesystemDocumentFile.transferTo(filesystemPath);
		
		filesystemReportDocument.setFilesystemPath(filesystemPath.normalize().toString());
		
		// set path info to use to access file
		// TODO consider removing this docsPathInfo property and using MvcUriComponentsBuilder to
		// construct it for the report pages
		filesystemReportDocument.setDocsPathInfo(
				MvcUriComponentsBuilder.fromController(ReportDocumentController.class)
				.build().toUri()
					.relativize(
							MvcUriComponentsBuilder.fromMethodName(
									ReportDocumentController.class, 
									"getReportDocument",
									student.getId(),
									experimentId,
									originalFilename.replace(File.separator, "__"),
									new Object(), new Object()
									).build(false).toUri()
							).toString()
				);
	}

	public Path buildFilesystemPath(Integer experimentId, Integer studentId, String originalFilename) throws IOException {
		String directoryPathname = reportUploadFilePath
				+ File.separator + "student_" + studentId
				+ File.separator + "experiment_" + experimentId;
		Path directoryPath = Paths.get(directoryPathname);
		Files.createDirectories(directoryPath);
		// replace characters that match separator characters on this system with "__"
		Path filesystemPath = Paths.get(
				directoryPath.normalize().toString(), 
				originalFilename.replace(File.separator, "__")
				);
		return filesystemPath;
	}
}
