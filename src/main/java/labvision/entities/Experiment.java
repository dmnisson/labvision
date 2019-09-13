package labvision.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Experiment {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	private String name;
	
	private String description;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Course_id" )
	private Course course;
	
	@OneToMany( mappedBy="experiment", targetEntity=Measurement.class )
	private List<Measurement<?>> measurements;
	
	@OneToMany
	@JoinColumn( name="accepted_experiment_id" )
	private List<Result<?>> acceptedResults;
	
	@OneToMany
	@JoinColumn( name="obtaining_experiment_id" )
	private List<Result<?>> obtainedResults;
	
	@OneToMany( mappedBy="experiment", targetEntity=ReportedResult.class )
	private List<ReportedResult> reportedResults;
	
	@Temporal(TemporalType.DATE)
	private Date reportDueDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<Measurement<?>> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<Measurement<?>> measurements) {
		this.measurements = measurements;
	}

	public List<Result<?>> getAcceptedResults() {
		return acceptedResults;
	}

	public void setAcceptedResults(List<Result<?>> acceptedResults) {
		this.acceptedResults = acceptedResults;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Result<?>> getObtainedResults() {
		return obtainedResults;
	}

	public void setObtainedResults(List<Result<?>> obtainedResults) {
		this.obtainedResults = obtainedResults;
	}

	public Date getReportDueDate() {
		return reportDueDate;
	}

	public void setReportDueDate(Date reportDueDate) {
		this.reportDueDate = reportDueDate;
	}

	public List<ReportedResult> getReportedResults() {
		return reportedResults;
	}

	public void setReportedResults(List<ReportedResult> reportedResults) {
		this.reportedResults = reportedResults;
	}
}
