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

import labvision.entities.PersistableAmount;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Parameter;
import labvision.entities.Student;
import labvision.measure.Amount;
import labvision.measure.SI;
import labvision.models.ExperimentViewModel;
import labvision.models.NavbarModel;
import labvision.models.StudentExperimentViewModel;
import labvision.services.ExperimentService;
import labvision.services.StudentDashboardService;
import labvision.services.StudentExperimentService;
import labvision.services.StudentService;

public class StudentServlet extends HttpServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -4488832460194220512L;

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
				doGetExperiments(request, response, session);
				break;
			case "experiment":
				doGetExperiment(request, response, session, pathParts[2]);
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
			String experimentId) throws ServletException, IOException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		Experiment experiment = experimentService.getExperiment(Integer.parseInt(experimentId), ExperimentPrefetch.PREFETCH_VALUES);
		List<Measurement> measurements = experiment.getMeasurements().stream()
				.collect(Collectors.toCollection(ArrayList::new));
		
		StudentExperimentViewModel experimentViewModel = new StudentExperimentViewModel();
		
		experimentViewModel.setMeasurementUnits(measurements.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						m -> m.systemUnit(m.getQuantityTypeId().getQuantityClass().getQuantityType())
							.getSymbol())));
		experimentViewModel.setParameterUnits(measurements.stream()
				.flatMap(m -> m.getParameters().stream())
				.collect(Collectors.toMap(
						Function.identity(),
						p -> p.systemUnit(p.getQuantityTypeId().getQuantityClass().getQuantityType())
							.getSymbol())));
		experimentViewModel.setMeasurementValues(measurements.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						m -> experimentService.getMeasurementValues(m, student, true))));
		experimentViewModel.setReportDisplay(experimentService.getReportedResults(experiment, student).stream()
				.collect(Collectors.toMap(
						Function.identity(),
						ExperimentViewModel.REPORT_DISPLAY_FUNCTION)));
		
		request.setAttribute("experiment", experiment);
		request.setAttribute("experimentViewModel", experimentViewModel);
		request.getRequestDispatcher("/WEB-INF/student/experiment.jsp").forward(request, response);
	}

	private void doGetExperiments(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		StudentExperimentService studentExperimentTableService = (StudentExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_EXPERIMENT_TABLE_SERVICE_ATTR);
		
		int studentId = ((Student) session.getAttribute("user")).getId();
		
		request.setAttribute("currentExperiments", studentExperimentTableService.getCurrentExperiments(studentId));
		request.setAttribute("pastExperiments", studentExperimentTableService.getPastExperiments(studentId));
		request.getRequestDispatcher("/WEB-INF/student/experiments.jsp").forward(request, response);
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		StudentDashboardService dashboardService = (StudentDashboardService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_DASHBOARD_SERVICE_ATTR);

		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		
		request.setAttribute("student", student);
		request.setAttribute("currentExperiments", dashboardService.getCurrentExperiments(studentId));
		request.setAttribute("recentExperiments", dashboardService.getRecentExperiments(studentId));
		request.setAttribute("recentCourses", dashboardService.getRecentCourses(studentId));
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
