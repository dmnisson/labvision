package labvision.entities;

import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;

import labvision.measure.SI;

/**
 * Identifies a type of quantity (acceleration, mass, charge, etc.)
 * @author davidnisson
 *
 */
public enum QuantityTypeId {
	UNKNOWN(null),
	DIMENSIONLESS(Dimensionless.class),
	ANGLE(Angle.class),
	SOLID_ANGLE(SolidAngle.class),
	LENGTH(Length.class),
	AREA(Area.class),
	VOLUME(Volume.class),
	MASS(Mass.class),
	TIME(Time.class),
	SPEED(Speed.class),
	ACCELERATION(Acceleration.class),
	FORCE(Force.class),
	ENERGY(Energy.class),
	POWER(Power.class),
	PRESSURE(Pressure.class),
	ELECTRIC_CHARGE(ElectricCharge.class),
	ELECTRIC_CURRENT(ElectricCurrent.class),
	ELECTRIC_POTENTIAL(ElectricPotential.class),
	ELECTRIC_RESISTANCE(ElectricResistance.class),
	ELECTRIC_CONDUCTANCE(ElectricConductance.class),
	ELECTRIC_CAPACITANCE(ElectricCapacitance.class),
	ELECTRIC_INDUCTANCE(ElectricInductance.class),
	TEMPERATURE(Temperature.class),
	AMOUNT_OF_SUBSTANCE(AmountOfSubstance.class),
	CATALYTIC_ACTIVITY(CatalyticActivity.class),
	LUMINOUS_FLUX(LuminousFlux.class),
	LUMINOUS_INTENSITY(LuminousIntensity.class),
	ILLUMINANCE(Illuminance.class),
	MAGNETIC_FLUX(MagneticFlux.class),
	MAGNETIC_FLUX_DENSITY(MagneticFluxDensity.class),
	RADIATION_DOSE_ABSORBED(RadiationDoseAbsorbed.class),
	RADIATION_DOSE_EFFECTIVE(RadiationDoseEffective.class),
	RADIOACTIVITY(Radioactivity.class)
	;
	
	private final Class<? extends Quantity<?>> quantityClass;

	private QuantityTypeId(Class<? extends Quantity<?>> quantityClass) {
		this.quantityClass = quantityClass;
	}
	
	public Class<?> getQuantityClass() {
		return quantityClass == null ? Quantity.class : quantityClass;
	}
	
	public static QuantityTypeId of(Class<?> quantityClass) {
		return Stream.of(QuantityTypeId.values())
				.filter(qt -> qt.quantityClass == quantityClass)
				.findFirst()
				.orElse(UNKNOWN);
	}
	
	public Unit<?> getSystemUnit(Dimension dimension) {
		if (this.equals(QuantityTypeId.UNKNOWN)) {
			Unit<?> unit = SI.getInstance().getUnits(dimension)
					.stream().findAny()
					.orElse(null);
			if (unit != null) {
				return unit;
			} else {
				return SI.getInstance().makeDerivedUnit(dimension);
			}
		} else {
			return getSystemUnitHelper(dimension);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <Q extends Quantity<Q>> Unit<Q> getSystemUnitHelper(Dimension dimension) {
		Unit<Q> quantityUnit = SI.getInstance().getUnit((Class<Q>) getQuantityClass());
		if (!quantityUnit.getDimension().equals(dimension)) {
			throw new ClassCastException("dimension mismatch");
		}
		return quantityUnit;
	}
}
