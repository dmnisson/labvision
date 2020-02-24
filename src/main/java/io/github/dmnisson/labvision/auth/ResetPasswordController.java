package io.github.dmnisson.labvision.auth;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.DashboardUrlService;
import io.github.dmnisson.labvision.models.NavbarModel;

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
	
	@ModelAttribute
	public void populateModel(Model model) {
		model.addAttribute("navbarModel", buildNavbarModel());
	}
	
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
	
	@GetMapping("/begin/{token}")
	public String beginPasswordResetWithToken(@PathVariable String token, Model model) {
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "updatePasswordWithToken",
						token, null, null)
				.replaceQuery(null)
				.build()
				.toUriString());
		
		model.addAttribute("token", token);
		
		return "resetpassword";
	}
	
	@PostMapping("/set")
	public String updatePassword(
			String username, String password, String newPassword, String confirmNewPassword,
			@AuthenticationPrincipal UserDetails user) {
		String error = changePasswordInternal(username, password, newPassword, confirmNewPassword, user,
				authenticationManager, null);
		
		if (error != null) {
			return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "beginPasswordReset", new Object())
				.replaceQueryParam("error", error)
				.build().toUriString();
		}
		
		return "redirect:" + dashboardUrlService.getDashboardUrl(user.getAuthorities());
	}
	
	@PostMapping("/set/{token}")
	public String updatePasswordWithToken(
			@PathVariable String token, String newPassword, String confirmNewPassword) {
		UserDetails user = userDetailsManager.loadUserByPasswordResetToken(token);
		
		String username = user.getUsername();
		String error = changePasswordInternal(username, "temp_" + username, newPassword, confirmNewPassword,
				user, null, token);
		
		if (error != null) {
			return "redirect:" + MvcUriComponentsBuilder
					.fromMethodName(ResetPasswordController.class, "beginPasswordResetWithToken", token, null)
					.replaceQueryParam("error", error)
					.build().toUriString();
		}
		
		userDetailsManager.clearPasswordResetToken(user.getUsername());
		
		return "redirect:" + dashboardUrlService.getDashboardUrl(user.getAuthorities());
	}
	
	private String changePasswordInternal(String username, String password, String newPassword, String confirmNewPassword,
			UserDetails user, final AuthenticationManager authenticationManagerToSet, String token) {
		String error = null;
		
		if (!user.getUsername().equalsIgnoreCase(username)) {
			error = "unauthorized";
		} else if (!newPassword.equals(confirmNewPassword)) {
			error = "unmatched";
		} else {
			try {
				userDetailsManager.setAuthenticationManager(authenticationManagerToSet);
				userDetailsManager.loadUserByUsername(username);
				if (Objects.isNull(token)) {
					userDetailsManager.changePassword(password, passwordEncoder.encode(newPassword));
				} else {
					userDetailsManager.changePasswordWithToken(token, passwordEncoder.encode(newPassword));
				}
			} catch (AuthenticationException ex) {
				error = "unauthorized";
			}
		}
		return error;
	}
	
	private NavbarModel buildNavbarModel() {
		NavbarModel navbarModel = new NavbarModel();
		navbarModel.setLogoutLink("/logout");
		return navbarModel;
	}
}
