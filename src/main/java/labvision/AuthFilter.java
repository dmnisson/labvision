package labvision;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.entities.User;

public class AuthFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession session = httpRequest.getSession(false);
		
		boolean isForbidden = false;
		if (session == null || session.getAttribute("user") == null) {
			isForbidden = true;
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
