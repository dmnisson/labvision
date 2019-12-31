package labvision.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import labvision.ExperimentPrefetch;
import labvision.dto.experiment.ExperimentInfo;
import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import labvision.dto.experiment.ParameterForExperimentView;
import labvision.dto.experiment.ParameterValueForExperimentView;
import labvision.dto.experiment.report.ResultInfo;
import labvision.dto.faculty.experiment.ExperimentForFacultyExperimentTable;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Experiment_;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Measurement_;
import labvision.entities.Parameter;
import labvision.entities.Student;
import labvision.measure.Amount;

public class ExperimentService extends JpaService {

	public ExperimentService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	/**
	 * Get a list of measurement IDs, names, and unit symbols for an experiment
	 * @param experimentId
	 * @return the measurement names and unit strings
	 */
	public List<MeasurementForExperimentView> getMeasurements(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.MeasurementForExperimentView(" +
					"	m.id," +
					"	m.name," +
					"	m.quantityTypeId) " +
					"FROM Measurement m " +
					"WHERE m.experiment.id=:experimentid " +
					"ORDER BY LOWER(m.name) ASC";
			
			TypedQuery<MeasurementForExperimentView> query = manager.createQuery(
					queryString, MeasurementForExperimentView.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream()
					.map(m -> new MeasurementForExperimentView(
							m.getId(), m.getName(), m.getQuantityTypeId()))
					.collect(Collectors.toList());
		});
	}
	
