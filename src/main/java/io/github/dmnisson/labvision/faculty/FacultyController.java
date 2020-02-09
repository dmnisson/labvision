package io.github.dmnisson.labvision.faculty;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.dmnisson.labvision.DatabaseAction;
import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.dto.faculty.ExperimentForFacultyExperimentTable;
import io.github.dmnisson.labvision.dto.faculty.ReportForFacultyExperimentView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForFacultyReportView;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.Parameter;
import io.github.dmnisson.labvision.entities.QuantityTypeId;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.reportdocs.ReportDocumentService;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.InstructorRepository;
import io.github.dmnisson.labvision.repositories.MeasurementRepository;
import io.github.dmnisson.labvision.repositories.MeasurementValueRepository;
import io.github.dmnisson.labvision.repositories.ParameterRepository;
import io.github.dmnisson.labvision.repositories.ParameterValueRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;

@Controller
@RequestMapping("/faculty")
public class FacultyController {
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private MeasurementRepository measurementRepository;
	
	@Autowired
	private MeasurementValueRepository measurementValueRepository;
	
	@Autowired
	private ParameterRepository parameterRepository;
	
	@Autowired
	private ParameterValueRepository parameterValueRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private ReportDocumentService reportDocumentService;
	
	@ModelAttribute
	public void populateModel(Model model) {
		NavbarModel navbarModel = buildFacultyNavbar();
		model.addAttribute("navbarModel", navbarModel);
	}
	
	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		Instructor instructor = (Instructor) user;
		
		model.addAttribute("instructor", instructor);
		
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
	
