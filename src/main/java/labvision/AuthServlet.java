package labvision;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import labvision.auth.DeviceAuthentication;
import labvision.auth.DeviceToken;
import labvision.entities.Device;
import labvision.entities.User;
import labvision.services.UserService;

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
		UserService userService = (UserService)
				this.getServletContext().getAttribute(LabVisionServletContextListener.USER_SERVICE_ATTR);
		
		DeviceToken deviceToken = DeviceAuthentication.getDeviceToken(req);
		DeviceAuthentication deviceAuthentication = (DeviceAuthentication) getServletContext()
				.getAttribute(LabVisionServletContextListener.DEVICE_AUTHENTICATION_ATTR);
		
		if (Objects.isNull(deviceToken)) {
			req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
		} else {
			User user = userService.getUser(deviceToken.getUserId(), true);
			
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
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | KeyStoreException | CertificateException e) {
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
		
		UserService userService = (UserService) this.getServletContext()
				.getAttribute(LabVisionServletContextListener.USER_SERVICE_ATTR);
		DeviceAuthentication deviceAuthentication = (DeviceAuthentication) this.getServletContext()
				.getAttribute(LabVisionServletContextListener.DEVICE_AUTHENTICATION_ATTR);
		
		User user = userService.getUser(username, true);
		
		if (user == null) {
			// redirect to login page with error message
			String redirect = req.getRequestURL().append("?error=401").toString();
			resp.sendRedirect(redirect);
		}
		
		try {
			DeviceToken deviceToken = DeviceAuthentication.getDeviceToken(req);
			if (!user.passwordMatches(password)) {
				req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
			} else {
				HttpSession session = req.getSession();
				session.setAttribute("user", user);
				
				if (!rememberMe) {
					DeviceAuthentication.clearDeviceToken(resp);
			    } else {
					if (Objects.isNull(deviceToken)) {
						// first add the device to the database
						Device device = new Device();
						device.setUser(user);
						device = userService.addDevice(user, device);
						
						// then create the new device token
						deviceToken = deviceAuthentication.createDeviceToken(device, user, req);
					} else {
						// renew the token
						deviceToken = deviceAuthentication.createDeviceToken(
								userService.getDevice(deviceToken.getDeviceId()),
								user,
								req
						);
					}
					deviceAuthentication.addDeviceToken(resp, deviceToken);
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
		
		// clear device token if user has one
		DeviceAuthentication.clearDeviceToken(resp);
		
		session.invalidate();
		
		resp.sendRedirect(req.getContextPath() + "/login");
	}
}