	/**
	 * Get a list of parameter names and units for a given measurement
	 * @param measurementId the ID of the measurement
	 * @return the names and unit strings of the parameters
	 */
	public List<ParameterForExperimentView> getParameters(int measurementId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.ParameterForExperimentView(" +
					"	p.id," +
					"	p.name," +
					"	p.quantityTypeId) " +
					"FROM Parameter p " +
					"WHERE p.measurement.id=:measurementid " +
					"ORDER BY LOWER(p.name) ASC";
			
			TypedQuery<ParameterForExperimentView> query = manager.createQuery(
					queryString, ParameterForExperimentView.class);
			query.setParameter("measurementid", measurementId);
			return query.getResultStream()
					.map(r -> new ParameterForExperimentView(
							r.getId(), r.getName(), r.getQuantityTypeId()))
					.collect(Collectors.toList());
		});
	}
	
	/**
	 * Get a mapping of parameter IDs to Amount objects representing values of parameters
	 * @param measurementValueId the measurement value ID
	 * @return the parameter amounts mapped to the parameter IDs
	 */
	public Map<Integer, ParameterValueForExperimentView> getParameterValues(int measurementValueId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.ParameterValueForExperimentView(" +
					"	pv.id," +
					"	pv.variable.id," +
					"	pv.value.value," +
					"	pv.value.uncertainty) " +
					"FROM ParameterValue pv " +
					"WHERE pv.measurementValue.id=:measurementvalueid";
			
			TypedQuery<ParameterValueForExperimentView> query = manager.createQuery(
					queryString, ParameterValueForExperimentView.class);
			query.setParameter("measurementvalueid", measurementValueId);
			return query.getResultStream()
					.collect(Collectors.toMap(
							ParameterValueForExperimentView::getParameterId,
							Function.identity()));
		});
	}
	
	public Experiment getExperiment(int id, ExperimentPrefetch prefetchValues) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<Experiment> cq = cb.createQuery(Experiment.class);
			Root<Experiment> e = cq.from(Experiment.class);
			if (!prefetchValues.equals(ExperimentPrefetch.NO_PREFETCH)) {
				Fetch<Experiment, Measurement> m = e.fetch(Experiment_.measurements,
						JoinType.LEFT);
				m.fetch(Measurement_.parameters,
						JoinType.LEFT);
				e.fetch(Experiment_.course, JoinType.LEFT);
				e.fetch(Experiment_.reportedResults, JoinType.LEFT);
				e.fetch(Experiment_.instructors, JoinType.LEFT);
			}
			cq.select(e).where(cb.equal(e.get(Experiment_.id), id));
			
			TypedQuery<Experiment> query = manager.createQuery(cq);
			Experiment experiment = query.getResultStream().findAny().orElse(null);
			
			if (Objects.isNull(experiment)) return null;
			
			if (prefetchValues.equals(ExperimentPrefetch.NO_PREFETCH)) {
				return experiment;
			}
			
			// fetch course classes and students after the first query
			// so as to avoid a Cartesian product
			experiment.getCourse().getCourseClasses().size();
			experiment.getCourse().getCourseClasses().forEach(cc -> {
				cc.getStudents().size();
			});
			
			if (!prefetchValues.equals(ExperimentPrefetch.PREFETCH_VALUES)) {
				return experiment;
			}
			
			// fetch values after the first query so as to avoid a Cartesian product
			experiment.getMeasurements().stream()
			.flatMap(measurement -> 
				Stream.concat(Stream.of(measurement),
						measurement.getParameters().stream()))
			.forEach(variable -> variable.getValues().size());
			
			experiment.getMeasurements().stream()
			.flatMap(measurement -> measurement.getValues().stream())
			.forEach(value -> value.getParameterValues().size());
			
			return experiment;
		});
	}
	
	/**
	 * Add a measurement to an experiment
	 * @param experiment the experiment to add
	 * @param name the name of the measurement
	 * @param quantityClass the quantity type
	 * @throws IllegalArgumentException if the quantity type is unknown
	 * @return the new measurement
	 */
	public <Q extends Quantity<Q>> Measurement addMeasurement(
			Experiment experiment, String name, Class<Q> quantityClass) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Measurement measurement = experiment.addMeasurement(name, quantityClass);
			manager.persist(measurement);
			manager.merge(experiment);
			
			manager.getTransaction().commit();
			
			return measurement;
		});
	}
	
	/**
	 * Add a measurement to an experiment
	 * @param experiment the experiment to add
	 * @param name the name of the measurement
	 * @param quantityClass the quantity type
	 * @param dimension the dimension; may be null if the quantity class is recognized
	 * @throws IllegalArgumentException if the dimension is unspecified for an unknown quantity type or if the dimension 
	 * is inconsistent with that of the quantity type
	 * @return the new measurement
	 */
	public <Q extends Quantity<Q>> Measurement addMeasurement(
			Experiment experiment, String name, Class<Q> quantityClass, Dimension dimension) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Measurement measurement = experiment.addMeasurement(name, quantityClass, dimension);
			manager.persist(measurement);
			manager.merge(experiment);
			
			manager.getTransaction().commit();
			
			return measurement;
		});
	}
	
	/**
	 * Add a parameter to a measurement
	 * @param measurement the measurement
	 * @param name the parameter name
	 * @param quantityType the quantity type
	 * @throws IllegalArgumentException if the quantity type is unknown
	 * @return the new parameter
	 */
	public <Q extends Quantity<Q>> Parameter addParameter(Measurement measurement, String name, Class<Q> quantityType) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Parameter parameter = measurement.addParameter(name, quantityType);
			manager.persist(parameter);
			manager.merge(measurement);
			
			manager.getTransaction().commit();
			
			return parameter;
		});
	}
	
	/**
	 * Add a parameter to a measurement
	 * @param measurement the measurement
	 * @param name the parameter name
	 * @param quantityType the quantity type
	 * @param dimension the dimension; may be null if known from quantity type
	 * @throws IllegalArgumentException if the dimension is unspecified for an unknown quantity type or if the dimension 
	 * is inconsistent with that of the quantity type
	 * @return the new parameter
	 */
	public <Q extends Quantity<Q>> Parameter addParameter(Measurement measurement, String name, Class<Q> quantityType, Dimension dimension) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Parameter parameter = measurement.addParameter(name, quantityType, dimension);
			manager.persist(parameter);
			manager.merge(measurement);
			
			manager.getTransaction().commit();
			
			return parameter;
		});
	}
	
	/**
	 * Add a measurement value
	 * @param student
	 * @param measurement
	 * @param measurementAmount
	 * @param parameterAmounts
	 * @param courseClass
	 * @return
	 */
	
	public MeasurementValue addMeasurementValue(Student student, Measurement measurement,
			Amount<?> measurementAmount, Map<Parameter, Amount<?>> parameterAmounts, CourseClass courseClass) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			// necessary for collections and statistical analysis results to update properly
			Measurement managedMeasurement = manager.merge(measurement);
			Student managedStudent = manager.merge(student);
			CourseClass managedCourseClass = manager.merge(courseClass);
			
			MeasurementValue value = managedMeasurement.addValue(
					managedStudent,
					managedCourseClass,
					measurementAmount,
					LocalDateTime.now());
			
			parameterAmounts.forEach((parameter, amount) -> {
				Parameter managedParameter = manager.merge(parameter);
				value.addParameterValue(managedParameter, amount);
			});
			
			manager.persist(value);
			manager.getTransaction().commit();
			
			return value;
		});
	}
	
	public Measurement getMeasurement(int id, boolean prefetch) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			CriteriaQuery<Measurement> cq = cb.createQuery(Measurement.class);
			Root<Measurement> m = cq.from(Measurement.class);
			if (prefetch) {
				m.fetch(Measurement_.experiment, JoinType.LEFT)
					.fetch(Experiment_.course, JoinType.LEFT);
				m.fetch(Measurement_.parameters, JoinType.LEFT);
			}
			cq.select(m).where(cb.equal(m.get(Measurement_.id), id));
			TypedQuery<Measurement> query = manager.createQuery(cq);
			return query.getResultStream().findAny().orElse(null);
		});
	}
	
	public Map<Measurement, String> getMeasurementUnits(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT m FROM Measurement m " +
					"LEFT JOIN FETCH m.parameters p " +
					"WHERE m.experiment.id=:experimentid";
			TypedQuery<Measurement> query = manager.createQuery(queryString, Measurement.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream()
					.collect(Collectors.toMap(
							Function.identity(),
							m -> m.systemUnit(m.getQuantityTypeId().getQuantityClass().getQuantityType())
							.getSymbol()));
		});
	}

	public Map<Parameter, String> getParameterUnits(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT DISTINCT p FROM Parameter p " +
					"WHERE p.measurement.experiment.id=:experimentid";
			TypedQuery<Parameter> query = manager.createQuery(queryString, Parameter.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream()
					.collect(Collectors.toMap(
							Function.identity(),
							p -> p.systemUnit(p.getQuantityTypeId().getQuantityClass().getQuantityType())
							.getSymbol()));
		});
	}
	
	public ExperimentInfo getExperimentInfo(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.ExperimentInfo(" +
					"	e.id," +
					"	e.name," +
					"	e.course.name" +
					") FROM Experiment e " +
					"WHERE e.id=:experimentid";
			TypedQuery<ExperimentInfo> query = manager.createQuery(queryString, ExperimentInfo.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public List<ResultInfo> getAcceptedResults(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.report.ResultInfo(" +
					"	ar.id," +
					"	ar.name," +
					"	ar.value.value," +
					"	ar.value.uncertainty," +
					"	ar.variable.quantityTypeId" +
					") FROM Experiment e " +
					"JOIN e.acceptedResults ar " +
					"WHERE e.id=:experimentid";
			TypedQuery<ResultInfo> query = manager.createQuery(queryString, ResultInfo.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultList();
		});
	}
	
	public LocalDateTime getReportDueDate(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT e.reportDueDate FROM Experiment e " +
					"WHERE e.id=:experimentid";
			TypedQuery<LocalDateTime> query = manager.createQuery(queryString, LocalDateTime.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public List<Integer> getInstructorIdsFor(int experimentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT i.id FROM Experiment e " +
					"JOIN e.instructors i " +
					"WHERE e.id=:experimentid";
			TypedQuery<Integer> query = manager.createQuery(queryString, Integer.class);
			query.setParameter("experimentid", experimentId);
			return query.getResultList();
		});
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

	public Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> getMeasurementValuesForInstructor(int experimentId, int instructorId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.experiment.MeasurementValueForFacultyExperimentView(" +
					"	mv.id," +
					"	m.id," +
					"	m.name," +
					"	mv.value.value," +
					"	mv.value.uncertainty," +
					"	mv.taken," +
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
										.collect(Collectors.toList()))
							));
		});
	}

	public List<ReportedResultForStudentExperimentView> getReportedResults(int experimentId, int studentId) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.student.experiment.ReportedResultForStudentExperimentView(" +
				    "	rr.id," +
					"	rr.name," +
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
