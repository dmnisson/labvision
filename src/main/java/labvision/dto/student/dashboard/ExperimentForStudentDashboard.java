package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class ExperimentForStudentDashboard {

	protected final String name;
	protected final int id;
	protected final LocalDateTime lastUpdated;

	public ExperimentForStudentDashboard(int id, String name, LocalDateTime lastUpdated) {
		this.name = name;
		this.id = id;
		this.lastUpdated = lastUpdated;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

}