package labvision.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;

import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable;
import labvision.entities.CourseClass;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;
import labvision.utils.ThrowingWrappers;

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
	
	// measurement Id -> course class ID -> student ID -> measurement values
	public Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> getMeasurementValues(int experimentId, int instructorId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.MeasurementValueForFacultyExperimentView(" +
					"	mv.id," +
					"	m.id," +
					"	m.name," +
					"	mv.value.value," +
					"	mv.value.uncertainty," +
					"	m.dimension," +
					"	m.quantityTypeId," +
					"	cc.id," +
					"	s.id) " +
					"FROM MeasurementValue mv " +
					"JOIN mv.courseClass cc " +
					"JOIN mv.variable m " +
					"JOIN mv.student s " +
					"JOIN m.experiment e " +
					"JOIN cc.instructors i " +
					"WHERE e.id=:experimentid AND i.id=:instructorid";
			TypedQuery<MeasurementValueForFacultyExperimentView> query = manager.createQuery(
					queryString, MeasurementValueForFacultyExperimentView.class);
			query.setParameter("experimentid", experimentId);
			query.setParameter("instructorid", instructorId);
			return query.getResultStream()
					.collect(Collectors.groupingBy(
							MeasurementValueForFacultyExperimentView::getMeasurementId,
							Collectors.groupingBy(
									MeasurementValueForFacultyExperimentView::getCourseClassId,
									Collectors.groupingBy(
											MeasurementValueForFacultyExperimentView::getStudentId,
											Collectors.toList()
											))));
		});
	}
	
	public Map<Integer, String> getExperimentPaths(int instructorId, ServletContext context) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT e.id FROM Experiment e " +
					"JOIN e.instructors i " +
					"WHERE i.id=:instructorid";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("instructorid", instructorId);
			return query.getResultStream()
					.collect(Collectors.toMap(
							Function.identity(),
							ThrowingWrappers.throwingFunctionWrapper(
									id -> getPathFor(FACULTY_SERVLET_NAME, "/experiment/" + id, context))
							));
		});
	}
	
	public Map<Integer, String> getEditMeasurementPaths(int instructorId, ServletContext context) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT m.id FROM Experiment e " +
					"JOIN e.instructors i " +
					"JOIN e.measurements m " +
					"WHERE i.id=:instructorid";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("instructorid", instructorId);
			return query.getResultStream()
					.collect(Collectors.toMap(
							Function.identity(),
							ThrowingWrappers.throwingFunctionWrapper(
									id -> getPathFor(FACULTY_SERVLET_NAME, "/measurement/" + id + "/edit", context))
							));
		});
	}
}
