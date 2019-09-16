package labvision.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
public class Parameter extends Variable<Parameter, ParameterValue> {
	
	@ManyToOne( targetEntity=Measurement.class, fetch=FetchType.LAZY )
	@JoinColumn( name="Measurement_id" )
	private Measurement measurement;
	
	@OneToMany( mappedBy="variable", targetEntity=ParameterValue.class )
	private List<ParameterValue> values;

	@Enumerated(EnumType.STRING)
	private QuantityTypeId quantityTypeId;
	
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

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}

	public void setQuantityTypeId(QuantityTypeId quantityTypeId) {
		this.quantityTypeId = quantityTypeId;
	}
}
