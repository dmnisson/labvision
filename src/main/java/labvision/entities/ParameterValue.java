package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Value of a parameter
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
@Entity
public class ParameterValue extends VariableValue<Parameter, ParameterValue> {
	@ManyToOne(targetEntity = Parameter.class, fetch = FetchType.LAZY )
	@JoinColumn( name="Parameter_id" )
	private Parameter variable;
	
	@ManyToOne(targetEntity = MeasurementValue.class, fetch = FetchType.LAZY )
	@JoinColumn( name="MeasurementValue_id" )
	private MeasurementValue measurementValue;

	@Override
	public Parameter getVariable() {
		return variable;
	}

	@Override
	public void setVariable(Parameter parameter) {
		this.variable = parameter;
	}}
