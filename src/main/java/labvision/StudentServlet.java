package labvision;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.viewmodels.NavbarModel;
import labvision.entities.Student;
import labvision.entities.User;
import labvision.entities.UserRole;
import labvision.viewmodels.Dashboard;

public class StudentServlet extends HttpServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -4488832460194220512L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		System.out.println(session);
		
		if (session == null || session.getAttribute("user") == null ||
				((User)session.getAttribute("user")).getRole() != UserRole.STUDENT) {
			response.sendRedirect("/login/student");
			return;
		}
		
		NavbarModel navbarModel = (NavbarModel) this.getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_NAVBAR_ATTR);
		request.setAttribute("navbarModel", navbarModel);
		
		if (request.getPathInfo() == null) {
			response.sendRedirect("/student/dashboard");
		} else {
			switch (request.getPathInfo()) {
			case "/dashboard":
				doGetDashboard(request, response, session);
				break;
			default:
				response.sendRedirect("/student/dashboard");
			}
		}
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		Dashboard dashboardModel = new Dashboard();
			
		dashboardModel.setStudent((Student) session.getAttribute("user"));
		
		request.setAttribute("dashboardModel", dashboardModel);
		request.getRequestDispatcher("/WEB-INF/student/dashboard.jsp").forward(request, response);
	}
}
