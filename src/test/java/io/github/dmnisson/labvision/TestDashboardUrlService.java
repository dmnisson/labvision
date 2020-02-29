package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class TestDashboardUrlService extends LabvisionApplicationTests {
	
	@Autowired
	private DashboardUrlService dashboardUrlService;
	
	@Test
	public void getDashboardUrl_String_ShouldReturnCorrectDashboardPaths() {
		assertEquals("/student/dashboard", dashboardUrlService.getDashboardUrl("ROLE_STUDENT"));
		assertEquals("/faculty/dashboard", dashboardUrlService.getDashboardUrl("ROLE_FACULTY"));
		assertEquals("/admin/dashboard", dashboardUrlService.getDashboardUrl("ROLE_ADMIN"));
	}
	
	@Test
	public void getDashboardUrl_Collection_ShouldReturnCorrectDashboardPaths() {
		
		assertEquals("/student/dashboard", dashboardUrlService.getDashboardUrl(
				Stream.of("ROLE_STUDENT", "ROLE_ADMIN")
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList())
				));
		
		assertEquals("/faculty/dashboard", dashboardUrlService.getDashboardUrl(
				Stream.of("ROLE_FACULTY", "ROLE_ADMIN")
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList())
				));
		
		assertEquals("/admin/dashboard", dashboardUrlService.getDashboardUrl(
				Stream.of("ROLE_ADMIN")
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList())
				));
	}
	
}
