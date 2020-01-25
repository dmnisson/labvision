package io.github.dmnisson.labvision.entities;

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
	Integer getId();
}
