package labvision.services;

import java.util.Map;
import java.util.Objects;
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
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Measurement_;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
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
}
