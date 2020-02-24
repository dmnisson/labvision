package io.github.dmnisson.labvision.auth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.UserRole;
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
	
	@Autowired
	private LabVisionAuthConfig labVisionAuthConfig;
	
	@Autowired
	private LabVisionPasswordBlacklist labVisionPasswordBlacklist;
	
	@ModelAttribute
	public void populateModel(Model model) {
		model.addAttribute("navbarModel", buildNavbarModel());
		model.addAttribute("minPasswordLength", labVisionAuthConfig.getMinPasswordLength());
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
			@AuthenticationPrincipal LabVisionUserDetails user) {
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
		LabVisionUserDetails user = (LabVisionUserDetails) userDetailsManager.loadUserByPasswordResetToken(token);
		
		String username = user.getUsername();
		String error = changePasswordInternal(username, token, newPassword, confirmNewPassword,
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
			LabVisionUserDetails user, final AuthenticationManager authenticationManagerToSet, String token) {
		String error = null;
		
		if (!user.getUsername().equalsIgnoreCase(username)) {
			error = "unauthorized";
		} else if ((error = validatePassword(user.getLabVisionUser(), newPassword)) != null) {
			// do nothing else
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
	
	private String validatePassword(LabVisionUser user, String newPassword) {
		HashSet<String> userSpecificBlacklistValues = new HashSet<>();
		
		userSpecificBlacklistValues.add(user.getUsername());
		userSpecificBlacklistValues.addAll(Arrays.asList(user.getDisplayName().split("\\s+")));
		
		if (user.getAdminInfo() != null) {
			userSpecificBlacklistValues.add(user.getAdminInfo().getEmail());
			userSpecificBlacklistValues.add(user.getAdminInfo().getPhone());
		}
		
		if (user.getRole().equals(UserRole.FACULTY)) {
			userSpecificBlacklistValues.add(((Instructor) user).getEmail());
		}
		
		if (newPassword.length() < labVisionAuthConfig.getMinPasswordLength()) {
			return "tooshort";
		} else if (labVisionPasswordBlacklist.isBlacklisted(newPassword, userSpecificBlacklistValues)) {
			return "blacklisted";
		}
		
		return null;
	}

	private NavbarModel buildNavbarModel() {
		NavbarModel navbarModel = new NavbarModel();
		navbarModel.setLogoutLink("/logout");
		return navbarModel;
	}
}
