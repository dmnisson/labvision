package labvision;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.ExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.Parameter;
import labvision.entities.Student;
import labvision.measure.Amount;
import labvision.measure.SI;
import labvision.models.NavbarModel;
import labvision.services.ExperimentService;
import labvision.services.ServletMappingNotFoundException;
import labvision.services.ServletNotFoundException;
import labvision.services.StudentCourseService;
import labvision.services.StudentDashboardService;
import labvision.services.StudentExperimentService;
import labvision.services.StudentReportService;
import labvision.services.StudentService;
import labvision.utils.ThrowingWrappers;

public class StudentServlet extends HttpServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -4488832460194220512L;

	public static final String STUDENT_SERVLET_NAME = "labvision-student";
	
	private static String URL_COMPUTATION_ERROR_MESSAGE = "Could not compute URLs. "
			+ "This is likely a problem with the app configuration. "
			+ "Please contact your institution for assistance.";
	
	private IPathConstructor getPathConstructor(ServletContext context) {
		return (IPathConstructor) context.getAttribute(LabVisionServletContextListener.PATH_CONSTRUCTOR_ATTR);
	}
	
	/**
	 * Retrieve mapping of experiment IDs to paths for experiment detail views
	 * @param experimentIds the IDs
	 * @param context the servlet context
	 * @return the mapping of experiment IDs to paths
	 */
	private Map<Integer, String> getExperimentPaths(Collection<? extends Integer> experimentIds, ServletContext context) {
		return experimentIds.stream().distinct()
				.collect(Collectors.toMap(
						Function.identity(), 
						ThrowingWrappers.throwingFunctionWrapper(id ->
								getPathConstructor(context)
									.getPathFor(STUDENT_SERVLET_NAME, "/experiment/" + id))
						));
	}
	
	/**
	 * Retrieve mapping of measurement IDs to paths for creating new measurement values
	 * @param measurementIds the IDs
	 * @param context the servlet context
	 * @return the mapping of measurement IDs to new-value paths
	 */
	private Map<Integer, String> getNewMeasurementValuePaths(Collection<? extends Integer> measurementIds, ServletContext context) {
		return measurementIds.stream().distinct()
				.collect(Collectors.toMap(
						Function.identity(),
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathConstructor(context)
									.getPathFor(STUDENT_SERVLET_NAME, "/measurement/newvalue/" + id))
						));
	}
	
	/**
	 * Retrieve mapping of report IDs to report view paths
	 * @param reportIds the report IDs
	 * @param context the servlet context
	 * @return the mapping of report IDs to report view paths
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	private Map<Integer, String> getReportPaths(Collection<? extends Integer> reportIds, ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		return reportIds.stream()
				.collect(Collectors.toMap(Function.identity(), 
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathConstructor(context)
									.getPathFor(STUDENT_SERVLET_NAME, "/report/" + id))));
	}
	
	/**
	 * Get the path for creating a new report
	 * @param context the servlet context
	 * @return the path
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	private String getNewReportPath(ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor(context).getPathFor(STUDENT_SERVLET_NAME, "/report/new");
	}
	
	/**
	 * Retrieve mapping of course IDs to URLs of course detail pages
	 * @param courseIds the course IDs
	 * @param context the servlet context
	 * @return the mapping of course IDs to course view paths
	 */
	private Map<Integer, String> getCoursePaths(Collection<? extends Integer> courseIds, ServletContext context) {
		return courseIds.stream().distinct()
				.collect(Collectors.toMap(Function.identity(),
					ThrowingWrappers.throwingFunctionWrapper(
							id -> getPathConstructor(context)
								.getPathFor(STUDENT_SERVLET_NAME, "/course/" + id))));
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			response.sendRedirect("/student/dashboard");
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "dashboard":
				doGetDashboard(request, response, session);
				break;
			case "experiments":
				try {
					doGetExperiments(request, response, session);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					Logger.getLogger(this.getClass())
						.error(URL_COMPUTATION_ERROR_MESSAGE, e);
					response.sendError(500, URL_COMPUTATION_ERROR_MESSAGE);
				}
				break;
			case "experiment":
				try {
					doGetExperiment(request, response, session, pathParts[2]);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					Logger.getLogger(this.getClass())
						.error(URL_COMPUTATION_ERROR_MESSAGE, e);
					response.sendError(500, URL_COMPUTATION_ERROR_MESSAGE);
				}
				break;
			case "measurement":
				doGetMeasurement(request, response, session, pathParts[2]);
				break;
			case "reports":
				doGetReports(request, response, session);
				break;
			case "report":
				doGetReport(request, response, session, pathParts[2]);
				break;
			case "courses":
				doGetCourses(request, response, session);
				break;
			case "course":
				doGetCourse(request, response, session, pathParts[2]);
				break;
			case "errors":
				doGetErrors(request, response, session);
				break;
			case "profile":
				doGetProfile(request, response, session);
				break;
			default:
				response.sendRedirect("/student/dashboard");
			}
		}
	}

	private void doGetMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourse(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourses(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetErrors(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReports(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiment(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String experimentIdString) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		StudentExperimentService studentExperimentService = (StudentExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_EXPERIMENT_SERVICE_ATTR);
		StudentReportService studentReportService = (StudentReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_REPORT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		int experimentId = Integer.parseInt(experimentIdString);
		Experiment experiment = studentExperimentService.getExperiment(experimentId, ExperimentPrefetch.PREFETCH_NO_VALUES);
		
		List<MeasurementForExperimentView> measurements = studentExperimentService.getMeasurements(experimentId);
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues = studentExperimentService.getMeasurementValues(experimentId, studentId);
		List<ReportedResultForStudentExperimentView> reportedResults = studentExperimentService.getReportedResults(experimentId, studentId);
		
		request.setAttribute("experiment", experiment);
		request.setAttribute("measurements", measurements);
		request.setAttribute("measurementValues", measurementValues);
		// measurement ID -> parameters
		request.setAttribute("parameters", measurements.stream()
				.collect(Collectors.toMap(
						MeasurementForExperimentView::getId,
						m -> studentExperimentService.getParameters(m.getId()))));
		// measurement ID -> measurement value ID -> parameter ID -> parameter value
		request.setAttribute("parameterValues", measurements.stream()
				.map(MeasurementForExperimentView::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> measurementValues.get(id).stream()
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> studentExperimentService.getParameterValues(vid))))));
		request.setAttribute("reportedResults", reportedResults);
		request.setAttribute("reportPaths", getReportPaths(reportedResults.stream()
				.map(ReportedResultForStudentExperimentView::getId).collect(Collectors.toList()), getServletContext()));
		request.setAttribute("newReportPath", getNewReportPath(getServletContext()));
		request.setAttribute("newMeasurementValuePaths", getNewMeasurementValuePaths(
				measurements.stream()
					.collect(Collectors.mapping(
							MeasurementForExperimentView::getId,
							Collectors.toList())),
				getServletContext()));
		
		request.getRequestDispatcher("/WEB-INF/student/experiment.jsp").forward(request, response);
	}

	private void doGetExperiments(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		StudentExperimentService studentExperimentService = (StudentExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_EXPERIMENT_SERVICE_ATTR);
		StudentReportService studentReportService = (StudentReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_REPORT_SERVICE_ATTR);
		
		int studentId = ((Student) session.getAttribute("user")).getId();
		
		List<CurrentExperimentForStudentExperimentTable> currentExperiments = studentExperimentService.getCurrentExperiments(studentId);
		request.setAttribute("currentExperiments", currentExperiments);
		
		List<PastExperimentForStudentExperimentTable> pastExperiments = studentExperimentService.getPastExperiments(studentId);
		request.setAttribute("pastExperiments", pastExperiments);
		
		request.setAttribute("experimentPaths",
				getExperimentPaths(
						Stream.concat(currentExperiments.stream(), pastExperiments.stream())
							.map(ExperimentForStudentExperimentTable::getId)
							.collect(Collectors.toList()),
						getServletContext())
				);
		
		request.setAttribute("newReportPath", getNewReportPath(getServletContext()));
		
		request.getRequestDispatcher("/WEB-INF/student/experiments.jsp").forward(request, response);
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		StudentDashboardService dashboardService = (StudentDashboardService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_DASHBOARD_SERVICE_ATTR);
		StudentCourseService studentCourseService = (StudentCourseService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_COURSE_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		
		request.setAttribute("student", student);
		
		List<CurrentExperimentForStudentDashboard> currentExperiments = dashboardService.getCurrentExperiments(studentId);
		request.setAttribute("currentExperiments", currentExperiments);
		
		List<RecentExperimentForStudentDashboard> recentExperiments = dashboardService.getRecentExperiments(studentId);
		request.setAttribute("recentExperiments", recentExperiments);
		
		List<RecentCourseForStudentDashboard> recentCourses = dashboardService.getRecentCourses(studentId);
		request.setAttribute("recentCourses", recentCourses);
		
		request.setAttribute("experimentPaths", 
				getExperimentPaths(
						Stream.concat(currentExperiments.stream(), recentExperiments.stream())
						.map(ExperimentForStudentDashboard::getId)
						.collect(Collectors.toList()),
				getServletContext()
				));
		
		request.setAttribute("coursePaths", 
				getCoursePaths(
						recentCourses.stream()
						.map(RecentCourseForStudentDashboard::getId)
						.collect(Collectors.toList()),
				getServletContext()
				));
		
		request.getRequestDispatcher("/WEB-INF/student/dashboard.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doPost404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doPostMeasurement(request, response, session, Arrays.copyOfRange(pathParts, 2, pathParts.length));
				break;
			case "report":
				doPostReport(request, response, session, pathParts[2]);
				break;
			default:
				doPost404(request, response);
			}
		}
	}

	private void doPostReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String[] info) throws IOException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		StudentService studentService = (StudentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		
		switch (info[0]) {
		case "newvalue":
			Measurement measurement = experimentService.getMeasurement(Integer.parseInt(info[1]), true);
			
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
			
			experimentService.addMeasurementValue(measurement, student, 
					measurementAmount, parameterAmounts,
					studentService);
			
			response.sendRedirect(request.getContextPath() + "/student/experiment/" + measurement.getExperiment().getId());
		}
	}

	private void doPost404(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(404, "Cannot POST to " + request.getRequestURI());
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doPut404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doPutMeasurement(request, response, session, pathParts[2]);
				break;
			case "report":
				doPutReport(request, response, session, pathParts[2]);
				break;
			case "profile":
				doPutProfile(request, response, session);
				break;
			default:
				doPut404(request, response);
			}
		}
	}

	private void doPutProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPutReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPut404(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doDelete404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doDeleteMeasurement(request, response, session, pathParts[2]);
				break;
			default:
				doDelete404(request, response);
			}
		}
	}

	private void doDeleteMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDelete404(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	private void initStudentNavbar(HttpServletRequest req) {
		NavbarModel model = new NavbarModel();
		
		model.addNavLink("Dashboard", "/student/dashboard");
		model.addNavLink("Experiments", "/student/experiments");
		model.addNavLink("Reports",	"/student/reports");
		model.addNavLink("Errors", "/student/errors");
		model.addNavLink(model.new NavLink(
				"Account", 
				"#", 
				new NavbarModel.NavLink[] {
						model.new NavLink("Profile", "/student/profile"),
						model.new NavLink("Courses", "/student/courses")
				}
			));
		
		model.setLogoutLink("/logout");
		
		req.setAttribute("navbarModel", model);
	}
}
