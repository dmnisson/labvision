package labvision.entities;

import java.util.List;
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
	
	@ManyToMany( mappedBy="instructors" )
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

	public Set<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(Set<Experiment> experiments) {
		this.experiments = experiments;
	}

}
