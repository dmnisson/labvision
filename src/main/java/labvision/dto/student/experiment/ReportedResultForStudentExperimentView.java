package labvision.dto.student.experiment;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReportedResultForStudentExperimentView {
	private final int id;
	private final String reportDocumentFilename;
	private final LocalDateTime added;
	
	public ReportedResultForStudentExperimentView(int reportedResultId, String reportDocumentFilename, LocalDateTime added) {
		this.id = reportedResultId;
		this.reportDocumentFilename = reportDocumentFilename;
		this.added = added;
	}

	public int getReportedResultId() {
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
		if (Objects.isNull(reportDocumentFilename)) {
			return String.format("Report %d", id);
		} else {
			return reportDocumentFilename;
		}
	}
}
