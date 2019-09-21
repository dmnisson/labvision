package labvision.entities;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Computations of results for measurements
 * @author davidnisson
 *
 */
@Embeddable
public class ResultComputation {
	@OneToMany( targetEntity=Measurement.class )
	@JoinColumn( name="ResultComputation_id" )
	private List<Measurement> measurements;
	
	/** mXparser formula */
	private String formula;
	
	/** variable names */
	@OneToMany( targetEntity=MXParserVariableName.class )
	@JoinColumn( name="ResultComputation_id" )
	private List<MXParserVariableName<?, ?>> variableNames;

	public List<Measurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<Measurement> measurements) {
		this.measurements = measurements;
	}
	
	public void addMeasurement(Measurement measurement) {
		this.measurements.add(measurement);
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public List<MXParserVariableName<?, ?>> getVariableNames() {
		return variableNames;
	}

	public void setVariableNames(List<MXParserVariableName<?, ?>> variableNames) {
		this.variableNames = variableNames;
	}
	
	public void addMXParserVariableName(MXParserVariableName<?, ?> name) {
		variableNames.add(name);
	}
}
