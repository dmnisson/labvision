package labvision.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
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
import labvision.entities.Parameter;
import labvision.entities.ReportDocument;
import labvision.entities.ReportedResult;
import labvision.entities.Result;
import labvision.entities.Student;
import labvision.entities.User;
import labvision.services.CourseService;
import labvision.services.ExperimentService;
import labvision.services.InstructorService;
import labvision.services.JpaService;
import labvision.services.StudentService;
import labvision.services.UserService;

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
		JpaService.clearTable(Parameter.class, manager);
		JpaService.clearTable(MeasurementValue.class, manager);
		JpaService.clearTable(CourseClass.class, manager);
		JpaService.clearTable(Measurement.class, manager);
		JpaService.clearTable(ReportedResult.class, manager);
		JpaService.clearTable(ReportDocument.class, manager);
		JpaService.clearTable(Result.class, manager);
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
		
		CourseService courseService = new CourseService(emf);
		ExperimentService experimentService = new ExperimentService(emf);
		
		// course
		Course course = new Course();
		course.setName("Physics 101");
		courseService.addCourse(course);
		
		// experiment
		Experiment rodLengthExperiment = courseService.addExperiment(
				course,
				"How long is the rod?",
				"Measure the rod length using the ruler the best that you can.",
				LocalDateTime.of(2100, 1, 1, 0, 0));
		
		// course class
		CourseClass courseClass = courseService.addCourseClass(course, "Test Physics 101 Class");
		courseClass = courseService.addStudentToCourseClass(courseClass.getId(), student1);
		courseClass = courseService.addInstructorToCourseClass(courseClass.getId(), instructor1);
		
		// measurement
		Measurement rodLengthMeasurement = experimentService.addMeasurement(
				rodLengthExperiment, "Length", Length.class);
		
		// parameter
		experimentService.addParameter(
				rodLengthMeasurement, "Temperature", Temperature.class);
		
		UserService userService = new UserService(emf);
		InstructorService instructorService = new InstructorService(emf);
		StudentService studentService = new StudentService(emf, config);
		
		SecureRandom random = new SecureRandom();
		try {			
			student1.updatePassword(config, random, "Password123");
			userService.addUser(student1);
			System.out.println("User student1 added");
			
			instructor1.updatePassword(config, random, "Password123");
			userService.addUser(instructor1);
			System.out.println("User instructor1 added");
			
			instructorService.addExperiment(instructor1.getId(), rodLengthExperiment.getId());
			studentService.addActiveExperiment(student1.getId(), rodLengthExperiment.getId());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		emf.close();
	}
}
