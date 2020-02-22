package io.github.dmnisson.labvision.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.persistence.CascadeType;
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

import io.github.dmnisson.labvision.ReportStatus;
import tec.units.ri.quantity.QuantityDimension;

@Entity
public class Experiment implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Integer id;
	
	private String name;
	
	@Column(length = 4096)
	private String description;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Course_id" )
	private Course course;
	
	@ManyToMany( mappedBy="activeExperiments", targetEntity=Student.class )
	private Set<Student> activeStudents = new HashSet<>();
	
	@ManyToMany( mappedBy="experiments", targetEntity=Instructor.class )
	private Set<Instructor> instructors = new HashSet<>();
	
	@OneToMany( mappedBy="experiment", 
			targetEntity=Measurement.class,
			cascade=CascadeType.ALL )
	private Set<Measurement> measurements = new HashSet<>();
	
	@OneToMany
	@JoinColumn( name="accepted_experiment_id" )
	private List<Result> acceptedResults = new ArrayList<>();
	
	@OneToMany
	@JoinColumn( name="obtaining_experiment_id" )
	private List<Result> obtainedResults = new ArrayList<>();
	
	@OneToMany( mappedBy="experiment", targetEntity=ReportedResult.class )
	private Set<ReportedResult> reportedResults = new HashSet<>();
	
	private LocalDateTime reportDueDate;

	public Integer getId() {
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

	public Set<Measurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Set<Measurement> measurements) {
		this.measurements = measurements;
	}
	
	public void addMeasurement(Measurement measurement) {
		this.measurements.add(measurement);
		measurement.setExperiment(this);
	}
	
	/**
	 * Creates and adds a new measurement
	 * @param name the name of the measurement
	 * @param quantityType the type of quantity to be measured
	 * @throws IllegalArgumentException if the quantity type is unknown
	 * @return the measurement entity
	 */
	public <Q extends Quantity<Q>> Measurement addMeasurement(String name, Class<Q> quantityType) {
		return addMeasurement(name, quantityType, QuantityDimension.of(quantityType));
	}
	
	/**
	 * Creates and adds a new measurement
	 * @param name the name of the measurement
	 * @param quantityType the type of quantity to be measured
	 * @param dimension the dimension of the measured quantity
	 * @throws IllegalArgumentException if the dimension is unspecified for an unknown 
	 * quantity type or if the dimension is inconsistent with that of the quantity type
	 * @return the measurement entity
	 */
	public <Q extends Quantity<Q>> Measurement addMeasurement(String name, Class<Q> quantityType, Dimension dimension) {
		Measurement measurement = new Measurement();
		measurement.setName(name);
		measurement.updateQuantityType(quantityType, dimension);
		addMeasurement(measurement);
		return measurement;
	}


	public void removeMeasurement(Measurement measurement) {
		measurements.remove(measurement);
		measurement.setExperiment(null);
	}
	
	public List<Result> getAcceptedResults() {
		return acceptedResults;
	}

	public void setAcceptedResults(List<Result> acceptedResults) {
		this.acceptedResults = acceptedResults;
	}
	
	public void addAcceptedResult(Result acceptedResult) {
		this.acceptedResults.add(acceptedResult);
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
	
	public void addObtainedResult(Result obtainedResult) {
		this.obtainedResults.add(obtainedResult);
	}

	public LocalDateTime getReportDueDate() {
		return reportDueDate;
	}

	public void setReportDueDate(LocalDateTime reportDueDate) {
		this.reportDueDate = reportDueDate;
	}

	public Set<ReportedResult> getReportedResults() {
		return reportedResults;
	}
	
	public void setReportedResults(Set<ReportedResult> reportedResults) {
		this.reportedResults = reportedResults;
	}
	
	public void addReportedResult(ReportedResult reportedResult) {
		this.reportedResults.add(reportedResult);
		reportedResult.setExperiment(this);
	}
	
	public ReportedResult addReportedResult(Student student) {
		ReportedResult reportedResult = new ReportedResult();
		student.addReportedResult(reportedResult);
		addReportedResult(reportedResult);
		return reportedResult;
	}
	
	public void removeReportedResult(ReportedResult reportedResult) {
		this.reportedResults.remove(reportedResult);
		reportedResult.setExperiment(null);
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

	public Set<Instructor> getInstructors() {
		return instructors;
	}

	public void setInstructors(Set<Instructor> instructors) {
		this.instructors = instructors;
	}
	
	public void addInstructor(Instructor instructor) {
		this.instructors.add(instructor);
		instructor.getExperiments().add(this);
	}
	
	public void removeInstructor(Instructor instructor) {
		this.instructors.remove(instructor);
		instructor.getExperiments().remove(this);
	}

	@Override
	public int hashCode() {
		return 127;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Experiment other = (Experiment) obj;
		if (id == null)
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<Integer> getStudentIds() {
		return this.getCourse().getCourseClasses().stream()
				.flatMap(cc -> cc.getStudents().stream())
				.map(s -> s.getId())
				.collect(Collectors.toList());
	}

	public Set<Student> getActiveStudents() {
		return activeStudents;
	}
}
