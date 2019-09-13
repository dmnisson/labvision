package labvision;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import labvision.entities.Course;
import labvision.entities.Device;
import labvision.entities.Experiment;
import labvision.entities.MeasurementValue;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.entities.User;
import labvision.viewmodels.StudentDashboard;

/**
 * Provides access to the database to load and maniuplate entity objects.
 * @author davidnisson
 *
 */
public class LabVisionDataAccess {
	private EntityManagerFactory entityManagerFactory;
	
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
	
	public StudentDashboard getDashboard(int studentId) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		StudentDashboard dashboard = new StudentDashboard();
		
		Student student = manager.find(Student.class, studentId);
		
		// set dashboard values from objects in database
		dashboard.setStudent(student);
		
		// sort measurement values by date last taken
		Stream<MeasurementValue<?>> measurementValueStream = student.getMeasurementValues()
				.stream()
				.sorted((m1, m2) -> m2.getTaken().compareTo(m1.getTaken()));
		
		// sort recent courses by date last measurement value was taken
		dashboard.setRecentCourses(measurementValueStream
				.map(m -> m.getCourseClass().getCourse())
				.distinct()
				.collect(Collectors.toList()));
		
		dashboard.setRecentExperiments(measurementValueStream
				.map(m -> m.getMeasurement().getExperiment())
				.distinct()
				.collect(Collectors.toList()));
		
		dashboard.setCurrentExperiments(student.getActiveExperiments().stream()
				.collect(Collectors.toMap(Experiment::getCourse, Function.identity()))
				);
		
		dashboard.setMaxRecentCourses(student.getStudentPreferences()
				.getMaxRecentCourses());
		
		dashboard.setMaxRecentExperiments(student.getStudentPreferences()
				.getMaxRecentExperiments());
		
		return dashboard;
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

	public Map<Experiment, ReportedResult> getReportedResults(Student student) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		TypedQuery<Object[]> query = manager.createQuery(
				"SELECT r, r.experiment " +
				"FROM ReportedResult r " +
				"WHERE r.student.id=:studentid",
				Object[].class);
		query.setParameter("studentid", student.getId());
		return query.getResultList().stream()
				.collect(Collectors.toMap(
						(Object[] row) -> (Experiment) row[1],
						(Object[] row) -> (ReportedResult) row[0],
						(r1, r2) -> r1));
	}

	public Map<Experiment, ReportStatus> getReportStatus(Student student) {
		return getRecentExperiments(student).stream().collect(
				Collectors.toMap(e -> e, LabVisionDataAccess::getReportStatus));
	}
	
	public static ReportStatus getReportStatus(Experiment experiment) {
		if (isNullOrEmpty(experiment.getMeasurements())) {
			return ReportStatus.NOT_SUBMITTED;
		} else if (isNullOrEmpty(experiment.getObtainedResults())) {
			return ReportStatus.MEASUREMENT_VALUES_REPORTED;
		} else {
			// check for accepted values not obtained
			if (experiment.getAcceptedResults().stream()
					.anyMatch(ar -> experiment.getObtainedResults().stream()
							.noneMatch(or -> or.getName().equals(ar.getName())))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else if (experiment.getAcceptedResults().stream()
					.anyMatch(ar -> experiment.getReportedResults().stream()
							.flatMap(rr -> rr.getResults().stream())
							.noneMatch(r -> r.getName().equals(ar.getName())))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else if (experiment.getReportedResults().stream()
					.anyMatch(rr -> Objects.isNull(rr.getReportDocument()))) {
				return ReportStatus.RESULTS_IN_PROGRESS;
			} else {
				return ReportStatus.COMPLETED;
			}
		}
	}
	
	private static <E> boolean isNullOrEmpty(Collection<E> coll) {
		return Objects.isNull(coll) || coll.isEmpty();
	}
}
