package labvision.entities;

import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.persistence.Embeddable;

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
	
	/**
	 * The uncertainty in system units
	 */
	public double uncertainty;
	
	/**
	 * Retrieves the Amount object, which implements Quantity, that this
	 * embeddable is designed to persist.
	 * 
	 * @param variable the Variable for which this amount is set
	 * @param unit the unit
	 * @return the Amount view
	 * @throws ClassCastException if the quantity class is not right for this object
	 */
	@SuppressWarnings("unchecked")
	public <Q extends Quantity<Q>> Amount<Q> asAmount(
			Variable<?, ?> variable, Unit<Q> unit) throws ClassCastException {
		// verify the quantity class
		if (!variable.getQuantityTypeId().equals(QuantityTypeId.UNKNOWN)) {
			unit.asType((Class<Q>) variable.getQuantityTypeId().getQuantityClass());
		}
		// verify the dimensions
		if (!unit.getDimension().equals(variable.dimensionObject())) {
			throw new UnconvertibleException("dimension mismatch");
		}
		return new Amount<Q>(value, uncertainty, unit);
	}
	
	/**
	 * Convenience for asAmount(this.getQuantityTypeId().getSystemUnit(dimensionObject()))
	 * @param variable the Variable for which this amount is set
	 * @return the Amount view
	 */
	public Amount<?> asSystemAmount(Variable<?, ?> variable) {
		return asAmount(variable, variable.getQuantityTypeId()
				.getSystemUnit(variable.dimensionObject()));
	}
	
	/**
	 * Sets this PersistableAmount to match the values specified in the given amount
	 * @param variable the Variable for which this amount is set
	 * @param amount the new amount
	 * @param quantityClass the quantity class
	 * @throws ClassCastException if the quantity class is not right for this object
	 */
	public <Q extends Quantity<Q>> void setAmount(Variable<?, ?> variable, Amount<Q> amount, Class<Q> quantityClass) throws ClassCastException {
		// verify the quantity class
		if (!QuantityTypeId.of(quantityClass).equals(QuantityTypeId.UNKNOWN)
				&& !variable.getQuantityTypeId().equals(QuantityTypeId.UNKNOWN)
				&& !variable.getQuantityTypeId().getQuantityClass().equals(quantityClass)) {
			throw new ClassCastException("quantity class mismatch");
		}
		
		Amount<Q> convertedAmount = amount.toAmount(amount.getUnit().getSystemUnit());
		
		value = convertedAmount.getValue().doubleValue();
		uncertainty = convertedAmount.getUncertainty().doubleValue();
	}
}
