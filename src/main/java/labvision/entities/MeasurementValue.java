package labvision.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MeasurementValue<Q extends Quantity<Q>> {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@ManyToOne
	private Measurement<Q> measurement;
	
	@ManyToOne
	private Student student;
	
	@ManyToOne
	private CourseClass courseClass;
	
	private double value;
	
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime taken;
	
	@OneToMany( targetEntity=ParameterValue.class )
	private List<ParameterValue<Q, ?>> parameterValues;

	public Measurement<Q> getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Measurement<Q> measurement) {
		this.measurement = measurement;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public void setCourseClass(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public List<ParameterValue<Q, ?>> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List<ParameterValue<Q, ?>> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public LocalDateTime getTaken() {
		return taken;
	}

	public void setTaken(LocalDateTime taken) {
		this.taken = taken;
	}
}
