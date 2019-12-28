package labvision.entities;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

/**
 * A report of experimental results by a student
 * @author davidnisson
 *
 */
@Entity
public class ReportedResult implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Integer id;
	
	private String name;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Student_id" )
	private Student student;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Experiment_id" )
	private Experiment experiment;
	
	@OneToMany
	@JoinColumn( name="ReportedResult_id" )
	private List<Result> results = new ArrayList<>();

	@CreationTimestamp
	private LocalDateTime added;
	
	@OneToOne( cascade = CascadeType.ALL )
	private ReportDocument reportDocument;
	
	@Column
	private BigDecimal score;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	public void addResult(Result result) {
		this.results.add(result);
	}

	public LocalDateTime getAdded() {
		return added;
	}

	public void setAdded(LocalDateTime added) {
		this.added = added;
	}

	public ReportDocument getReportDocument() {
		return reportDocument;
	}

	public void setReportDocument(ReportDocument reportDocument) {
		this.reportDocument = reportDocument;
		reportDocument.setReportedResult(this);
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	@Override
	public int hashCode() {
		return 8191;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportedResult other = (ReportedResult) obj;
		if (id == null)
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}

	public ReportDocument makeReportDocument(String filename, FileType fileType, String filesystemPath) {
		FilesystemReportDocument reportDocument = new FilesystemReportDocument();
		reportDocument.setFilename(filename);
		reportDocument.setFileType(fileType);
		reportDocument.setFilesystemPath(filesystemPath);
		this.reportDocument = reportDocument;
		reportDocument.setReportedResult(this);
		return reportDocument;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
