package labvision.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Instructor extends User {

	@Column
	private String name;
	
	@Column
	private String email;
	
	@ManyToMany( targetEntity=CourseClass.class )
	private Set<CourseClass> courseClasses;
	
	@ManyToMany( targetEntity=Experiment.class )
	private Set<Experiment> experiments;
	
	@Override
	public UserRole getRole() {
		return UserRole.FACULTY;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<CourseClass> getCourseClasses() {
		return courseClasses;
	}

	public void setCourseClasses(Set<CourseClass> courseClasses) {
		this.courseClasses = courseClasses;
	}
	
	public void addCourseClass(CourseClass courseClass) {
		this.courseClasses.add(courseClass);
		courseClass.getInstructors().add(this);
	}
	
	public void removeCourseClass(CourseClass courseClass) {
		this.courseClasses.remove(courseClass);
		courseClass.getInstructors().remove(this);
	}

	public Set<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(Set<Experiment> experiments) {
		this.experiments = experiments;
	}
	
	public void addExperiment(Experiment experiment) {
		this.experiments.add(experiment);
		experiment.getInstructors().add(this);
	}

}
