package labvision.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import labvision.LabVisionConfig;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Instructor;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.QuantityTypeId;
import labvision.entities.Student;
import labvision.entities.User;
import labvision.services.CourseService;
import labvision.services.ExperimentService;
import labvision.services.JpaService;
import labvision.services.UserService;
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
		sc.close();
		
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
		JpaService.clearTable(MeasurementValue.class, manager);
		JpaService.clearTable(CourseClass.class, manager);
		JpaService.clearTable(Measurement.class, manager);
		JpaService.clearTable(Student.class, manager);
		JpaService.clearTable(Experiment.class, manager);
		JpaService.clearTable(Course.class, manager);
		JpaService.clearTable(User.class, manager);
		manager.close();
				
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
		course.addExperiment(rodLengthExperiment);
		
		// course class
		CourseClass courseClass = new CourseClass();
		courseClass.setName("Test Physics 101 Class");
		courseClass.addStudent(student1);
		courseClass.addInstructor(instructor1);
		course.addCourseClass(courseClass);
		
		// measurement
		Measurement rodLengthMeasurement = new Measurement();
		rodLengthMeasurement.setName("Length");
		rodLengthMeasurement.setQuantityTypeId(QuantityTypeId.LENGTH);
		rodLengthMeasurement.updateDimensionObject(QuantityDimension.LENGTH);
		rodLengthExperiment.addMeasurement(rodLengthMeasurement);
		
		student1.addActiveExperiment(rodLengthExperiment);
		instructor1.addExperiment(rodLengthExperiment);
		
		CourseService courseService = new CourseService(emf);
		ExperimentService experimentService = new ExperimentService(emf);
		UserService userService = new UserService(emf);
		
		courseService.addCourse(course);
		experimentService.addExperiment(rodLengthExperiment);
		
		SecureRandom random = new SecureRandom();
		try {
			student1.updatePassword(config, random, "Password123");
			userService.addUser(student1);
			System.out.println("User student1 added");
			
			instructor1.updatePassword(config, random, "Password123");
			userService.addUser(instructor1);
			System.out.println("User instructor1 added");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		emf.close();
	}
}
