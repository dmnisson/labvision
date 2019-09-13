package labvision;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	private void doGetReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReports(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiments(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetDashboard(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
	}

	
}