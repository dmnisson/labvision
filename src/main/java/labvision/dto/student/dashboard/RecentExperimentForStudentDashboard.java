package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class RecentExperimentForStudentDashboard extends ExperimentForStudentDashboard {
	private final LocalDateTime mostRecentValueTaken;
	
	public RecentExperimentForStudentDashboard(int id, String name, LocalDateTime mostRecentValueTaken) {
		super(id, name);
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
