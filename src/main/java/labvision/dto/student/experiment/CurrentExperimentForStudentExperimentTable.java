package labvision.dto.student.experiment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrentExperimentForStudentExperimentTable {
	private final int id;
	private final String name;
	private final LocalDateTime reportDueDate;
	private final LocalDateTime lastReportUpdated;
	private final BigDecimal totalReportScore;
	
	public CurrentExperimentForStudentExperimentTable(int id, String name, LocalDateTime reportDueDate,
			LocalDateTime lastReportUpdated, BigDecimal totalReportScore) {
		this.id = id;
		this.name = name;
		this.reportDueDate = reportDueDate;
		this.lastReportUpdated = lastReportUpdated;
		this.totalReportScore = totalReportScore;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getReportDueDate() {
		return reportDueDate;
	}

	public LocalDateTime getLastReportUpdated() {
		return lastReportUpdated;
	}

	public BigDecimal getTotalReportScore() {
		return totalReportScore;
	}
}
