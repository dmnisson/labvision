package labvision;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LabVisionServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String configPath = event.getServletContext().getInitParameter("config_file");
		LabVisionConfig config = new LabVisionConfig(configPath);
		
		event.getServletContext().setAttribute("config", config);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
		
		event.getServletContext().setAttribute("emf", emf);
		
		LabVisionDataAccess dataAccess = new LabVisionDataAccess(emf);
		
		event.getServletContext().setAttribute("dataAccess", dataAccess);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		EntityManagerFactory emf = (EntityManagerFactory) event.getServletContext().getAttribute("emf");
		emf.close();
	}
}
