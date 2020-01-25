package io.github.dmnisson.labvision.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Instructor extends LabVisionUser {

	@Column
	private String name;
	
	@Column
	private String email;
	
	@ManyToMany( targetEntity=CourseClass.class )
	private Set<CourseClass> courseClasses = new HashSet<>();
	
	@ManyToMany( targetEntity=Experiment.class )
	private Set<Experiment> experiments = new HashSet<>();
	
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

	@Override
	public String getDisplayName() {
		return (Objects.isNull(name) || name.isEmpty())
				? super.getDisplayName() : name;
	}
}
