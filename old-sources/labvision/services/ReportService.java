package labvision.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;

import com.google.common.io.ByteStreams;

import labvision.LabVisionConfig;
import labvision.dto.experiment.report.ReportForFacultyReportView;
import labvision.dto.experiment.report.ReportForReportView;
import labvision.dto.experiment.report.ResultInfo;
import labvision.dto.faculty.report.ReportForFacultyExperimentView;
import labvision.dto.student.reports.ReportForStudentReportsTable;
import labvision.entities.Course;
import labvision.entities.Course_;
import labvision.entities.Experiment;
import labvision.entities.Experiment_;
import labvision.entities.ExternalReportDocument;
import labvision.entities.FileType;
import labvision.entities.FilesystemReportDocument;
import labvision.entities.ReportDocument;
import labvision.entities.ReportDocumentType;
import labvision.entities.ReportedResult;
import labvision.entities.ReportedResult_;
import labvision.entities.Student;
import labvision.entities.Student_;
import labvision.utils.URLUtils;

public class ReportService extends JpaService {
	private static final String REPORT_DOCUMENT_SERVLET_NAME = "report-docs";
	private final LabVisionConfig config;
		
	public ReportService(EntityManagerFactory entityManagerFactory,
			LabVisionConfig config) {
		super(entityManagerFactory);
		this.config = config;
	}

