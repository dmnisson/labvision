package io.github.dmnisson.labvision.student;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.dto.experiment.ExperimentInfo;
import io.github.dmnisson.labvision.dto.experiment.MeasurementForErrorsView;
import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForReportView;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import io.github.dmnisson.labvision.dto.student.reports.ReportForStudentReportsTable;
import io.github.dmnisson.labvision.entities.CourseClass;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.ExternalReportDocument;
import io.github.dmnisson.labvision.entities.FileType;
import io.github.dmnisson.labvision.entities.FilesystemReportDocument;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.MeasurementValue;
import io.github.dmnisson.labvision.entities.Parameter;
import io.github.dmnisson.labvision.entities.ReportDocument;
import io.github.dmnisson.labvision.entities.ReportDocumentType;
import io.github.dmnisson.labvision.entities.ReportedResult;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.measure.Amount;
import io.github.dmnisson.labvision.measure.SI;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.reportdocs.ReportDocumentService;
import io.github.dmnisson.labvision.repositories.CourseClassRepository;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.MeasurementRepository;
import io.github.dmnisson.labvision.repositories.MeasurementValueRepository;
import io.github.dmnisson.labvision.repositories.ParameterRepository;
import io.github.dmnisson.labvision.repositories.ParameterValueRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;
import io.github.dmnisson.labvision.utils.URLUtils;

