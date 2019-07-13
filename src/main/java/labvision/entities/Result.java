package labvision.entities;

import java.util.List;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Result<Q extends Quantity<Q>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	 
	private String name;
	  
	private String quantityClassName;
	  
	private Class<Q> quantityClass;
	  
	@OneToMany
	private List<MeasurementValue<?>> measurementValues;
	  
	private ResultComputation computation;
	  
	private Amount<Q> resultValue;

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
	
	public String getQuantityClassName() {
		return quantityClassName;
	}
	
	public void setQuantityClassName(String quantityClassName) {
		this.quantityClassName = quantityClassName;
	}
	
	public Class<Q> getQuantityClass() {
		return quantityClass;
	}
	
	public void setQuantityClass(Class<Q> quantityClass) {
		this.quantityClass = quantityClass;
	}
	
	public List<MeasurementValue<?>> getMeasurementValues() {
		return measurementValues;
	}
	
	public void setMeasurementValues(List<MeasurementValue<?>> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public ResultComputation getComputation() {
		return computation;
	}
	
	public void setComputation(ResultComputation computation) {
		this.computation = computation;
	}
	
	public Amount<Q> getResultValue() {
		return resultValue;
	}
	
	public void setResultValue(Amount<Q> resultValue) {
		this.resultValue = resultValue;
	}
}
