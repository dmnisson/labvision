package labvision.services;

import java.time.LocalDateTime;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import labvision.dto.course.CourseInfo;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.Student;

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

	public Experiment addExperiment(Course course, String name, String description, LocalDateTime reportDueDate) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Experiment experiment = course.addExperiment(name, description, reportDueDate);
			
			manager.persist(experiment);
			manager.getTransaction().commit();
			
			return experiment;
		});
	}

	public CourseClass addCourseClass(Course course, String name) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			CourseClass courseClass = course.addCourseClass(name);
			
			manager.persist(courseClass);
			manager.getTransaction().commit();
			
			return courseClass;
		});
	}
	
	/**
	 * Add a student to a course class
	 * @param courseClassId the course class ID
	 * @param student the student to add
	 * @return the course class with the added student
	 */
	public CourseClass addStudentToCourseClass(int courseClassId, Student student) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			CourseClass courseClass = manager.find(CourseClass.class, courseClassId);
			courseClass.addStudent(student);
			
			manager.getTransaction().commit();
			return courseClass;
		});
	}
	
	/**
	 * Add an instructor to a course class
	 * @param courseClassId the course class ID
	 * @param instructor the instructor to add
	 * @return the course class with the added student
	 */
	public CourseClass addInstructorToCourseClass(int courseClassId, Instructor instructor) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			CourseClass courseClass = manager.find(CourseClass.class, courseClassId);
			courseClass.addInstructor(instructor);
			
			manager.getTransaction().commit();
			return courseClass;
		});
	}
	
	/**
	 * Get information about the course with the given ID
	 * @param id the course ID
	 * @return the course information
	 */
	public CourseInfo getCourseInfo(int id) {
		return withEntityManager(manager -> {
			String queryString =
					"SELECT new labvision.dto.course.CourseInfo("
					+ "	c.id,"
					+ "	c.name"
					+ ") FROM Course c "
					+ "WHERE c.id=:courseid";
			TypedQuery<CourseInfo> query = manager.createQuery(queryString, CourseInfo.class);
			query.setParameter("courseid", id);
			return query.getResultStream().findAny().orElse(null);
		});
	}

	/**
	 * Create an experiment and add it to a course
	 * @param courseId the course id
	 * @param name the name of the new experiment
	 * @param description the description of the new experiment
	 * @param reportDueDate the due date for submission of reports
	 * @return the newly created experiment object
	 */
	public Experiment addExperiment(int courseId, String name, String description, LocalDateTime reportDueDate) {
		return withEntityManager(manager -> {
			Course course = manager.find(Course.class, courseId);
			
			manager.getTransaction().begin();
			Experiment experiment = course.addExperiment(name, description, reportDueDate);
			manager.persist(experiment);
			manager.getTransaction().commit();
			
			return experiment;
		});
	}
}
