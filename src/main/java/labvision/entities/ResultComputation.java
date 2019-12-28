package labvision.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Represents a result variable dependent on measurement variables by a given formula
 * @author davidnisson
 *
 */
@Entity(name = "ResultComputation")
public class ResultComputation extends Variable<ResultComputation, Result> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	@OneToMany( targetEntity=Measurement.class )
	@JoinColumn( name="ResultComputation_id" )
	private List<Measurement> measurements = new ArrayList<>();
	
	/** mXparser formula */
	private String formula;
	
	/** variable names: first variable name corresponds to the dependent variable, 
	 * then subsequent variables correspond to measurements */
	@OneToMany( targetEntity=MXParserVariableName.class )
	@JoinColumn( name="ResultComputation_id" )
	private List<MXParserVariableName<?, ?>> variableNames = new ArrayList<>();
	
	@OneToMany( mappedBy="variable", targetEntity = Result.class )
	private List<Result> values = new ArrayList<>();

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

	@Override
	public List<Result> getValues() {
		return values;
	}

	@Override
	public void setValues(List<Result> values) {
		this.values = values;
		values.stream()
			.filter(value -> value.getVariable() != this)
			.forEach(value -> value.setVariable(this));
	}

	@Override
	public void addValue(Result value) {
		this.values.add(value);
		value.setVariable(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
