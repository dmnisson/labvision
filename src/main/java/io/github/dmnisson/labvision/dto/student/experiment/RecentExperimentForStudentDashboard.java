package io.github.dmnisson.labvision.dto.student.experiment;

import java.time.LocalDateTime;

public class RecentExperimentForStudentDashboard extends ExperimentForStudentDashboard {
	private final LocalDateTime mostRecentValueTaken;
	
	public RecentExperimentForStudentDashboard(
			int id,
			String name,
			LocalDateTime lastUpdated,
			LocalDateTime mostRecentValueTaken,
			LocalDateTime reportDueDate) {
		super(id, name, lastUpdated, reportDueDate);
		this.mostRecentValueTaken = mostRecentValueTaken;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getMostRecentValueTaken() {
		return mostRecentValueTaken;
	}
}
