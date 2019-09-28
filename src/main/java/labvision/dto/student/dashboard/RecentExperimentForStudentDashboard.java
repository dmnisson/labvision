package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class RecentExperimentForStudentDashboard {
	private final int id;
	private final String name;
	private final LocalDateTime mostRecentValueTaken;
	
	public RecentExperimentForStudentDashboard(int id, String name, LocalDateTime mostRecentValueTaken) {
		this.id = id;
		this.name = name;
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
