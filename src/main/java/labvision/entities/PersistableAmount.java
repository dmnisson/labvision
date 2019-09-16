package labvision.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import tec.units.ri.quantity.QuantityDimension;

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
public class PersistableAmount implements Serializable {
	/**
	 * Unique version number for serialization
	 */
	private static final long serialVersionUID = -419676055576745650L;
	
	/**
	 * The value in system units
	 */
	public double value;
	
	/**
	 * The uncertainty in system units
	 */
	public double uncertainty;
	
	/**
	 * The quantity type identifier
	 */
	@Enumerated(EnumType.STRING)
	public QuantityTypeId quantityTypeId;
	
	/**
	 * The dimensions as a string.
	 * The string format consists of a space separated list of pairs of each character
	 * representing the dimension followed immediately by the integer, e.g.
	 * 
	 * I1 T1
	 * 
	 * for electric charge. The string is empty for dimensionless quantities.
	 */
	private String dimension;
	
	/**
	 * Retrieves the Amount object, which implements Quantity, that this
	 * embeddable is designed to persist.
	 * 
	 * @param unit the unit
	 * @return the Amount view
	 * @throws ClassCastException if the quantity class is not right for this object
	 */
	@SuppressWarnings("unchecked")
	public <Q extends Quantity<Q>> Amount<Q> asAmount(Unit<Q> unit) throws ClassCastException {
		// verify the quantity class
		if (!quantityTypeId.equals(QuantityTypeId.UNKNOWN)) {
			unit.asType((Class<Q>) quantityTypeId.getQuantityClass());
		}
		// verify the dimensions
		if (!unit.getDimension().equals(dimensionObject())) {
			throw new UnconvertibleException("dimension mismatch");
		}
		return new Amount<Q>(value, uncertainty, unit);
	}
	
	/**
	 * Convenience for asAmount(this.getQuantityTypeId().getSystemUnit(dimensionObject()))
	 * @return the Amount view
	 */
	public Amount<?> asSystemAmount() {
		return asAmount(quantityTypeId.getSystemUnit(dimensionObject()));
	}
	
	/**
	 * Sets this PersistableAmount to match the values specified in the given amount
	 * @param amount the new amount
	 * @param quantityClass the quantity class
	 * @throws ClassCastException if the quantity class is not right for this object
	 */
	public <Q extends Quantity<Q>> void setAmount(Amount<Q> amount, Class<Q> quantityClass) throws ClassCastException {
		// verify the quantity class
		if (!QuantityTypeId.of(quantityClass).equals(QuantityTypeId.UNKNOWN)
				&& !quantityTypeId.getQuantityClass().equals(quantityClass)) {
			throw new ClassCastException("quantity class mismatch");
		}
		
		Amount<Q> convertedAmount = amount.toAmount(amount.getUnit().getSystemUnit());
		
		value = convertedAmount.getValue().doubleValue();
		uncertainty = convertedAmount.getUncertainty().doubleValue();
	}
	
	/**
	 * Sets this PersistableAmount to match the value from a given
	 * quantity of unknown type to be cast to a given quantity class, and
	 * with the given default uncertainty if the quantity is not a subclass of Amount
	 */
	@SuppressWarnings("unchecked")
	<Q extends Quantity<Q>> void setAmountFromQuantity(Quantity<?> quantity, Class<?> quantityClass, double defaultUncertainty) {
		setAmount(Amount.fromQuantity(quantity.asType((Class<Q>) quantityClass), defaultUncertainty),
				(Class<Q>) quantityClass);
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	public Dimension dimensionObject() {
		return dimensionObjectFor(dimension);
	}
	
	static Dimension dimensionObjectFor(String dimension) {
		if (dimension == null || dimension == "") {
			return QuantityDimension.NONE;
		}
		
		String[] dimensionBaseStrings = dimension.split(" ");
		return Arrays.stream(dimensionBaseStrings)
		.map(bs -> {
			Dimension base = QuantityDimension.parse(bs.charAt(0));
			int p = Integer.parseInt(bs.substring(1));
			return base.pow(p);
		})
		.reduce(QuantityDimension.NONE, Dimension::multiply);
	}
	
	public void updateDimensionObject(Dimension dimension) {
		this.dimension = dimension.getBaseDimensions().entrySet().stream()
				.map(e -> e.getKey().toString().substring(1, 2) + e.getValue())
				.reduce("", (s1, s2) -> String.join(" ", s1, s2));
	}
}
