package labvision.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
	
	@OneToMany( mappedBy="course",
			targetEntity=CourseClass.class,
			cascade=CascadeType.ALL )
	private List<CourseClass> courseClasses = new ArrayList<>();

	@OneToMany( mappedBy="course", targetEntity=Experiment.class )
	private List<Experiment> experiments = new ArrayList<>();
	
	public Course() {
		super();
	}
	
	public Course(String name) {
		super();
		setName(name);
	}
	
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

	public void addCourseClass(CourseClass courseClass) {
		this.courseClasses.add(courseClass);
		courseClass.setCourse(this);
	}
	
	/**
	 * Create a new CourseClass and add it to this course
	 * @param name the name of the class
	 * @return the new class
	 */
	public CourseClass addCourseClass(String name) {
		CourseClass courseClass = new CourseClass();
		courseClass.setName(name);
		addCourseClass(courseClass);
		return courseClass;
	}
	
	public List<Experiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<Experiment> experiments) {
		this.experiments = experiments;
	}
	
	public void addExperiment(Experiment experiment) {
		this.experiments.add(experiment);
		experiment.setCourse(this);
	}
	
	/**
	 * Create a new experiment and add it to this course
	 * @param name the name of the experiment
	 * @param description description of the experiment
	 * @param reportDueDate due date for reports
	 */
	public Experiment addExperiment(String name, String description, LocalDateTime reportDueDate) {
		Experiment experiment = new Experiment();
		experiment.setName(name);
		experiment.setDescription(description);
		experiment.setReportDueDate(reportDueDate);
		addExperiment(experiment);
		return experiment;
	}
}
