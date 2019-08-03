package labvision.entities;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Value of a parameter
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
@Entity
public class ParameterValue<M extends Quantity<M>, P extends Quantity<P>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	@ManyToOne(targetEntity = Parameter.class)
	private Parameter<M, P> parameter;
	
	@ManyToOne(targetEntity = ParameterValue.class)
	private ParameterValue<M, P> parameterValue;
	
	/** Value in SI units */
	private double value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Parameter<M, P> getParameter() {
		return parameter;
	}

	public void setParameter(Parameter<M, P> parameter) {
		this.parameter = parameter;
	}

	public ParameterValue<M, P> getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(ParameterValue<M, P> parameterValue) {
		this.parameterValue = parameterValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
