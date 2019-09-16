package labvision.entities;

import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import flanagan.analysis.ErrorProp;

/**
 * Interface for working with amounts with uncertainties
 * @author davidnisson
 *
 * @param <Q>
 */
public class Amount<Q extends Quantity<Q>> implements Quantity<Q> {

	private final Number value;
	private final Number uncertainty;
	private final Unit<Q> unit;
	
	public Amount(Number value, Number uncertainty, Unit<Q> unit) {
		this.value = value;
		this.uncertainty = uncertainty;
		this.unit = unit;
	}

	public Number getUncertainty() {
		return uncertainty;
	}
	
	/**
	 * Gets an ErrorProp object to work with the Flanagan library
	 * @returns the ErrorProp
	 */
	ErrorProp toErrorProp() {
		return new ErrorProp(value.doubleValue(), uncertainty.doubleValue());
	}
	
	/**
	 * Creates a new Amount from a Flanagan ErrorProp
	 * @param <Q> the quantity type
	 * @param errorProp the ErrorProp
	 * @param unit the unit
	 * @return the Amount
	 */
	static <Q extends Quantity<Q>> Amount<Q> fromErrorProp(ErrorProp errorProp, Unit<Q> unit) {
		return new Amount<>(errorProp.getValue(), errorProp.getError(), unit);
	}

	/**
	 * Creates an Amount from a Quantity object of another type, with uncertainty set
	 * to either the one in the quantity if it is an amount, or the given value
	 * @param <Q> the quantity type
	 * @param quantity the quantity
	 * @param defaultUncertainty the uncertainty value to use if the quantity is not another Amount
	 * @return the Amount
	 */
	public static <Q extends Quantity<Q>> Amount<Q> fromQuantity(Quantity<Q> quantity, Number defaultUncertainty) {
		if (quantity instanceof Amount) {
			return new Amount<>(quantity.getValue(), ((Amount<Q>) quantity).getUncertainty(), quantity.getUnit());
		} else {
			return new Amount<>(quantity.getValue(), defaultUncertainty, quantity.getUnit());
		}
	}
	
	@Override
	public Quantity<Q> add(Quantity<Q> augend) {
		return fromErrorProp(this.toErrorProp().plus(fromQuantity(augend, 0).toErrorProp()), unit);
	}

	@Override
	public Quantity<Q> subtract(Quantity<Q> subtrahend) {
		return fromErrorProp(this.toErrorProp().minus(fromQuantity(subtrahend, 0).toErrorProp()), unit);
	}

	@Override
	public Quantity<?> divide(Quantity<?> divisor) {
		return fromErrorProp(this.toErrorProp().over(fromQuantity(divisor, 0).toErrorProp()),
				unit.divide(divisor.getUnit()));
	}

	@Override
	public Quantity<Q> divide(Number divisor) {
		return fromErrorProp(this.toErrorProp().over(divisor.doubleValue()), unit);
	}

	@Override
	public Quantity<?> multiply(Quantity<?> multiplier) {
		return fromErrorProp(this.toErrorProp().times(fromQuantity(multiplier, 0).toErrorProp()),
				unit.multiply(multiplier.getUnit()));
	}

	@Override
	public Quantity<Q> multiply(Number multiplier) {
		return fromErrorProp(this.toErrorProp().times(multiplier.doubleValue()), unit);
	}

	public Amount<?> sqrt() {
		return fromErrorProp(ErrorProp.sqrt(this.toErrorProp()), unit.root(2));
	}
	
	@Override
	public Quantity<?> inverse() {
		return fromErrorProp(this.toErrorProp().inverse(), unit.inverse());
	}

	@Override
	public Quantity<Q> to(Unit<Q> unit) throws UnconvertibleException {
		return toAmount(unit);
	}
	
	public Amount<Q> toAmount(Unit<Q> unit) throws UnconvertibleException {
		if (unit.equals(this.unit)) {
			return this;
		}
		
		UnitConverter converter = this.unit.getConverterTo(unit);
		
		// estimate error by differentiating conversion function
		final double delta = value.doubleValue() * 1e-4;
		final double convertedValue1 = converter.convert(value.doubleValue());
		final double convertedValue2 = converter.convert(value.doubleValue() + delta);
		final double convertedUncertainty = (convertedValue2 - convertedValue1) * uncertainty.doubleValue() / delta;
		
		return new Amount<>((convertedValue1 + convertedValue2) / 2, convertedUncertainty, unit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
		unit.asType(type);
		return (Quantity<T>) this;
	}

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public Unit<Q> getUnit() {
		return unit;
	}
}
