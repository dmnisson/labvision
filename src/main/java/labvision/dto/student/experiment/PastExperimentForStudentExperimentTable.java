package labvision.dto.student.experiment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PastExperimentForStudentExperimentTable extends ExperimentForStudentExperimentTable {
	private final long reportCount;
	private final LocalDateTime lastReportUpdated;
	private final BigDecimal totalReportScore;
	
	public PastExperimentForStudentExperimentTable(
			int id,
			String name, 
			LocalDateTime lastUpdated,
			long reportCount,
			LocalDateTime lastReportUpdated,
			BigDecimal totalReportScore) {
		super(id, name, lastUpdated);
		this.reportCount = reportCount;
		this.lastReportUpdated = lastReportUpdated;
		this.totalReportScore = totalReportScore;
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
