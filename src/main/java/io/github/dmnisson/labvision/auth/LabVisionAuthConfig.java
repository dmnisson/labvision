package io.github.dmnisson.labvision.auth;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="app.auth")
public class LabVisionAuthConfig {
	
	@NotNull @Min(8) private int minPasswordLength = 8;
	private Resource passwordBlacklistFile;
	@NotNull private int minBlacklistedPasswordLength = 3;
	@NotNull @Min(1) @Max(100) private int maxFailedLogins = 25;
	
	public int getMinPasswordLength() {
		return minPasswordLength;
	}
	public void setMinPasswordLength(int minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}
	public Resource getPasswordBlacklistFile() {
		return passwordBlacklistFile;
	}
	public void setPasswordBlacklistFile(Resource passwordBlacklistFile) {
		this.passwordBlacklistFile = passwordBlacklistFile;
	}
	public int getMinBlacklistedPasswordLength() {
		return minBlacklistedPasswordLength;
	}
	public void setMinBlacklistedPasswordLength(int minBlacklistedPasswordLength) {
		this.minBlacklistedPasswordLength = minBlacklistedPasswordLength;
	}
	public int getMaxFailedLogins() {
		return maxFailedLogins;
	}
	public void setMaxFailedLogins(int maxFailedLogins) {
		this.maxFailedLogins = maxFailedLogins;
	}
	
}
