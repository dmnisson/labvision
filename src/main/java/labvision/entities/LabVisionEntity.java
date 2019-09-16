package labvision.entities;

/**
 * Interface for all LabVision entities with an integer ID
 * @author davidnisson
 *
 */
public interface LabVisionEntity {
	/**
	 * Database identifier
	 * @return identifier
	 */
	int getId();
}
