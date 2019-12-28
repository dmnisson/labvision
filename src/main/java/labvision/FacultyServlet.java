package labvision;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;
import labvision.models.NavbarModel;
import labvision.services.ExperimentService;
import labvision.services.InstructorService;
import labvision.utils.ThrowingWrappers;

/**
 * Servlet for handling faculty endpoints
 * @author davidnisson
 *
 */
public class FacultyServlet extends HttpServlet {

	/**
	 * Unique identifier for this version for serialization.
	 */
	private static final long serialVersionUID = -3003289972742929324L;
	
	public static final String FACULTY_SERVLET_NAME = "labvision-faculty";
	
	private IPathConstructor getPathConstructor(ServletContext context) {
		return (IPathConstructor) context.getAttribute(LabVisionServletContextListener.PATH_CONSTRUCTOR_ATTR);
	}
	
	private Map<Integer, String> getExperimentPaths(Collection<? extends Integer> experimentIds, ServletContext context) {
		return experimentIds.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathConstructor(context)
								.getPathFor(FACULTY_SERVLET_NAME, "/experiment/" + id))
						));
	}
	
	private Map<Integer, String> getEditMeasurementPaths(Collection<? extends Integer> measurementIds, ServletContext context) {
		return measurementIds.stream()
				.collect(Collectors.toMap(
						Function.identity(),
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathConstructor(context)
								.getPathFor(FACULTY_SERVLET_NAME, "/measurement/" + id + "/edit"))
						));
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
			doGetExperiment(req, resp, session, pathParts[2]);
			break;
		case "reports":
			doGetReports(req, resp, session);
			break;
		case "report":
			doGetReport(req, resp, session, pathParts[2]);
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
		case "measurement":
			doGetMeasurement(req, resp, session, pathParts[2], 
					pathParts.length == 3 ? null : pathParts[3]);
			break;
		default:
			resp.sendRedirect("/faculty/dashboard");
		}
	}

	private void doGetMeasurement(
			HttpServletRequest req,
			HttpServletResponse resp,
			HttpSession session,
			String measurementIdString,
			String measurementAction) {
		if (measurementAction == null) {
			measurementAction = "view";
		}
		
		// TODO Auto-generated method stub
		
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

	private void doGetReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReports(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String experimentIdString) throws ServletException, IOException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		Instructor instructor = (Instructor) session.getAttribute("user");
		int instructorId = instructor.getId();
		int experimentId = Integer.parseInt(experimentIdString);
		Experiment experiment = experimentService.getExperiment(
				experimentId, ExperimentPrefetch.PREFETCH_VALUES);
		
		List<MeasurementForExperimentView> measurements = experimentService.getMeasurements(experimentId);
		Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> measurementValues = experimentService.getMeasurementValuesForInstructor(experimentId, instructorId);
		
		req.setAttribute("experiment", experiment);
		req.setAttribute("measurements", measurements);
		req.setAttribute("parameters", measurements.stream()
				.map(MeasurementForExperimentView::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> experimentService.getParameters(id))));
		req.setAttribute("measurementValues", measurementValues);
		req.setAttribute("parameterValues", measurements.stream()
				.map(MeasurementForExperimentView::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> measurementValues.get(id).entrySet().stream()
							.flatMap(e -> e.getValue().entrySet().stream())
							.flatMap(e -> e.getValue().stream())
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> experimentService.getParameterValues(vid))))));
		req.setAttribute("editMeasurementPaths", getEditMeasurementPaths(
				measurements.stream()
					.map(MeasurementForExperimentView::getId)
					.collect(Collectors.toList()),
				getServletContext()));
		req.getRequestDispatcher("/WEB-INF/faculty/experiment.jsp").forward(req, resp);
	}

	private void doGetExperiments(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws ServletException, IOException {		
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
				
		Instructor instructor = (Instructor) session.getAttribute("user");
		int instructorId = instructor.getId();
		List<ExperimentForFacultyExperimentTable> experiments = experimentService.getExperiments(instructorId);
		
		req.setAttribute("experiments", experiments);
		req.setAttribute("experimentPaths", getExperimentPaths(
				experiments.stream()
					.map(ExperimentForFacultyExperimentTable::getId)
					.collect(Collectors.toList()),
				getServletContext()));
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
			doPostExperiment(req, resp, session, pathParts[2]);
			break;
		case "report":
			doPostReport(req, resp, session, pathParts[2]);
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

	private void doPostReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
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
