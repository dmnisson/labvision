package io.github.dmnisson.labvision.entities;

import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.persistence.Embeddable;

import io.github.dmnisson.labvision.measure.Amount;
import io.github.dmnisson.labvision.measure.SI;

/**
 * An amount representation that can be persisted into the database.
 * Stores the quantity type, value, and uncertainty. Provides a method
 * that retrieves an immutable Amount object that corresponds to the
 * current value and uncertainty.
 * 
 * Business logic that works with quantities should never use this class
 * directly, but should always work with the Amount object returned by
 * asAmount().
 * 
 * @author davidnisson
 */
@Embeddable
public class PersistableAmount {
	/**
	 * The value in system units
	 */
	public double value;
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(double uncertainty) {
		this.uncertainty = uncertainty;
	}

	/**
	 * The uncertainty in system units
	 */
	public double uncertainty;
	
	/**
	 * Retrieves the Amount object, which implements Quantity, that this
	 * embeddable is designed to persist.
	 * 
	 * @param unit the unit
	 * @return the Amount view
	 */
	public <Q extends Quantity<Q>> Amount<Q> asAmount(Unit<Q> unit) throws ClassCastException {
		return new Amount<Q>(value, uncertainty, unit);
	}
	
	/**
	 * Returns an Amount view of this object for a variable in the SI system units
	 * @param variable the Variable for which this amount is set
	 * @param quantityType the expected quantity type for this amount
	 * @return the Amount view
	 */
	public <Q extends Quantity<Q>> Amount<Q> asSystemAmount(Variable<?, ?> variable, Class<Q> quantityType) {
		if (variable.getQuantityTypeId().equals(QuantityTypeId.UNKNOWN)) {
			return asAmount(SI.getInstance().makeOrGetUnit(variable.dimensionObject())
					.asType(quantityType));
		} else {
			return asAmount(SI.getInstance().getUnitFor(variable, 
					variable.getQuantityTypeId().getQuantityClass().getQuantityType())
					.asType(quantityType));
		}
	}
	
	/**
	 * Sets this PersistableAmount to match the values specified in the given amount
	 * @param variable the Variable for which this amount is set
	 * @param amount the new amount
	 * @throws ClassCastException if the quantity type does not match the one expected by the
	 * variable
	 * @throws UnconvertibleException if the units of the amount are not compatible
	 * with the variable
	 */
	public <Q extends Quantity<Q>> void setAmount(Variable<?, ?> variable, Amount<Q> amount) throws ClassCastException {
		// verify the quantity type
		if (!variable.getQuantityTypeId().equals(QuantityTypeId.UNKNOWN)) {
			amount.getUnit().asType(variable.getQuantityTypeId().getQuantityClass().getQuantityType());
		} else {
			// verify the dimensions
			if (!amount.getUnit().getDimension().equals(variable.dimensionObject())) {
				throw new UnconvertibleException("dimension mismatch: cannot convert " 
						+ amount.getUnit() + " to " + variable.dimensionObject());
			}
		}
		
		Amount<Q> convertedAmount = amount.toAmount(amount.getUnit().getSystemUnit());
		
		value = convertedAmount.getValue().doubleValue();
		uncertainty = convertedAmount.getUncertainty().doubleValue();
	}
}
