package labvision.services;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

public class StudentReportService extends ReportService {
	public StudentReportService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public String getNewReportPath(ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathFor(STUDENT_SERVLET_NAME, "/report/new", context);
	}
}
