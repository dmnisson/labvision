package labvision;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import labvision.auth.DeviceAuthentication;
import labvision.auth.DeviceToken;
import labvision.entities.User;
import labvision.services.UserService;

public class AuthFilter implements Filter {
	private FilterConfig filterConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession session = httpRequest.getSession(false);
		
		UserService userService = (UserService) filterConfig.getServletContext()
				.getAttribute(LabVisionServletContextListener.USER_SERVICE_ATTR);
		
		boolean isForbidden = false;
		if (session == null || session.getAttribute("user") == null) {
			DeviceToken token = DeviceAuthentication.getDeviceToken(httpRequest);
			if (token == null) {
				isForbidden = true;
			} else {
				DeviceAuthentication deviceAuth = (DeviceAuthentication) filterConfig.getServletContext()
						.getAttribute(LabVisionServletContextListener.DEVICE_AUTHENTICATION_ATTR);
				try {
					User user = userService.getUser(token.getUserId(), true);
					isForbidden = !deviceAuth.verifyDeviceToken(
						token,
						user,
						httpRequest
					);
					if (!isForbidden) {
						// set device's user in session
						httpRequest.getSession().setAttribute("user", user);
						
						// renew the device token
						DeviceToken newToken = deviceAuth.createDeviceToken(
								userService.getDevice(token.getDeviceId()),
								user,
								httpRequest
						);
						System.out.println(newToken.toString());
						deviceAuth.addDeviceToken(httpResponse, newToken);
					}
				} catch (InvalidKeyException | NoSuchAlgorithmException
						| SignatureException | KeyStoreException | CertificateException e) {
					Logger.getLogger(this.getClass())
						.error("Exception verifying device signature", e);
					isForbidden = true;
				}
			}
		} else {
			switch(((User)session.getAttribute("user")).getRole()) {
			case STUDENT:
				isForbidden = !(httpRequest.getServletPath().equals("/reportdocs") ||
						httpRequest.getServletPath().equals("/student"));
				break;
			case FACULTY:
				isForbidden = !(httpRequest.getServletPath().equals("/reportdocs") ||
						httpRequest.getServletPath().equals("/faculty"));
				break;
			case ADMIN:
				isForbidden = !(httpRequest.getServletPath().equals("/reportdocs") ||
						httpRequest.getServletPath().equals("/admin"));
				break;
			default:
				isForbidden = true;
			}
		}
		
		if (isForbidden) {
			httpResponse.sendRedirect("/login?redirect=" + 
					URLEncoder.encode(httpRequest.getRequestURI(), StandardCharsets.UTF_8.toString()));
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}
