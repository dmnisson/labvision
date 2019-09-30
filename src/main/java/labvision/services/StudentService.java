package labvision.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import labvision.LabVisionConfig;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Student;

public class StudentService extends JpaService {
	public StudentService(EntityManagerFactory entityManagerFactory,
			LabVisionConfig config) {
		super(entityManagerFactory);
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
