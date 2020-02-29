package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.ExtendedModelMap;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.entities.LabVisionUser;

public class TestEntryPage extends LabvisionApplicationTests {
	
	@Autowired
	private LabvisionController labvisionController;
	
	@MockBean
	private DashboardUrlService dashboardUrlService;
	
	@Test
	public void welcomePage_ShouldAddUserAndDashboardUrlToModel() throws Exception {
		final LabVisionUser user = mock(LabVisionUser.class);
		
		final String displayName = "Test User";
		when(user.getDisplayName()).thenReturn(displayName);
		
		final LabVisionUserDetails labVisionUserDetails = mock(LabVisionUserDetails.class);
		
		when(labVisionUserDetails.getLabVisionUser()).thenReturn(user);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		final String dashboardUrl = "/student/dashboard";
		when(dashboardUrlService.getDashboardUrl(
				Mockito.<Collection<? extends GrantedAuthority>>any()
				))
			.thenReturn(dashboardUrl);
		
		String view = labvisionController.welcomePage(labVisionUserDetails, model);
		
		assertEquals("welcome", view);
		assertEquals(displayName, 
				((LabVisionUser) model.getAttribute("user")).getDisplayName());
		assertEquals(dashboardUrl, model.getAttribute("dashboardUrl"));
	}
}
