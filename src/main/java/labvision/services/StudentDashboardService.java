package labvision.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import labvision.LabVisionConfig;
import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;

public class StudentDashboardService extends JpaService {
	private final LabVisionConfig config;
	
	public StudentDashboardService(EntityManagerFactory entityManagerFactory, LabVisionConfig config) {
		super(entityManagerFactory);
		this.config = config;
	}

	public List<CurrentExperimentForStudentDashboard> getCurrentExperiments(int studentId) {
		return withEntityManager(manager -> {
			String baseQueryString = 
					"SELECT new labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard(" +
					"	e.id," +
					"	e.name," +
					"	c.id," +
					"	c.name," +
					EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
					"	e.reportDueDate" +
					") " +
					"FROM Student s " +
					"JOIN s.activeExperiments e " +
					"JOIN e.course c " +
					"LEFT JOIN e.measurements m " +
					"LEFT JOIN m.values mv " +
					"LEFT JOIN e.reportedResults rr " +
					"LEFT JOIN mv.student s2 " +
					"LEFT JOIN rr.student s3 " +
					"WHERE s.id=:studentid AND " +
					"(s2.id IS NULL OR s2.id=:studentid) AND " +
					"(s3.id IS NULL OR s3.id=:studentid) ";
			
			String queryString1 = baseQueryString +
					"AND rr.added IS NULL " +
					"GROUP BY e.id, e.name, c.id, c.name " +
					"ORDER BY e.reportDueDate ASC";
			
			TypedQuery<CurrentExperimentForStudentDashboard> query1 = manager.createQuery(
					queryString1,
					CurrentExperimentForStudentDashboard.class);
			query1.setParameter("studentid", studentId);
			
			String queryString2 = baseQueryString +
					"AND rr.added IS NOT NULL " +
					"GROUP BY e.id, e.name, c.id, c.name " +
					"ORDER BY lu DESC";
			
			TypedQuery<CurrentExperimentForStudentDashboard> query2 = manager.createQuery(
					queryString2,
					CurrentExperimentForStudentDashboard.class);
			query2.setParameter("studentid", studentId);
			
			return Stream.concat(query1.getResultStream(), query2.getResultStream())
					.collect(Collectors.toList());
		});
	}
	
	public List<RecentExperimentForStudentDashboard> getRecentExperiments(int studentId) {
		int limit = withEntityManager(manager -> {
			String queryString =
					"SELECT sp.maxRecentExperiments " +
					"FROM StudentPreferences sp " +
					"WHERE sp.student.id=:studentid";
			TypedQuery<Integer> limitQuery = manager.createQuery(queryString, Integer.class);
			limitQuery.setParameter("studentid", studentId);
			return limitQuery.getResultStream().findAny()
					.orElseGet(() -> config.getStudentDashboardMaxRecentExperiments());
		});
		return getRecentExperiments(studentId, limit);
	}
	
	public List<RecentExperimentForStudentDashboard> getRecentExperiments(int studentId, int limit) {
		return withEntityManager(manager -> {
			String baseQueryString =
					"SELECT new labvision.dto.student.dashboard.RecentExperimentForStudentDashboard(" +
				    "	e.id," +
				    "	e.name," +
				    EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
				    "	MAX(mv.taken)," +
				    "	e.reportDueDate" +
					") " +
					"FROM Experiment e " +
					"LEFT JOIN e.measurements m " +
					"LEFT JOIN m.values mv " +
					"LEFT JOIN mv.student s " +
					"LEFT JOIN e.reportedResults rr " +
					"LEFT JOIN rr.student s2 " +
					"WHERE (s.id IS NULL OR s.id=:studentid) AND " +
					"(s2.id IS NULL OR s2.id=:studentid) AND " +
					"(s.id IS NOT NULL OR s2.id IS NOT NULL)";
			
			String queryString1 = baseQueryString +
					"AND rr.added IS NULL " +
					"GROUP BY e.id, e.name " +
					"ORDER BY e.reportDueDate ASC";
			TypedQuery<RecentExperimentForStudentDashboard> query1 = manager.createQuery(
					queryString1, 
					RecentExperimentForStudentDashboard.class);
			query1.setParameter("studentid", studentId);
			query1.setMaxResults(limit);
			
			String queryString2 = baseQueryString +
					"AND rr.added IS NOT NULL " +
					"GROUP BY e.id, e.name " +
					"ORDER BY lu DESC";
			TypedQuery<RecentExperimentForStudentDashboard> query2 = manager.createQuery(
					queryString2, 
					RecentExperimentForStudentDashboard.class);
			query2.setParameter("studentid", studentId);
			query2.setMaxResults(limit);
			
			return Stream.concat(query1.getResultStream(), query2.getResultStream())
					.limit(limit)
					.collect(Collectors.toList());
		});
	}
	
	public List<RecentCourseForStudentDashboard> getRecentCourses(int studentId) {
		int limit = withEntityManager(manager -> {
			String queryString =
					"SELECT sp.maxRecentCourses " +
					"FROM StudentPreferences sp " +
					"WHERE sp.student.id=:studentid";
			TypedQuery<Integer> limitQuery = manager.createQuery(queryString, Integer.class);
			limitQuery.setParameter("studentid", studentId);
			return limitQuery.getResultStream().findAny()
					.orElseGet(() -> config.getStudentDashboardMaxRecentCourses());
		});
		return getRecentCourses(studentId, limit);
	}
	
	public List<RecentCourseForStudentDashboard> getRecentCourses(int studentId, int limit) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.dashboard.RecentCourseForStudentDashboard(" +
				    "	c.id," +
				    "	c.name," +
				    "	MAX(mv.taken)," +
				    EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu" +
					") " +
				    "FROM Course c " +
					"JOIN c.experiments e " +
				    "LEFT JOIN e.measurements m " +
					"LEFT JOIN m.values mv " +
				    "LEFT JOIN mv.student s " +
				    "LEFT JOIN e.reportedResults rr " +
					"LEFT JOIN rr.student s2 " +
					"WHERE (s.id IS NULL OR s.id=:studentid) " +
					"AND (s2.id IS NULL OR s2.id=:studentid) " +
					"AND (s.id IS NOT NULL OR s2.id IS NOT NULL)" +
					"GROUP BY c " +
					"ORDER BY lu DESC";
			TypedQuery<RecentCourseForStudentDashboard> query = manager.createQuery(
					queryString, 
					RecentCourseForStudentDashboard.class);
			query.setParameter("studentid", studentId);
			query.setMaxResults(limit);
			return query.getResultList();
		});
	}
}
