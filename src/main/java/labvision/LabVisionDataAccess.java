package labvision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import labvision.entities.Course;
import labvision.entities.Device;
import labvision.entities.Experiment;
import labvision.entities.LabVisionEntity;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.entities.User;

/**
 * Provides access to the database to load and maniuplate entity objects.
 * @author davidnisson
 *
 */
public class LabVisionDataAccess {
	private EntityManagerFactory entityManagerFactory;
	
	/**
	 * A query that selects for entities related to a given student and another entity
	 * @author davidnisson
	 *
	 * @param <E> the entity type to constrain as being related to a student
	 * @param <C> the entity type of the other attribute to query for
	 * @param <T> the type of object to query for
	 */
	static class StudentSelectQuery<E, C extends LabVisionEntity, T> {
		final String mapFieldName; // the name of the field mapping to the entity type C
		final Root<E> root;
		final Join<E, Student> s;
		final Join<E, C> e;
		final CriteriaQuery<T> cq;
		
		public StudentSelectQuery(String mapFieldName,
				Root<E> root,
				Join<E, Student> s,
				Join<E, C> e,
				CriteriaQuery<T> cq) {
			this.mapFieldName = mapFieldName;
			this.root = root;
			this.s = s;
			this.e = e;
			this.cq = cq;
		}

		static <E, C extends LabVisionEntity, T> StudentSelectQuery<E, C, T> selectEntities(
				EntityManager manager,
				C mappedEntity,
				Student student,
				Function<CriteriaBuilder, Function<Root<E>, Function<Join<E, Student>, Function<Join<E, C>, Selection<? extends T>>>>> selectionFactory,
				String mapFieldName,
				Class<E> entityClass, Class<T> queryClass) {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<T> cq = cb.createQuery(queryClass);
			Root<E> root = cq.from(entityClass);
			Join<E, Student> s = root.join("student");
			Join<E, C> e = root.join(mapFieldName);
			cq.select(selectionFactory.apply(cb).apply(root).apply(s).apply(e))
			.where(cb.and(
					cb.equal(s.get("id"),  student.getId()),
					cb.equal(e.get("id"), mappedEntity.getId())));
			return new StudentSelectQuery<>(mapFieldName, root, s, e, cq);
		}
	}
	
	public LabVisionDataAccess(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public User getUser(String username) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		TypedQuery<User> userQuery = manager.createQuery(
				"SELECT u FROM User u WHERE u.username=:username",
				User.class);
		userQuery.setParameter("username", username);
		return userQuery.getSingleResult();
	}
	
	public User getUser(int userId) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		return manager.find(User.class, userId);
	}
	
	public void addUser(User user) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		manager.persist(user);
		transaction.commit();
	}

	public Device addNewDevice(User user) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		Device device = new Device();
		device.setUser(user);
		
		manager.getTransaction().begin();
		manager.persist(device);
		manager.getTransaction().commit();
		
		return device;
	}

	public Experiment getExperiment(int id) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		return manager.find(Experiment.class, id);
	}
	
	public List<Experiment> getRecentExperiments(Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		TypedQuery<Object[]> query = manager.createQuery(
				"SELECT DISTINCT mv, mv.measurement.experiment " +
		        "FROM MeasurementValue mv " +
				"WHERE mv.student.id=:studentid " +
		        "ORDER BY mv.taken DESC",
				Object[].class);
		query.setParameter("studentid", student.getId());
		return query.getResultList().stream()
				.map(row -> (Experiment) row[1])
				.collect(Collectors.toList());
	}

	public List<Course> getRecentCourses(Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		TypedQuery<Object[]> query = manager.createQuery(
				"SELECT DISTINCT mv, mv.courseClass.course " +
				        "FROM MeasurementValue mv " +
						"WHERE mv.student.id=:studentid " +
				        "ORDER BY mv.taken DESC",
				Object[].class);
		query.setParameter("studentid", student.getId());
		return query.getResultList().stream()
				.map(row -> (Course) row[1])
				.collect(Collectors.toList());
	}

	public List<ReportedResult> getReportedResults(Experiment experiment, Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();

		StudentSelectQuery<ReportedResult, Experiment, ReportedResult> sesq = 
				StudentSelectQuery.selectEntities(
				manager,
				experiment, 
				student, 
				cb -> (rr -> (s -> (e -> rr))),
				"experiment",
				ReportedResult.class,
				ReportedResult.class);
		
		TypedQuery<ReportedResult> query = manager.createQuery(sesq.cq);
		return query.getResultList();
	}

	public LocalDateTime getLastReportUpdated(Experiment experiment, Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		StudentSelectQuery<ReportedResult, Experiment, LocalDateTime> sesq = 
				StudentSelectQuery.selectEntities(
				manager,
				experiment, 
				student, 
				cb -> (rr -> (s -> (e -> cb.greatest(rr.get("added"))))),
				"experiment",
				ReportedResult.class,
				LocalDateTime.class);
		sesq.cq.groupBy(sesq.e);
		
		TypedQuery<LocalDateTime> query = manager.createQuery(sesq.cq);
		List<LocalDateTime> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public BigDecimal getTotalReportScore(Experiment experiment, Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		StudentSelectQuery<ReportedResult, Experiment, BigDecimal> sesq =
				StudentSelectQuery.selectEntities(
				manager,
				experiment, 
				student, 
				cb -> (rr -> (s -> (e -> cb.sum(rr.get("score")) ))),
				"experiment",
				ReportedResult.class,
				BigDecimal.class);
		sesq.cq.groupBy(sesq.e);
		
		TypedQuery<BigDecimal> query = manager.createQuery(sesq.cq);
		List<BigDecimal> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public List<MeasurementValue> getMeasurementValues(Measurement measurement, Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		StudentSelectQuery<MeasurementValue, Measurement, MeasurementValue> sesq =
				StudentSelectQuery.selectEntities(
						manager,
						measurement,
						student,
						cb -> (mv -> (s -> (e -> mv))),
						"measurement",
						MeasurementValue.class,
						MeasurementValue.class);
		
		TypedQuery<MeasurementValue> query = manager.createQuery(sesq.cq);
		return query.getResultList();
	}

	public List<ParameterValue> getParameterValues(Parameter parameter, Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();

		StudentSelectQuery<ParameterValue, Parameter, ParameterValue> sesq =
				StudentSelectQuery.selectEntities(
						manager,
						parameter,
						student,
						cb -> (mv -> (s -> (e -> mv))),
						"parameter",
						ParameterValue.class,
						ParameterValue.class);
		
		TypedQuery<ParameterValue> query = manager.createQuery(sesq.cq);
		return query.getResultList();
	}
}