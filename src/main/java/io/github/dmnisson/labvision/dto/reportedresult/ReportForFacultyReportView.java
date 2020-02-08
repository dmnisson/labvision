package io.github.dmnisson.labvision.dto.reportedresult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.github.dmnisson.labvision.entities.FileType;
import io.github.dmnisson.labvision.entities.ReportDocumentType;

public class ReportForFacultyReportView extends ReportForReportView {
	private final int studentId;
	private final String studentName;
	
	public ReportForFacultyReportView(int id, int experimentId, String name, FileType documentFileType,
			ReportDocumentType documentType, String filename, LocalDateTime documentLastUpdated, BigDecimal score,
			int studentId, String studentName) {
		super(id, experimentId, name, documentFileType, documentType, filename, documentLastUpdated, score);
		this.studentId = studentId;
		this.studentName = studentName;
	}
	
	public int getStudentId() {
		return studentId;
	}
	public String getStudentName() {
		return studentName;
	}
}
