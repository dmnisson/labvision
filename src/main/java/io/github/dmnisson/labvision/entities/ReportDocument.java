package io.github.dmnisson.labvision.entities;

import java.net.URL;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class ReportDocument implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	
	private String filename;
	
	@Enumerated(EnumType.STRING)
	private FileType fileType;
	
	/**
	 * Whether this document is hosted externally by a user-given URL or on the file system
	 */
	@Enumerated(EnumType.STRING)
	private ReportDocumentType documentType;
	
	@OneToOne( mappedBy = "reportDocument" )
	private ReportedResult reportedResult;
	
	@UpdateTimestamp
	private LocalDateTime lastUpdated;
	
	public abstract URL getReportDocumentURL(URL context);

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public ReportedResult getReportedResult() {
		return reportedResult;
	}

	public void setReportedResult(ReportedResult reportedResult) {
		this.reportedResult = reportedResult;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getContentType() {
		String contentType = "application/octet-stream";
		if (getFileType() != null) {
			contentType = getFileType().getContentType();
		}
		return contentType;
	}

	public ReportDocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(ReportDocumentType documentType) {
		this.documentType = documentType;
	}
}
