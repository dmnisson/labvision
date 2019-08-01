package labvision.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Student {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	private String name;
	
	@ManyToMany
	private List<CourseClass> courseClasses;
	
	@OneToMany
	private List<MeasurementValue<?>> measurementValues;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CourseClass> getCourseClasses() {
		return courseClasses;
	}

	public void setCourseClasses(List<CourseClass> courseClasses) {
		this.courseClasses = courseClasses;
	}

	public List<MeasurementValue<?>> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(List<MeasurementValue<?>> measurementValues) {
		this.measurementValues = measurementValues;
	}
}
