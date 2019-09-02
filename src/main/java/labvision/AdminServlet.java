package labvision;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends HttpServlet {

	/**
	 * Unique identifier for serialization version.
	 */
	private static final long serialVersionUID = 943402655698671674L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initAdminNavbar(req);
		
		if (req.getPathInfo() == null) {
			resp.sendRedirect("/admin/dashboard");
		} else {
			String[] pathParts = req.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "users":
				doGetUsers(req, resp, session);
				break;
			case "students":
				doGetStudents(req, resp, session);
				break;
			case "student":
				doGetStudent(req, resp, session, pathParts[2]);
				break;
			case "faculty":
				doGetFaculty(req, resp, session);
				break;
			case "instructor":
				doGetInstructor(req, resp, session, pathParts[2]);
				break;
			case "admins":
				doGetAdmins(req, resp, session);
				break;
			case "admin":
				doGetAdmin(req, resp, session, pathParts[2]);
				break;
			case "devices":
				doGetDevices(req, resp, session);
				break;
			case "device":
				doGetDevice(req, resp, session);
				break;
			case "courses":
				doGetCourses(req, resp, session);
				break;
			case "course":
				doGetCourse(req, resp, session, pathParts[2]);
				break;
			case "courseclasses":
				doGetCourseClasses(req, resp, session, pathParts[2]);
				break;
			case "courseclass":
				doGetCourseClass(req, resp, session, pathParts[2], pathParts[3]);
				break;
			case "experiments":
				doGetExperiments(req, resp, session);
				break;
			case "experiment":
				doGetExperiment(req, resp, session, pathParts[2]);
				break;
			case "measurements":
				doGetMeasurements(req, resp, session);
				break;
			case "measurement":
				doGetMeasurement(req, resp, session, pathParts[2]);
				break;
			case "reports":
				doGetReports(req, resp, session);
				break;
			case "report":
				doGetReport(req, resp, session, pathParts[2]);
				break;
			default:
				resp.sendRedirect("/admin/dashboard");
			}
		}
	}

	private void doGetReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetReports(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetMeasurement(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetMeasurements(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetExperiments(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourseClass(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourseClasses(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourse(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourses(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetDevice(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetDevices(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetAdmins(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetInstructor(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetFaculty(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetStudents(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetUsers(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void initAdminNavbar(HttpServletRequest req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initAdminNavbar(req);
		
		if (req.getPathInfo() == null) {
			doPost404(req, resp);
		} else {
			String[] pathParts = req.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "student":
				doPostStudent(req, resp, session, pathParts[2]);
				break;
			case "faculty":
				doPostFaculty(req, resp, session);
				break;
			case "instructor":
				doPostInstructor(req, resp, session, pathParts[2]);
				break;
			case "admin":
				doPostAdmin(req, resp, session, pathParts[2]);
				break;
			case "device":
				doPostDevice(req, resp, session);
				break;
			case "course":
				doPostCourse(req, resp, session, pathParts[2]);
				break;
			case "courseclass":
				doPostCourseClass(req, resp, session, pathParts[2], pathParts[3]);
				break;
			case "experiment":
				doPostExperiment(req, resp, session, pathParts[2]);
				break;
			case "measurement":
				doPostMeasurement(req, resp, session, pathParts[2]);
				break;
			case "report":
				doPostReport(req, resp, session, pathParts[2]);
				break;
			default:
				doPost404(req, resp);
			}
		}
	}

	private void doPostReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostMeasurement(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostCourseClass(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string,
			String string2) {
		// TODO Auto-generated method stub
		
	}

	private void doPostCourse(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostDevice(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPostAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostInstructor(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPostFaculty(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPostStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPost404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
HttpSession session = req.getSession(false);
		
		initAdminNavbar(req);
		
		if (req.getPathInfo() == null) {
			doPut404(req, resp);
		} else {
			String[] pathParts = req.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "student":
				doPutStudent(req, resp, session, pathParts[2]);
				break;
			case "faculty":
				doPutFaculty(req, resp, session);
				break;
			case "instructor":
				doPutInstructor(req, resp, session, pathParts[2]);
				break;
			case "admin":
				doPutAdmin(req, resp, session, pathParts[2]);
				break;
			case "device":
				doPutDevice(req, resp, session);
				break;
			case "course":
				doPutCourse(req, resp, session, pathParts[2]);
				break;
			case "courseclass":
				doPutCourseClass(req, resp, session, pathParts[2], pathParts[3]);
				break;
			case "experiment":
				doPutExperiment(req, resp, session, pathParts[2]);
				break;
			case "measurement":
				doPutMeasurement(req, resp, session, pathParts[2]);
				break;
			case "report":
				doPutReport(req, resp, session, pathParts[2]);
				break;
			default:
				doPut404(req, resp);
			}
		}
	}

	private void doPutReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutMeasurement(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutCourseClass(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string,
			String string2) {
		// TODO Auto-generated method stub
		
	}

	private void doPutCourse(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutDevice(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPutAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutInstructor(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutFaculty(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPutStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPut404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		initAdminNavbar(req);
		
		if (req.getPathInfo() == null) {
			doDelete404(req, resp);
		} else {
			String[] pathParts = req.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "student":
				doDeleteStudent(req, resp, session, pathParts[2]);
				break;
			case "faculty":
				doDeleteFaculty(req, resp, session);
				break;
			case "instructor":
				doDeleteInstructor(req, resp, session, pathParts[2]);
				break;
			case "admin":
				doDeleteAdmin(req, resp, session, pathParts[2]);
				break;
			case "device":
				doDeleteDevice(req, resp, session);
				break;
			case "course":
				doDeleteCourse(req, resp, session, pathParts[2]);
				break;
			case "courseclass":
				doDeleteCourseClass(req, resp, session, pathParts[2], pathParts[3]);
				break;
			case "experiment":
				doDeleteExperiment(req, resp, session, pathParts[2]);
				break;
			case "measurement":
				doDeleteMeasurement(req, resp, session, pathParts[2]);
				break;
			case "report":
				doDeleteReport(req, resp, session, pathParts[2]);
				break;
			default:
				doDelete404(req, resp);
			}
		}
	}

	private void doDeleteReport(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteMeasurement(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteExperiment(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteCourseClass(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteCourse(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteDevice(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteInstructor(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteFaculty(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doDeleteStudent(HttpServletRequest req, HttpServletResponse resp, HttpSession session, String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDelete404(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		
	}
}
