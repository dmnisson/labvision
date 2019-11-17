package labvision.dto.experiment;

import labvision.entities.QuantityTypeId;

public class ParameterForExperimentView {
	private final int id;
	private final String name;
	private final QuantityTypeId quantityTypeId;
	private final String unitString;
	
	public ParameterForExperimentView(int id, String name, QuantityTypeId quantityTypeId) {
		this(id, name, quantityTypeId, null);
	}
	
	public ParameterForExperimentView(int id, String name, QuantityTypeId quantityTypeId, String unitString) {
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

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}

	public String getUnitString() {
		return unitString;
	}
}