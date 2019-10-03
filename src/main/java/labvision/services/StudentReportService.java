package labvision.services;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;

import labvision.utils.ThrowingWrappers;

public class StudentReportService extends ReportService {
	public StudentReportService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public Map<Integer, String> getReportPaths(int experimentId, ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT rr.id FROM Experiment e " +
					"JOIN e.reportedResults rr " +
					"WHERE e.id=:experimentid";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream()
					.collect(Collectors.toMap(Function.identity(), 
							ThrowingWrappers.throwingFunctionWrapper(
									id -> getPathFor(STUDENT_SERVLET_NAME, "/report/" + id, context))));
		});
		//;
	}
	
	public String getNewReportPath(ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathFor(STUDENT_SERVLET_NAME, "/report/new", context);
	}
}
