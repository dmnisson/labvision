package labvision.dto.experiment.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import labvision.entities.FileType;
import labvision.entities.ReportDocumentType;

public class ReportForReportView {
	private final int id;
	private final int experimentId;
	private final String name;
	private final FileType documentFileType;
	private final ReportDocumentType documentType;
	private final String filename;
	private final LocalDateTime documentLastUpdated;
	private final BigDecimal score;
	
	public ReportForReportView(int id, int experimentId, String name, FileType documentFileType, ReportDocumentType documentType,
			String filename, LocalDateTime documentLastUpdated, BigDecimal score) {
		this.id = id;
		this.experimentId = experimentId;
		this.name = name;
		this.documentFileType = documentFileType;
		this.documentType = documentType;
		this.filename = filename;
		this.documentLastUpdated = documentLastUpdated;
		this.score = score;
	}

	public int getId() {
		return id;
	}

	public int getExperimentId() {
		return experimentId;
	}
	
	public String getName() {
		return name;
	}

	public FileType getDocumentFileType() {
		return documentFileType;
	}

	public ReportDocumentType getDocumentType() {
		return documentType;
	}

	public LocalDateTime getDocumentLastUpdated() {
		return documentLastUpdated;
	}

	public BigDecimal getScore() {
		return score;
	}

	public String getFilename() {
		return filename;
	}
}
