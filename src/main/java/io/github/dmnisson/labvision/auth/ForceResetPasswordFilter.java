package io.github.dmnisson.labvision.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import io.github.dmnisson.labvision.entities.LabVisionUser;

public class ForceResetPasswordFilter extends GenericFilterBean {
	
	private static final String RESET_PASSWORD_PATH = "/resetpassword/begin";
	private static final String RESET_PASSWORD_PATTERN = "/resetpassword/begin/**";
	private static final String SET_PASSWORD_PATTERN = "/resetpassword/set/**";
	
	private List<AntPathRequestMatcher> bypassAntMatchers = new ArrayList<>();
	
	{
		bypassAntMatchers.addAll(Stream.of(RESET_PASSWORD_PATTERN, SET_PASSWORD_PATTERN)
				.map(AntPathRequestMatcher::new)
				.collect(Collectors.toSet()));
	}
	
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
				&& bypassAntMatchers.stream().noneMatch(matcher -> matcher.matches(httpRequest))) {
			httpResponse.sendRedirect(RESET_PASSWORD_PATH);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void addBypassAntMatchers(String... patterns) {
		bypassAntMatchers.addAll(
				Stream.of(patterns).map(AntPathRequestMatcher::new).collect(Collectors.toSet()));
	}

	public void addBypassAntMatcher(String pattern) {
		bypassAntMatchers.add(new AntPathRequestMatcher(pattern));
		
	}

}
