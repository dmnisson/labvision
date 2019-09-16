package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * mXparser variable name for a parameter
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
@Entity(name="ParameterMXParserVariableName")
public class ParameterMXParserVariableName extends MXParserVariableName<Parameter, ParameterValue> {

	@OneToOne
	private Parameter variable;
	
	@Override
	public Parameter getVariable() {
		return variable;
	}

	@Override
	public void setVariable(Parameter variable) {
		this.variable = variable;
	}
	
}
