package labvision.dto.experiment;

import labvision.entities.QuantityTypeId;

public class MeasurementForExperimentView {
	private final int id;
	private final String name;
	private final QuantityTypeId quantityTypeId;
	
	public MeasurementForExperimentView(int id, String name, QuantityTypeId quantityTypeId) {
		this.id = id;
		this.name = name;
		this.quantityTypeId = quantityTypeId;
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
}
