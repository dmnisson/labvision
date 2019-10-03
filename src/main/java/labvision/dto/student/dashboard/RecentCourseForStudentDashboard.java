package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class RecentCourseForStudentDashboard {
	private final int id;
	private final String name;
	private final LocalDateTime mostRecentValueTaken;
	private final LocalDateTime lastUpdated;
	
	public RecentCourseForStudentDashboard(int id, String name, LocalDateTime mostRecentValueTaken, LocalDateTime lastUpdated) {
		this.id = id;
		this.name = name;
		this.mostRecentValueTaken = mostRecentValueTaken;
		this.lastUpdated = lastUpdated;
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

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}
}
