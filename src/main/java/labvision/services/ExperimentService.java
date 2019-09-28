package labvision.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import labvision.ExperimentPrefetch;
import labvision.entities.Experiment;
import labvision.entities.Experiment_;
import labvision.entities.Instructor;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Measurement_;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.measure.Amount;

public class ExperimentService extends JpaService {

	public ExperimentService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
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

	public List<ReportedResult> getReportedResults(Experiment experiment, Instructor instructor) {
		return withEntityManager(manager -> {
			String queryString = "SELECT rr FROM ReportedResult rr " +
					"JOIN rr.experiment e " +
					"JOIN e.instructors i " +
					"WHERE e.id=:experimentid AND i.id=:instructorid";
			TypedQuery<ReportedResult> query = manager.createQuery(queryString, ReportedResult.class);
			query.setParameter("experimentid", experiment.getId());
			query.setParameter("instructorid", instructor.getId());
			return query.getResultList();
		});
	}

	public Set<Experiment> getExperiments(Instructor instructor) {
		return withEntityManager(manager -> {
			String queryString = "SELECT e FROM Experiment e " +
					"JOIN e.instructors i " +
					"WHERE i.id=:instructorid";
			TypedQuery<Experiment> query = manager.createQuery(queryString, Experiment.class);
			query.setParameter("instructorid", instructor.getId());
			return query.getResultStream().collect(Collectors.toSet());
		});
	}

