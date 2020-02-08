package io.github.dmnisson.labvision.entities;

import javax.measure.Quantity;

/**
 * A class that stores a quantity type as a Class object
 * @param <Q> the quantity type
 * @author davidnisson
 *
 */
public class QuantityClass<Q extends Quantity<Q>> {
	private final Class<Q> quantityType;

	public QuantityClass(Class<Q> quantityType) {
		this.quantityType = quantityType;
	}

	public Class<Q> getQuantityType() {
		return quantityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((quantityType == null) ? 0 : quantityType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuantityClass<?> other = (QuantityClass<?>) obj;
		if (quantityType == null) {
			if (other.quantityType != null)
				return false;
		} else if (!quantityType.equals(other.quantityType))
			return false;
		return true;
	}
}
