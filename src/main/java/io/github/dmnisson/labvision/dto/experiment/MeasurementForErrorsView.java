package io.github.dmnisson.labvision.dto.experiment;

import io.github.dmnisson.labvision.entities.QuantityTypeId;

public class MeasurementForErrorsView extends MeasurementInfo {
	private final double mean;
	private final double sampleStandardDeviation;
	private final long sampleSize;
	
	public MeasurementForErrorsView(
			int id,
			String name,
			QuantityTypeId quantityTypeId,
			double mean,
			double sampleStandardDeviation,
			long sampleSize) {
		super(id, name, quantityTypeId);
		this.mean = mean;
		this.sampleStandardDeviation = sampleStandardDeviation;
		this.sampleSize = sampleSize;
	}

	public double getMean() {
		return mean;
	}

	public double getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}

	public long getSampleSize() {
		return sampleSize;
	}
}
