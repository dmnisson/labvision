package labvision.dto.experiment.report;

import labvision.entities.QuantityTypeId;
import labvision.measure.SI;

/**
 * Information required to display a result
 * @author David Nisson
 */
public class ResultInfo {
	private final int id;
	private final String name;
	private final double value;
	private final double uncertainty;
	private final QuantityTypeId quantityTypeId;
	
	public ResultInfo(int id, String name, double value, double uncertainty, QuantityTypeId quantityTypeId) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.uncertainty = uncertainty;
		this.quantityTypeId = quantityTypeId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public double getUncertainty() {
		return uncertainty;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}
	
	public String getUnitString() {
		return SI.getInstance().getUnitFor(quantityTypeId).toString();
	}
}