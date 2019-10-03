package labvision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import labvision.entities.PersistableAmount;
import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.ExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
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

public class StudentServlet extends HttpServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -4488832460194220512L;

	private static String URL_COMPUTATION_ERROR_MESSAGE = "Could not compute URLs. "
			+ "This is likely a problem with the app configuration. "
			+ "Please contact your institution for assistance.";
	
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
		
		request.setAttribute("experiment", experiment);
		request.setAttribute("measurementUnits", studentExperimentService.getMeasurementUnits(experimentId));
		request.setAttribute("parameterUnits", studentExperimentService.getParameterUnits(experimentId));
		request.setAttribute("measurementValues", studentExperimentService.getMeasurementValues(experimentId, studentId));
		request.setAttribute("reportedResults", studentExperimentService.getReportedResults(experimentId, studentId));
		request.setAttribute("reportPaths", studentReportService.getReportPaths(experimentId, getServletContext()));
		request.setAttribute("newReportPath", studentReportService.getNewReportPath(getServletContext()));
		request.setAttribute("newMeasurementValuePaths", studentExperimentService.getNewMeasurementValuePaths(
				experiment.getMeasurements().stream()
					.collect(Collectors.mapping(Measurement::getId, Collectors.toList())),
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
				studentExperimentService.getExperimentPaths(
						Stream.concat(currentExperiments.stream(), pastExperiments.stream())
							.map(ExperimentForStudentExperimentTable::getId)
							.collect(Collectors.toList()),
						getServletContext())
				);
		
		request.setAttribute("newReportPath", studentReportService.getNewReportPath(getServletContext()));
		
		request.getRequestDispatcher("/WEB-INF/student/experiments.jsp").forward(request, response);
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		StudentDashboardService dashboardService = (StudentDashboardService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_DASHBOARD_SERVICE_ATTR);
		StudentExperimentService studentExperimentService = (StudentExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_EXPERIMENT_SERVICE_ATTR);
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
				studentExperimentService.getExperimentPaths(
						Stream.concat(currentExperiments.stream(), recentExperiments.stream())
						.map(ExperimentForStudentDashboard::getId)
						.collect(Collectors.toList()),
				getServletContext()
				));
		
		request.setAttribute("coursePaths", 
				studentCourseService.getCoursePaths(
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
