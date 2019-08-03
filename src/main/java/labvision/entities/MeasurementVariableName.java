package labvision.entities;

import javax.measure.Quantity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * mXparser variable name for a measurement
 * @author davidnisson
 *
 */
@Entity
public class MeasurementVariableName<Q extends Quantity<Q>> {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	
	@OneToOne( targetEntity = Measurement.class )
	private Measurement<Q> measurement;
	
	private String variableName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Measurement<Q> getMeasurement() {
		return measurement;
	}

	public void setMeasurement(Measurement<Q> measurement) {
		this.measurement = measurement;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
