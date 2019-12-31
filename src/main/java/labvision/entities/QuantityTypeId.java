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
	UNKNOWN(null, "Custom"),
	DIMENSIONLESS(new QuantityClass<>(Dimensionless.class), "Dimensionless"),
	ANGLE(new QuantityClass<>(Angle.class), "Angle"),
	SOLID_ANGLE(new QuantityClass<>(SolidAngle.class), "Solid Angle"),
	LENGTH(new QuantityClass<>(Length.class), "Length"),
	AREA(new QuantityClass<>(Area.class), "Area"),
	VOLUME(new QuantityClass<>(Volume.class), "Volume"),
	MASS(new QuantityClass<>(Mass.class), "Mass"),
	TIME(new QuantityClass<>(Time.class), "Time"),
	SPEED(new QuantityClass<>(Speed.class), "Speed"),
	ACCELERATION(new QuantityClass<>(Acceleration.class), "Acceleration"),
	FORCE(new QuantityClass<>(Force.class), "Force"),
	ENERGY(new QuantityClass<>(Energy.class), "Energy"),
	POWER(new QuantityClass<>(Power.class), "Power"),
	PRESSURE(new QuantityClass<>(Pressure.class), "Pressure"),
	ELECTRIC_CHARGE(new QuantityClass<>(ElectricCharge.class), "Electric Charge"),
	ELECTRIC_CURRENT(new QuantityClass<>(ElectricCurrent.class), "Electric Current"),
	ELECTRIC_POTENTIAL(new QuantityClass<>(ElectricPotential.class), "Electric Potential"),
	ELECTRIC_RESISTANCE(new QuantityClass<>(ElectricResistance.class), "Electric Resistance"),
	ELECTRIC_CONDUCTANCE(new QuantityClass<>(ElectricConductance.class), "Electric Conductance"),
	ELECTRIC_CAPACITANCE(new QuantityClass<>(ElectricCapacitance.class), "Electric Capacitance"),
	ELECTRIC_INDUCTANCE(new QuantityClass<>(ElectricInductance.class), "Electric Inductance"),
	TEMPERATURE(new QuantityClass<>(Temperature.class), "Temperature"),
	AMOUNT_OF_SUBSTANCE(new QuantityClass<>(AmountOfSubstance.class), "Amount of a Substance"),
	CATALYTIC_ACTIVITY(new QuantityClass<>(CatalyticActivity.class), "Catalytic Activity"),
	LUMINOUS_FLUX(new QuantityClass<>(LuminousFlux.class), "Luminous Flux"),
	LUMINOUS_INTENSITY(new QuantityClass<>(LuminousIntensity.class), "Luminous Intensity"),
	ILLUMINANCE(new QuantityClass<>(Illuminance.class), "Illuminance"),
	MAGNETIC_FLUX(new QuantityClass<>(MagneticFlux.class), "Magnetic Flux"),
	MAGNETIC_FLUX_DENSITY(new QuantityClass<>(MagneticFluxDensity.class), "Magnetic Flux Density"),
	RADIATION_DOSE_ABSORBED(new QuantityClass<>(RadiationDoseAbsorbed.class), "Absorbed Radiation Dose"),
	RADIATION_DOSE_EFFECTIVE(new QuantityClass<>(RadiationDoseEffective.class), "Effective Radiation Dose"),
	RADIOACTIVITY(new QuantityClass<>(Radioactivity.class), "Radioactivity");
	
	private final QuantityClass<?> quantityClass;
	private final String displayName;
		
	private QuantityTypeId(QuantityClass<?> quantityClass, String displayName) {
		this.quantityClass = quantityClass;
		this.displayName = displayName;
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
	
	public String getDisplayName() {
		return displayName;
	}
}
