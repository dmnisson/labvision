package labvision.dto.faculty.experiment;

import java.math.BigDecimal;

public class ExperimentForFacultyExperimentTable {
	private final int id;
	private final String name;
	private final long reportedResultsCount;
	private final BigDecimal averageStudentScore;
	
	public ExperimentForFacultyExperimentTable(int id, String name, long reportedResultsCount,
			BigDecimal averageStudentScore) {
		this.id = id;
		this.name = name;
		this.reportedResultsCount = reportedResultsCount;
		this.averageStudentScore = averageStudentScore;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getReportedResultsCount() {
		return reportedResultsCount;
	}

	public BigDecimal getAverageStudentScore() {
		return averageStudentScore;
	}
}
