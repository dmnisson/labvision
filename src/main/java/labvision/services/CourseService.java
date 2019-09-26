package labvision.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import labvision.entities.Course;

public class CourseService extends JpaService {

	public CourseService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public void addCourse(Course course) {
		withEntityManager(manager -> {
			EntityTransaction tx = manager.getTransaction();
			tx.begin();
			manager.persist(course);
			tx.commit();
		});
	}

}
