package labvision.entities;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import labvision.ReportStatus;

@Entity
public class Experiment implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	private String name;
	
	@Column(length = 4096)
	private String description;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Course_id" )
	private Course course;
	
	@ManyToMany( targetEntity=Instructor.class )
	private List<Instructor> instructors;
	
	@OneToMany( mappedBy="experiment", targetEntity=Measurement.class )
	private List<Measurement> measurements;
	
	@OneToMany
	@JoinColumn( name="accepted_experiment_id" )
	private List<Result> acceptedResults;
	
	@OneToMany
	@JoinColumn( name="obtaining_experiment_id" )
	private List<Result> obtainedResults;
	
	@OneToMany( mappedBy="experiment", targetEntity=ReportedResult.class )
	private List<ReportedResult> reportedResults;
	
	private LocalDateTime reportDueDate;

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

	public List<Measurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<Measurement> measurements) {
		this.measurements = measurements;
	}

	public List<Result> getAcceptedResults() {
		return acceptedResults;
	}

	public void setAcceptedResults(List<Result> acceptedResults) {
		this.acceptedResults = acceptedResults;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Result> getObtainedResults() {
		return obtainedResults;
	}

	public void setObtainedResults(List<Result> obtainedResults) {
		this.obtainedResults = obtainedResults;
	}

	public LocalDateTime getReportDueDate() {
		return reportDueDate;
	}

	public void setReportDueDate(LocalDateTime reportDueDate) {
		this.reportDueDate = reportDueDate;
	}

	public List<ReportedResult> getReportedResults() {
		return reportedResults;
	}
	
	public void setReportedResults(List<ReportedResult> reportedResults) {
		this.reportedResults = reportedResults;
	}
	
	public LocalDateTime getLastReportUpdated() {
		return reportedResults.stream()
				.map(rr -> rr.getAdded())
				.max(LocalDateTime::compareTo)
				.orElse(null);
	}
	
	public ReportStatus getReportStatus(Student student) {
		if (isNullOrEmpty(measurements)) {
			return ReportStatus.NOT_SUBMITTED;
		} else if (isNullOrEmpty(obtainedResults)) {
			return ReportStatus.MEASUREMENT_VALUES_REPORTED;
		} else {
			// check for accepted values not obtained
			if (acceptedResults.stream()
					.anyMatch(ar -> obtainedResults.stream()
							.noneMatch(or -> or.getName().equals(ar.getName())))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else if (acceptedResults.stream()
					.anyMatch(ar -> reportedResults.stream()
							.flatMap(rr -> rr.getResults().stream())
							.noneMatch(r -> r.getName().equals(ar.getName())))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else if (reportedResults.stream()
					.anyMatch(rr -> Objects.isNull(rr.getReportDocument()))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else {
				return ReportStatus.COMPLETED;
			}
		}
	}
	
	private static <E> boolean isNullOrEmpty(Collection<E> coll) {
		return Objects.isNull(coll) || coll.isEmpty();
	}
}
