package labvision;

import java.util.List;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
/**
 * A parameter that may affect a measurement outcome.
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
public class Parameter<M extends Quantity<M>, P extends Quantity<P>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	private String name;
	
	private String quantityClassName;
	
	private Class<P> quantityClass;
	
	@ManyToOne
	private Measurement<M> measurement;
	
	@OneToMany( targetEntity=ParameterValue.class )
	private List<ParameterValue<M, P>> parameterValues;

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

	public Measurement<M> getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Measurement<M> measurement) {
		this.measurement = measurement;
	}

	public List<ParameterValue<M, P>> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List<ParameterValue<M, P>> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public String getQuantityClassName() {
		return quantityClassName;
	}

	public void setQuantityClassName(String quantityClassName) throws ClassNotFoundException {
		if (Class.forName("javax.measure.quantity." + quantityClassName) != quantityClass) {
			throw new ClassCastException();
		}
		this.quantityClassName = quantityClassName;
	}

	public Class<P> getQuantityClass() {
		return quantityClass;
	}

	public void setQuantityClass(Class<P> quantityClass) {
		this.quantityClass = quantityClass;
		try {
			this.setQuantityClassName(quantityClass.getSimpleName());
		}
		catch (ClassNotFoundException | ClassCastException e) {}
	}
}
