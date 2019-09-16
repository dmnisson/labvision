package labvision.entities;

import java.util.List;

import javax.measure.Unit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class Variable<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private QuantityTypeId quantityTypeId;
	
	private String dimension;
	
	@Override
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}

	public void setQuantityTypeId(QuantityTypeId quantityTypeId) {
		this.quantityTypeId = quantityTypeId;
	}
	
	public Unit<?> systemUnit() {
		return getQuantityTypeId().getSystemUnit(
				PersistableAmount.dimensionObjectFor(getDimension()));
	}

	public abstract List<A> getValues();
	
	public abstract void setValues(List<A> values);
}
