package io.github.dmnisson.labvision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

public class DashboardUrlService {
	private HashMap<String, String> dashboardUrls = new HashMap<>();
	
	{
		dashboardUrls.put("ROLE_STUDENT", "/student/dashboard");
	}
	
	public String getDashboardUrl(String role) {
		return dashboardUrls.get(role);
	}
	
	public String getDashboardUrl(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
				.map(auth -> getDashboardUrl(auth.getAuthority()))
				.filter(Objects::nonNull)
				.findAny().orElseThrow(() -> new IllegalStateException("no dashboard url found"));
	}
}
