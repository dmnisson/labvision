package labvision.dto.student.experiment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReportedResultForStudentExperimentView {
	private final int id;
	private final String name;
	private final String reportDocumentFilename;
	private final LocalDateTime added;
	private final BigDecimal score;
	
	public ReportedResultForStudentExperimentView(int reportedResultId, String name, String reportDocumentFilename, LocalDateTime added, 
			BigDecimal score) {
		this.id = reportedResultId;
		this.name = name;
		this.reportDocumentFilename = reportDocumentFilename;
		this.added = added;
		this.score = score;
	}

	public int getId() {
		return id;
	}

	public String getReportDocumentFilename() {
		return reportDocumentFilename;
	}
	
	public LocalDateTime getAdded() {
		return added;
	}
	
	/**
	 * String to display report in student experiment view
	 * @return the string
	 */
	public String getReportDisplay() {
		if (Objects.nonNull(name) && !name.isEmpty()) {
			return name;
		} else if (Objects.nonNull(reportDocumentFilename) &&
				!reportDocumentFilename.isEmpty()) {
			return reportDocumentFilename;
		} else {
			return String.format("Report %d", id);
		}
	}

	public BigDecimal getScore() {
		return score;
	}
}
