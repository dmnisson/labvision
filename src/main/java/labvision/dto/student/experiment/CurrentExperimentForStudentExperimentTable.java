package labvision.dto.student.experiment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrentExperimentForStudentExperimentTable extends ExperimentForStudentExperimentTable {
	private final LocalDateTime reportDueDate;
	private final LocalDateTime lastReportUpdated;
	private final BigDecimal totalReportScore;
	
	public CurrentExperimentForStudentExperimentTable(int id, String name, LocalDateTime reportDueDate,
			LocalDateTime lastReportUpdated, Number totalReportScore) {
		super(id, name);
		this.reportDueDate = reportDueDate;
		this.lastReportUpdated = lastReportUpdated;
		this.totalReportScore = new BigDecimal(totalReportScore.toString());
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
