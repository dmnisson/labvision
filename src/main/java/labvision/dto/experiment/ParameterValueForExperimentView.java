package labvision.dto.experiment;

public class ParameterValueForExperimentView {
	private final int id;
	private final int parameterId;
	private final double value;
	private final double uncertainty;
	
	public ParameterValueForExperimentView(int id, int parameterId, double value, double uncertainty) {
		super();
		this.id = id;
		this.parameterId = parameterId;
		this.value = value;
		this.uncertainty = uncertainty;
	}

	public int getId() {
		return id;
	}

	public int getParameterId() {
		return parameterId;
	}

	public double getValue() {
		return value;
	}

	public double getUncertainty() {
		return uncertainty;
	}
}
