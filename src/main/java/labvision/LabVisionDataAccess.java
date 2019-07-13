package labvision;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import labvision.viewmodels.Dashboard;

/**
 * Provides access to the database to load and maniuplate entity objects.
 * @author davidnisson
 *
 */
public class LabVisionDataAccess {
	private EntityManagerFactory entityManagerFactory;
	
	public LabVisionDataAccess(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public Dashboard getDashboard() {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		Dashboard dashboard = new Dashboard();
		
		// TODO set dashboard values from objects in database
		
		return dashboard;
	}
}
