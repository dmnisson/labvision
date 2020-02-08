package io.github.dmnisson.labvision.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.github.dmnisson.labvision.entities.LabVisionUser;

public class LabVisionUserDetails extends User {

	private static final long serialVersionUID = 1L;
	
	private final LabVisionUser labVisionUser;
	
	public LabVisionUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, LabVisionUser labVisionUser) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.labVisionUser = labVisionUser;
	}

	public LabVisionUser getLabVisionUser() {
		return labVisionUser;
	}
}
