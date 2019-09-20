package labvision.measure;

import javax.measure.Dimension;

import tec.units.ri.AbstractSystemOfUnits;

/**
 * Thrown when an SI unit cannot be found for a given dimension.
 * Used internally by makeDerivedUnit.
 * @author davidnisson
 *
 */
class NoSystemUnitFoundException extends RuntimeException {

	private static final long serialVersionUID = 7833112899477225719L;
	private final AbstractSystemOfUnits system;
	private final Dimension dimension;
	
	public NoSystemUnitFoundException(AbstractSystemOfUnits system, Dimension dimension) {
		super("No " + system.getName() + " unit found for dimension " + dimension);
		this.system = system;
		this.dimension = dimension;
	}

	public AbstractSystemOfUnits getSystem() {
		return system;
	}

	public Dimension getDimension() {
		return dimension;
	}
}