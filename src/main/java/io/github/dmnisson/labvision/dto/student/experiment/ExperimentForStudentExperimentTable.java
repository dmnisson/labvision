package io.github.dmnisson.labvision.dto.student.experiment;

import java.time.LocalDateTime;

import io.github.dmnisson.labvision.dto.experiment.ExperimentInfo;

public class ExperimentForStudentExperimentTable extends ExperimentInfo {

	private final LocalDateTime lastUpdated;

	public ExperimentForStudentExperimentTable(int id, String name, LocalDateTime lastUpdated) {
		this(id, name, null, lastUpdated);
	}

	public ExperimentForStudentExperimentTable(int id, String name, String courseName, LocalDateTime lastUpdated) {
		super(id, name, courseName);
		this.lastUpdated = lastUpdated;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}
}