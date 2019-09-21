package labvision.entities;

import java.util.Objects;

import javax.measure.UnconvertibleException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import labvision.entities.LabVisionEntity;
import labvision.measure.Amount;
import labvision.measure.SI;

@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class VariableValue<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	private PersistableAmount value;
	
	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public abstract V getVariable();
	
	public abstract void setVariable(V variable);

	public PersistableAmount getValue() {
		return value;
	}

	public void setValue(PersistableAmount value) {
		V variable = getVariable();
		if (getVariable().getQuantityTypeId().equals(QuantityTypeId.UNKNOWN)) {
			setAmountValue(value.asAmount(SI.getInstance().makeOrGetUnit(variable.dimensionObject())));
		} else {
			setAmountValue(value.asAmount(variable.systemUnit(variable.getQuantityTypeId()
					.getQuantityClass().getQuantityType())));
		}
	}

	/**
	 * Sets this VariableValue to match the values specified in the given amount
	 * @param amount the new amount
	 * @throws ClassCastException if the quantity type does not match the one expected by the
	 * variable
	 * @throws UnconvertibleException if the units of the amount are not compatible
	 * with the variable
	 */
	public void setAmountValue(Amount<?> amount) {
		if (Objects.isNull(value)) {
			value = new PersistableAmount();
		}
		
		value.setAmount(getVariable(), amount);
	}
}
