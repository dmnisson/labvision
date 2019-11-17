package labvision.dto.student.experiment;

import labvision.entities.QuantityTypeId;

public class MeasurementValueForStudentMeasurementValueTable {
	private final Integer id;
	private final Integer measurementId;
	private final String measurementName;
	private final Double value;
	private final Double uncertainty;
	private final String dimension;
	private final QuantityTypeId quantityTypeId;
	private final String unitString;
	
	public MeasurementValueForStudentMeasurementValueTable(
			Integer id,
			Integer measurementId,
			String measurementName,
			Double value,
			Double uncertainty,
			String dimension,
			QuantityTypeId quantityTypeId
			) {
		this(id, measurementId, measurementName, value, uncertainty, dimension, quantityTypeId, null);
	}
	
	public MeasurementValueForStudentMeasurementValueTable(MeasurementValueForStudentMeasurementValueTable mv, String unitString) {
		this(mv.id, mv.measurementId, mv.measurementName, mv.value, mv.uncertainty, mv.dimension, mv.getQuantityTypeId(), unitString);
	}
	
	public MeasurementValueForStudentMeasurementValueTable(
			Integer id,
			Integer measurementId,
			String measurementName,
			Double value,
			Double uncertainty,
			String dimension,
			QuantityTypeId quantityTypeId,
			String unitString
			) {
		this.id = id;
		this.measurementId = measurementId;
		this.measurementName = measurementName;
		this.value = value;
		this.uncertainty = uncertainty;
		this.dimension = dimension;
		this.quantityTypeId = quantityTypeId;
		this.unitString = unitString;
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

	public String getUnitString() {
		return unitString;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}
}
