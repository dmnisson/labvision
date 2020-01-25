package io.github.dmnisson.labvision.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import io.github.dmnisson.labvision.measure.SI;
import tec.units.ri.quantity.QuantityDimension;

@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class Variable<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private Integer id;
	
	/**
	 * The name of the variable
	 */
	private String name;
	
	/**
	 * The quantity type identifier
	 */
	@Enumerated(EnumType.STRING)
	private QuantityTypeId quantityTypeId;
	
	/**
	 * The dimensions as a string.
	 * The string format consists of a space separated list of pairs of each character
	 * representing the dimension followed immediately by the integer, e.g.
	 * 
	 * I1 T1
	 * 
	 * for electric charge. The string is empty for dimensionless quantities.
	 */
	private String dimension;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public QuantityTypeId getQuantityTypeId() {
		return quantityTypeId;
	}

	public void setQuantityTypeId(QuantityTypeId quantityTypeId) {
		this.quantityTypeId = quantityTypeId;
		if (!quantityTypeId.equals(QuantityTypeId.UNKNOWN)) {
			updateDimensionObject(
					SI.getInstance().getUnit(
							quantityTypeId.getQuantityClass().getQuantityType())
						.getDimension()
					);
		}
	}
	
	public <Q extends Quantity<Q>> Unit<Q> systemUnit(Class<Q> quantityType) {
		if (quantityTypeId.equals(QuantityTypeId.UNKNOWN)) {
			return SI.getInstance().makeOrGetUnit(dimensionObjectFor(dimension))
					.asType(quantityType);
		} else {
			return SI.getInstance().getUnit(quantityTypeId.getQuantityClass().getQuantityType())
					.asType(quantityType);
		}
	}

	public abstract List<A> getValues();
	
	public abstract void setValues(List<A> values);
	
	public abstract void addValue(A value);
	
	public Dimension dimensionObject() {
		return dimensionObjectFor(dimension);
	}
	
	public void updateDimensionObject(Dimension dimension) {
		if (!quantityTypeId.equals(QuantityTypeId.UNKNOWN)) {
			Dimension quantityDimension = QuantityDimension.of(
					quantityTypeId.getQuantityClass().getQuantityType());
			
			if (!Objects.isNull(quantityDimension) && 
					!quantityDimension.equals(dimension)) {
				throw new IllegalStateException("Dimension " + dimension.toString() + " " +
						" is incompatible with that of the specified quantity type, " +
						quantityDimension.toString());
			}
		}
		this.dimension = dimensionStringFor(dimension);
	}
	
	/**
	 * Updates the quantityTypeId and dimension fields for the given quantity type
	 * @param quantityType the quantity type
	 * @param dimension the dimension
	 */
	public <Q extends Quantity<Q>> void updateQuantityType(Class<Q> quantityType, Dimension dimension) {
		QuantityTypeId quantityTypeId = QuantityTypeId.of(quantityType);
		
		if (quantityTypeId.equals(QuantityTypeId.UNKNOWN) && dimension == null) {
			throw new IllegalArgumentException(
					"No dimension specified for unknown quantity type of class "
			+ quantityType);
		}
		
		if (!QuantityDimension.of(quantityType).equals(dimension)) {
			throw new IllegalArgumentException(
					"Dimension " + dimension + " is incompatible with quantity class " + quantityType
			);
		}
		
		setQuantityTypeId(QuantityTypeId.of(quantityType));
		if (dimension != null) {
			updateDimensionObject(dimension);
		}
	}
	
	/**
	 * Get a Dimension object from a string representation that was in the database
	 * @param dimension the dimension object
	 * @return the string representation
	 */
	public static Dimension dimensionObjectFor(String dimension) {
		if (dimension == null || dimension == "") {
			return QuantityDimension.NONE;
		}
		
		String[] dimensionBaseStrings = Stream.of(dimension.split(" "))
				.filter(bs -> !bs.isEmpty())
				.toArray(String[]::new);
		return Arrays.stream(dimensionBaseStrings)
		.map(bs -> {
			Dimension base = QuantityDimension.parse(bs.charAt(0));
			int p = Integer.parseInt(bs.substring(1));
			return base.pow(p);
		})
		.reduce(QuantityDimension.NONE, Dimension::multiply);
	}
	
	
	/**
	 * Create string representation that is used to store the dimension in the database
	 * @param dimension the dimension object
	 * @return the string representation as it would appear in the database
	 */
	public static String dimensionStringFor(Dimension dimension) {
		Map<? extends Dimension, Integer> baseDimensions = dimension.getBaseDimensions();
		if (baseDimensions == null) {
			if (dimension.equals(QuantityDimension.NONE)) {
				return "";
			}
			return dimension.toString().substring(1, 2) + "1";
		} else {
			return baseDimensions.entrySet().stream()
					.map(e -> e.getKey().toString().substring(1, 2) + e.getValue())
					.reduce("", (s1, s2) -> String.join(" ", s1, s2));
		}
	}

	@Override
	public int hashCode() {
		return 19;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable<?, ?> other = (Variable<?, ?>) obj;
		if (id == null)
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}
}
