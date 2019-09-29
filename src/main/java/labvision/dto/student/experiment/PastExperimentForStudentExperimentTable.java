package labvision.dto.student.experiment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PastExperimentForStudentExperimentTable {
	private final int id;
	private final String name;
	private final long reportCount;
	private final LocalDateTime lastReportUpdated;
	private final BigDecimal totalReportScore;
	
	public PastExperimentForStudentExperimentTable(int id, String name, long reportCount,
			LocalDateTime lastReportUpdated, BigDecimal totalReportScore) {
		this.id = id;
		this.name = name;
		this.reportCount = reportCount;
		this.lastReportUpdated = lastReportUpdated;
		this.totalReportScore = totalReportScore;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public long getReportCount() {
		return reportCount;
	}
	public LocalDateTime getLastReportUpdated() {
		return lastReportUpdated;
	}
	public BigDecimal getTotalReportScore() {
		return totalReportScore;
	}
}
