package labvision.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import labvision.LabVisionConfig;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
import labvision.entities.ReportedResult;
import labvision.entities.Student;

class TestStudentExperimentService {
	private static EntityManagerFactory entityManagerFactory;
	private static StudentExperimentService service;
	private static List<Student> students = new ArrayList<>();
	private static List<Course> courses = new ArrayList<>();
	private static List<CourseClass> courseClasses = new ArrayList<>();
	private static List<Experiment> experiments = new ArrayList<>();
	private static Map<Experiment, List<Measurement>> measurements = new HashMap<>();
	private static Map<Measurement, List<MeasurementValue>> measurementValues = new HashMap<>();
	private static Map<Measurement, List<Parameter>> parameters = new HashMap<>();
	private static Map<MeasurementValue, List<ParameterValue>> parameterValues = new HashMap<>();
	private static Map<Experiment, List<ReportedResult>> reports = new HashMap<>();
	private static LocalDateTime initDateTime;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory(
				LabVisionConfig.TESTING_PERSISTENCE_UNIT_NAME);
		
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		LabVisionConfig config = new LabVisionConfig("~/.labvision/test.properties");
		SecureRandom random = new SecureRandom();
		
		students.add(new Student("Student One", "student1", "password123", config, random));
		students.add(new Student("Student Two", "student2", "password123", config, random));
		students.add(new Student("Student Three", "student3", "password123", config, random));
		
		courses.add(new Course("Test 101"));
		courses.add(new Course("Test 102"));
		
		courseClasses.add(courses.get(0).addCourseClass("Test 101 Morning"));
		courseClasses.add(courses.get(0).addCourseClass("Test 101 Afternoon"));
		
		initDateTime = LocalDateTime.now();
		
		experiments.add(courses.get(0).addExperiment("Test Experiment 1", "Description of Test Experiment 1", initDateTime.plusDays(23)));
		experiments.add(courses.get(0).addExperiment("Test Experiment 2", "Description of Test Experiment 1", initDateTime.plusDays(25)));
		
		students.get(0).addActiveExperiment(experiments.get(0));
		students.get(1).addActiveExperiment(experiments.get(1));
		
		reports.put(experiments.get(0), new ArrayList<>());
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusDays(1)));
		reports.get(experiments.get(0)).get(0).setScore(new BigDecimal("45"));
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusHours(14)));
		reports.get(experiments.get(0)).get(1).setScore(new BigDecimal("50"));
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		
		courses.forEach(c -> manager.persist(c));
		students.forEach(s -> manager.persist(s));
		courseClasses.forEach(cc -> manager.persist(cc));
		experiments.forEach(e -> manager.persist(e));
		reports.forEach((e, l) -> l.forEach(rr -> manager.persist(rr)));
		
		tx.commit();
		
		service = new StudentExperimentService(entityManagerFactory);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		JpaService.clearTable(ReportedResult.class, manager);
		JpaService.clearTable(Student.class, manager);
		JpaService.clearTable(Experiment.class, manager);
		JpaService.clearTable(CourseClass.class, manager);
		JpaService.clearTable(Course.class, manager);
		
		entityManagerFactory.close();
	}

	@Test
	void testGetCurrentExperiments() {
		List<CurrentExperimentForStudentExperimentTable> results1 = 
				service.getCurrentExperiments(students.get(0).getId());
		List<CurrentExperimentForStudentExperimentTable> results2 = 
				service.getCurrentExperiments(students.get(1).getId());
		List<CurrentExperimentForStudentExperimentTable> results3 =
				service.getCurrentExperiments(students.get(2).getId());
		
		assertEquals(1, results1.size());
		assertEquals(1, results2.size());
		assertTrue(results3.isEmpty());
		
		assertEquals("Test Experiment 1", results1.get(0).getName());
		assertEquals("Test Experiment 2", results2.get(0).getName());
		
		assertEquals(initDateTime.plusDays(23), results1.get(0).getReportDueDate());
		assertEquals(initDateTime.plusDays(25), results2.get(0).getReportDueDate());
		
		assertEquals(0, BigDecimal.valueOf(95).compareTo(results1.get(0).getTotalReportScore()));
		assertEquals(0, BigDecimal.ZERO.compareTo(results2.get(0).getTotalReportScore()));
	}

}
