package labvision.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import labvision.measure.Amount;
import tec.units.ri.quantity.Quantities;

@Entity( name="Measurement" )
public class Measurement extends Variable<Measurement, MeasurementValue> implements LabVisionEntity {
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="Experiment_id" )
	private Experiment experiment;
	
	@OneToMany(
			mappedBy="measurement",
			targetEntity=Parameter.class,
			cascade=CascadeType.ALL)
	private List<Parameter> parameters = new ArrayList<>();
	
	@OneToMany( mappedBy="variable", targetEntity=MeasurementValue.class )
	private List<MeasurementValue> values = new ArrayList<>();

	@AttributeOverrides({
		@AttributeOverride( name="value", column=@Column( name="mean_value" ) ),
		@AttributeOverride( name="uncertainty", column=@Column( name="mean_uncertainty" ) )
	})
	@Embedded
	private PersistableAmount mean;
	
	@AttributeOverrides({
		@AttributeOverride( name="value", column=@Column( name="sampleStandardDeviation_value" ) ),
		@AttributeOverride( name="uncertainty", column=@Column( name="sampleStandardDeviation_uncertainty" ) )
	})
	@Embedded
	private PersistableAmount sampleStandardDeviation;

	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(Parameter parameter) {
		this.parameters.add(parameter);
		parameter.setMeasurement(this);
	}
	
	public <Q extends Quantity<Q>> Parameter addParameter(String name, Class<Q> quantityType) {
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.updateQuantityType(quantityType);
		addParameter(parameter);
		return parameter;
	}

	public PersistableAmount getMean() {
		return mean;
	}

	public PersistableAmount getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}
	
	private <Q extends Quantity<Q>> Amount<Q> helpComputeAverage(Unit<Q> unit) {
		Amount<Q> avg = this.values.stream()
				.map(mv -> mv.getValue().asAmount(unit))
				.reduce(new Amount<>(0, 0, unit), (a1, a2) -> a1.add(a2))
				.divideAmount(this.values.size());
		this.mean = new PersistableAmount();
		this.mean.setAmount(this, avg);
		return avg;
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
	
	private <Q extends Quantity<Q>, V extends Quantity<V>> void helpComputeSampleStandardDeviation(
			Amount<V> variance, Unit<Q> unit) {
		Amount<Q> ssd = Amount.fromQuantity(
				Quantities.getQuantity(variance.sqrt()
						.divide(this.values.size() - 1)
						.getValue(), unit), 0);
		this.sampleStandardDeviation = new PersistableAmount();
		this.sampleStandardDeviation.setAmount(this, ssd);
	}
	
	public void computeStatistics() {
		Unit<?> systemUnit = systemUnit(getQuantityTypeId().getQuantityClass().getQuantityType());
		
		if (this.values.size() > 0) {
			Amount<?> avg = helpComputeAverage(systemUnit);
			Unit<?> varianceUnit = avg.getUnit().pow(2);
			Amount<?> variance = helpComputeVariance(avg, varianceUnit);
			helpComputeSampleStandardDeviation(variance, systemUnit);
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
	@Override
	public void addValue(MeasurementValue value) {
		this.values.add(value);
		value.setVariable(this);
		computeStatistics();
	}
	
	public MeasurementValue addValue(Student student, CourseClass courseClass, Amount<?> amount, LocalDateTime taken) {
		MeasurementValue measurementValue = new MeasurementValue();
		measurementValue.setVariable(this);
		measurementValue.setAmountValue(amount);
		measurementValue.setTaken(taken);
		this.values.add(measurementValue);
		
		student.addMeasurementValue(measurementValue);
		courseClass.addMeasurementValue(measurementValue);
		
		computeStatistics();
		
		return measurementValue;
	}
}
