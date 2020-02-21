package io.github.dmnisson.labvision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

public class DashboardUrlService {
	private HashMap<String, String> dashboardUrls = new HashMap<>();
	
	{
		dashboardUrls.put("ROLE_STUDENT", "/student/dashboard");
		dashboardUrls.put("ROLE_FACULTY", "/faculty/dashboard");
		dashboardUrls.put("ROLE_ADMIN", "/admin/dashboard");
	}
	
	public String getDashboardUrl(String role) {
		return dashboardUrls.get(role);
	}
	
	public String getDashboardUrl(Collection<? extends GrantedAuthority> authorities) {
		if (authorities.stream()
				.filter(auth -> Objects.nonNull(auth.getAuthority()))
				.noneMatch(auth -> !auth.getAuthority().equals("ROLE_ADMIN"))) {
			return getDashboardUrl("ROLE_ADMIN");
		}
		
		// get non-admin dashboard if user is not solely an admin account
		return authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.filter(Objects::nonNull)
				.filter(role -> !role.equals("ROLE_ADMIN"))
				.map(role -> getDashboardUrl(role))
				.findAny().orElseThrow(() -> new IllegalStateException("no dashboard url found"));
	}
}
