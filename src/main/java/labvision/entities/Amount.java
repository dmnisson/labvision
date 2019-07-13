package labvision.entities;

import javax.measure.Quantity;
import javax.persistence.Embeddable;

@Embeddable
public class Amount<Q extends Quantity<Q>> {
	/** Value in SI units. */
	double value;
	
	/** Uncertainty in SI units. */
	double uncertainty;
	
	protected Amount() {
	}
	
	public Amount(double value, double uncertainty) {
		this.value = value;
		this.uncertainty = uncertainty;
	}
}
