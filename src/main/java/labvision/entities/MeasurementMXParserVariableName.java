package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity( name="MeasurementMXParserVariableName" )
public class MeasurementMXParserVariableName extends MXParserVariableName<Measurement, MeasurementValue> {

	@OneToOne
	private Measurement variable;
	
	@Override
	public Measurement getVariable() {
		return variable;
	}

	@Override
	public void setVariable(Measurement variable) {
		this.variable = variable;
	}

}
