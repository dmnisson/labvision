package labvision.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;

import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.utils.ThrowingWrappers;

public class StudentExperimentService extends ExperimentService {
	public StudentExperimentService(EntityManagerFactory entityManagerFactory) {
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
	
	public Map<Measurement, List<MeasurementValue>> getMeasurementValues(int experimentId, int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT mv FROM MeasurementValue mv " +
					"JOIN mv.courseClass cc " +
					"JOIN cc.course c " +
					"JOIN c.experiments e " +
					"LEFT JOIN FETCH mv.variable v " +
					"LEFT JOIN FETCH mv.parameterValues pv " +
					"WHERE e.id=:experimentid AND mv.student.id=:studentid";
			TypedQuery<MeasurementValue> query = manager.createQuery(queryString, MeasurementValue.class);
			query.setParameter("experimentid", experimentId);
			query.setParameter("studentid", studentId);
			return query.getResultStream()
					.collect(Collectors.groupingBy(
							MeasurementValue::getVariable));
		});
	}
	
	public List<ReportedResultForStudentExperimentView> getReportedResults(int experimentId, int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.experiment.ReportedResultForStudentExperimentView(" +
				    "	rr.id," +
				    "	rd.filename," +
				    "	rr.added" +
				    ") " +
				    "FROM ReportedResult rr " +
				    "LEFT JOIN rr.reportDocument rd " +
				    "WHERE rr.experiment.id=:experimentid AND rr.student.id=:studentid";
			TypedQuery<ReportedResultForStudentExperimentView> query = manager.createQuery(
					queryString,
					ReportedResultForStudentExperimentView.class);
			query.setParameter("experimentid", experimentId);
			query.setParameter("studentid", studentId);
			return query.getResultList();
		});
	}
	
	public Map<Integer, String> getExperimentPaths(Collection<? extends Integer> experimentIds, ServletContext context) {
		return experimentIds.stream().distinct()
				.collect(Collectors.toMap(
						Function.identity(), 
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathFor(STUDENT_SERVLET_NAME, "/experiment/" + id, context))
						));
	}
	
	public Map<Integer, String> getNewMeasurementValuePaths(Collection<? extends Integer> measurementIds, ServletContext context) {
		return measurementIds.stream().distinct()
				.collect(Collectors.toMap(
						Function.identity(),
						ThrowingWrappers.throwingFunctionWrapper(
								id -> getPathFor(STUDENT_SERVLET_NAME, "/measurement/newvalue/" + id, context))
						));
	}
}
