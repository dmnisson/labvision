package io.github.dmnisson.labvision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;

@Controller
@RequestMapping("/resetpassword")
public class ResetPasswordController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private LabVisionUserDetailsManager userDetailsManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private DashboardUrlService dashboardUrlService;
	
	@GetMapping("/begin")
	public String beginPasswordReset(Model model) {
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "updatePassword",
						null, null, null, null, null)
				.replaceQuery(null)
				.build()
				.toUriString());
		
		return "resetpassword";
	}
	
	@PostMapping("/set")
	public String updatePassword(
			String username, String password, String newPassword, String confirmNewPassword,
			@AuthenticationPrincipal UserDetails user) {
		String error = null;
		
		if (!user.getUsername().equalsIgnoreCase(username)) {
			error = "unauthorized";
		} else if (!newPassword.equals(confirmNewPassword)) {
			error = "unmatched";
		} else {
			try {
				userDetailsManager.setAuthenticationManager(authenticationManager);
				userDetailsManager.loadUserByUsername(username);
				userDetailsManager.changePassword(password, passwordEncoder.encode(newPassword));
			} catch (AuthenticationException ex) {
				error = "unauthorized";
			}
		}
		
		if (error != null) {
			return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "beginPasswordReset", new Object())
				.replaceQueryParam("error", error)
				.build().toUriString();
		}
		
		return "redirect:" + dashboardUrlService.getDashboardUrl(user.getAuthorities());
	}
}
