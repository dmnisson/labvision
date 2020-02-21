package io.github.dmnisson.labvision.dto.admin;

public class LabVisionUserInfo {

	private final Integer id;
	private final String username;
	private final String displayName;
	
	public LabVisionUserInfo(Integer id, String username, String displayName) {
		super();
		this.id = id;
		this.username = username;
		this.displayName = displayName;
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
	
}
