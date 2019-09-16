package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * mXparser variable name for a variable
 * @author davidnisson
 *
 * @param <V> the variable entity type
 * @param <A> the variable value entity type
 */
@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class MXParserVariableName<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	private String variableName;

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public abstract V getVariable();

	public abstract void setVariable(V variable);

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
