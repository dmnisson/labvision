package labvision.measure;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Unit;

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

	/**
	 * Creates a new derived SI unit with the given dimension
	 * @param dimension the dimension
	 * @return the derived unit
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Unit<?> makeDerivedUnit(Dimension dimension) {
		Unit<?> derivedUnit = dimension.getBaseDimensions().entrySet().stream()
				.<Unit>map(e -> Units.getInstance().getUnits(e.getKey()).stream()
						.filter(u -> Objects.isNull(u.getBaseUnits()))
						.findAny().orElseThrow(() -> new RuntimeException(
								"cannot find SI unit for dimension " + dimension.toString()))
						.pow(e.getValue()))
				.reduce(AbstractUnit.ONE, Unit::multiply);
		derivedUnits.add(derivedUnit);
		return derivedUnit;
	}
}
