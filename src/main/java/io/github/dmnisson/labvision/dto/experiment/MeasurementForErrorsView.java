package io.github.dmnisson.labvision.dto.experiment;

import io.github.dmnisson.labvision.entities.QuantityTypeId;

public class MeasurementForErrorsView extends MeasurementInfo {
	private final Double mean;
	private final Double sampleStandardDeviation;
	private final Long sampleSize;
	
	public MeasurementForErrorsView(
			int id,
			String name,
			QuantityTypeId quantityTypeId,
			Double mean,
			Double sampleStandardDeviation,
			Long sampleSize) {
		super(id, name, quantityTypeId);
		this.mean = mean;
		this.sampleStandardDeviation = sampleStandardDeviation;
		this.sampleSize = sampleSize;
	}

	public Double getMean() {
		return mean;
	}

	public Double getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}

	public Long getSampleSize() {
		return sampleSize;
	}
}
