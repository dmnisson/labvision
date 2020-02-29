package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Student;

public class TestEntryPage extends LabvisionApplicationTests {
	
	@Autowired
	private LabvisionController labvisionController;
	
	@MockBean
	private DashboardUrlService dashboardUrlService;
	
	@Test
	public void welcomePage_ShouldAddUserAndDashboardUrlToModel() throws Exception {
		final Student student = new Student();
		student.setUsername("testuser1");
		
		UserDetails userDetails = User
				.withUsername("testuser1")
				.password("testpassword")
				.authorities("STUDENT")
				.build();
		
		LabVisionUserDetails labVisionUserDetails = new LabVisionUserDetails(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.isEnabled(),
				userDetails.isAccountNonExpired(),
				userDetails.isCredentialsNonExpired(),
				userDetails.isAccountNonLocked(),
				userDetails.getAuthorities(),
				student
				);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		final String dashboardUrl = "/student/dashboard";
		when(dashboardUrlService.getDashboardUrl(
				Mockito.<Collection<? extends GrantedAuthority>>any()
				))
			.thenReturn(dashboardUrl);
		
		String view = labvisionController.welcomePage(labVisionUserDetails, model);
		
		assertEquals("welcome", view);
		assertEquals(student.getDisplayName(), 
				((LabVisionUser) model.getAttribute("user")).getDisplayName());
		assertEquals(dashboardUrl, model.getAttribute("dashboardUrl"));
	}
}
