package io.github.dmnisson.labvision.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.github.dmnisson.labvision.repositories.LabVisionUserRepository;

public class LabVisionAuthenticationProvider extends DaoAuthenticationProvider {

	@Autowired
	private LabVisionUserRepository labVisionUserRepository;
	
	@Autowired
	@Qualifier("userDetailsService")
	@Override
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		super.setUserDetailsService(userDetailsService);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		try {
			
			Authentication auth = super.authenticate(authentication);
			
			labVisionUserRepository.resetFailedLogins(authentication.getName());
			
			return auth;
			
		} catch (BadCredentialsException e) {
			((LabVisionUserDetailsManager) getUserDetailsService()).updateFailedLogins(authentication.getName());
			
			throw e;
		}
	}
	
}
