package labvision.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			String baseQueryString =
					"SELECT new labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable(" +
					"	e.id," +
					"	e.name," +
					EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
					"	e.reportDueDate," +
					"	MAX(rr.added)," +
					"	CASE WHEN COUNT(rr.score) > 0 THEN SUM(rr.score) ELSE 0 END" +
					") " +
					"FROM Student s " +
					"JOIN s.activeExperiments e " +
					"LEFT JOIN e.reportedResults rr ON rr.student.id=:studentid " +
					"LEFT JOIN e.measurements m " +
					"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
					"WHERE s.id=:studentid";
			
			String queryString1 = baseQueryString + " " +
					"AND rr.id IS NULL AND mv.id IS NULL " +
					"GROUP BY e " +
					"ORDER BY e.reportDueDate ASC";
			
			// TODO consider refactoring
			TypedQuery<CurrentExperimentForStudentExperimentTable> query1 = manager.createQuery(
					queryString1,
					CurrentExperimentForStudentExperimentTable.class
					);
			query1.setParameter("studentid", studentId);
			
			TypedQuery<CurrentExperimentForStudentExperimentTable> query2 = manager.createNamedQuery(
					"CurrentExperimentForStudentExperimentTable_DataSubmitted",
					CurrentExperimentForStudentExperimentTable.class
					);
			query2.setParameter("studentid", studentId);
			return Stream.concat(query1.getResultStream(), query2.getResultStream())
					.collect(Collectors.toList());
		});
	}
	
	public List<PastExperimentForStudentExperimentTable> getPastExperiments(int studentId) {
		return withEntityManager(manager -> {
			TypedQuery<PastExperimentForStudentExperimentTable> query = manager.createNamedQuery(
					"PastExperimentForStudentExperimentTable",
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