@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private ReportDocumentService reportDocumentService;
	
	@Autowired
	private MeasurementRepository measurementRepository;

	@Autowired
	private MeasurementValueRepository measurementValueRepository;

	@Autowired
	private ParameterRepository parameterRepository;
	
	@Autowired
	private ParameterValueRepository parameterValueRepository;

	@Autowired
	private CourseClassRepository courseClassRepository;
	
	@Autowired
	private StudentRepository studentRepository;

	@ModelAttribute
	public void populateModel(Model model) {
		NavbarModel navbarModel = buildStudentNavbar();
		model.addAttribute("navbarModel", navbarModel);
	}
	
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		model.addAttribute("student", user);
		
		Integer studentId = user.getId();
		
		List<CurrentExperimentForStudentDashboard> currentExperiments = Stream.concat(
				experimentRepository.findCurrentExperimentsForStudentDashboardNoReports(studentId).stream(),
				experimentRepository.findCurrentExperimentsForStudentDashboardWithReports(studentId).stream()
				).collect(Collectors.toList());
		model.addAttribute("currentExperiments", currentExperiments);
		
		List<RecentExperimentForStudentDashboard> recentExperiments = Stream.concat(
				experimentRepository.findRecentExperimentsForStudentDashboardNoReports(studentId).stream(),
				experimentRepository.findRecentExperimentsForStudentDashboardWithReports(studentId).stream()
				).collect(Collectors.toList());
		model.addAttribute("recentExperiments", recentExperiments);
		
		List<RecentCourseForStudentDashboard> recentCourses = courseRepository.findRecentCoursesForStudentDashboard(studentId);
		model.addAttribute("recentCourses", recentCourses);
		
		return "student/dashboard";
	}
	
	// --- EXPERIMENT PAGES ---
	
	@GetMapping("/experiments")
	public String experiments(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Integer studentId = user.getId();
		
		List<CurrentExperimentForStudentExperimentTable> currentExperiments = experimentRepository.findCurrentExperimentsForStudentExperimentTable(studentId);
		model.addAttribute("currentExperiments", currentExperiments);
		
		List<PastExperimentForStudentExperimentTable> pastExperiments = experimentRepository.findPastExperimentsForStudentExperimentTable(studentId);
		model.addAttribute("pastExperiments", pastExperiments);

		return "student/experiments";
	}
	
	@GetMapping("/experiment/{experimentId}")
	public String getExperiment(@PathVariable Integer experimentId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		model.addAttribute("student", user);
		
		Integer studentId = user.getId();
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		List<MeasurementInfo> measurements = measurementRepository.findForExperimentView(experimentId);
		model.addAttribute("measurements", measurements);
		
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues =
				measurements.stream()
					.map(MeasurementInfo::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							mid -> measurementValueRepository.findForStudentExperimentView(mid, studentId)
							));
		model.addAttribute("measurementValues", measurementValues);
		
		Map<Integer, List<ParameterForExperimentView>> parameters =
				measurements.stream()
					.map(MeasurementInfo::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							mid -> parameterRepository.findForExperimentView(mid)
							));
		model.addAttribute("parameters", parameters);
		
		// measurement value ID -> parameter ID -> parameter value
		Map<Integer, Map<Integer, ParameterValueForExperimentView>> parameterValues =
				measurementValues.entrySet().stream()
					.flatMap(e -> e.getValue().stream())
					.collect(Collectors.toMap(
							MeasurementValueForExperimentView::getId,
							mv -> parameters.get(mv.getMeasurementId()).stream()
								.map(ParameterForExperimentView::getId)
								.collect(Collectors.toMap(
										Function.identity(),
										pid -> parameterValueRepository.getForExperimentView(mv.getId(), pid)
										))
							));
		model.addAttribute("parameterValues", parameterValues);
		
		List<ReportedResultForStudentExperimentView> reportedResults =
				reportedResultRepository.findReportsForStudentExperimentView(experimentId, studentId);
		model.addAttribute("reportedResults", reportedResults);
		
		return "student/experiment";
	}
	
	@PostMapping("/measurementvalue/new/{measurementId}")
	public String createMeasurementValue(@PathVariable Integer measurementId, HttpServletRequest request,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// need to initialize the measurementValues collection
		Student student = studentRepository.findById(user.getId()).get();
		
		Measurement measurement = measurementRepository.findById(measurementId)
				.orElseThrow(() -> new ResourceNotFoundException(Measurement.class, measurementId));
		
		CourseClass courseClass = courseClassRepository.findWithStudentIdAndExperimentId(
				student.getId(),
				measurement.getExperiment().getId()
				).stream().findAny()
					.orElseThrow(() -> new ResourceNotFoundException(
							CourseClass.class,
							"student " + student.getId() + " and experiment " + measurement.getExperiment().getId()));

		Amount<?> measurementAmount = new Amount<>(
				Double.parseDouble(request.getParameter("measurementValue")),
				Double.parseDouble(request.getParameter("measurementUncertainty")),
				SI.getInstance().getUnitFor(measurement, measurement.getQuantityTypeId()
						.getQuantityClass().getQuantityType()));
			
		Map<Parameter, Amount<?>> parameterAmounts = measurement.getParameters().stream()
				.collect(Collectors.toMap(Function.identity(), 
						p -> new Amount<>(
								Double.parseDouble(request.getParameter("parameterValue" + p.getId())),
								Double.parseDouble(request.getParameter("parameterUncertainty" + p.getId())),
								SI.getInstance().getUnitFor(p, p.getQuantityTypeId().getQuantityClass()
										.getQuantityType())))
								);
		
		MeasurementValue measurementValue = measurement.addValue(student, courseClass, measurementAmount, LocalDateTime.now());
			
		measurement.getParameters().stream()
			.forEach(p -> measurementValue.addParameterValue(p, parameterAmounts.get(p)));
		
		measurementValueRepository.save(measurementValue);
		
		return "redirect:" + MvcUriComponentsBuilder.fromMethodName(
				StudentController.class,
				"getExperiment",
				measurement.getExperiment().getId(), new Object(), new Object()
				).toUriString();
	}
	
	// --- REPORT PAGES ---
	
	@GetMapping("/reports")
	public String reports(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Integer studentId = user.getId();
		
		List<ReportForStudentReportsTable> reports = reportedResultRepository.findReportsForStudentReportsTable(studentId);
		model.addAttribute("reports", reports);
		
		return "student/reports";
	}
	
	// Helper functions for report documents
	private String getReportUrl(ReportedResult reportedResult) {
		return MvcUriComponentsBuilder.fromMethodName(
				StudentController.class, "getReport", reportedResult.getId(), new Object(), new Object()
				).toUriString();
	}
	
	@GetMapping("student/report/{reportId}")
	public String getReport(@PathVariable Integer reportId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException {
		ReportForReportView report = reportedResultRepository.findForReportView(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		model.addAttribute("report", report);
		
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(report.getExperimentId()).get();
		model.addAttribute("experiment", experiment);
		
		List<ResultInfo> acceptedResults = experimentRepository.getAcceptedResultInfoFor(report.getExperimentId());
		model.addAttribute("acceptedResults", acceptedResults);
		
		String reportDocumentUrl = reportDocumentService.buildReportDocumentUrl(reportId);
		model.addAttribute("reportDocumentUrl", reportDocumentUrl);
		
		return "student/report";
	}
	
	@GetMapping("student/report/new/{experimentId}")
	public String newReport(@PathVariable Integer experimentId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(experimentId).get();
		model.addAttribute("experiment", experiment);
		
		List<ResultInfo> acceptedResults = experimentRepository.getAcceptedResultInfoFor(experimentId);
		model.addAttribute("acceptedResults", acceptedResults);
		
		String actionUrl = MvcUriComponentsBuilder.fromMethodName(
				StudentController.class,
				"createReport",
				experimentId, null, null, null, null, null, null
				).replaceQuery(null)
				.build()
				.toUriString();
		model.addAttribute("actionUrl", actionUrl);
		
		return "student/editreport";
	}
	
	@PostMapping("student/report/new/{experimentId}")
	public String createReport(@PathVariable Integer experimentId,
			String reportName, @RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws IOException {
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		// need to initialize reportedResults
		Student student = studentRepository.findById(user.getId()).get();
		
		ReportedResult reportedResult = experiment.addReportedResult(student);
		reportedResult.setName(reportName);
		
		ReportDocument reportDocument = null;
		
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
		
		if (Objects.nonNull(reportDocument)) reportedResult.setReportDocument(reportDocument);
		
		reportedResult = reportedResultRepository.save(reportedResult);
		
		return "redirect:" + getReportUrl(reportedResult);
	}
	
	@GetMapping("student/report/edit/{reportId}")
	public String editReport(@PathVariable Integer reportId, @RequestParam(name="uploadfile", defaultValue="false") boolean uploadfile, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException {
		ReportForReportView report = reportedResultRepository.findForReportView(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		model.addAttribute("report", report);
		
		String reportDocumentUrl = reportDocumentService.buildReportDocumentUrl(reportId);
		model.addAttribute("reportDocumentUrl", reportDocumentUrl);
		
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(report.getExperimentId()).get();
		model.addAttribute("experiment", experiment);
		
		List<ResultInfo> acceptedResults = experimentRepository.getAcceptedResultInfoFor(report.getExperimentId());
		model.addAttribute("acceptedResults", acceptedResults);
		
		String actionUrl = MvcUriComponentsBuilder.fromMethodName(StudentController.class, "updateReport", reportId,
				null, null, null, null, null, null)
				.replaceQuery(null)
				.toUriString();
		model.addAttribute("actionUrl", actionUrl);
		
		model.addAttribute("uploadfile", uploadfile);
		
		return "student/editreport";
	}
	
	@PostMapping("/report/edit/{reportId}")
	public String updateReport(@PathVariable Integer reportId,
			String reportName, @RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws IOException {
		ReportedResult reportedResult = reportedResultRepository.findById(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		
		Student student = (Student) user;
		
		Experiment experiment = reportedResult.getExperiment();
		
		reportedResult.setName(reportName);
		
		ReportDocument reportDocument = null;
		
		switch (documentType) {
		case EXTERNAL:
			if (Objects.nonNull(externalDocumentURL)) {
				reportDocument = reportedResult.getReportDocument();
				
				ExternalReportDocument externalReportDocument;
				if (!reportDocument.getDocumentType().equals(ReportDocumentType.EXTERNAL)) {
					externalReportDocument = new ExternalReportDocument();
				} else {
					externalReportDocument = (ExternalReportDocument) reportDocument;
				}
				
				externalReportDocument.setReportDocumentURLString(externalDocumentURL.toString());
				
				String filename = URLUtils.getFilenameFromURL(externalDocumentURL);
				externalReportDocument.setFilename(filename);
				
				externalReportDocument.setFileType(FileType.fromFilename(filename));
			}
			break;
		case FILESYSTEM:
			if (Objects.nonNull(filesystemDocumentFile)) {
				reportDocument = reportedResult.getReportDocument();
				
				FilesystemReportDocument filesystemReportDocument;
				if (!reportDocument.getDocumentType().equals(ReportDocumentType.FILESYSTEM)) {
					filesystemReportDocument = new FilesystemReportDocument();
				} else {
					filesystemReportDocument = (FilesystemReportDocument) reportDocument;
				}
				
				reportDocumentService.updateFilesystemReportDocumentEntity(experiment.getId(), filesystemDocumentFile, 
						student, filesystemReportDocument);
			}
		}
		
		if (Objects.nonNull(reportDocument)) reportedResult.setReportDocument(reportDocument);
		
		reportedResult = reportedResultRepository.save(reportedResult);
		
		return "redirect:" + getReportUrl(reportedResult);
	}
	
	@GetMapping("/errors")
	public String errors(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Student student = (Student) user;
		Integer studentId = student.getId();
		
		List<ExperimentInfo> experiments = experimentRepository.findExperimentInfoForStudent(studentId);
		Map<Integer, List<MeasurementForErrorsView>> measurements = experiments.stream()
				.map(ExperimentInfo::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						eid -> measurementRepository.findMeasurementsForErrorsView(eid)));
		
		model.addAttribute("experiments", experiments);
		model.addAttribute("measurements", measurements);
		
		return "student/errors";
	}
	
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/profile";
	}
	
	@GetMapping("/courses")
	public String courses(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/courses";
	}
	
	@GetMapping("/course/{courseId}")
	public String getCourse(@PathVariable Integer courseId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/course";
	}
	
	private NavbarModel buildStudentNavbar() {
		NavbarModel navbarModel = new NavbarModel();
		
		navbarModel.addNavLink("Dashboard", StudentController.class, "dashboard", new Object(), new Object());
		navbarModel.addNavLink("Experiments", StudentController.class, "experiments", new Object(), new Object());
		navbarModel.addNavLink("Reports",	StudentController.class, "reports", new Object(), new Object());
		navbarModel.addNavLink("Errors", StudentController.class, "errors", new Object(), new Object());
		navbarModel.addNavLink(navbarModel.new NavLink(
				"Account", 
				"#", 
				new NavbarModel.NavLink[] {
						navbarModel.createNavLink("Profile", StudentController.class, "profile", new Object(), new Object()),
						navbarModel.createNavLink("Courses", StudentController.class, "courses", new Object(), new Object())
				}
			));
		
		navbarModel.setLogoutLink("/logout");
		
		return navbarModel;
	}
}
