package labvision.dto.experiment;

import labvision.entities.QuantityTypeId;

public class MeasurementForExperimentTable {
	private final int id;
	private final String name;
	private final QuantityTypeId quantityTypeId;
	private final String unitString;
	
	public MeasurementForExperimentTable(int id, String name, QuantityTypeId quantityTypeId) {
		this(id, name, quantityTypeId, null);
	}
	
	public MeasurementForExperimentTable(int id, String name, QuantityTypeId quantityTypeId, String unitString) {
		this.id = id;
		this.name = name;
		this.quantityTypeId = quantityTypeId;
		this.unitString = unitString;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUnitString() {
		return unitString;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}
}
