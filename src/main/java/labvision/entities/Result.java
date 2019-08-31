package labvision.entities;

import java.util.List;

import javax.measure.Quantity;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@Entity
public class Result<Q extends Quantity<Q>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	 
	private String name;
	  
	private String quantityClassName;
	
	@Transient
	private Class<Q> quantityClass;
	  
	@OneToMany
	@JoinColumn( name="Result_id" )
	private List<MeasurementValue<?>> measurementValues;
	
	@Embedded
	private ResultComputation computation;
	
	@Embedded
	@Type( type = "labvision.entities.types.AmountType" )
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
	
	public Class<Q> getQuantityClass() {
		return quantityClass;
	}
	
	public void setQuantityClassName(String quantityClassName) throws ClassNotFoundException {
		if (Class.forName("javax.measure.quantity." + quantityClassName) != quantityClass) {
			throw new ClassCastException();
		}
		this.quantityClassName = quantityClassName;
	}
	
	public void setQuantityClass(Class<Q> quantityClass) {
		this.quantityClass = quantityClass;
		try {
			this.setQuantityClassName(quantityClass.getSimpleName());
		}
		catch (ClassNotFoundException | ClassCastException e) {}
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
