package io.github.dmnisson.labvision.student;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="app.student")
public class StudentPreferencesConfig {
	
	@Min(1)
	@NotNull
	private @NotNull @Min(1) int defaultDashboardMaxCurrentExperiments = 5;
	
	@Min(1)
	@NotNull
	private @NotNull @Min(1) int defaultDashboardMaxRecentExperiments = 5;
	
	@Min(1)
	@NotNull
	private @NotNull @Min(1) int defaultDashboardMaxRecentCourses = 5;

	public int getDefaultMaxCurrentExperiments() {
		return defaultDashboardMaxCurrentExperiments;
	}

	public void setDefaultMaxCurrentExperiments(int defaultMaxCurrentExperiments) {
		this.defaultDashboardMaxCurrentExperiments = defaultMaxCurrentExperiments;
	}

	public int getDefaultMaxRecentExperiments() {
		return defaultDashboardMaxRecentExperiments;
	}

	public void setDefaultMaxRecentExperiments(int defaultMaxRecentExperiments) {
		this.defaultDashboardMaxRecentExperiments = defaultMaxRecentExperiments;
	}

	public @NotNull @Min(1) int getDefaultMaxRecentCourses() {
		return defaultDashboardMaxRecentCourses;
	}

	public void setDefaultMaxRecentCourses(@NotNull @Min(1) int defaultMaxRecentCourses) {
		this.defaultDashboardMaxRecentCourses = defaultMaxRecentCourses;
	}
}