	public int getReportStudentIdByFilesystemPath(String filesystemPath) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT rr.student.id FROM FilesystemReportDocument frd " +
					"JOIN frd.reportedResult rr " +
					"WHERE frd.filesystemPath=:filesystempath";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("filesystempath", filesystemPath);
			return query.getResultStream().findAny().orElse(-1);
		});
	}

	public Set<Integer> getExperimentInstructorsByFilesystemPath(String filesystemPath) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT rr.experiment.instructors FROM FilesystemReportDocument frd " +
					"JOIN frd.reportedResult rr " +
					"WHERE frd.filesystemPath=:filesystempath";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("filesystempath", filesystemPath);
			return query.getResultStream().collect(Collectors.toSet());
		});
	}

	public Integer getReportStudentId(int reportId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT rr.student.id " +
					"FROM ReportedResult rr " +
					"WHERE rr.id=:reportid";
			
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("reportid", reportId);
			return query.getResultStream().findAny().orElse(null);
		});
	}
	
	public ReportForReportView getReport(int reportId) {
		return getReport(reportId, ReportForReportView.class);
	}

	public <DTO extends ReportForReportView> DTO getReport(
			int reportId, Class<DTO> dtoClass) {
		return withEntityManager(manager -> {
			String dtoClassName = dtoClass.getCanonicalName();
			boolean needStudentInfo = ReportForFacultyReportView.class.isAssignableFrom(dtoClass);
			
			String queryString =
					"SELECT new " + dtoClassName + "(" +
					"	rr.id," +
					"	rr.experiment.id," +
					"	rr.name," +
					"	rr.reportDocument.fileType," +
					"	rr.reportDocument.documentType," +
					"	rr.reportDocument.filename," +
					"	rr.reportDocument.lastUpdated," +
					"	rr.score" +
					(needStudentInfo ? ", rr.student.id" : "") +
					(needStudentInfo ? ", rr.student.name" : "") +
					") FROM ReportedResult rr " +
					"WHERE rr.id=:reportid";
			
			// first check if report exists
			TypedQuery<DTO> query = 
					manager.createQuery(queryString, dtoClass);
			query.setParameter("reportid", reportId);
			return query.getResultStream().findAny().orElse(null);
		});
	}
	
	public List<ResultInfo> getAcceptedResults(int reportId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.report.ResultInfo(" +
					"	ar.id," +
					"	ar.name," +
					"	ar.value.value," +
					"	ar.value.uncertainty," +
					"	ar.variable.quantityTypeId" +
					") FROM ReportedResult rr " +
					"JOIN rr.experiment e " +
					"JOIN e.acceptedResults ar " +
					"WHERE rr.id=:reportid";
			TypedQuery<ResultInfo> query = manager.createQuery(queryString, ResultInfo.class);
			query.setParameter("reportid", reportId);
			return query.getResultList();
		});
	}
	
	public String getDocumentURL(int reportId, ServletContext context, String hostname, int port) throws MalformedURLException {
		return withEntityManager(manager -> {
			ReportedResult reportedResult = manager.find(ReportedResult.class, reportId);
			return reportedResult.getReportDocument()
					.getReportDocumentURL(
							new URL("https", hostname, port,
									(
											context.getContextPath()
											+ context.getServletRegistration(REPORT_DOCUMENT_SERVLET_NAME)
												.getMappings().stream()
												.findAny().get()
									).replace("/*", "")
							)
					)
					.toString();
		});
	}

	public ReportedResult createExternalReport(int experimentId, int studentId, String reportName, String documentURLString) throws MalformedURLException {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = initializeReport(experimentId, studentId, reportName, manager);
			
			URL documentURL = new URL(documentURLString);
			
			ExternalReportDocument reportDocument = new ExternalReportDocument();
			String filename = URLUtils.getFilenameFromURL(documentURL);
			reportDocument.setFilename(filename);
			reportDocument.setFileType(FileType.fromFilename(filename));
			reportDocument.setReportDocumentURLString(documentURLString);
			
			report.setReportDocument(reportDocument);
			
			manager.persist(report);
			
			manager.getTransaction().commit();
			
			return report;
		});
	}

	public ReportedResult createBasicReport(int experimentId, int studentId, String reportName) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = initializeReport(experimentId, studentId, reportName, manager);
			manager.persist(report);
			
			manager.getTransaction().commit();
			
			return report;
		});
	}

	public ReportedResult createFilesystemReport(int experimentId, int studentId, String reportName,
			String contentType, String submittedFileName, InputStream fileContent) throws IOException {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = initializeReport(experimentId, studentId, reportName, manager);
			
			// create the report document object
			FilesystemReportDocument reportDocument = new FilesystemReportDocument();
			reportDocument.setFilename(submittedFileName);
			FileType fileType = FileType.ofContentType(contentType);
			if (fileType == null) {
				fileType = FileType.ofExtension(
						submittedFileName.substring(submittedFileName.lastIndexOf('.'))
						);
			}
			reportDocument.setFileType(fileType);
			
			// save the file into the configured path
			Path filesystemPath = saveDocumentFile(experimentId, studentId, submittedFileName, fileContent);
			
			reportDocument.setFilesystemPath(filesystemPath.normalize().toString());
			
			reportDocument.setDocsPathInfo(docsPathInfoFor(studentId, experimentId, submittedFileName));
						
			report.setReportDocument(reportDocument);
			
			manager.persist(report);
			
			manager.getTransaction().commit();
			
			return report;
		});
	}

	public Path saveDocumentFile(int experimentId, int studentId, String submittedFileName, InputStream fileContent)
			throws IOException, FileNotFoundException {
		String directoryPathname = getFilesystemDirectoryPathname(experimentId, studentId);
		Path directoryPath = Paths.get(directoryPathname);
		Files.createDirectories(directoryPath);
		
		Path filesystemPath = Paths.get(directoryPath.normalize().toString(), submittedFileName);
		
		FileOutputStream fileOutputStream = new FileOutputStream(filesystemPath.toFile());
		
		ByteStreams.copy(fileContent, fileOutputStream);
		
		fileOutputStream.close();
		return filesystemPath;
	}

	public String getFilesystemDirectoryPathname(int experimentId, int studentId) {
		return config.getReportUploadFilePath()
							+ File.separator + "student_" + studentId
							+ File.separator + "experiment_" + experimentId;
	}
	
	/**
	 * Get path information part of URL to retrieve file from server
	 * @param studentId the student id
	 * @param experimentId the experiment id
	 * @param filename the document filename
	 * @return the path info
	 */
	private String docsPathInfoFor(int studentId, int experimentId, String filename) {
		return "/" + studentId + "/" + experimentId + "/" + filename;
	}
	
	private ReportedResult initializeReport(int experimentId, int studentId, String reportName, EntityManager manager) {
		Experiment experiment = manager.find(Experiment.class, experimentId);
		Student student = manager.find(Student.class, studentId);
		
		ReportedResult report = new ReportedResult();
		report.setExperiment(experiment);
		report.setStudent(student);
		report.setName(reportName);
		
		return report;
	}

	public FilesystemReportDocument getReportDocument(int studentId, int experimentId, String filename) {
		return withEntityManager(manager -> {
			String queryString = "SELECT rd FROM FilesystemReportDocument rd "
					+ "JOIN rd.reportedResult rr "
					+ "WHERE rr.student.id=:studentid"
					+ "	AND rr.experiment.id=:experimentid"
					+ "	AND rd.filename=:filename";
			TypedQuery<FilesystemReportDocument> query = manager.createQuery(
					queryString,
					FilesystemReportDocument.class
					);
			query.setParameter("studentid", studentId);
			query.setParameter("experimentid", experimentId);
			query.setParameter("filename", filename);
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public void renameReport(int reportId, String reportName) {
		withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = manager.find(ReportedResult.class, reportId);
			
			report.setName(reportName);
			
			manager.getTransaction().commit();
		});
	}

	public <D extends ReportDocument> void replaceReportDocument(
			EntityManager manager,
			ReportDocument oldReportDocument,
			D newReportDocument) {
		// persist the new report document to the database
		manager.persist(newReportDocument);
		
		// set the report object to point to the new report document
		ReportedResult reportedResult = oldReportDocument.getReportedResult();
		reportedResult.setReportDocument(newReportDocument);
		
		// remove the old report document
		manager.remove(oldReportDocument);
	}
	
	public void updateFilesystemReport(int reportId, String reportName, 
			String contentType, String submittedFileName,
			InputStream fileContent) throws IOException {
		withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = manager.find(ReportedResult.class, reportId);
			
			report.setName(reportName);
			
			ReportDocument reportDocument = report.getReportDocument();
			boolean needNewDocumentObject = !reportDocument.getDocumentType()
					.equals(ReportDocumentType.FILESYSTEM);
			
			FilesystemReportDocument filesystemReportDocument;
			if (needNewDocumentObject) {
				filesystemReportDocument = new FilesystemReportDocument();
			} else {
				filesystemReportDocument = (FilesystemReportDocument) reportDocument;
			}
			
			ReportedResult reportedResult = reportDocument.getReportedResult();
			
			filesystemReportDocument.setFileType(FileType.ofContentType(contentType));
			
			filesystemReportDocument.setFilename(submittedFileName);
			
			int experimentId = reportedResult.getExperiment().getId();
			int studentId = reportedResult.getStudent().getId();
			Path documentPath = saveDocumentFile(
					experimentId,
					studentId, 
					submittedFileName,
					fileContent);
			
			filesystemReportDocument.setFilesystemPath(documentPath.toString());
			
			filesystemReportDocument.setDocsPathInfo(
					docsPathInfoFor(studentId, experimentId, submittedFileName));
			
			if (needNewDocumentObject) {
				replaceReportDocument(manager, reportDocument, filesystemReportDocument);
			}
			
			manager.getTransaction().commit();
		});
	}

	public void updateExternalReport(int reportId, String reportName,
			String externalDocumentURL) throws MalformedURLException {
		withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = manager.find(ReportedResult.class, reportId);
			
			report.setName(reportName);
			
			ReportDocument reportDocument = report.getReportDocument();
			boolean needNewDocumentObject = !reportDocument.getDocumentType()
					.equals(ReportDocumentType.EXTERNAL);
			
			ExternalReportDocument externalReportDocument;
			if (needNewDocumentObject) {
				externalReportDocument = new ExternalReportDocument();
			} else {
				externalReportDocument = (ExternalReportDocument) reportDocument;
			}
			
			externalReportDocument.setReportDocumentURLString(externalDocumentURL);
			
			String filename = URLUtils.getFilenameFromURL(new URL(externalDocumentURL));
			externalReportDocument.setFilename(filename);
			
			externalReportDocument.setFileType(FileType.fromFilename(filename));
			
			if (needNewDocumentObject) {
				replaceReportDocument(manager, reportDocument, externalReportDocument);
			}
			
			manager.getTransaction().commit();
		});
	}

	public List<ReportForStudentReportsTable> getStudentReports(int studentId) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			CriteriaQuery<ReportForStudentReportsTable> cq = cb.createQuery(ReportForStudentReportsTable.class);
			Root<ReportedResult> root = cq.from(ReportedResult.class);
			Join<ReportedResult, Experiment> e = root.join(ReportedResult_.experiment);
			Join<Experiment, Course> c = e.join(Experiment_.course);
			
			cq.select(cb.construct(
					ReportForStudentReportsTable.class,
					root.get(ReportedResult_.id),
					root.get(ReportedResult_.name),
					e.get(Experiment_.id),
					e.get(Experiment_.name),
					c.get(Course_.id),
					c.get(Course_.name),
					root.get(ReportedResult_.score),
					root.get(ReportedResult_.added),
					cb.lessThanOrEqualTo(
							cb.currentTimestamp().as(LocalDateTime.class),
							e.get(Experiment_.reportDueDate)
							))
			)
			.where(cb.equal(root.get(ReportedResult_.student).get(Student_.id), studentId))
			.orderBy(cb.desc(root.get(ReportedResult_.added)));
			
			TypedQuery<ReportForStudentReportsTable> query = manager.createQuery(cq);
			return query.getResultList();
		});
	}

	public List<ReportForFacultyExperimentView> getReportsForExperiment(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.faculty.report.ReportForFacultyExperimentView("
					+ "	rr.id,"
					+ " rr.student.id,"
					+ " rr.name,"
					+ "	rr.added,"
					+ "	rr.score"
					+ ") FROM ReportedResult rr "
					+ "WHERE rr.experiment.id=:experimentid "
					+ "ORDER BY rr.added DESC";
			TypedQuery<ReportForFacultyExperimentView> query = manager.createQuery(
					queryString, ReportForFacultyExperimentView.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultList();
		});
	}

	public void scoreReport(int reportId, BigDecimal score) {
		withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			ReportedResult report = manager.find(ReportedResult.class, reportId);
			report.setScore(score);
			
			manager.getTransaction().commit();
		});
	}
}
