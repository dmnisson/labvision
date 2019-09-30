package labvision.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable;
import labvision.entities.CourseClass;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;

public class InstructorExperimentService extends ExperimentService {

	public InstructorExperimentService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public List<ExperimentForFacultyExperimentTable> getExperiments(int instructorId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable(" +
					"	e.id," +
					"	e.name," +
					"	COUNT(rr)," +
					"	SUM(rr.score)/COUNT(DISTINCT s)" +
					") " +
					"FROM Experiment e " +
					"JOIN e.instructors i " +
					"LEFT JOIN e.reportedResults rr " +
					"LEFT JOIN rr.student s " +
					"WHERE i.id=:instructorid " +
					"GROUP BY e";
			TypedQuery<ExperimentForFacultyExperimentTable> query = manager.createQuery(
					queryString, 
					ExperimentForFacultyExperimentTable.class);
			query.setParameter("instructorid", instructorId);
			return query.getResultList();
		});
	}
	
	public Map<Measurement, Map<CourseClass, Map<Student, List<MeasurementValue>>>> getMeasurementValues(int experimentId, int instructorId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT mv " +
					"FROM MeasurementValue mv " +
					"JOIN FETCH mv.courseClass cc " +
					"JOIN FETCH mv.variable m " +
					"JOIN FETCH mv.student s " +
					"LEFT JOIN FETCH mv.parameterValues pv " +
					"JOIN m.experiment e " +
					"JOIN cc.instructors i " +
					"WHERE e.id=:experimentid AND i.id=:instructorid";
			TypedQuery<MeasurementValue> query = manager.createQuery(queryString, MeasurementValue.class);
			query.setParameter("experimentid", experimentId);
			query.setParameter("instructorid", instructorId);
			return query.getResultStream()
					.collect(Collectors.groupingBy(
							MeasurementValue::getVariable,
							Collectors.groupingBy(
									MeasurementValue::getCourseClass,
									Collectors.groupingBy(
											MeasurementValue::getStudent,
											Collectors.toList()
											))));
		});
	}
}
