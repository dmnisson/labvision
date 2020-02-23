package io.github.dmnisson.labvision.student;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.AccessDeniedException;
import io.github.dmnisson.labvision.CourseService;
import io.github.dmnisson.labvision.ReportedResultService;
import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.admin.AdminController;
import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseTable;
import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseView;
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
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.CourseClass;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.MeasurementValue;
import io.github.dmnisson.labvision.entities.Parameter;
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
import io.github.dmnisson.labvision.utils.PaginationUtils;

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
	private ReportedResultService reportedResultService;
	
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

	@Autowired
	private CourseService courseService;
	
	@Autowired
	private LabVisionUserDetailsManager userDetailsManager;

	@ModelAttribute
	public void populateModel(Model model, @AuthenticationPrincipal LabVisionUserDetails userDetails) {
		NavbarModel navbarModel = buildStudentNavbar(userDetails);
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
	public String getExperiment(@PathVariable Integer experimentId, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model,
			@Qualifier("measurementValues") Map<Integer, Pageable> measurementValuesPageables,
			String activePane) throws AccessDeniedException {
		
		model.addAttribute("student", user);
		
		Integer studentId = user.getId();
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		if (!courseService.checkStudentEnrolled(studentId, experiment.getCourse().getId())) {
			throw new AccessDeniedException(Experiment.class, experimentId);
		}
		
		model.addAttribute("experiment", experiment);
		
		List<MeasurementInfo> measurements = measurementRepository.findForExperimentView(experimentId);
		model.addAttribute("measurements", measurements);
		
		Map<Integer, Page<MeasurementValueForExperimentView>> measurementValuePages =
				measurements.stream()
					.map(MeasurementInfo::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							mid -> measurementValueRepository
									.findForStudentExperimentView(mid, studentId, measurementValuesPageables.get(mid))
							));
		
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues =
				measurements.stream()
					.map(MeasurementInfo::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							mid -> measurementValueRepository
									.findForStudentExperimentView(mid, studentId, measurementValuesPageables.get(mid))
									.getContent()
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
		
		PaginationUtils.addMappedPageModelAttributes(
				model,
				measurementValuePages,
				"measurementValues",
				".",
				StudentController.class,
				"getExperiment", experimentId, null, null, null, null
				);
		
		final int activeMeasurementId = StringUtils.isEmpty(activePane) ?
				measurements.get(0).getId() :
				Integer.parseInt(activePane.substring(activePane.indexOf('.') + 1));
		model.addAttribute("activeMeasurementId",  activeMeasurementId);
		
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
				measurement.getExperiment().getId(), null, null, null, null
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
	
	@GetMapping("student/report/{reportId}")
	public String getReport(@PathVariable Integer reportId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException, AccessDeniedException {
		ReportForReportView report = reportedResultRepository.findForReportView(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		
		Integer studentId = user.getId();
		
		if (!reportedResultRepository.getOne(reportId).getStudent().getId().equals(studentId)) {
			throw new AccessDeniedException(ReportedResult.class, reportId);
		}
		
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
	public String newReport(@PathVariable Integer experimentId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws AccessDeniedException {
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(experimentId).get();
		
		if (!courseService.checkStudentEnrolled(
				user.getId(), 
				courseRepository.findCourseInfoForExperiment(experimentId)
					.get()
					.getId()
				)) {
			throw new AccessDeniedException(Experiment.class, experimentId);
		}
		
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
		
		return "editors/editreport";
	}
	
	@PostMapping("student/report/new/{experimentId}")
	public String createReport(@PathVariable Integer experimentId,
			String reportName, @RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws IOException, AccessDeniedException {
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		if (!courseService.checkStudentEnrolled(
				user.getId(), 
				experiment.getCourse().getId())) {
			throw new AccessDeniedException(Experiment.class, experimentId);
		}
		
		ReportedResult reportedResult = reportedResultService.createReportedResult(experimentId, reportName, documentType,
				externalDocumentURL, filesystemDocumentFile, user, experiment);
		
		return "redirect:" + getReportUrl(reportedResult);
	}

	private String getReportUrl(ReportedResult reportedResult) {
		return MvcUriComponentsBuilder.fromMethodName(
				StudentController.class, "getReport", reportedResult.getId(), new Object(), new Object()
		).toUriString();
	}

	@GetMapping("student/report/edit/{reportId}")
	public String editReport(@PathVariable Integer reportId, @RequestParam(name="uploadfile", defaultValue="false") boolean uploadfile, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException, AccessDeniedException {
		ReportForReportView report = reportedResultRepository.findForReportView(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		
		if (!reportedResultRepository.getOne(reportId).getStudent().getId().equals(user.getId())) {
			throw new AccessDeniedException(ReportedResult.class, reportId);
		}
		
		model.addAttribute("report", report);
		
		String reportDocumentUrl = reportDocumentService.buildReportDocumentUrl(reportId);
		model.addAttribute("reportDocumentUrl", reportDocumentUrl);
		
		String changeReportDocumentUrl = MvcUriComponentsBuilder
				.fromMethodName(StudentController.class, "editReport", reportId, true, null, null)
				.build()
				.toUriString();
		model.addAttribute("changeReportDocumentUrl", changeReportDocumentUrl);
		
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
		
		return "editors/editreport";
	}
	
	@PostMapping("/report/edit/{reportId}")
	public String updateReport(@PathVariable Integer reportId,
			String reportName, @RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws IOException, AccessDeniedException {
		ReportedResult reportedResult = reportedResultRepository.findById(reportId)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, reportId));
		
		if (!reportedResultRepository.getOne(reportId).getStudent().getId().equals(user.getId())) {
			throw new AccessDeniedException(ReportedResult.class, reportId);
		}
		
		Student student = (Student) user;
		
		reportedResult = reportedResultService.updateReportedResult(reportName, documentType, externalDocumentURL, filesystemDocumentFile,
				reportedResult, student);
		
		return "redirect:" + getReportUrl(reportedResult);
	}

	@GetMapping("/errors")
	public String errors(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model, Pageable pageable) {
		Student student = (Student) user;
		Integer studentId = student.getId();
		
		Page<ExperimentInfo> experiments = experimentRepository.findExperimentInfoForStudent(studentId, pageable);
		
		System.out.println(experiments.stream().map(e -> e.getId()).collect(Collectors.toList()));
		
		Map<Integer, List<MeasurementForErrorsView>> measurements = experiments.stream()
				.map(ExperimentInfo::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						eid -> {
							System.out.println(eid);
							
							return measurementRepository.findMeasurementsForErrorsView(eid);
						}));
		
		model.addAttribute("experiments", experiments.getContent());
		model.addAttribute("measurements", measurements);
		
		PaginationUtils.addPageModelAttributes(model, experiments, null, 
				StudentController.class, "errors", null, null, null);
		
		return "student/errors";
	}
	
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Student student = (Student) user;
		model.addAttribute("student", student);
		return "student/profile";
	}
	
	@GetMapping("/profile/edit")
	public String editProfile(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Student student = (Student) user;
		model.addAttribute("student", student);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder.fromMethodName(
				StudentController.class, 
				"updateProfile", null, null, null
				).replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "student/editprofile";
	}
	
	@PostMapping("/profile/edit")
	public String updateProfile(String studentName,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		userDetailsManager.updateStudent(user.getId(), studentName, false);
		
		return "redirect:" + MvcUriComponentsBuilder.fromMethodName(StudentController.class, "profile", null, null)
			.toUriString();
	}
	
	@GetMapping("/courses")
	public String courses(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model, Pageable pageable) {
		Student student = (Student) user;
		
		Page<CourseForStudentCourseTable> courses = courseRepository.findForStudentCourseTable(student.getId(), pageable);
		model.addAttribute("courses", courses.getContent());
		
		PaginationUtils.addPageModelAttributes(model, courses, null, 
				StudentController.class, "courses", null, null, null);
		
		return "student/courses";
	}
	
	@GetMapping("/course/{courseId}")
	public String getCourse(@PathVariable Integer courseId,
			@RequestParam(defaultValue="currentExperiments") String activePane,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user,
			Model model,
			@Qualifier("currentExperiments") Pageable currentExperimentsPageable,
			@Qualifier("pastExperiments") Pageable pastExperimentsPageable) {
		Student student = (Student) user;
		
		CourseForStudentCourseView course = courseRepository.findForStudentCourseView(student.getId(), courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		model.addAttribute("course", course);
		
		model.addAttribute("activePane", activePane);
		
		Page<CurrentExperimentForStudentExperimentTable> currentExperiments = experimentRepository
				.findCurrentExperimentsForStudentCourseExperimentTable(
						student.getId(),
						courseId,
						currentExperimentsPageable
						);
		model.addAttribute("currentExperiments", currentExperiments.getContent());
		PaginationUtils.addPageModelAttributes(
				model, 
				currentExperiments, 
				"currentExperiments", 
				StudentController.class, 
				"getCourse",
				courseId, null, null, null, null, null);
		
		Page<PastExperimentForStudentExperimentTable> pastExperiments = experimentRepository
				.findPastExperimentsForStudentCourseExperimentTable(
						student.getId(),
						courseId, 
						pastExperimentsPageable
						);
		model.addAttribute("pastExperiments", pastExperiments.getContent());
		PaginationUtils.addPageModelAttributes(
				model, 
				pastExperiments, 
				"pastExperiments", 
				StudentController.class, 
				"getCourse",
				courseId, null, null, null, null, null);
		
		return "student/course";
	}
	
	private NavbarModel buildStudentNavbar(LabVisionUserDetails userDetails) {
		NavbarModel navbarModel = new NavbarModel();
		
		navbarModel.addNavLink("Dashboard", StudentController.class, "dashboard", new Object(), new Object());
		navbarModel.addNavLink("Experiments", StudentController.class, "experiments", new Object(), new Object());
		navbarModel.addNavLink("Reports",	StudentController.class, "reports", new Object(), new Object());
		navbarModel.addNavLink("Errors", StudentController.class, "errors", new Object(), new Object(), new Object());
		navbarModel.addNavLink(navbarModel.new NavLink(
				"Account", 
				"#", 
				new NavbarModel.NavLink[] {
						navbarModel.createNavLink("Profile", StudentController.class, "profile", null, null),
						navbarModel.createNavLink("Courses", StudentController.class, "courses", null, null, null)
				}
			));
		
		if (userDetailsManager.isAdmin(userDetails)) {
			navbarModel.addNavLink("Admin", AdminController.class, "dashboard", null, null);
		}
		
		navbarModel.setLogoutLink("/logout");
		
		return navbarModel;
	}
}
