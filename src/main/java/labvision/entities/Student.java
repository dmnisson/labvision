package labvision.entities;

import java.util.List;
import java.util.Set;

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
public class Student extends User {
	
	@Column
	private String name;
	
	@ManyToMany( mappedBy="students" )
	private Set<CourseClass> courseClasses;
	
	@OneToMany( mappedBy="student", targetEntity=MeasurementValue.class )
	private List<MeasurementValue> measurementValues;

	@OneToOne
	private StudentPreferences studentPreferences;
	
	@ManyToMany
	private List<Experiment> activeExperiments;
	
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

	public List<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(List<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}

	public List<Experiment> getActiveExperiments() {
		return activeExperiments;
	}

	public void setActiveExperiments(List<Experiment> activeExperiments) {
		this.activeExperiments = activeExperiments;
	}

	public StudentPreferences getStudentPreferences() {
		return studentPreferences;
	}

	public void setStudentPreferences(StudentPreferences studentPreferences) {
		this.studentPreferences = studentPreferences;
	}
	
	@Override
	public UserRole getRole() {
		return UserRole.STUDENT;
	}
}
