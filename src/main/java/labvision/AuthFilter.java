package labvision;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

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
		
		LabVisionConfig config = (LabVisionConfig) filterConfig.getServletContext()
				.getAttribute(LabVisionServletContextListener.CONFIG_ATTR);
		LabVisionDataAccess dataAccess = (LabVisionDataAccess) filterConfig.getServletContext()
				.getAttribute(LabVisionServletContextListener.DATA_ACCESS_ATTR);
		
		boolean isForbidden = false;
		if (session == null || session.getAttribute("user") == null) {
			DeviceToken token = DeviceAuthentication.getDeviceToken(httpRequest);
			if (token == null) {
				isForbidden = true;
			} else {
				DeviceAuthentication deviceAuth = new DeviceAuthentication(config, dataAccess);
				try {
					isForbidden = !deviceAuth.verifyDeviceToken(token,
							dataAccess.getUser(token.getUserId()),
							httpRequest);
				} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
						| SignatureException e) {
					Logger.getLogger(this.getClass())
						.error("Exception verifying device signature", e);
					isForbidden = true;
				}
			}
		} else {
			switch(((User)session.getAttribute("user")).getRole()) {
			case STUDENT:
				isForbidden = !httpRequest.getServletPath().equals("/student");
				break;
			case FACULTY:
				isForbidden = !httpRequest.getServletPath().equals("/faculty");
				break;
			case ADMIN:
				isForbidden = !httpRequest.getServletPath().equals("/admin");
				break;
			default:
				isForbidden = true;
			}
		}
		
		if (isForbidden) {
			httpResponse.sendRedirect("/login");
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}