package labvision.entities;

import java.util.List;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import tec.units.ri.quantity.Quantities;

@Entity( name="Measurement" )
public class Measurement extends Variable<Measurement, MeasurementValue> implements LabVisionEntity {
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Experiment_id" )
	private Experiment experiment;
	
	@OneToMany( mappedBy="measurement", targetEntity=Parameter.class )
	private List<Parameter> parameters;
	
	@OneToMany( mappedBy="variable", targetEntity=MeasurementValue.class )
	private List<MeasurementValue> values;

	@Embedded
	private PersistableAmount mean;
	
	@Embedded
	private PersistableAmount sampleStandardDeviation;

	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public PersistableAmount getMean() {
		return mean;
	}

	public PersistableAmount getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}
	
	private <Q extends Quantity<Q>> Amount<Q> helpComputeAverage(Unit<Q> unit) {
		return this.values.stream()
				.map(mv -> mv.getValue().asAmount(unit))
				.reduce(new Amount<>(0, 0, unit), (a1, a2) -> a1.add(a2))
				.divideAmount(this.values.size());
	}
	
	private <Q extends Quantity<Q>, V extends Quantity<V>> Amount<V> helpComputeVariance(Amount<Q> avg, Unit<V> varianceUnit) {
		return this.values.stream()
				.map(mv -> mv.getValue().asAmount(avg.getUnit()).subtract(avg))
				.map(d -> {
					Amount<?> d2 = d.multiply(d);
					UnitConverter converter;
					try {
						converter = d2.getUnit().getConverterToAny(varianceUnit);
					} catch (IncommensurableException e) {
						throw new RuntimeException(e);
					}
					if (!converter.isLinear()) {
						throw new IllegalArgumentException("varianceUnit does not have a linear relationship with d2.unit");
					}
					return new Amount<>(
							converter.convert(d2.getValue()),
							converter.convert(d2.getUncertainty()),
							varianceUnit);
				})
				.reduce(new Amount<V>(0, 0, varianceUnit), (a1, a2) -> a1.add(a2));
	}
	
	private <Q extends Quantity<Q>, V extends Quantity<V>> Amount<Q> helpComputeSampleStandardDeviation(
			Amount<V> variance, Unit<Q> unit) {
		return Amount.fromQuantity(
				Quantities.getQuantity(variance.sqrt()
						.divide(this.values.size() - 1)
						.getValue(), unit), 0);
	}
	
	public void computeStatistics() {
		Unit<?> systemUnit = systemUnit();
		
		if (this.values.size() > 0) {
			Amount<?> avg = helpComputeAverage(systemUnit);
			Unit<?> varianceUnit = avg.getUnit().pow(2);
			Amount<?> variance = helpComputeVariance(avg, varianceUnit);
			Amount<?> ssd = helpComputeSampleStandardDeviation(variance, systemUnit);
			
			this.mean = new PersistableAmount();
			this.mean.setAmountFromQuantity(avg, getQuantityTypeId().getQuantityClass(), 0);

			this.sampleStandardDeviation = new PersistableAmount();
			this.sampleStandardDeviation.setAmountFromQuantity(ssd,
					getQuantityTypeId().getQuantityClass(),
					0);
		}
		else {
			this.mean = null;
			this.sampleStandardDeviation = null;
		}
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	@Override
	public List<MeasurementValue> getValues() {
		return values;
	}
	@Override
	public void setValues(List<MeasurementValue> values) {
		this.values = values;
		values.stream()
		.filter(value -> value.getVariable() != this)
		.forEach(value -> {
			value.setVariable(this);;
		});
		computeStatistics();
	}

}
