package io.github.dmnisson.labvision.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;

@Entity
public class CourseClass implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Integer id;
	
	private String name;

	@ManyToOne( optional=false, fetch=FetchType.LAZY )
	@JoinColumn( name="Course_id" )
	private Course course;
	
	@ManyToMany( mappedBy="courseClasses", targetEntity=Student.class )
	private Set<Student> students = new HashSet<>();
	
	@ManyToMany( mappedBy="courseClasses", targetEntity=Instructor.class )
	private Set<Instructor> instructors = new HashSet<>();
	
	@OneToMany( mappedBy="courseClass", targetEntity=MeasurementValue.class )
	private List<MeasurementValue> measurementValues = new ArrayList<>();
	
	public Integer getId() {
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

	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}
	
	public void addStudent(Student student) {
		this.students.add(student);
		student.getCourseClasses().add(this);
	}
	
	public void removeStudent(Student student) {
		this.students.remove(student);
		student.getCourseClasses().remove(this);
	}

	public List<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(List<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public void addMeasurementValue(MeasurementValue measurementValue) {
		this.measurementValues.add(measurementValue);
		measurementValue.setCourseClass(this);
	}

	public Set<Instructor> getInstructors() {
		return instructors;
	}

	public void setInstructors(Set<Instructor> instructors) {
		this.instructors = instructors;
	}
	
	public void addInstructor(Instructor instructor) {
		this.instructors.add(instructor);
		instructor.getCourseClasses().add(this);
	}
	
	public void removeInstructor(Instructor instructor) {
		this.instructors.remove(instructor);
		instructor.getCourseClasses().remove(this);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseClass other = (CourseClass) obj;
		if (id == null)
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}
}