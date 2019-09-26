package labvision.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import labvision.LabVisionConfig;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.CourseClass_;
import labvision.entities.Experiment;
import labvision.entities.Experiment_;
import labvision.entities.MeasurementValue;
import labvision.entities.MeasurementValue_;
import labvision.entities.Measurement_;
import labvision.entities.Student;
import labvision.entities.StudentPreferences;
import labvision.entities.StudentPreferences_;
import labvision.entities.Student_;

public class StudentService extends JpaService {
	private final LabVisionConfig config;

	public StudentService(EntityManagerFactory entityManagerFactory,
			LabVisionConfig config) {
		super(entityManagerFactory);
		this.config = config;
	}
	
	public Student getStudent(int id,
			boolean prefetchActiveExperiments,
			boolean prefetchCourseClasses,
			boolean prefetchMeasurementValues) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<Student> cq = cb.createQuery(Student.class);
			Root<Student> s = cq.from(Student.class);
			if (prefetchActiveExperiments) {
				s.fetch(Student_.activeExperiments, JoinType.LEFT);
			}
			if (prefetchCourseClasses) {
				s.fetch(Student_.courseClasses, JoinType.LEFT);
			}
			// ensure that only one bag is fetched to avoid a Cartesian product
			if (!prefetchActiveExperiments && prefetchMeasurementValues) {
				s.fetch(Student_.measurementValues, JoinType.LEFT);
			}
			
			TypedQuery<Student> query = manager.createQuery(cq);
			final Student student = query.getResultStream().findAny().orElse(null);
			
			if (prefetchActiveExperiments && prefetchMeasurementValues) {
				student.getMeasurementValues().size();
			}
			
			return student;
		});
	}
	
	public void addActiveExperiment(Student student, Experiment experiment) {
		withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			student.addActiveExperiment(manager.merge(experiment));
			tx.commit();
		});
	}
	
	public void removeActiveExperiment(Student student, Experiment experiment) {
		withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			student.removeActiveExperiment(manager.merge(experiment));
			tx.commit();
		});
	}

	public Map<Course, Experiment> getCurrentExperimentsMap(Student student) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<Experiment> cq = cb.createQuery(Experiment.class);
			Root<Student> s = cq.from(Student.class);
			Join<Student, Experiment> e = s.join(Student_.activeExperiments);
			e.fetch(Experiment_.course, JoinType.LEFT);
			cq.select(e).where(cb.equal(s.get(Student_.id), student.getId()));
			
			TypedQuery<Experiment> query = manager.createQuery(cq);
			return query.getResultStream()
					.collect(Collectors.toMap(Experiment::getCourse, Function.identity()));
		});
	}

	/** Query an integer limit that is specified in the StudentPreferences table */
	private int queryStudentPrefLimit(Student student, 
			SingularAttribute<StudentPreferences, Integer> limitAttr,
			int defaultLimit) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
			Root<StudentPreferences> sp = cq.from(StudentPreferences.class);
			Join<StudentPreferences, Student> s = sp.join(StudentPreferences_.student);
			cq.select(sp.get(limitAttr))
			 .where(cb.equal(s.get(Student_.id), student.getId()));
			
			TypedQuery<Integer> query = manager.createQuery(cq);
			return query.getResultStream().findAny()
					.orElse(defaultLimit);
		});
	}
	
	/** Query an entity type corresponding to a student by 
	 * its most recent measurement value taken */
	private <Q, X> List<Q> getRecentEntities(Student student,
			Function<Root<MeasurementValue>, Join<X, Q>> joinFactory,
			int limit, Class<Q> entityType) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<MeasurementValue> mv = cq.from(MeasurementValue.class);
			Join<X, Q> j = joinFactory.apply(mv);
			Join<MeasurementValue, Student> s = mv.join(MeasurementValue_.student);
			cq.multiselect(j, cb.greatest(mv.get(MeasurementValue_.taken)))
				.where(cb.equal(s.get(Student_.id), student.getId()))
				.groupBy(j)
				.orderBy(cb.desc(cb.greatest(mv.get(MeasurementValue_.taken))));
					
			TypedQuery<Tuple> query = manager.createQuery(cq);
			if (limit > 0) {
				query.setMaxResults(limit);
			}
			return query.getResultStream().map(t -> t.get(0, entityType))
					.collect(Collectors.toList());
		});
	}
	
	public List<Experiment> getRecentExperiments(Student student) {
		int limit = queryStudentPrefLimit(student, 
				StudentPreferences_.maxRecentExperiments,
				config.getStudentDashboardMaxRecentExperiments());
		return getRecentExperiments(student, limit);
	}
	
	/** limit of 0 means all experiments ordered most recent first */
	public List<Experiment> getRecentExperiments(Student student, int limit) {
		return getRecentEntities(student,
				mv -> mv.join(MeasurementValue_.variable)
				.join(Measurement_.experiment), 
				limit,
				Experiment.class);
	}

	public List<Course> getRecentCourses(Student student) {
		int limit = queryStudentPrefLimit(student, 
				StudentPreferences_.maxRecentCourses, 
				config.getStudentDashboardMaxRecentCourses());
		return getRecentCourses(student, limit);
	}
	
	public List<Course> getRecentCourses(Student student, int limit) {
		return getRecentEntities(student,
				mv -> mv.join(MeasurementValue_.courseClass)
				.join(CourseClass_.course),
				limit,
				Course.class);
	}

	public CourseClass getCourseClass(Course course, Student student) {
		return getCourseClass(course, student, false);
	}
	
	public CourseClass getCourseClass(Course course, Student student, boolean prefetchMeasurementValues) {
		return withEntityManager(manager -> {
			String queryString = "SELECT cc FROM CourseClass cc " +
					"JOIN cc.students s " +
					(prefetchMeasurementValues ? "LEFT JOIN FETCH cc.measurementValues mv " : "") +
					"WHERE cc.course.id=:courseid AND s.id=:studentid";
			TypedQuery<CourseClass> query = manager.createQuery(queryString, CourseClass.class);
			query.setParameter("courseid", course.getId());
			query.setParameter("studentid", student.getId());
			return query.getResultStream().findFirst().orElse(null);
		});
	}
}
