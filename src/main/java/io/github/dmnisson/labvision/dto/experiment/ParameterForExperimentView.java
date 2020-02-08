package io.github.dmnisson.labvision.dto.experiment;

import io.github.dmnisson.labvision.entities.QuantityTypeId;

public class ParameterForExperimentView {
	private final int id;
	private final String name;
	private final QuantityTypeId quantityTypeId;
	
	public ParameterForExperimentView(int id, String name, QuantityTypeId quantityTypeId) {
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
