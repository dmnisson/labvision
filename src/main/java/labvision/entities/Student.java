package labvision;

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
}