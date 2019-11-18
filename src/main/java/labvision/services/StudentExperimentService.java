package labvision.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.measure.SI;

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
	
	/**
	 * Get a mapping of measurement IDs to lists of measurement values the student has taken
	 * for a given experiment
	 * @param experimentId
	 * @param studentId
	 * @return map of measurement IDs to lists of measurement value DTOs
	 */
	public Map<Integer, List<MeasurementValueForExperimentView>> getMeasurementValues(int experimentId, int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.MeasurementValueForExperimentView(" +
					"	mv.id," +
					"	m.id," +
					"	m.name," +
					"	mv.value.value," +
					"	mv.value.uncertainty," +
					"	mv.taken," +
					"	m.dimension," + 
					"	m.quantityTypeId) " +
					"FROM Measurement m " +
					"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
					"WHERE m.experiment.id=:experimentid " +
					"ORDER BY mv.taken ASC";
			
			TypedQuery<MeasurementValueForExperimentView> query = manager.createQuery(
					queryString, 
					MeasurementValueForExperimentView.class);
			query.setParameter("studentid", studentId);
			query.setParameter("experimentid", experimentId);
			
			return query.getResultStream()
					.collect(Collectors.groupingBy(
							MeasurementValueForExperimentView::getMeasurementId,
							Collectors.collectingAndThen(Collectors.toList(),
									l -> l.stream()
										.filter(row -> row.getId() != null)
										.map(row -> new MeasurementValueForExperimentView(row, 
												SI.getInstance().getUnitFor(row.getQuantityTypeId()).toString())
												)
										.collect(Collectors.toList()))
							));
		});
	}
	
	public List<ReportedResultForStudentExperimentView> getReportedResults(int experimentId, int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.experiment.ReportedResultForStudentExperimentView(" +
				    "	rr.id," +
				    "	rd.filename," +
				    "	rr.added," +
				    "	rr.score" +
				    ") " +
				    "FROM ReportedResult rr " +
				    "LEFT JOIN rr.reportDocument rd " +
				    "WHERE rr.experiment.id=:experimentid AND rr.student.id=:studentid " +
				    "ORDER BY rr.added DESC";
			TypedQuery<ReportedResultForStudentExperimentView> query = manager.createQuery(
					queryString,
					ReportedResultForStudentExperimentView.class);
			query.setParameter("experimentid", experimentId);
			query.setParameter("studentid", studentId);
			return query.getResultList();
		});
	}
}
