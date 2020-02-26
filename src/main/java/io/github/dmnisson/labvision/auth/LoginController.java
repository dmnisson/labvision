package io.github.dmnisson.labvision.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
	
	@GetMapping("/login")
	public String loginPage(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			HttpServletRequest request,
			Model model) {
		
		if (error != null) {
			model.addAttribute("error", getErrorCode(request, "SPRING_SECURITY_LAST_EXCEPTION"));
		}
		
		model.addAttribute("logout", logout);
		
		return "login";
	}

	private String getErrorCode(HttpServletRequest request, String key) {
		Exception exception = (Exception) request.getSession().getAttribute(key);
		
		if (exception instanceof BadCredentialsException) {
			return "badcredentials";
		} else if (exception instanceof LockedException) {
			return "locked";
		} else if (exception instanceof DisabledException) {
			return "disabled";
		} else {
			return "badcredentials";
		}
	}
}
