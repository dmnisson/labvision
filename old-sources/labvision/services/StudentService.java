package labvision.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import labvision.LabVisionConfig;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.Student;

public class StudentService extends JpaService {
	public StudentService(EntityManagerFactory entityManagerFactory,
			LabVisionConfig config) {
		super(entityManagerFactory);
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

	public Experiment addActiveExperiment(int studentId, int experimentId) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Student student = manager.find(Student.class, studentId);
			Experiment experiment = manager.find(Experiment.class, experimentId);
			student.addActiveExperiment(experiment);
			
			manager.getTransaction().commit();
			
			return experiment;
		});
	}
}