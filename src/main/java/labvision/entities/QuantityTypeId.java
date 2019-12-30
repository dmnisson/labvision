package labvision.entities;

import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.measure.quantity.*;

import labvision.measure.SI;

/**
 * Identifies a type of quantity (acceleration, mass, charge, etc.)
 * @author davidnisson
 *
 */
public enum QuantityTypeId {
	UNKNOWN(null),
	DIMENSIONLESS(new QuantityClass<>(Dimensionless.class)),
	ANGLE(new QuantityClass<>(Angle.class)),
	SOLID_ANGLE(new QuantityClass<>(SolidAngle.class)),
	LENGTH(new QuantityClass<>(Length.class)),
	AREA(new QuantityClass<>(Area.class)),
	VOLUME(new QuantityClass<>(Volume.class)),
	MASS(new QuantityClass<>(Mass.class)),
	TIME(new QuantityClass<>(Time.class)),
	SPEED(new QuantityClass<>(Speed.class)),
	ACCELERATION(new QuantityClass<>(Acceleration.class)),
	FORCE(new QuantityClass<>(Force.class)),
	ENERGY(new QuantityClass<>(Energy.class)),
	POWER(new QuantityClass<>(Power.class)),
	PRESSURE(new QuantityClass<>(Pressure.class)),
	ELECTRIC_CHARGE(new QuantityClass<>(ElectricCharge.class)),
	ELECTRIC_CURRENT(new QuantityClass<>(ElectricCurrent.class)),
	ELECTRIC_POTENTIAL(new QuantityClass<>(ElectricPotential.class)),
	ELECTRIC_RESISTANCE(new QuantityClass<>(ElectricResistance.class)),
	ELECTRIC_CONDUCTANCE(new QuantityClass<>(ElectricConductance.class)),
	ELECTRIC_CAPACITANCE(new QuantityClass<>(ElectricCapacitance.class)),
	ELECTRIC_INDUCTANCE(new QuantityClass<>(ElectricInductance.class)),
	TEMPERATURE(new QuantityClass<>(Temperature.class)),
	AMOUNT_OF_SUBSTANCE(new QuantityClass<>(AmountOfSubstance.class)),
	CATALYTIC_ACTIVITY(new QuantityClass<>(CatalyticActivity.class)),
	LUMINOUS_FLUX(new QuantityClass<>(LuminousFlux.class)),
	LUMINOUS_INTENSITY(new QuantityClass<>(LuminousIntensity.class)),
	ILLUMINANCE(new QuantityClass<>(Illuminance.class)),
	MAGNETIC_FLUX(new QuantityClass<>(MagneticFlux.class)),
	MAGNETIC_FLUX_DENSITY(new QuantityClass<>(MagneticFluxDensity.class)),
	RADIATION_DOSE_ABSORBED(new QuantityClass<>(RadiationDoseAbsorbed.class)),
	RADIATION_DOSE_EFFECTIVE(new QuantityClass<>(RadiationDoseEffective.class)),
	RADIOACTIVITY(new QuantityClass<>(Radioactivity.class))
	;
	
	private final QuantityClass<?> quantityClass;

	private QuantityTypeId(QuantityClass<?> quantityClass) {
		this.quantityClass = quantityClass;
	}
	
	public QuantityClass<?> getQuantityClass() {
		return quantityClass;
	}
	
	public String getUnitString() {
		return SI.getInstance().getUnitFor(this).toString();
	}

	public static <Q extends Quantity<Q>> QuantityTypeId of(QuantityClass<Q> quantityClass) {
		return Stream.of(QuantityTypeId.values())
				.filter(qt -> qt.quantityClass.equals(quantityClass))
				.findFirst()
				.orElse(UNKNOWN);
	}
	
	public static <Q extends Quantity<Q>> QuantityTypeId of(Class<Q> quantityType) {
		return Stream.of(QuantityTypeId.values())
				.filter(qt -> qt.quantityClass != null && 
					qt.quantityClass.getQuantityType().equals(quantityType))
				.findFirst()
				.orElse(UNKNOWN);
	}
}
