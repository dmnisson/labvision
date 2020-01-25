package io.github.dmnisson.labvision.student;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.dto.experiment.MeasurementForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import io.github.dmnisson.labvision.dto.student.reports.ReportForStudentReportsTable;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.MeasurementRepository;
import io.github.dmnisson.labvision.repositories.MeasurementValueRepository;
import io.github.dmnisson.labvision.repositories.ParameterRepository;
import io.github.dmnisson.labvision.repositories.ParameterValueRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;

@Controller
public class StudentController {
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private MeasurementRepository measurementRepository;

	@Autowired
	private MeasurementValueRepository measurementValueRepository;

	@Autowired
	private ParameterRepository parameterRepository;
	
	@Autowired
	private ParameterValueRepository parameterValueRepository;
	
	@ModelAttribute
	public void populateModel(Model model) {
		NavbarModel navbarModel = buildStudentNavbar();
		model.addAttribute("navbarModel", navbarModel);
	}
	
	@GetMapping("/student/dashboard")
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
	
	@GetMapping("/student/experiments")
	public String experiments(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Integer studentId = user.getId();
		
		List<CurrentExperimentForStudentExperimentTable> currentExperiments = experimentRepository.findCurrentExperimentsForStudentExperimentTable(studentId);
		model.addAttribute("currentExperiments", currentExperiments);
		
		List<PastExperimentForStudentExperimentTable> pastExperiments = experimentRepository.findPastExperimentsForStudentExperimentTable(studentId);
		model.addAttribute("pastExperiments", pastExperiments);

		return "student/experiments";
	}
	
	@GetMapping("/student/experiment/{experimentId}")
	public String getExperiment(@PathVariable Integer experimentId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		model.addAttribute("student", user);
		
		Integer studentId = user.getId();
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		List<MeasurementForExperimentView> measurements = measurementRepository.findForExperimentView(experimentId);
		model.addAttribute("measurements", measurements);
		
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues =
				measurements.stream()
					.map(MeasurementForExperimentView::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							mid -> measurementValueRepository.findForStudentExperimentView(mid, studentId)
							));
		model.addAttribute("measurementValues", measurementValues);
		
		Map<Integer, List<ParameterForExperimentView>> parameters =
				measurements.stream()
					.map(MeasurementForExperimentView::getId)
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
	
	@GetMapping("/student/reports")
	public String reports(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Integer studentId = user.getId();
		
		List<ReportForStudentReportsTable> reports = reportedResultRepository.findReportsForStudentReportsTable(studentId);
		model.addAttribute("reports", reports);
		
		return "student/reports";
	}
	
	@GetMapping("student/report/new/{experimentId}")
	public String newReport(@PathVariable Integer experimentId, @AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/editreport";
	}
	
	@GetMapping("/student/errors")
	public String errors(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/errors";
	}
	
	@GetMapping("/student/profile")
	public String profile(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/profile";
	}
	
	@GetMapping("/student/courses")
	public String courses(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		// TODO
		return "student/courses";
	}
	
	@GetMapping("/student/course/{courseId}")
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
