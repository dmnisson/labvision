package labvision.services;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;

public class StudentExperimentTableService extends JpaService {

	public StudentExperimentTableService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public List<CurrentExperimentForStudentExperimentTable> getCurrentExperiments(int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable(" +
					"	e.id," +
					"	e.name," +
					"	e.reportDueDate," +
					"	MAX(rr.added)," +
					"	SUM(rr.score)" +
					") " +
					"FROM Student s " +
					"JOIN s.activeExperiments e " +
					"LEFT JOIN e.reportedResults rr " +
					"WHERE s.id=:studentid AND (rr.student.id IS NULL OR rr.student.id=:studentid) " +
					"GROUP BY e ";
			TypedQuery<CurrentExperimentForStudentExperimentTable> query = manager.createQuery(
					queryString,
					CurrentExperimentForStudentExperimentTable.class
					);
			query.setParameter("studentid", studentId);
			return query.getResultList();
		});
	}
	
	public List<PastExperimentForStudentExperimentTable> getPastExperiments(int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.experiment.PastExperimentForStudentExperimentTable(" +
				    "	e.id," +
					"	e.name," +
				    "	COUNT(rr)," +
					"	MAX(rr.added)," +
				    "	SUM(rr.score)" +
					") " +
				    "FROM Experiment e " +
					"JOIN e.measurements m " +
				    "JOIN m.values mv " +
					"JOIN mv.student s " +
				    "LEFT JOIN e.reportedResults rr " +
					"LEFT JOIN s.activeExperiments ae " +
					"WHERE s.id=:studentid AND " +
					"(rr.student.id IS NULL OR rr.student.id=:studentid) " +
					"GROUP BY e " +
					"HAVING SUM(CASE WHEN ae.id=e.id THEN 1 END) = 0";
			TypedQuery<PastExperimentForStudentExperimentTable> query = manager.createQuery(
					queryString,
					PastExperimentForStudentExperimentTable.class);
			query.setParameter("studentid", studentId);
			return query.getResultList();
		});
	}
}
