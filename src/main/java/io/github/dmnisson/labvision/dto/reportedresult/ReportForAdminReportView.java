package io.github.dmnisson.labvision.dto.reportedresult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.github.dmnisson.labvision.entities.FileType;
import io.github.dmnisson.labvision.entities.ReportDocumentType;

public class ReportForAdminReportView extends ReportForReportView {

	private final String studentUsername;

	public ReportForAdminReportView(int id, int experimentId, String name, FileType documentFileType,
			ReportDocumentType documentType, String filename, LocalDateTime documentLastUpdated, BigDecimal score,
			String studentUsername) {
		super(id, experimentId, name, documentFileType, documentType, filename, documentLastUpdated, score);
		this.studentUsername = studentUsername;
	}

	public String getStudentUsername() {
		return studentUsername;
	}
	
}
