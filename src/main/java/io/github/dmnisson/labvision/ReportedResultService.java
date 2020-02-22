package io.github.dmnisson.labvision;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.ExternalReportDocument;
import io.github.dmnisson.labvision.entities.FileType;
import io.github.dmnisson.labvision.entities.FilesystemReportDocument;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.ReportDocument;
import io.github.dmnisson.labvision.entities.ReportDocumentType;
import io.github.dmnisson.labvision.entities.ReportedResult;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.reportdocs.ReportDocumentService;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;
import io.github.dmnisson.labvision.utils.URLUtils;

@Service
public class ReportedResultService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private ReportDocumentService reportDocumentService;
	
	public ReportedResult createReportedResult(Integer experimentId, String reportName, ReportDocumentType documentType, URL externalDocumentURL, MultipartFile filesystemDocumentFile, LabVisionUser user, Experiment experiment) throws IOException {
		// need to initialize reportedResults
		Student student = studentRepository.findById(user.getId()).get();
		
		ReportedResult reportedResult = experiment.addReportedResult(student);
		reportedResult.setName(reportName);
		
		ReportDocument reportDocument = null;
		
		if (Objects.nonNull(documentType)) {
			switch (documentType) {
			case EXTERNAL:
				if (Objects.nonNull(externalDocumentURL)) {
					ExternalReportDocument externalReportDocument = new ExternalReportDocument();
					
					String filename = URLUtils.getFilenameFromURL(externalDocumentURL);
					externalReportDocument.setFilename(filename);
					externalReportDocument.setFileType(FileType.fromFilename(filename));
					externalReportDocument.setReportDocumentURLString(externalDocumentURL.toString());
					
					reportDocument = externalReportDocument;
				}
				break;
			case FILESYSTEM:
				if (Objects.nonNull(filesystemDocumentFile)) {
					// create the report filesystem document
					FilesystemReportDocument filesystemReportDocument = new FilesystemReportDocument();
					
					reportDocumentService.updateFilesystemReportDocumentEntity(experimentId, filesystemDocumentFile, student,
							filesystemReportDocument);
					
					reportDocument = filesystemReportDocument;
				}
				break;
			}
		}
		
		if (Objects.nonNull(reportDocument)) reportedResult.setReportDocument(reportDocument);
		
		reportedResult = reportedResultRepository.save(reportedResult);
		return reportedResult;
	}

	public ReportedResult updateReportedResult(String reportName, ReportDocumentType documentType, URL externalDocumentURL, MultipartFile filesystemDocumentFile, ReportedResult reportedResult, Student student) throws IOException {
		Experiment experiment = reportedResult.getExperiment();
		
		reportedResult.setName(reportName);
		
		ReportDocument reportDocument = null;
		
		switch (documentType) {
		case EXTERNAL:
			if (Objects.nonNull(externalDocumentURL)) {
				reportDocument = reportedResult.getReportDocument();
				
				ExternalReportDocument externalReportDocument;
				if (Objects.isNull(reportDocument) ||
						!reportDocument.getDocumentType().equals(ReportDocumentType.EXTERNAL)) {
					externalReportDocument = new ExternalReportDocument();
				} else {
					externalReportDocument = (ExternalReportDocument) reportDocument;
				}
				
				externalReportDocument.setReportDocumentURLString(externalDocumentURL.toString());
				
				String filename = URLUtils.getFilenameFromURL(externalDocumentURL);
				externalReportDocument.setFilename(filename);
				
				externalReportDocument.setFileType(FileType.fromFilename(filename));
				
				reportDocument = externalReportDocument;
			}
			break;
		case FILESYSTEM:
			if (Objects.nonNull(filesystemDocumentFile)) {
				reportDocument = reportedResult.getReportDocument();
				
				FilesystemReportDocument filesystemReportDocument;
				if (Objects.isNull(reportDocument) 
						|| !reportDocument.getDocumentType().equals(ReportDocumentType.FILESYSTEM)) {
					filesystemReportDocument = new FilesystemReportDocument();
				} else {
					filesystemReportDocument = (FilesystemReportDocument) reportDocument;
				}
				
				reportDocumentService.updateFilesystemReportDocumentEntity(experiment.getId(), filesystemDocumentFile, 
						student, filesystemReportDocument);
				
				reportDocument = filesystemReportDocument;
			}
		}
		
		if (Objects.nonNull(reportDocument)) reportedResult.setReportDocument(reportDocument);
		
		reportedResult = reportedResultRepository.save(reportedResult);
		return reportedResult;
	}

}
