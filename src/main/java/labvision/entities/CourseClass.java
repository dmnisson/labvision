package labvision;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;

@Entity
public class CourseClass {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	private String name;

	@ManyToOne
	private Course course;
	
	@ManyToMany( targetEntity=Student.class )
	private List<Student> students;
	
	@OneToMany( targetEntity=MeasurementValue.class )
	private List<MeasurementValue<?>> measurementValues;
	
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

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public List<MeasurementValue<?>> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(List<MeasurementValue<?>> measurementValues) {
		this.measurementValues = measurementValues;
	}
}