package labvision.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity( name="MeasurementValue" )
public class MeasurementValue extends VariableValue<Measurement, MeasurementValue> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@ManyToOne( targetEntity = Measurement.class, fetch=FetchType.LAZY )
	@JoinColumn( name="Measurement_id" )
	private Measurement variable;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Student_id" )
	private Student student;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="CourseClass_id" )
	private CourseClass courseClass;
	
	private LocalDateTime taken = LocalDateTime.now();
	
	@OneToMany( mappedBy="measurementValue", targetEntity=ParameterValue.class )
	private List<ParameterValue> parameterValues = new ArrayList<>();

	@Override
	public Measurement getVariable() {
		return variable;
	}
	
	@Override
	public void setVariable(Measurement variable) {
		if (!Objects.isNull(this.variable)) {
			 // remove from old measurement's statistics
			this.variable.computeStatistics();
		}
		this.variable = variable;
		
		 // add to new measurement's statistics
		this.variable.computeStatistics();
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

	public List<ParameterValue> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List<ParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public void addParameterValue(ParameterValue parameterValue) {
		this.parameterValues.add(parameterValue);
		parameterValue.setMeasurementValue(this);
	}
	
	public LocalDateTime getTaken() {
		return taken;
	}

	public void setTaken(LocalDateTime taken) {
		this.taken = taken;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
