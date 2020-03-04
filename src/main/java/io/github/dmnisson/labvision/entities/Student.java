package io.github.dmnisson.labvision.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Stores information about a student user including course classes and measurements
 * taken
 * @author davidnisson
 *
 */
@Entity( name = "Student" )
public class Student extends LabVisionUser {
	
	@Column
	private String name;
	
	@ManyToMany( targetEntity=CourseClass.class )
	private Set<CourseClass> courseClasses = new HashSet<>();
	
	@OneToMany( mappedBy="student", targetEntity=MeasurementValue.class )
	private List<MeasurementValue> measurementValues = new ArrayList<>();

	@OneToMany( mappedBy="student", targetEntity=ReportedResult.class )
	private List<ReportedResult> reportedResults = new ArrayList<>();
	
	@OneToOne( cascade = CascadeType.ALL )
	private StudentPreferences studentPreferences;
	
	@ManyToMany
	private List<Experiment> activeExperiments = new ArrayList<>();
	
	public Student() {
		super();
	}
	
	/**
	 * Creates a new student with the given username and password
	 * @param name name of student
	 * @param username username
	 * @throws NoSuchAlgorithmException
	 */
	public Student(String name, String username) {
		super(username);
		setName(name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<CourseClass> getCourseClasses() {
		return courseClasses;
	}

	public void setCourseClasses(Set<CourseClass> courseClasses) {
		this.courseClasses = courseClasses;
	}
	
	public void addCourseClass(CourseClass courseClass) {
		courseClasses.add(courseClass);
		courseClass.getStudents().add(this);
	}
	
	public void removeCourseClass(CourseClass courseClass) {
		courseClasses.remove(courseClass);
		courseClass.getStudents().remove(this);
	}

	public List<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(List<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public void addMeasurementValue(MeasurementValue measurementValue) {
		measurementValues.add(measurementValue);
		measurementValue.setStudent(this);
	}
	
	public List<Experiment> getActiveExperiments() {
		return activeExperiments;
	}

	public void setActiveExperiments(List<Experiment> activeExperiments) {
		this.activeExperiments = activeExperiments;
	}

	public void addActiveExperiment(Experiment activeExperiment) {
		this.activeExperiments.add(activeExperiment);
		activeExperiment.getActiveStudents().add(this);
	}
	
	public void removeActiveExperiment(Experiment activeExperiment) {
		this.activeExperiments.removeIf(
				e -> !Objects.isNull(e) && e.equals(activeExperiment));
	}
	
	public StudentPreferences getStudentPreferences() {
		return studentPreferences;
	}

	public void setStudentPreferences(StudentPreferences studentPreferences) {
		this.studentPreferences = studentPreferences;
		studentPreferences.setStudent(this);
	}
	
	@Override
	public UserRole getRole() {
		return UserRole.STUDENT;
	}

	@Override
	public String getDisplayName() {
		return (Objects.isNull(name) || name.isEmpty())
				? super.getDisplayName() : name;
	}

	public List<ReportedResult> getReportedResults() {
		return reportedResults;
	}

	public void setReportedResults(List<ReportedResult> reportedResults) {
		this.reportedResults = reportedResults;
	}
	
	public void addReportedResult(ReportedResult reportedResult) {
		this.reportedResults.add(reportedResult);
		reportedResult.setStudent(this);
	}
}
