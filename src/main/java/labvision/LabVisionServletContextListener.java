package labvision;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import labvision.services.ExperimentService;
import labvision.services.InstructorService;
import labvision.services.StudentService;
import labvision.services.UserService;

public class LabVisionServletContextListener implements ServletContextListener {

	public static final String STUDENT_SERVICE_ATTR = "studentService";
	public static final String EXPERIMENT_SERVICE_ATTR = "experimentService";
	public static final String INSTRUCTOR_SERVICE_ATTR = "instructorService";
	public static final String USER_SERVICE_ATTR = "userService";
	public static final String ENTITY_MANAGER_FACTORY_ATTR = "emf";
	public static final String CONFIG_ATTR = "config";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String configPath = event.getServletContext().getInitParameter("config_file");
		LabVisionConfig config = new LabVisionConfig(configPath);
		
		event.getServletContext().setAttribute(CONFIG_ATTR, config);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
		
		event.getServletContext().setAttribute(ENTITY_MANAGER_FACTORY_ATTR, emf);
		
		UserService userService = new UserService(emf);
		event.getServletContext().setAttribute(USER_SERVICE_ATTR, userService);
		
		StudentService studentService = new StudentService(emf, config);
		event.getServletContext().setAttribute(STUDENT_SERVICE_ATTR, studentService);
		
		InstructorService instructorService = new InstructorService(emf);
		event.getServletContext().setAttribute(INSTRUCTOR_SERVICE_ATTR, instructorService);
		
		ExperimentService experimentService = new ExperimentService(emf);
		event.getServletContext().setAttribute(EXPERIMENT_SERVICE_ATTR, experimentService);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		EntityManagerFactory emf = (EntityManagerFactory) event.getServletContext().getAttribute(ENTITY_MANAGER_FACTORY_ATTR);
		if (emf != null) {
			emf.close();
		}
	}
}
