package io.github.dmnisson.labvision.faculty;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.AccessDeniedException;
import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.admin.AdminController;
import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.dto.faculty.ExperimentForFacultyExperimentTable;
import io.github.dmnisson.labvision.dto.faculty.ReportForFacultyExperimentView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForFacultyReportView;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.experiment.ExperimentService;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.models.NavbarModelImpl;
import io.github.dmnisson.labvision.reportdocs.ReportDocumentService;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;
import io.github.dmnisson.labvision.utils.EditorUtils;
import io.github.dmnisson.labvision.utils.ExperimentEditorData;
import io.github.dmnisson.labvision.utils.ViewModelUtils;

@Controller
@RequestMapping("/faculty")
public class FacultyController {
	
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private ExperimentRepository experimentRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	@Autowired
	private ExperimentService experimentService;
	@Autowired
	private ReportDocumentService reportDocumentService;
	@Autowired
	private LabVisionUserDetailsManager userDetailsManager;

	@ModelAttribute
	public void populateModel(Model model, @AuthenticationPrincipal LabVisionUserDetails userDetails) {
		NavbarModel navbarModel = buildFacultyNavbar(userDetails);
		model.addAttribute("navbarModel", navbarModel);
	}
	
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Instructor instructor = (Instructor) user;
		
		model.addAttribute("instructor", instructor);
		
		model.addAttribute("numOfExperiments", experimentRepository.countExperimentsForInstructor(instructor.getId()));
		model.addAttribute("numOfStudents", studentRepository.countStudentsForInstructor(instructor.getId()));
		model.addAttribute("numOfUnscoredReports", reportedResultRepository.countUnscoredReportsForInstructor(instructor.getId()));
		
