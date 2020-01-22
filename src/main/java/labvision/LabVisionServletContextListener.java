package labvision;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import labvision.auth.DeviceAuthentication;
import labvision.auth.DeviceTokenSigning;
import labvision.services.CourseService;
import labvision.services.DashboardService;
import labvision.services.ExperimentService;
import labvision.services.InstructorService;
import labvision.services.JpaService;
import labvision.services.ReportService;
import labvision.services.StudentService;
import labvision.services.UserService;

public class LabVisionServletContextListener implements ServletContextListener {

	public static final String DEVICE_AUTHENTICATION_ATTR = "deviceAuthentication";
	public static final String PATH_CONSTRUCTOR_ATTR = "pathConstructor";
	public static final String COURSE_SERVICE_ATTR = "courseService";
	public static final String STUDENT_DASHBOARD_SERVICE_ATTR = "studentDashboardService";
	public static final String STUDENT_SERVICE_ATTR = "studentService";
	public static final String EXPERIMENT_SERVICE_ATTR = "experimentService";
	public static final String INSTRUCTOR_SERVICE_ATTR = "instructorService";
	public static final String USER_SERVICE_ATTR = "userService";
	public static final String ENTITY_MANAGER_FACTORY_ATTR = "emf";
	public static final String CONFIG_ATTR = "config";
	public static final String REPORT_SERVICE_ATTR = "reportService";
	public static final String DEVICE_TOKEN_SIGNING_ATTR = "deviceTokenSigning";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String configPath = event.getServletContext().getInitParameter("config_file");
		LabVisionConfig config = new LabVisionConfig(configPath);
		
		event.getServletContext().setAttribute(CONFIG_ATTR, config);
		
		DeviceTokenSigning deviceTokenSigning = new DeviceTokenSigning(config);
		event.getServletContext().setAttribute(DEVICE_TOKEN_SIGNING_ATTR, deviceTokenSigning);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
		
		event.getServletContext().setAttribute(ENTITY_MANAGER_FACTORY_ATTR, emf);
		
		IPathConstructor pathConstructor = new PathConstructor(event.getServletContext());
		event.getServletContext().setAttribute(PATH_CONSTRUCTOR_ATTR, pathConstructor);
		
		UserService userService = new UserService(emf);
		event.getServletContext().setAttribute(USER_SERVICE_ATTR, userService);
		
		DeviceAuthentication deviceAuthentication = new DeviceAuthentication(config);
		event.getServletContext().setAttribute(DEVICE_AUTHENTICATION_ATTR, deviceAuthentication);
		
		StudentService studentService = new StudentService(emf, config);
		event.getServletContext().setAttribute(STUDENT_SERVICE_ATTR, studentService);
		
		InstructorService instructorService = new InstructorService(emf);
		event.getServletContext().setAttribute(INSTRUCTOR_SERVICE_ATTR, instructorService);
		
		ExperimentService experimentService = new ExperimentService(emf);
		event.getServletContext().setAttribute(EXPERIMENT_SERVICE_ATTR, experimentService);
		
		JpaService studentDashboardService = new DashboardService(emf, config);
		event.getServletContext().setAttribute(STUDENT_DASHBOARD_SERVICE_ATTR, studentDashboardService);
		
		CourseService studentCourseService = new CourseService(emf);
		event.getServletContext().setAttribute(COURSE_SERVICE_ATTR, studentCourseService);
		
		ReportService reportService = new ReportService(emf, config);
		event.getServletContext().setAttribute(REPORT_SERVICE_ATTR, reportService);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		EntityManagerFactory emf = (EntityManagerFactory) event.getServletContext().getAttribute(ENTITY_MANAGER_FACTORY_ATTR);
		if (emf != null) {
			emf.close();
		}
	}
}
