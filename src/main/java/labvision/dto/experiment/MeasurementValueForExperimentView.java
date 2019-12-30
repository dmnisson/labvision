package labvision.dto.experiment;

import java.time.LocalDateTime;

import labvision.entities.QuantityTypeId;

public class MeasurementValueForExperimentView {
	private final Integer id;
	private final Integer measurementId;
	private final String measurementName;
	private final Double value;
	private final Double uncertainty;
	private final LocalDateTime taken;
	private final String dimension;
	private final QuantityTypeId quantityTypeId;
	
	public MeasurementValueForExperimentView(
			Integer id,
			Integer measurementId,
			String measurementName,
			Double value,
			Double uncertainty,
			LocalDateTime taken,
			String dimension,
			QuantityTypeId quantityTypeId
			) {
		this.id = id;
		this.measurementId = measurementId;
		this.measurementName = measurementName;
		this.value = value;
		this.uncertainty = uncertainty;
		this.taken = taken;
		this.dimension = dimension;
		this.quantityTypeId = quantityTypeId;
	}
	
	public Integer getId() {
		return id;
	}
	public Integer getMeasurementId() {
		return measurementId;
	}
	public String getMeasurementName() {
		return measurementName;
	}
	public String getDimension() {
		return dimension;
	}
	public Double getValue() {
		return value;
	}
	public Double getUncertainty() {
		return uncertainty;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}

	public LocalDateTime getTaken() {
		return taken;
	}
}
