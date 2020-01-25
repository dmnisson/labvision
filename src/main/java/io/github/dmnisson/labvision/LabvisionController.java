package io.github.dmnisson.labvision;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;

/**
 * Main controller class for LabVision index page
 * @author David Nisson
 *
 */
@Controller
public class LabvisionController {
	@Autowired
	private DashboardUrlService dashboardUrlService;
	
	@GetMapping(value= {"/","/welcome"})
	public String welcomePage(@AuthenticationPrincipal LabVisionUserDetails userDetails, Model model) {
		if (Objects.nonNull(userDetails)) {
			model.addAttribute("user", userDetails.getLabVisionUser());
			String dashboardUrl = dashboardUrlService.getDashboardUrl(userDetails.getAuthorities());
			model.addAttribute("dashboardUrl", dashboardUrl);
		}
		return "welcome";
	}
}
