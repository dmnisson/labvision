package io.github.dmnisson.labvision.entities;

import java.util.Objects;

import javax.persistence.Entity;

@Entity
public class AdminOnly extends LabVisionUser {

	@Override
	public UserRole getRole() {
		return UserRole.ADMIN;
	}

	@Override
	public String getDisplayName() {
		
		if (Objects.nonNull(adminInfo.getFirstName())) {
			if (Objects.nonNull(adminInfo.getLastName())) {
				return adminInfo.getFirstName() + " " + adminInfo.getLastName();
			} else {
				return adminInfo.getFirstName();
			}
		} else if (Objects.nonNull(adminInfo.getLastName())) {
			return adminInfo.getLastName();
		}
		
		return super.getDisplayName();
		
	}
	
}
