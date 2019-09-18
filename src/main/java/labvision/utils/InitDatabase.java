package labvision.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import labvision.LabVisionConfig;
import labvision.LabVisionDataAccess;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.Measurement;
import labvision.entities.QuantityTypeId;
import labvision.entities.Student;
import labvision.entities.User;
import tec.units.ri.quantity.QuantityDimension;

/**
 * Initialize the database with test users
 * @author davidnisson
 *
 */
public class InitDatabase {
	
	public static void main(String[] args) {
		System.out.println("WARNING: This will drop all existing users, experiments, "
				+ "courses, course classes, and measurements from the database! Continue? (y/n)");
		Scanner sc = new Scanner(System.in);
		String response = sc.nextLine();
		if (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("yes")) {
			System.out.println("Aborted");
			System.exit(0);
		}
		
		String configPath;
		if (args.length > 0) {
			configPath = args[0];
		} else {
			configPath = "~/.labvision/app.properties";
		}
		
		LabVisionConfig config = new LabVisionConfig(configPath);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
		
		// clear users
		EntityManager manager = emf.createEntityManager();
		clearTable(CourseClass.class, manager);
		clearTable(Measurement.class, manager);
		clearTable(Experiment.class, manager);
		clearTable(Course.class, manager);
		clearTable(User.class, manager);
		
		LabVisionDataAccess dataAccess = new LabVisionDataAccess(emf);
		
		// users
		Student student1 = new Student();
		student1.setUsername("student1");
		Instructor instructor1 = new Instructor();
		instructor1.setUsername("instructor1");
		
		// experiment
		Experiment rodLengthExperiment = new Experiment();
		rodLengthExperiment.setName("How Long is the Rod?");
		rodLengthExperiment.setDescription("Measure the rod length using the ruler the best that you can.");
		rodLengthExperiment.setReportDueDate(LocalDateTime.of(2100, 1, 1, 0, 0));
		
		// course
		Course course = new Course();
		course.setName("Physics 101");
		rodLengthExperiment.setCourse(course);
		
		// course class
		CourseClass courseClass = new CourseClass();
		courseClass.setName("Test Physics 101 Class");
		courseClass.setCourse(course);
		List<Student> courseClassStudents = Arrays.asList(student1);
		List<Instructor> courseClassInstructors = Arrays.asList(instructor1);
		courseClass.setStudents(courseClassStudents);
		courseClass.setInstructors(courseClassInstructors);
		
		// measurement
		Measurement rodLengthMeasurement = new Measurement();
		rodLengthMeasurement.setName("Length");
		rodLengthMeasurement.setQuantityTypeId(QuantityTypeId.LENGTH);
		rodLengthMeasurement.updateDimensionObject(QuantityDimension.LENGTH);
		rodLengthMeasurement.setExperiment(rodLengthExperiment);
		
		SecureRandom random = new SecureRandom();
		try {
			student1.updatePassword(config, random, "Password123");
			dataAccess.addUser(student1);
			System.out.println("User student1 added");
			
			instructor1.updatePassword(config, random, "Password123");
			dataAccess.addUser(instructor1);
			System.out.println("User instructor1 added");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// persist experiment, course, course class, and measurement
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		
		manager.persist(course);
		manager.persist(rodLengthExperiment);
		manager.persist(rodLengthMeasurement);
		manager.persist(courseClass);
		
		tx.commit();
		
		emf.close();
	}

	private static <T> void clearTable(Class<T> entityClass, EntityManager manager) {
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaDelete<T> cd = cb.createCriteriaDelete(entityClass);
		cd.from(entityClass);
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		manager.createQuery(cd).executeUpdate();
		tx.commit();
	}
}