	public BigDecimal getAverageStudentReportScore(Experiment experiment) {
		return withEntityManager(manager -> {
			String queryString = "SELECT SUM(rr.score)/COUNT(s) FROM ReportedResult rr " +
					"JOIN rr.student s " +
					"JOIN rr.experiment e " +
					"WHERE e.id=:experimentid " +
					"GROUP BY s";
			TypedQuery<BigDecimal> query = manager.createQuery(queryString, BigDecimal.class);
			query.setParameter("experimentid", experiment.getId());
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public List<MeasurementValue> getMeasurementValues(Measurement measurement, 
			boolean prefetchParameterValues, 
			boolean prefetchCourseClass,
			boolean prefetchStudent
			) {
		return withEntityManager(manager -> {
			String queryString = "SELECT mv FROM MeasurementValue mv " +
					"JOIN mv.variable m " +
					(prefetchParameterValues ? "LEFT JOIN FETCH mv.parameterValues pv " : "") +
					(prefetchCourseClass ? "LEFT JOIN FETCH mv.courseClass cc " : "") +
					(prefetchStudent ? "LEFT JOIN FETCH mv.student s " : "") +
					"WHERE m.id=:measurementid";
			TypedQuery<MeasurementValue> query = manager.createQuery(queryString, MeasurementValue.class);
			query.setParameter("measurementid", measurement.getId());
			return query.getResultList();
		});
	}
	
	public List<MeasurementValue> getMeasurementValues(Measurement measurement, Student student, boolean prefetchParameterValues) {
		return withEntityManager(manager -> {
			String queryString = "SELECT mv FROM MeasurementValue mv " +
					"JOIN mv.variable m " +
					"JOIN mv.student s " +
					(prefetchParameterValues ? "LEFT JOIN FETCH mv.parameterValues pv " : "") +
					"WHERE s.id=:studentid AND m.id=:measurementid";
			TypedQuery<MeasurementValue> query = manager.createQuery(queryString, MeasurementValue.class);
			query.setParameter("measurementid", measurement.getId());
			query.setParameter("studentid", student.getId());
			return query.getResultList();
		});
	}

	public List<ReportedResult> getReportedResults(Experiment experiment, Student student) {
		return withEntityManager(manager -> {
			String queryString = "SELECT rr FROM ReportedResult rr " +
					"JOIN rr.experiment e " +
					"JOIN rr.student s " +
					"WHERE s.id=:studentid AND e.id=:experimentid";
			TypedQuery<ReportedResult> query = manager.createQuery(queryString, ReportedResult.class);
			query.setParameter("experimentid", experiment.getId());
			query.setParameter("studentid", student.getId());
			return query.getResultList();
		});
	}

	public LocalDateTime getLastReportUpdated(Experiment experiment, Student student) {
		return withEntityManager(manager -> {
			String queryString = "SELECT MAX(rr.added) FROM ReportedResult rr " +
					"JOIN rr.experiment e " +
					"JOIN rr.student s " +
					"WHERE s.id=:studentid AND e.id=:experimentid";
			TypedQuery<LocalDateTime> query = manager.createQuery(queryString, LocalDateTime.class);
			query.setParameter("experimentid", experiment.getId());
			query.setParameter("studentid", student.getId());
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public BigDecimal getTotalReportScore(Experiment experiment, Student student) {
		return withEntityManager(manager -> {
			String queryString = "SELECT SUM(rr.score) FROM ReportedResult rr " +
					"JOIN rr.experiment e " +
					"JOIN rr.student s " +
					"WHERE s.id=:studentid AND e.id=:experimentid";
			TypedQuery<BigDecimal> query = manager.createQuery(queryString, BigDecimal.class);
			query.setParameter("experimentid", experiment.getId());
			query.setParameter("studentid", student.getId());
			return query.getResultStream().findAny().orElse(null);
		});
	}

	public List<Experiment> getPastExperiments(Student student) {
		return withEntityManager(manager -> {
			String queryString = "SELECT e FROM Experiment e " +
					"JOIN e.student s " +
					"WHERE s.id=:studentid AND e.id!=ANY(" +
						"SELECT ae.id FROM Student st " +
						"JOIN st.activeExperiments ae" +
						"WHERE st.id=:studentid)";
			TypedQuery<Experiment> query = manager.createQuery(queryString, Experiment.class);
			query.setParameter("studentid", student.getId());
			return query.getResultList();
		});
	}

	public void addExperiment(Experiment experiment) {
		withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			manager.persist(experiment);
			tx.commit();
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
	
	public Map<Measurement, List<MeasurementValue>> getStudentMeasurementValues(Experiment experiment, Student student, boolean prefetchParameterValues) {
		return experiment.getMeasurements().stream()
				.collect(Collectors.toMap(Function.identity(), 
						m -> getMeasurementValues(m, student, prefetchParameterValues)));
	}
	
	/** Returns the managed Measurement instance from this transaction. The value
	 * is NOT added to the detached measurement object passed in as the parameter. 
	 * The measurement value will also be added to the student entity in the database,
	 * but NOT the student object passed as the parameter to this function. */
	public <Q extends Quantity<Q>> Measurement addMeasurementValue(Measurement measurement, 
			Student student, Amount<Q> measurementAmount, 
			Map<? extends Parameter, ? extends Amount<?>> parameterAmounts,
			StudentService studentService) {
		return withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			Measurement mergedMeasurement = manager.merge(measurement);
			Student mergedStudent = manager.merge(student);
			
			MeasurementValue measurementValue = new MeasurementValue();
			measurementValue.setVariable(mergedMeasurement); // necessary for setAmountValue to know correct dimensions
			measurementValue.setAmountValue(measurementAmount);
			studentService.getCourseClass(
					mergedMeasurement.getExperiment().getCourse(), 
					student,
					true)
				.addMeasurementValue(measurementValue);
			
			mergedStudent.addMeasurementValue(measurementValue);
			
			// add parameter values
			parameterAmounts.forEach((p, a) -> {
				ParameterValue parameterValue = new ParameterValue();
				parameterValue.setVariable(p);
				parameterValue.setAmountValue(a);
				measurementValue.addParameterValue(parameterValue);
				manager.persist(parameterValue);
			});
			
			manager.persist(measurementValue);
			
			tx.commit();
			
			return mergedMeasurement;
		});
	}
	
	/** Returns the managed Parameter instance from this transaction. The value
	 * is NOT added to the detached measurement object passed in as the parameter. */
	public Parameter addParameterValue(Parameter parameter, ParameterValue value) {
		return withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			Parameter mergedParameter = manager.merge(parameter);
			manager.persist(value);
			mergedParameter.addValue(value);
			tx.commit();
			
			return mergedParameter;
		});
	}
}
