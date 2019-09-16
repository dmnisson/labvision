package labvision.entities;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Result implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	 
	private String name;
	  
	@OneToMany
	@JoinColumn( name="Result_id" )
	private List<MeasurementValue> measurementValues;
	
	@Embedded
	private ResultComputation computation;
	
	@Embedded
	private PersistableAmount resultValue;

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
	
	public List<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}
	
	public void setMeasurementValues(List<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public ResultComputation getComputation() {
		return computation;
	}
	
	public void setComputation(ResultComputation computation) {
		this.computation = computation;
	}
	
	public PersistableAmount getResultValue() {
		return resultValue;
	}
	
	public void setResultValue(PersistableAmount resultValue) {
		this.resultValue = resultValue;
	}
}
