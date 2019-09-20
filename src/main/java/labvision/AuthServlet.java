package labvision;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import labvision.auth.DeviceAuthentication;
import labvision.auth.DeviceToken;
import labvision.entities.User;

public class AuthServlet extends HttpServlet {
	/**
	 * Unique version ID for serialization
	 */
	private static final long serialVersionUID = -4995588582328863103L;

	private String dashboardRedirectFor(User user) {
		String redirect;
		switch (user.getRole()) {
		case STUDENT:
			redirect = "/student/dashboard";
			break;
		case FACULTY:
			redirect = "/faculty/dashboard";
			break;
		case ADMIN:
			redirect = "/admin/dashboard";
			break;
		default:
			redirect = "/login";	
		}
		return redirect;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		LabVisionConfig config = (LabVisionConfig) 
				this.getServletContext().getAttribute("config");
		LabVisionDataAccess dataAccess = (LabVisionDataAccess)
				this.getServletContext().getAttribute("dataAccess");
		
		DeviceToken deviceToken = DeviceAuthentication.getDeviceToken(req);
		DeviceAuthentication deviceAuthentication = 
				new DeviceAuthentication(config, dataAccess);
		
		if (Objects.isNull(deviceToken)) {
			req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
		} else {
			User user = dataAccess.getUser(deviceToken.getUserId());
			
			try {
				if (!deviceAuthentication.verifyDeviceToken(deviceToken, user, req)) {
					req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
				} else {
					String redirect = req.getParameter("redirect");
					if (Objects.isNull(redirect)) {
						redirect = req.getContextPath() + dashboardRedirectFor(user);
					}
					resp.sendRedirect(redirect);
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException e) {
				Logger.getLogger(this.getClass()).warnf(e,
						"Problem validating user %s on device %s, displaying login page",
						deviceToken.getUserId(), deviceToken.getDeviceId());
				// problem authenticating, so send user to login page
				req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		switch (req.getServletPath()) {
		case "/login":
			doPostLogin(req, resp);
			break;
		case "/logout":
			doPostLogout(req, resp);
			break;
		default:
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}

	private void doPostLogin(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		boolean rememberMe = req.getParameter("rememberMe") != null;
		
		LabVisionConfig config = (LabVisionConfig) this.getServletContext()
				.getAttribute("config");
		LabVisionDataAccess dataAccess = (LabVisionDataAccess) this.getServletContext()
				.getAttribute("dataAccess");
		
		User user = dataAccess.getUser(username);
		
		if (user == null) {
			// redirect to login page with error message
			String redirect = req.getRequestURL().append("?error=401").toString();
			resp.sendRedirect(redirect);
		}
		
		try {
			DeviceToken deviceToken = DeviceAuthentication.getDeviceToken(req);
			DeviceAuthentication deviceAuthentication = 
					new DeviceAuthentication(config, dataAccess);
			if (!user.passwordMatches(password)) {
				req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
			} else {
				HttpSession session = req.getSession();
				session.setAttribute("user", user);
				
				if (!rememberMe) {
					DeviceAuthentication.clearDeviceToken(resp);
			    } else {
					if (Objects.isNull(deviceToken)) {
						deviceToken = deviceAuthentication.createDeviceToken(user, req);
						deviceAuthentication.addDeviceToken(resp, deviceToken);
					}
				}
				
				String redirect = req.getParameter("redirect");
				if (Objects.isNull(redirect)) {
					redirect = req.getContextPath() + dashboardRedirectFor(user);
				}
				resp.sendRedirect(redirect);
			}
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(this.getClass()).errorf(
					e,
					"%s's password is hashed with unsupported algorithm %s",
					username,
					user.getHashAlgorithm());
			resp.sendError(500, "See logs for details.");
		}
	}
	
	
	private void doPostLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		session.invalidate();
		
		resp.sendRedirect(req.getContextPath() + "/login");
	}
}
