package labvision;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServlet;

public class LabVisionServlet extends HttpServlet {
	/**
	 * Unique identifier for version for serialization
	 */
	private static final long serialVersionUID = 250946325495998185L;

	private EntityManagerFactory emf;
	
	public void init() {
		// load the config file
		LabVisionConfig config = new LabVisionConfig();
		
		// create the entity manager factory
		emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
	}
}
