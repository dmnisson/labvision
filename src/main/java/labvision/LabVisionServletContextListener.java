package labvision;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import labvision.viewmodels.NavbarModel;

public class LabVisionServletContextListener implements ServletContextListener {

	public static final String STUDENT_NAVBAR_ATTR = "studentNavbar";
	public static final String DATA_ACCESS_ATTR = "dataAccess";
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
		
		LabVisionDataAccess dataAccess = new LabVisionDataAccess(emf);
		
		event.getServletContext().setAttribute(DATA_ACCESS_ATTR, dataAccess);
		
		event.getServletContext().setAttribute(STUDENT_NAVBAR_ATTR, initStudentNavbar());
	}

	private NavbarModel initStudentNavbar() {
		NavbarModel model = new NavbarModel();
		
		model.addNavLink("Dashboard", "/student/dashboard");
		model.addNavLink("Experiments", "/student/experiments");
		model.addNavLink("Reports",	"/student/reports");
		model.addNavLink("Errors", "/student/errors");
		model.addNavLink(model.new NavLink(
				"Account", 
				"#", 
				new NavbarModel.NavLink[] {
						model.new NavLink("Profile", "/student/profile"),
						model.new NavLink("Courses", "/student/courses")
				}
			));
		
		model.setLogoutLink("/logout/student");
		
		return model;
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		EntityManagerFactory emf = (EntityManagerFactory) event.getServletContext().getAttribute(ENTITY_MANAGER_FACTORY_ATTR);
		emf.close();
	}
}
