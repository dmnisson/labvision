package labvision.entities;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Computations of results for measurements
 * @author davidnisson
 *
 */
@Embeddable
public class ResultComputation {
	@OneToMany( targetEntity=Measurement.class )
	@JoinColumn( name="ResultComputation_id" )
	List<Measurement<?>> measurements;
	
	/** mXparser formula */
	String formula;
}
