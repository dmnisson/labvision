package io.github.dmnisson.labvision.dto.student.experiment;

import java.time.LocalDateTime;

public class ExperimentForStudentExperimentTable {

	protected final int id;
	protected final String name;
	private final LocalDateTime lastUpdated;

	public ExperimentForStudentExperimentTable(int id, String name, LocalDateTime lastUpdated) {
		this.id = id;
		this.name = name;
		this.lastUpdated = lastUpdated;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}
}