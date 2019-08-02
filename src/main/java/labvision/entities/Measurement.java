package labvision.entities;

import java.util.List;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import static java.lang.Math.*;

@Entity
public class Measurement<Q extends Quantity<Q>> {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	private String name;
	
	private String quantityClassName;
	
	private Class<Q> quantityClass;
	
	protected Measurement() {
	}
	
	public Measurement(String name, Class<Q> quantityClass) {
		this.quantityClass = quantityClass;
		this.quantityClassName = quantityClass.getName();
	}
	
	@ManyToOne
	private Experiment experiment;
	
	@OneToMany( targetEntity=Parameter.class )
	private List<Parameter<Q, ?>> parameters;
	
	@OneToMany( targetEntity=MeasurementValue.class )
	private List<MeasurementValue<Q>> measurementValues;
	
	public boolean addMeasurementValue(MeasurementValue<Q> arg0) {
		return recomputeIf(measurementValues.add(arg0));
	}
	
	public boolean contains(Object arg0) {
		return measurementValues.contains(arg0);
	}
	public boolean remove(Object arg0) {
		return recomputeIf(measurementValues.remove(arg0));
	}

	private Amount<Q> mean;
	private Amount<Q> sampleStandardDeviation;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Parameter<Q, ?>> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter<Q, ?>> parameters) {
		this.parameters = parameters;
	}
	public List<MeasurementValue<Q>> getMeasurementValues() {
		return measurementValues;
	}
	public void setMeasurementValues(List<MeasurementValue<Q>> measurementValues) {
		this.measurementValues = measurementValues;
		measurementValues.stream()
		.filter(value -> value.getMeasurement() != this)
		.forEach(value -> {
			value.setMeasurement(this);
		});
		recomputeIf(measurementValues != null);
	}
	public Amount<Q> getMean() {
		return mean;
	}
	public void setMean(Amount<Q> mean) {
		this.mean = mean;
	}
	public Amount<Q> getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}
	public void setSampleStandardDeviation(Amount<Q> sampleStandardDeviation) {
		this.sampleStandardDeviation = sampleStandardDeviation;
	}
	
	private boolean recomputeIf(boolean success) {
		if (success) {
			if (this.measurementValues.size() > 0) {
				double avg = this.measurementValues.stream()
						.mapToDouble(value -> value.getValue())
						.average().getAsDouble();
				
				double variance = this.measurementValues.stream()
						.mapToDouble(value -> pow(value.getValue() - avg, 2))
						.sum();
				
				double ssd, stdDevInMean;
				if (this.measurementValues.size() > 1) {
					stdDevInMean = sqrt(variance 
							/ pow(this.measurementValues.size(), 2));
					ssd = sqrt(variance
							/ (this.measurementValues.size() - 1));
				}
				else {
					stdDevInMean = Double.NaN;
					ssd = Double.NaN;
				}
				
				this.setMean(new Amount<>(avg, stdDevInMean));
				if (!Double.isNaN(ssd)) {
					this.setSampleStandardDeviation(new Amount<>(ssd, 0));
				}
			}
			else {
				this.setMean(null);
				this.setSampleStandardDeviation(null);
			}
		}
		return success;
	}

	public String getQuantityClassName() {
		return quantityClassName;
	}

	public void setQuantityClassName(String quantityClassName) 
			throws ClassCastException, ClassNotFoundException {
		if (!Class.forName("javax.measure.quantity." + quantityClassName)
				.equals(quantityClass)) {
			throw new ClassCastException("did you forget to set the quantityClass first?");
		}
		this.quantityClassName = quantityClassName;
	}
	
	public Class<Q> getQuantityClass() {
		return quantityClass;
	}
	
	protected void setQuantityClass(Class<Q> quantityClass) {
		this.quantityClass = quantityClass;
		try {
		  this.setQuantityClassName(quantityClass.getSimpleName());
		}
		catch (ClassCastException | ClassNotFoundException ex) {}
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
}
