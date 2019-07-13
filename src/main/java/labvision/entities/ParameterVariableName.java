package labvision.entities;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * mXparser variable name for a parameter
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
@Entity
public class ParameterVariableName<M extends Quantity<M>, P extends Quantity<P>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	@OneToOne
	private Parameter<M, P> parameter;
	
	private String variableName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Parameter<M, P> getParameter() {
		return parameter;
	}

	public void setParameter(Parameter<M, P> parameter) {
		this.parameter = parameter;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