	// Helper to build attributes common to both experiment view and edit pages
	private Experiment buildExperimentModelAttributes(Integer experimentId, int instructorId, Model model) {
		List<MeasurementInfo> measurements;
		Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> measurementValues;
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		measurements = measurementRepository.findForExperimentView(experimentId);
		measurementValues = measurementValueRepository.findForInstructor(experimentId, instructorId).stream()
				.collect(Collectors.groupingBy(
						MeasurementValueForFacultyExperimentView::getMeasurementId,
						Collectors.groupingBy(
								MeasurementValueForFacultyExperimentView::getCourseClassId,
								Collectors.groupingBy(
										MeasurementValueForFacultyExperimentView::getStudentId,
										Collectors.toList()
										))));
		
		model.addAttribute("measurements", measurements);
		model.addAttribute("parameters", measurements.stream()
				.map(MeasurementInfo::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> parameterRepository.findForExperimentView(id))));
		model.addAttribute("measurementValues", measurementValues);
		model.addAttribute("parameterValues", measurements.stream()
				.map(MeasurementInfo::getId)
				.filter(id -> !Objects.isNull(measurementValues.get(id)))
				.collect(Collectors.toMap(
						Function.identity(),
						mid -> measurementValues.get(mid).entrySet().stream()
							.flatMap(e -> e.getValue().entrySet().stream())
							.flatMap(e -> e.getValue().stream())
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> parameterValueRepository.findForExperimentView(vid).stream()
										.collect(Collectors.toMap(
												ParameterValueForExperimentView::getParameterId,
												Function.identity()
												))
							))
						))
				);
		return experiment;
	}
	
	@GetMapping("/experiment/{experimentId}")
	public String getExperiment(@PathVariable Integer experimentId, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		
		Instructor instructor = (Instructor) user;
		int instructorId = instructor.getId();
		
		Experiment experiment = buildExperimentModelAttributes(experimentId, instructorId, model);
		
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
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		
		Instructor instructor = (Instructor) user;
		Integer instructorId = instructor.getId();
		
		Experiment experiment = buildExperimentModelAttributes(experimentId, instructorId, model);
		
		model.addAttribute("course", courseRepository.findCourseInfoForExperiment(experimentId).get());
		model.addAttribute("name", experiment.getName());
		model.addAttribute("description", experiment.getDescription());
		model.addAttribute("reportDueDate", experiment.getReportDueDate());
		
		model.addAttribute("actionURL", MvcUriComponentsBuilder
				.fromMethodName(
						FacultyController.class,
						"updateExperiment",
						experimentId, null, null, null
						).toUriString());
		
		model.addAttribute(
				"quantityTypeIdValues", 
				Stream.of(QuantityTypeId.values())
				.sorted((q1, q2) -> q1.getDisplayName().compareTo(q2.getDisplayName()))
				.collect(Collectors.toList())
		);
		
		return "faculty/editexperiment";
	}
	
	@PostMapping("/experiment/edit/{experimentId}")
	public String updateExperiment(@PathVariable Integer experimentId,
			@RequestParam Map<String, String> requestParams,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
		final Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		experiment.setName(requestParams.get("experimentName"));
		experiment.setDescription(requestParams.get("description"));
		experiment.setReportDueDate(LocalDateTime.parse(
				requestParams.get("submissionDeadline"),
				DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
			));
		
		// database actions for each measurement and parameter
		// mapping is from client-side key to database action so as to allow new ones to be added
		HashMap<String, DatabaseAction> measurementActions = new HashMap<>();
		HashMap<String, DatabaseAction> parameterActions = new HashMap<>();
		HashSet<String> measurementKeys = new HashSet<>();
		HashSet<String> parameterKeys = new HashSet<>();
		
		for (Map.Entry<String, String> requestParameterEntry : requestParams.entrySet()) {
			String requestParameterName = requestParameterEntry.getKey();
			Pattern measurementParameterNamePattern = 
					Pattern.compile("^measurementAction(N?\\d+)$");
			Matcher measurementMatcher = measurementParameterNamePattern
					.matcher(requestParameterName);
			
			if (measurementMatcher.find()) {
				measurementActions.put(
					measurementMatcher.group(1),
					DatabaseAction.valueOf(requestParameterEntry.getValue())
				);
				measurementKeys.add(measurementMatcher.group(1));
				
				// early continue
				continue;
			}
			
			Pattern parameterParameterNamePattern =
					Pattern.compile("^parameterAction(N?\\d+)$");
			Matcher parameterMatcher = parameterParameterNamePattern
					.matcher(requestParameterName);
			
			if (parameterMatcher.find()) {
				parameterActions.put(
					parameterMatcher.group(1),
					DatabaseAction.valueOf(requestParameterEntry.getValue())
				);
				parameterKeys.add(parameterMatcher.group(1));
				
				// early continue
				continue;
			}
			
			// ensure we get keys for variables with no explicit database action
			// (implicit DatabaseAction.UPDATE)
			Pattern measurementNameParameterNamePattern =
					Pattern.compile("^measurementName(N?\\d+)$");
			Matcher measurementNameMatcher = measurementNameParameterNamePattern
					.matcher(requestParameterName);
			
			if (measurementNameMatcher.find()) {
				measurementKeys.add(measurementNameMatcher.group(1));
				
				// early continue
				continue;
			}
			
			Pattern parameterNameParameterNamePattern =
					Pattern.compile("^parameterName(N?\\d+)$");
			Matcher parameterNameMatcher = parameterNameParameterNamePattern
					.matcher(requestParameterName);
			
			if (parameterNameMatcher.find()) {
				parameterKeys.add(parameterNameMatcher.group(1));
			}
		}
		
		// infer update actions from keys with no specified action
		measurementActions.putAll(measurementKeys.stream()
				.filter(key -> measurementActions.get(key) == null)
				.collect(Collectors.toMap(
						Function.identity(),
						key -> DatabaseAction.UPDATE
				))
		);
		
		parameterActions.putAll(parameterKeys.stream()
				.filter(key -> parameterActions.get(key) == null)
				.collect(Collectors.toMap(
						Function.identity(),
						key -> DatabaseAction.UPDATE
				))
		);
		
		HashMap<String, Measurement> measurements = new HashMap<>();
		HashMap<String, Parameter> parameters = new HashMap<>();
		
		Map<String, String> newMeasurementNames = measurementActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("measurementName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> requestParams.get("measurementName" + key)
				));
		Map<String, QuantityTypeId> newMeasurementQuantityTypeIds = measurementActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("measurementQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								requestParams.get("measurementQuantityTypeId" + key)
						)
				));
		Map<String, String> newParameterNames = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("parameterName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> requestParams.get("parameterName" + key)
				));
		Map<String, QuantityTypeId> newParameterQuantityTypeIds = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("parameterQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								requestParams.get("parameterQuantityTypeId" + key)
						)
				));
		
		// perform actions on measurements
		measurementActions.forEach((key, action) -> {
			Measurement measurement;
			switch (action) {
			case CREATE:
				measurement = experiment.addMeasurement(
					newMeasurementNames.get(key),
					newMeasurementQuantityTypeIds.get(key).getQuantityClass().getQuantityType()
				);
				measurements.put(key, measurement);
				break;
			case UPDATE:
				measurement = measurementRepository.findById(Integer.parseInt(key))
					.orElseThrow(() -> new ResourceNotFoundException(Measurement.class, Integer.parseInt(key)));
				measurement.setName(newMeasurementNames.get(key));
				measurement.setQuantityTypeId(newMeasurementQuantityTypeIds.get(key));
				measurement = measurementRepository.save(measurement);
				measurements.put(key, measurement);
				break;
			case DELETE:
				measurement = measurementRepository.findById(Integer.parseInt(key))
					.orElseThrow(() -> new ResourceNotFoundException(Measurement.class, Integer.parseInt(key)));
				experiment.removeMeasurement(measurement);
				measurementRepository.deleteById(measurement.getId());
			}
		});
		
		// perform actions on parameters
		parameterActions.forEach((key, action) -> {
			String measurementKey = requestParams.get("parameterMeasurementId" + key);
			Measurement measurement = measurements.get(measurementKey);
			Parameter parameter;
			switch (action) {
			case CREATE:
				parameter = measurement.addParameter(
						newParameterNames.get(key),
						newParameterQuantityTypeIds.get(key).getQuantityClass().getQuantityType());
				measurement = measurementRepository.save(measurement);
				parameters.put(key, parameter);
				break;
			case UPDATE:
				parameter = parameterRepository.findById(Integer.parseInt(key))
					.orElseThrow(() -> new ResourceNotFoundException(Parameter.class, Integer.parseInt(key)));
				parameter.setName(newParameterNames.get(key));
				parameter.setQuantityTypeId(newParameterQuantityTypeIds.get(key));
				parameter = parameterRepository.save(parameter);
				parameters.put(key, parameter);
				break;
			case DELETE:
				parameter = parameterRepository.findById(Integer.parseInt(key))
					.orElseThrow(() -> new ResourceNotFoundException(Parameter.class, Integer.parseInt(key)));
				measurement = parameter.getMeasurement();
				measurement.removeParameter(parameter);
				parameterRepository.deleteById(parameter.getId());
				measurement = measurementRepository.save(measurement);
			}
		});
		
		Experiment savedExperiment = experimentRepository.save(experiment);
		
		// send user back to experiment view
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(
						FacultyController.class,
						"getExperiment", savedExperiment.getId(),
						new Object(), new Object())
				.toUriString();
	}
	
	// helper that retrieves report information that is needed for faculty views and adds it to model
	private void getReportInfo(Integer reportId, Model model)
			throws MalformedURLException, UnsupportedEncodingException {
		ReportForFacultyReportView reportInfo = reportedResultRepository.findReportForFacultyReportView(reportId);
		List<ResultInfo> acceptedResults = reportedResultRepository.findAcceptedResultsForReportedResult(reportId);
		
		model.addAttribute("report", reportInfo);
		model.addAttribute("acceptedResults", acceptedResults);
		model.addAttribute("reportDocumentURL", reportDocumentService.buildReportDocumentUrl(reportId));
	}
	
	@GetMapping("/report/{reportId}")
	public String getReport(@PathVariable Integer reportId, 
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException {
		getReportInfo(reportId, model);
		
		return "faculty/report";
	}
	
	@GetMapping("/report/score/{reportId}")
	public String editReportScore(@PathVariable Integer reportId,
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) throws MalformedURLException, UnsupportedEncodingException {
		getReportInfo(reportId, model);
		
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
			@AuthenticationPrincipal(expression="labVisionUser") LabVisionUser user, Model model) {
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
		Instructor instructor = (Instructor) user;
		
		instructor.setName(instructorName);
		instructor.setEmail(instructorEmail);
		
		ConstraintViolationException exception = null;
		
		try {
			instructor = instructorRepository.save(instructor);
		} catch (TransactionSystemException e) {
			if (e.contains(ConstraintViolationException.class)) {
				exception = (ConstraintViolationException) e.getRootCause();
			} else {
				throw e;
			}
		} catch (ConstraintViolationException e) {
			exception = e;
		}
		
		if (exception != null) {
			return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(FacultyController.class, "editProfile",
						exception.getConstraintViolations().stream()
							.map(cv -> cv.getMessage())
							.toArray(String[]::new),
						new Object(), new Object())
				.toUriString();
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(FacultyController.class, "profile", new Object(), new Object())
				.toUriString();
	}
	
	private NavbarModel buildFacultyNavbar() {
		NavbarModel navbarModel = new NavbarModel();
		
		navbarModel.addNavLink("Dashboard", FacultyController.class, "dashboard", new Object(), new Object());
		navbarModel.addNavLink("Experiments", FacultyController.class, "experiments", new Object(), new Object());
		navbarModel.addNavLink(navbarModel.new NavLink(
			"Account",
			"#",
			new NavbarModel.NavLink[] {
				navbarModel.createNavLink("Profile", FacultyController.class, "profile", new Object(), new Object())
			}
		));
		
		navbarModel.setLogoutLink("/logout");
		
		return navbarModel;
	}
}
