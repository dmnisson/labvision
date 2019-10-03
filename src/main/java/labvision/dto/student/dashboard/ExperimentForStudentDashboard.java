package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class ExperimentForStudentDashboard {

	protected final String name;
	protected final int id;
	protected final LocalDateTime lastUpdated;
	protected final LocalDateTime reportDueDate;

	public ExperimentForStudentDashboard(
			int id,
			String name,
			LocalDateTime lastUpdated,
			LocalDateTime reportDueDate) {
		this.name = name;
		this.id = id;
		this.lastUpdated = lastUpdated;
		this.reportDueDate = reportDueDate;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public LocalDateTime getReportDueDate() {
		return reportDueDate;
	}
}