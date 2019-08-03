package labvision.entities;

import java.io.Serializable;

import javax.measure.Quantity;
import javax.persistence.Embeddable;

@Embeddable
public class Amount<Q extends Quantity<Q>> implements Serializable {
	/**
	 * Unique version number for serialization
	 */
	private static final long serialVersionUID = -419676055576745650L;

	/** Value in SI units. */
	public double value;
	
	/** Uncertainty in SI units. */
	public double uncertainty;
	
	protected Amount() {
	}
	
	public Amount(double value, double uncertainty) {
		this.value = value;
		this.uncertainty = uncertainty;
	}
}
