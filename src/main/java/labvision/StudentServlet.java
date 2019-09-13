package labvision;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.viewmodels.ExperimentsTableModel;
import labvision.viewmodels.NavbarModel;
import labvision.entities.Experiment;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;
import labvision.viewmodels.StudentDashboard;

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
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiments(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		EntityManagerFactory emf = (EntityManagerFactory) getServletContext()
				.getAttribute(LabVisionServletContextListener.ENTITY_MANAGER_FACTORY_ATTR);
		LabVisionDataAccess dataAccess = new LabVisionDataAccess(emf);
		
		Student student = (Student) session.getAttribute("user");
		ExperimentsTableModel experimentsTableModel = new ExperimentsTableModel();
		experimentsTableModel.setCurrentExperiments(student.getActiveExperiments());
		experimentsTableModel.setPastExperiments(dataAccess.getRecentExperiments(student));
		experimentsTableModel.setReportedResults(dataAccess.getReportedResults(student));
		experimentsTableModel.setReportStatus(dataAccess.getReportStatus(student));
		
		request.setAttribute("experimentsTableModel", experimentsTableModel);
		request.getRequestDispatcher("/WEB-INF/student/experiments.jsp").forward(request, response);
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		LabVisionDataAccess dataAccess = (LabVisionDataAccess) getServletContext()
				.getAttribute(LabVisionServletContextListener.DATA_ACCESS_ATTR);
		LabVisionConfig config = (LabVisionConfig) getServletContext()
				.getAttribute(LabVisionServletContextListener.CONFIG_ATTR);
		
		StudentDashboard dashboardModel = new StudentDashboard();
			
		Student student = (Student) session.getAttribute("user");
		dashboardModel.setStudent(student);
		dashboardModel.setCurrentExperiments(student.getActiveExperiments().stream()
				.collect(Collectors.toMap(Experiment::getCourse, e -> e)));		
		dashboardModel.setRecentExperiments(dataAccess.getRecentExperiments(student));
		dashboardModel.setRecentCourses(dataAccess.getRecentCourses(student));
		dashboardModel.setMaxRecentExperiments(config.getStudentDashboardMaxRecentExperiments());
		dashboardModel.setMaxRecentCourses(config.getStudentDashboardMaxRecentCourses());
		
		request.setAttribute("dashboardModel", dashboardModel);
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
				doPostMeasurement(request, response, session, pathParts[2]);
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
			String string) {
		// TODO Auto-generated method stub
		
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
