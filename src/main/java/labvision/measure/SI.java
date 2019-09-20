package labvision.measure;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.SystemOfUnits;

import labvision.entities.QuantityTypeId;
import labvision.entities.Variable;
import tec.units.ri.AbstractSystemOfUnits;
import tec.units.ri.AbstractUnit;
import tec.units.ri.unit.Units;

/**
 * Allows SI units to be created for a dimension even of unknown quantity type
 * @author davidnisson
 *
 */
public class SI extends AbstractSystemOfUnits {
	private Set<Unit<?>> derivedUnits = new HashSet<>();
	
	private static final SI INSTANCE = new SI();
	
	public static SI getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String getName() {
		return "SI";
	}

	@Override
	public Set<Unit<?>> getUnits() {
		HashSet<Unit<?>> units = new HashSet<>(Units.getInstance().getUnits());
		units.addAll(derivedUnits);
		return units;
	}

	@Override
	public Set<? extends Unit<?>> getUnits(Dimension dimension) {
		return Stream.concat(Units.getInstance().getUnits(dimension).stream(),
				derivedUnits.stream()
				.filter(u -> u.getDimension() == dimension))
				.collect(Collectors.toSet());
	}
	
	@Override
	public <Q extends Quantity<Q>> Unit<Q> getUnit(Class<Q> quantityType) {
		final SystemOfUnits unitsInstance = Units.getInstance();
		Unit<Q> unit = unitsInstance.getUnit(quantityType);
		
		if (unit == null) {
			try {
				Map<? extends Dimension, Integer> baseDimensions = quantityType.newInstance().getUnit()
						.getDimension().getBaseDimensions();
				if (baseDimensions == null) unit = null;
				unit = baseDimensions.entrySet().stream()
						.filter(e -> !unitsInstance.getUnits(e.getKey()).isEmpty())
						.<Unit<?>>map(e -> unitsInstance.getUnits(e.getKey()).stream()
								.findAny().get().pow(e.getValue()))
						.reduce(AbstractUnit.ONE, Unit::multiply)
						.asType(quantityType);
			} catch (InstantiationException | IllegalAccessException e) {
				unit = null;
			}
		}
		return unit;
	}
	
	/**
	 * Creates a new derived SI unit with the given dimension
	 * @param dimension the dimension
	 * @return the derived unit, or the corresponding base unit if the dimension is a
	 * base dimension, or null if no SI unit can be found for the given dimension
	 */
	public Unit<?> makeOrGetUnit(Dimension dimension) {
		Map<? extends Dimension, Integer> baseDimensions = dimension.getBaseDimensions();
		if (baseDimensions == null) {
			return Units.getInstance().getUnits(dimension).stream()
					.filter(u -> u.getBaseUnits() == null)
					.findAny().orElse(null);
		}
		
	    try {
			Unit<?> derivedUnit = baseDimensions.entrySet().stream()
					.<Unit<?>>map(e -> {
						Unit<?> baseUnit = makeOrGetUnit(e.getKey());
						if (baseUnit == null) {
							throw new NoSystemUnitFoundException(this, e.getKey());
						}
						return baseUnit.pow(e.getValue());
					})
					.reduce(AbstractUnit.ONE, Unit::multiply);
			derivedUnits.add(derivedUnit);
			return derivedUnit;
	    } catch (NoSystemUnitFoundException e) {
	    	return null;
	    }
	}

	/**
	 * Get the SI units for a given variable, based on the quantity type or
	 * dimension if quantity type is unknown
	 * @param variable
	 * @param quantityType the quantity type expected for the variable
	 * @return
	 * @throws ClassCastException if the supplied quantity class does not match
	 * the class of the quantity type
	 */
	public <Q extends Quantity<Q> > Unit<Q> getUnitFor(Variable<?, ?> variable, Class<Q> quantityType) {
		QuantityTypeId quantityTypeId = variable.getQuantityTypeId();
		if (quantityTypeId.equals(QuantityTypeId.UNKNOWN)) {
			return makeOrGetUnit(variable.dimensionObject()).asType(quantityType);
		} else {
			// make sure we throw ClassCastException if there is a mismatch between
			// the parameters and the stored variable's quantity type
			quantityTypeId.getQuantityClass().getQuantityType().asSubclass(quantityType);
			return getUnit(quantityType);
		}
	}
}
