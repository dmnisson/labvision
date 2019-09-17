package labvision.entities;

import java.util.List;

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
	private List<CourseClass> courseClasses;
	
	@ManyToMany( mappedBy="instructors" )
	private List<Experiment> experiments;
	
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

	public List<CourseClass> getCourseClasses() {
		return courseClasses;
	}

	public void setCourseClasses(List<CourseClass> courseClasses) {
		this.courseClasses = courseClasses;
	}

	public List<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}

}
