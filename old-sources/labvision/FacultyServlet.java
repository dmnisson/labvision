package labvision;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.dto.course.CourseInfo;
import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import labvision.dto.experiment.report.ReportForFacultyReportView;
import labvision.dto.experiment.report.ResultInfo;
import labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable;
import labvision.dto.faculty.report.ReportForFacultyExperimentView;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.Measurement;
import labvision.entities.Parameter;
import labvision.entities.QuantityTypeId;
import labvision.models.NavbarModel;
import labvision.services.CourseService;
import labvision.services.ExperimentService;
import labvision.services.ReportService;
import labvision.utils.ThrowingWrappers;

/**
 * Servlet for handling faculty endpoints
 * @author davidnisson
 *
 */
public class FacultyServlet extends AbstractLabVisionServlet {

	/**
	 * Unique identifier for this version for serialization.
	 */
	private static final long serialVersionUID = -3003289972742929324L;
	
	public static final String FACULTY_SERVLET_NAME = "labvision-faculty";
	
	private String getExperimentPath(int id) throws ServletNotFoundException,ServletMappingNotFoundException{
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/experiment/" + id);
	}
	
	private String getNewExperimentPath(int courseId) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/experiment/new/" + courseId);
	}
	
	private String getEditExperimentPath(int id) throws ServletNotFoundException,ServletMappingNotFoundException {
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/experiment/edit/" + id);
	}
	
	private String getEditMeasurementPath(int id) throws ServletNotFoundException,ServletMappingNotFoundException{
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/measurement/edit/" + id);
	}
	

	private String getReportScorePath(Integer id) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/report/score/" + id);
	}

	private String getReportPath(Integer id) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor()
				.getPathFor(FACULTY_SERVLET_NAME, "/report/" + id);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initFacultyNavbar(req);
		
		if (req.getPathInfo() == null) {
			resp.sendRedirect("/faculty/dashboard");
			return;
		}
		
		String[] pathParts = req.getPathInfo().split("/");
		switch (pathParts[1]) {
		case "dashboard":
			doGetDashboard(req, resp, session);
			break;
		case "experiments":
			doGetExperiments(req, resp, session);
			break;
		case "experiment":
			try {
				doGetExperiment(req, resp, session, pathParts[2],
						pathParts.length == 3 ? null : pathParts[3]);
			} catch (ServletNotFoundException | ServletMappingNotFoundException e1) {
				handleURLComputationError(resp, e1);
			}
			break;
		case "reports":
			doGetReports(req, resp, session);
			break;
		case "report":
			try {
				doGetReport(req, resp, session, pathParts[2],
					pathParts.length == 3 ? null : pathParts[3]);
			} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
				handleURLComputationError(resp, e);
			}
			break;
		case "courses":
			doGetCourses(req, resp, session);
			break;
		case "course":
			doGetCourse(req, resp, session, pathParts[2]);
			break;
		case "students":
			doGetStudents(req, resp, session);
			break;
		case "student":
			doGetStudent(req, resp, session, pathParts[2]);
			break;
		case "errors":
			doGetErrors(req, resp, session);
			break;
		case "profile":
			doGetProfile(req, resp, session);
			break;
		default:
			resp.sendRedirect("/faculty/dashboard");
		}
	}

	private void doGetProfile(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetErrors(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetStudents(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourse(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourses(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String action, String arg) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		if (arg == null) {
			arg = action;
			action = "view";
		}
		
		int reportId = Integer.parseInt(arg);
		
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		
		ReportForFacultyReportView reportInfo = reportService.getReport(reportId, ReportForFacultyReportView.class);
		List<ResultInfo> acceptedResults = reportService.getAcceptedResults(reportId);
		
		req.setAttribute("report", reportInfo);
		req.setAttribute("acceptedResults", acceptedResults);
		req.setAttribute("reportDocumentURL", reportService.getDocumentURL(
				reportId,
				getServletContext(),
				req.getServerName(),
				req.getServerPort()));
		req.setAttribute("scoring", action.equals("score"));
		req.setAttribute("scorePath", getReportScorePath(reportId));
		
		req.getRequestDispatcher("/WEB-INF/faculty/report.jsp").forward(req, resp);
	}

	private void doGetReports(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String action, String arg) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		if (arg == null) {
			arg = action;
			action = "view";
		}
		int experimentOrCourseId = Integer.parseInt(arg);
		
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		
		Instructor instructor = (Instructor) session.getAttribute("user");
		int instructorId = instructor.getId();
		Experiment experiment;
		List<MeasurementForExperimentView> measurements;
		Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> measurementValues;
		
		if (action.matches("edit|view")) {
			experiment = experimentService.getExperiment(
				experimentOrCourseId, ExperimentPrefetch.PREFETCH_VALUES);
			req.setAttribute("experiment", experiment);
			
			measurements = experimentService.getMeasurements(experimentOrCourseId);
			measurementValues = experimentService.getMeasurementValuesForInstructor(experimentOrCourseId, instructorId);
			
			req.setAttribute("measurements", measurements);
			req.setAttribute("parameters", measurements.stream()
					.map(MeasurementForExperimentView::getId)
					.collect(Collectors.toMap(
							Function.identity(),
							id -> experimentService.getParameters(id))));
			req.setAttribute("measurementValues", measurementValues);
			req.setAttribute("parameterValues", measurements.stream()
					.map(MeasurementForExperimentView::getId)
					.filter(id -> !Objects.isNull(measurementValues.get(id)))
					.collect(Collectors.toMap(
							Function.identity(),
							id -> measurementValues.get(id).entrySet().stream()
								.flatMap(e -> e.getValue().entrySet().stream())
								.flatMap(e -> e.getValue().stream())
								.map(MeasurementValueForExperimentView::getId)
								.collect(Collectors.toMap(
										Function.identity(),
										vid -> experimentService.getParameterValues(vid)
								))
							))
					);
			req.setAttribute("editMeasurementPaths", ThrowingWrappers.collectionToMap(measurements.stream()
				.map(MeasurementForExperimentView::getId)
				.collect(Collectors.toList()), id1 -> getEditMeasurementPath(id1)));
			
			switch (action) {
			case "view":
				List<Integer> studentIds = experiment.getStudentIds();
				List<ReportForFacultyExperimentView> reports = 
						reportService.getReportsForExperiment(experiment.getId());
				List<Integer> reportIds = reports.stream()
						.map(ReportForFacultyExperimentView::getId)
						.collect(Collectors.toList());
				Map<Integer, List<ReportForFacultyExperimentView>> reportsByStudentId =
						reports.stream()
						.collect(Collectors.groupingBy(r -> r.getStudentId()));
				
				req.setAttribute("editExperimentPath", getEditExperimentPath(experiment.getId()));
				
				req.setAttribute("studentIds", studentIds);
				req.setAttribute("reports", reportsByStudentId);
				req.setAttribute("reportPaths",
						ThrowingWrappers.collectionToMap(reportIds, id -> getReportPath(id)));
				req.setAttribute("reportScorePaths",
						ThrowingWrappers.collectionToMap(reportIds, id -> getReportScorePath(id)));
				
				req.getRequestDispatcher("/WEB-INF/faculty/experiment.jsp").forward(req, resp);
				break;
			case "edit":
				req.setAttribute("course", experimentService.getCourseInfo(experiment.getId()));
				req.setAttribute("name", experiment.getName());
				req.setAttribute("description", experiment.getDescription());
				req.setAttribute("reportDueDate", experiment.getReportDueDate());
				
				req.setAttribute("actionURL", getEditExperimentPath(experiment.getId()));
				
				req.setAttribute(
						"quantityTypeIdValues", 
						Stream.of(QuantityTypeId.values())
						.sorted((q1, q2) -> q1.getDisplayName().compareTo(q2.getDisplayName()))
						.collect(Collectors.toList())
				);
				
				req.getRequestDispatcher("/WEB-INF/faculty/editexperiment.jsp").forward(req, resp);
				break;
			default:
				resp.setStatus(500);
				new Exception("unexpected condition")
					.printStackTrace(resp.getWriter());
				resp.flushBuffer();
			}
		} else if (action.equals("new")) {
			CourseService courseService = (CourseService) getServletContext()
					.getAttribute(LabVisionServletContextListener.COURSE_SERVICE_ATTR);
			CourseInfo courseInfo = courseService.getCourseInfo(experimentOrCourseId);
			req.setAttribute("course", courseInfo);
			req.setAttribute("actionURL", getNewExperimentPath(courseInfo.getId()));
			req.getRequestDispatcher("/WEB-INF/faculty/editexperiment.jsp").forward(req, resp);
		} else {
			resp.sendError(400, "Unrecognized action: " + action);
		}
	}

	private void doGetExperiments(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws ServletException, IOException {		
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
				
		Instructor instructor = (Instructor) session.getAttribute("user");
		int instructorId = instructor.getId();
		List<ExperimentForFacultyExperimentTable> experiments = experimentService.getExperiments(instructorId);
		
		req.setAttribute("experiments", experiments);
		req.setAttribute("experimentPaths", ThrowingWrappers.collectionToMap(experiments.stream()
			.map(ExperimentForFacultyExperimentTable::getId)
			.collect(Collectors.toList()), id -> getExperimentPath(id)));
		req.getRequestDispatcher("/WEB-INF/faculty/experiments.jsp").forward(req, resp);
	}

	private void doGetDashboard(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws ServletException, IOException {
		Instructor instructor = (Instructor) session.getAttribute("user");
		
		req.setAttribute("instructor", instructor);
		req.getRequestDispatcher("/WEB-INF/faculty/dashboard.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initFacultyNavbar(req);
		
		if (req.getPathInfo() == null) {
			doPost404(req, resp);
			return;
		}
		
		String[] pathParts = req.getPathInfo().split("/");
		switch (pathParts[1]) {
		case "experiment":
			try {
				doPostExperiment(req, resp, session, pathParts[2],
						pathParts.length == 3 ? null : pathParts[3]);
			} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
				handleURLComputationError(resp, e);
			}
			break;
		case "report":
			try {
				doPostReport(req, resp, session, pathParts[2],
					pathParts.length == 3 ? null : pathParts[3]);
			} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
				handleURLComputationError(resp, e);
			}
			break;
		case "student":
			doPostStudent(req, resp, session, pathParts[2]);
			break;
		default:
			doPost404(req, resp);
		}
	}

	private void doPostStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, 
			String action, String arg) throws IOException, ServletNotFoundException, ServletMappingNotFoundException {
		if (arg == null) {
			arg = action;
			action = "score";
		}
		int reportId = Integer.parseInt(arg);
		
		try {
			BigDecimal score = new BigDecimal(req.getParameter("score"));
			
			ReportService reportService = (ReportService) getServletContext()
					.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
			
			reportService.scoreReport(reportId, score);
			
			resp.sendRedirect(getReportPath(reportId));
		} catch (NumberFormatException e) {
			resp.sendRedirect(getReportScorePath(reportId));
		}
	}

	private void doPostExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String action, String arg) throws IOException, ServletNotFoundException, ServletMappingNotFoundException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		CourseService courseService = (CourseService) getServletContext()
				.getAttribute(LabVisionServletContextListener.COURSE_SERVICE_ATTR);
		
		String name = req.getParameter("experimentName");
		String description = req.getParameter("description");
		LocalDateTime reportDueDate = LocalDateTime.parse(
			req.getParameter("submissionDeadline"),
			DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
		);
		
		Experiment experiment;
		
		switch(action) {
		case "new":
			int courseId = Integer.parseInt(arg);
			experiment = courseService.addExperiment(courseId, name, description, reportDueDate);
			break;
		case "edit":
			int experimentId = Integer.parseInt(arg);
			experiment = experimentService.updateExperiment(experimentId, name, description, reportDueDate);
			break;
		default:
			// early exit
			resp.sendError(422, "Unrecognized action: " + action);
			return;
		}
		
		int experimentId = experiment.getId();
		
		// database actions for each measurement and parameter
		// mapping is from client-side key to database action so as to allow new ones to be added
		HashMap<String, DatabaseAction> measurementActions = new HashMap<>();
		HashMap<String, DatabaseAction> parameterActions = new HashMap<>();
		HashSet<String> measurementKeys = new HashSet<>();
		HashSet<String> parameterKeys = new HashSet<>();
		
		for (Enumeration<String> n = req.getParameterNames(); n.hasMoreElements(); ) {
			String requestParameterName = n.nextElement();
			Pattern measurementParameterNamePattern = 
					Pattern.compile("^measurementAction(N?\\d+)$");
			Matcher measurementMatcher = measurementParameterNamePattern
					.matcher(requestParameterName);
			
			if (measurementMatcher.find()) {
				measurementActions.put(
					measurementMatcher.group(1),
					DatabaseAction.valueOf(req.getParameter(requestParameterName))
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
					DatabaseAction.valueOf(req.getParameter(requestParameterName))
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
				.filter(key -> Objects.nonNull(req.getParameter("measurementName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> req.getParameter("measurementName" + key)
				));
		Map<String, QuantityTypeId> newMeasurementQuantityTypeIds = measurementActions.keySet().stream()
				.filter(key -> Objects.nonNull(req.getParameter("measurementQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								req.getParameter("measurementQuantityTypeId" + key)
						)
				));
		Map<String, String> newParameterNames = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(req.getParameter("parameterName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> req.getParameter("parameterName" + key)
				));
		Map<String, QuantityTypeId> newParameterQuantityTypeIds = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(req.getParameter("parameterQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								req.getParameter("parameterQuantityTypeId" + key)
						)
				));
		
		// add new measurements
		measurementActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.CREATE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				Measurement measurement = experimentService.addMeasurement(
					experiment.getId(),
					newMeasurementNames.get(key),
					newMeasurementQuantityTypeIds.get(key).getQuantityClass().getQuantityType()
				);
				measurements.put(key, measurement);
			});
		
		// update existing measurements
		measurementActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.UPDATE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				measurements.put(key, experimentService.updateMeasurement(
						Integer.parseInt(key),
						newMeasurementNames.get(key),
						newMeasurementQuantityTypeIds.get(key).getQuantityClass().getQuantityType()
				));
			});
		
		// add new parameters
		parameterActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.CREATE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				String measurementKey = req.getParameter("parameterMeasurementId" + key);
				Measurement measurement = measurements.get(measurementKey);
				Parameter parameter = experimentService.addParameter(
					measurement.getId(),
					newParameterNames.get(key),
					newParameterQuantityTypeIds.get(key).getQuantityClass().getQuantityType()
				);
				parameters.put(key, parameter);
			});
		
		// update existing parameters
		parameterActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.UPDATE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				parameters.put(key, experimentService.updateParameter(
						Integer.parseInt(key),
						newParameterNames.get(key),
						newParameterQuantityTypeIds.get(key).getQuantityClass().getQuantityType()
				));
			});
		
		// delete old parameters
		parameterActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.DELETE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				experimentService.removeParameter(Integer.parseInt(key));
			});
		
		// delete old measurements
		measurementActions.entrySet().stream()
			.filter(e -> e.getValue() == DatabaseAction.DELETE)
			.map(Map.Entry::getKey)
			.forEach(key -> {
				experimentService.removeMeasurement(Integer.parseInt(key));
			});
		
		// send user back to experiment view
		resp.sendRedirect(getExperimentPath(experimentId));
	}

	
	
	private void doPost404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initFacultyNavbar(req);
		
		if (req.getPathInfo() == null) {
			doPut404(req, resp);
			return;
		}
		
		String[] pathParts = req.getPathInfo().split("/");
		switch (pathParts[1]) {
		case "experiment":
			doPutExperiment(req, resp, session, pathParts[2]);
			break;
		case "report":
			doPutReport(req, resp, session, pathParts[2]);
			break;
		case "student":
			doPutStudent(req, resp, session, pathParts[2]);
			break;
		case "profile":
			doPutProfile(req, resp, session);
			break;
		default:
			doPut404(req, resp);
		}
	}

	private void doPutProfile(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPutStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPut404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initFacultyNavbar(req);
		
		if (req.getPathInfo() == null) {
			doDelete404(req, resp);
			return;
		}
		
		String[] pathParts = req.getPathInfo().split("/");
		switch (pathParts[1]) {
		case "experiment":
			doDeleteExperiment(req, resp, session, pathParts[2]);
			break;
		case "report":
			doDeleteReport(req, resp, session, pathParts[2]);
			break;
		case "student":
			doDeleteStudent(req, resp, session, pathParts[2]);
			break;
		default:
			doDelete404(req, resp);
		}
	}

	private void doDeleteStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDelete404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	private void initFacultyNavbar(HttpServletRequest req) {
		NavbarModel navbarModel = new NavbarModel();
		
		navbarModel.addNavLink("Dashboard", "/faculty/dashboard");
		navbarModel.addNavLink("Experiments", "/faculty/experiments");
		
		navbarModel.setLogoutLink("/logout");
		
		req.setAttribute("navbarModel", navbarModel);
	}

	
}
