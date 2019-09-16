package labvision.entities;

import java.net.URL;

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
	
	@OneToOne
	private ReportedResult reportedResult;
	
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
