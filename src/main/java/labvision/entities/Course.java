package labvision.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Course implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	private String name;
	
	@OneToMany( mappedBy="course", targetEntity=CourseClass.class )
	private List<CourseClass> courseClasses;

	@OneToMany( mappedBy="course", targetEntity=Experiment.class )
	private List<Experiment> experiments;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
