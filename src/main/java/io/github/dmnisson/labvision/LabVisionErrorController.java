package io.github.dmnisson.labvision;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;

@Controller
public class LabVisionErrorController implements ErrorController {

	@Autowired
	private DashboardUrlService dashboardUrlService;
	
	@RequestMapping("/error")
	public String handleError(@AuthenticationPrincipal LabVisionUserDetails userDetails, Model model, HttpServletRequest request) {
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		model.addAttribute("dashboardUrl", dashboardUrlService.getDashboardUrl(userDetails.getAuthorities()));
		
		int statusCode = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
		
		if (statusCode == HttpStatus.NOT_FOUND.value()) {
			return "errors/404";
		}
		
		if (statusCode == HttpStatus.FORBIDDEN.value()) {
			return "errors/403";
		}
		
		return "errors/generic";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
