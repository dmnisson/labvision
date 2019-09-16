package labvision.entities;

import java.util.Arrays;
import java.util.List;

import javax.measure.Dimension;
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

import tec.units.ri.quantity.QuantityDimension;

@Entity
@Inheritance( strategy=InheritanceType.TABLE_PER_CLASS )
public abstract class Variable<V extends Variable<V, A>, A extends VariableValue<V, A>> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private int id;
	
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
	public int getId() {
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
	}
	
	public Unit<?> systemUnit() {
		return getQuantityTypeId().getSystemUnit(
				dimensionObjectFor(getDimension()));
	}

	public abstract List<A> getValues();
	
	public abstract void setValues(List<A> values);
	
	public Dimension dimensionObject() {
		return dimensionObjectFor(dimension);
	}
	
	static Dimension dimensionObjectFor(String dimension) {
		if (dimension == null || dimension == "") {
			return QuantityDimension.NONE;
		}
		
		String[] dimensionBaseStrings = dimension.split(" ");
		return Arrays.stream(dimensionBaseStrings)
		.map(bs -> {
			Dimension base = QuantityDimension.parse(bs.charAt(0));
			int p = Integer.parseInt(bs.substring(1));
			return base.pow(p);
		})
		.reduce(QuantityDimension.NONE, Dimension::multiply);
	}
	
	public void updateDimensionObject(Dimension dimension) {
		this.dimension = dimension.getBaseDimensions().entrySet().stream()
				.map(e -> e.getKey().toString().substring(1, 2) + e.getValue())
				.reduce("", (s1, s2) -> String.join(" ", s1, s2));
	}
}
