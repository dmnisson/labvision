package io.github.dmnisson.labvision.dto.admin;

import io.github.dmnisson.labvision.entities.UserRole;

public class LabVisionUserForAdminTable {
	private final Integer id;
	private final String username;
	private final String displayName;
	private final UserRole userRole;
	private final boolean admin;
	
	public LabVisionUserForAdminTable(Integer id, String username, String displayName, UserRole userRole,
			boolean admin) {
		super();
		this.id = id;
		this.username = username;
		this.displayName = displayName;
		this.userRole = userRole;
		this.admin = admin;
	}

	public Integer getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public boolean isAdmin() {
		return admin;
	}
	
}
