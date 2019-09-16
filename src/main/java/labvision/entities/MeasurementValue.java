package labvision.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;
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
	
	private PersistableAmount value;
	
	private LocalDateTime taken;
	
	@OneToMany( mappedBy="measurementValue", targetEntity=ParameterValue.class )
	private List<ParameterValue> parameterValues;

	@Override
	public Measurement getVariable() {
		return variable;
	}
	
	@Override
	public void setVariable(Measurement variable) {
		if (this.variable == null) {
			 // remove from old measurement's statistics statistics
			this.variable.computeStatistics();
		}
		this.variable = variable;
		
		 // add to new measurement's statistics
		this.variable.computeStatistics();
	}

	public PersistableAmount getValue() {
		return value;
	}

	public void setValue(PersistableAmount value) {
		setValueHelper(value, variable.systemUnit());
	}
	
	@SuppressWarnings("unchecked")
	private <Q extends Quantity<Q>> void setValueHelper(PersistableAmount value, Unit<Q> unit) {
		this.value.setAmount(value.asAmount(unit), (Class<Q>) variable.getQuantityTypeId().getQuantityClass());
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
