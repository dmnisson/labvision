package io.github.dmnisson.labvision.dto.student.experiment;

import java.time.LocalDateTime;

import io.github.dmnisson.labvision.dto.experiment.ExperimentInfo;

public class ExperimentForStudentDashboard extends ExperimentInfo {

	protected final LocalDateTime lastUpdated;
	protected final LocalDateTime reportDueDate;

	public ExperimentForStudentDashboard(
			int id,
			String name,
			LocalDateTime lastUpdated,
			LocalDateTime reportDueDate) {
		this(id, name, null, lastUpdated, reportDueDate);
	}
	
	public ExperimentForStudentDashboard(
			int id,
			String name,
			String courseName,
			LocalDateTime lastUpdated,
			LocalDateTime reportDueDate) {
		super(id, name, courseName);
		this.lastUpdated = lastUpdated;
		this.reportDueDate = reportDueDate;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public LocalDateTime getReportDueDate() {
		return reportDueDate;
	}
}