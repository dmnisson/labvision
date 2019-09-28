package labvision.services;

import java.util.List;

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
			String queryString = 
					"SELECT new labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard(" +
					"	e.id," +
					"	e.name," +
					"	c.id," +
					"	c.name" +
					") " +
					"FROM Student s " +
					"JOIN s.activeExperiments e " +
					"JOIN e.course c " +
					"WHERE s.id=:studentid";
			TypedQuery<CurrentExperimentForStudentDashboard> query = manager.createQuery(
					queryString,
					CurrentExperimentForStudentDashboard.class);
			query.setParameter("studentid", studentId);
			return query.getResultList();
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
			String queryString =
					"SELECT new labvision.dto.student.dashboard.RecentExperimentForStudentDashboard(" +
				    "	e.id," +
				    "	e.name," +
				    "	MAX(mv.taken)" +
					") " +
					"FROM MeasurementValue mv " +
					"JOIN mv.courseClass cc " +
					"JOIN cc.students s " +
					"JOIN cc.course c " +
					"JOIN c.experiments e " +
					"WHERE s.id=:studentid " +
					"GROUP BY e " +
					"ORDER BY MAX(mv.taken) DESC";
			TypedQuery<RecentExperimentForStudentDashboard> query = manager.createQuery(
					queryString, 
					RecentExperimentForStudentDashboard.class);
			query.setParameter("studentid", studentId);
			query.setMaxResults(limit);
			return query.getResultList();
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
				    "	MAX(mv.taken)" +
					") " +
					"FROM MeasurementValue mv " +
					"JOIN mv.courseClass cc " +
					"JOIN cc.students s " +
					"JOIN cc.course c " +
					"WHERE s.id=:studentid " +
					"GROUP BY c " +
					"ORDER BY MAX(mv.taken) DESC";
			TypedQuery<RecentCourseForStudentDashboard> query = manager.createQuery(
					queryString, 
					RecentCourseForStudentDashboard.class);
			query.setParameter("studentid", studentId);
			query.setMaxResults(limit);
			return query.getResultList();
		});
	}
}