		return "faculty/dashboard";
	}
	
	@GetMapping("/experiments")
	public String experiments(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Instructor instructor = (Instructor) user;
		int instructorId = instructor.getId();
		
		List<ExperimentForFacultyExperimentTable> experiments = experimentRepository.findExperimentsForFacultyExperimentTable(instructorId);
		model.addAttribute("experiments", experiments);
		
		return "faculty/experiments";
	}
	
	private void checkExperimentAuthorizedForInstructor(int instructorId, Experiment experiment)
			throws AccessDeniedException {
		// make experiments accessible only to instructors who author them or teach courses with them
		if (!experimentRepository.findByIdForInstructor(experiment.getId(), instructorId).isPresent()
				&& !courseRepository.findByIdForInstructor(experiment.getCourse().getId(), instructorId).isPresent()) {
			throw new AccessDeniedException(Experiment.class, experiment.getId());
		}
	}
	
	@GetMapping("/experiment/{experimentId}")
	public String getExperiment(@PathVariable Integer experimentId, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws AccessDeniedException {
		
		Instructor instructor = (Instructor) user;
		int instructorId = instructor.getId();
		
		Experiment experiment = ViewModelUtils.buildExperimentModelAttributes(
				experimentService.getExperimentViewModel(experimentId, instructorId), model);
		
		checkExperimentAuthorizedForInstructor(instructorId, experiment);
		
		List<Integer> studentIds = experiment.getStudentIds();
		List<ReportForFacultyExperimentView> reports = 
				reportedResultRepository.findReportsForFacultyExperimentView(experimentId);
		Map<Integer, List<ReportForFacultyExperimentView>> reportsByStudentId =
				reports.stream()
				.collect(Collectors.groupingBy(r -> r.getStudentId()));
		
		model.addAttribute("studentIds", studentIds);
		model.addAttribute("reports", reportsByStudentId);
		
		return "faculty/experiment";
	}
	
	@GetMapping("/experiment/edit/{experimentId}")
	public String editExperiment(@PathVariable Integer experimentId,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws AccessDeniedException {
		
		Instructor instructor = (Instructor) user;
		Integer instructorId = instructor.getId();
		
		Experiment experiment = ViewModelUtils.buildExperimentModelAttributes(
				experimentService.getExperimentViewModel(experimentId, instructorId), model);
		
		checkExperimentAuthorizedForInstructor(instructorId, experiment);
		
		model.addAttribute("actionURL", MvcUriComponentsBuilder
				.fromMethodName(
						FacultyController.class,
						"updateExperiment",
						experimentId, null, null, null
						).toUriString());
		
		return "editors/editexperiment";
	}
	
	@PostMapping("/experiment/edit/{experimentId}")
	public String updateExperiment(@PathVariable Integer experimentId,
			@RequestParam Map<String, String> requestParams,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws AccessDeniedException {
		final Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		checkExperimentAuthorizedForInstructor(user.getId(), experiment);
		
		ExperimentEditorData editorData = EditorUtils.getExperimentEditorDataFromRequestParams(requestParams);
		
		Experiment savedExperiment = experimentService.updateExperiment(experiment, editorData);
		
		// send user back to experiment view
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(
						FacultyController.class,
						"getExperiment", savedExperiment.getId(),
						new Object(), new Object())
				.toUriString();
	}

	// helper that retrieves report information that is needed for faculty views and adds it to model
	private void getReportInfo(Integer reportId, LabVisionUser user, Model model)
			throws MalformedURLException, UnsupportedEncodingException, AccessDeniedException {
		ReportForFacultyReportView reportInfo = reportedResultRepository.findReportForFacultyReportView(reportId);
		List<ResultInfo> acceptedResults = reportedResultRepository.findAcceptedResultsForReportedResult(reportId);
		
		Experiment experiment = experimentRepository.getOne(reportInfo.getExperimentId());
		checkExperimentAuthorizedForInstructor(user.getId(), experiment);
		
		model.addAttribute("report", reportInfo);
		model.addAttribute("acceptedResults", acceptedResults);
		model.addAttribute("reportDocumentURL", reportDocumentService.buildReportDocumentUrl(reportId));
	}
	
	@GetMapping("/report/{reportId}")
	public String getReport(@PathVariable Integer reportId, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException, AccessDeniedException {
		getReportInfo(reportId, user, model);
		
		model.addAttribute("scorePath", MvcUriComponentsBuilder.fromMethodName(
				FacultyController.class,
				"scoreReport", reportId,
				null, null, null)
					.replaceQuery(null)
					.build()
					.toUriString()
		);
		
		return "faculty/report";
	}
	
	@GetMapping("/report/score/{reportId}")
	public String editReportScore(@PathVariable Integer reportId,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException, AccessDeniedException {
		getReportInfo(reportId, user, model);
		
		model.addAttribute("scoring", true);
		model.addAttribute("scorePath", MvcUriComponentsBuilder.fromMethodName(
				FacultyController.class,
				"scoreReport", reportId,
				null, null, null)
					.replaceQuery(null)
					.build()
					.toUriString()
		);
		
		return "faculty/report";
	}
	
	@PostMapping("/report/score/{reportId}")
	public String scoreReport(@PathVariable Integer reportId,
			BigDecimal score,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws AccessDeniedException {
		ReportForFacultyReportView report = reportedResultRepository.findReportForFacultyReportView(reportId);
		Experiment experiment = experimentRepository.getOne(report.getExperimentId());
		checkExperimentAuthorizedForInstructor(user.getId(), experiment);
		
		try {
			reportedResultRepository.updateReportScore(reportId, score);
			
			return "redirect:" + MvcUriComponentsBuilder
					.fromMethodName(FacultyController.class, 
							"getReport", reportId,
							new Object(), new Object()).toUriString();
		} catch (NumberFormatException e) {
			return "redirect:" + MvcUriComponentsBuilder
					.fromMethodName(FacultyController.class, 
							"editReportScore", reportId,
							new Object(), new Object()).toUriString();
		}
	}
	
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Instructor instructor = (Instructor) user;
		model.addAttribute("instructor", instructor);
		
		return "faculty/profile";
	}
	
	@GetMapping("/profile/edit")
	public String editProfile(String[] errors, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Instructor instructor = (Instructor) user;
		model.addAttribute("instructor", instructor);
		
		model.addAttribute("errors", errors);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(FacultyController.class,
						"updateProfile", null, null, null, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "faculty/editprofile";
	}
	
	@PostMapping("/profile/edit")
	public String updateProfile(
			String instructorName, String instructorEmail,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		
		try {
			userDetailsManager.updateInstructor(user.getId(), instructorName, instructorEmail, false);
		}
		catch (ConstraintViolationException e) {
			return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(FacultyController.class, "editProfile",
						e.getConstraintViolations().stream()
							.map(cv -> cv.getMessage())
							.toArray(String[]::new),
						new Object(), new Object())
				.toUriString();
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(FacultyController.class, "profile", new Object(), new Object())
				.toUriString();
	}
	
	private NavbarModel buildFacultyNavbar(LabVisionUserDetails userDetails) {
		NavbarModelImpl navbarModel = new NavbarModelImpl();
		
		navbarModel.addNavLink("Dashboard", FacultyController.class, "dashboard", new Object(), new Object());
		navbarModel.addNavLink("Experiments", FacultyController.class, "experiments", new Object(), new Object());
		navbarModel.addNavLink(navbarModel.new NavLink(
			"Account",
			"#",
			new NavbarModelImpl.NavLink[] {
				navbarModel.createNavLink("Profile", FacultyController.class, "profile", new Object(), new Object())
			}
		));
		
		if (userDetailsManager.isAdmin(userDetails)) {
			navbarModel.addNavLink("Admin", AdminController.class, "dashboard", null, null);
		}
		
		navbarModel.setLogoutLink("/logout");
		
		return navbarModel;
	}
}
