package labvision;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

/**
 * Computations of results for measurements
 * @author davidnisson
 *
 */
@Embeddable
public class ResultComputation {
	@OneToMany( targetEntity=Measurement.class )
	List<Measurement<?>> measurements;
	
	/** mXparser formula */
	String formula;
}
