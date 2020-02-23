package io.github.dmnisson.labvision.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import io.github.dmnisson.labvision.entities.LabVisionUser;

public class ForceResetPasswordFilter extends GenericFilterBean {
	
	private static final String RESET_PASSWORD_PATH = "/resetpassword/begin";
	private static final String SET_PASSWORD_PATH = "/resetpassword/set";
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (!(principal instanceof LabVisionUserDetails)) {
			chain.doFilter(request, response);
			return;
		}
		
		LabVisionUserDetails userDetails = (LabVisionUserDetails) principal;
		
		LabVisionUser user = userDetails.getLabVisionUser();
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (user != null && user.isPasswordResetForced() 
				&& !httpRequest.getRequestURI().equals(RESET_PASSWORD_PATH)
				&& !httpRequest.getRequestURI().equals(SET_PASSWORD_PATH)) {
			httpResponse.sendRedirect(RESET_PASSWORD_PATH);
		} else {
			chain.doFilter(request, response);
		}
	}

}
