package labvision.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import labvision.entities.LabVisionEntity;

@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class VariableValue<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	
	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public abstract V getVariable();
	
	public abstract void setVariable(V variable);
}
