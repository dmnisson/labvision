package labvision.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity( name="Parameter" )
/**
 * A parameter that may affect a measurement outcome.
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
public class Parameter extends Variable<Parameter, ParameterValue> {
	
	@ManyToOne( targetEntity=Measurement.class, fetch=FetchType.LAZY )
	@JoinColumn( name="Measurement_id" )
	private Measurement measurement;
	
	@OneToMany( mappedBy="variable", targetEntity=ParameterValue.class )
	private List<ParameterValue> values = new ArrayList<>();
	
	public Measurement getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Measurement measurement) {
		this.measurement = measurement;
	}

	@Override
	public List<ParameterValue> getValues() {
		return values;
	}

	@Override
	public void setValues(List<ParameterValue> parameterValues) {
		this.values = parameterValues;
	}

	@Override
	public void addValue(ParameterValue value) {
		this.values.add(value);
		value.setVariable(this);
	}
}
