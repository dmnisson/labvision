package labvision.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Speed;
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
import labvision.measure.Amount;
import tec.units.ri.unit.Units;

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
		experiments.add(courses.get(0).addExperiment("Test Experiment 2", "Description of Test Experiment 2", initDateTime.plusDays(25)));
		experiments.add(courses.get(0).addExperiment("Test Experiment 3", "Description of Test Experiment 3", initDateTime.plusDays(16)));
		experiments.add(courses.get(0).addExperiment("Test Experiment 4", "Description of Test Experiment 4", initDateTime.plusDays(18)));
		experiments.add(courses.get(0).addExperiment("Test Experiment 5", "Description of Test Experiment 5", initDateTime.plusDays(19)));
		
		IntStream.range(0, experiments.size()).forEach(i -> 
			students.get(0).addActiveExperiment(experiments.get(i)));
		IntStream.range(0, experiments.size()).forEach(i -> 
			students.get(1).addActiveExperiment(experiments.get(i)));
		
		reports.put(experiments.get(0), new ArrayList<>());
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusDays(1)));
		reports.get(experiments.get(0)).get(0).setScore(new BigDecimal("45"));
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusHours(14)));
		reports.get(experiments.get(0)).get(1).setScore(new BigDecimal("50"));
		
		measurements.put(experiments.get(2), new ArrayList<>());
		measurements.get(experiments.get(2)).add(
				experiments.get(2).addMeasurement("Angle", Angle.class));
		
		Measurement e3m1 = measurements.get(experiments.get(2)).get(0);
		measurementValues.put(e3m1, new ArrayList<>());
		measurementValues.get(e3m1)
			.add(e3m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(1.3, 0.1, Units.RADIAN),
					initDateTime.minusDays(1).minusHours(10)));
		measurementValues.get(e3m1)
			.add(e3m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(1.5, 0.5, Units.RADIAN),
					initDateTime.minusDays(1)));
		
		measurements.put(experiments.get(3), new ArrayList<>());
		measurements.get(experiments.get(3)).add(
				experiments.get(3).addMeasurement("Speed", Speed.class));
		
		Measurement e4m1 = measurements.get(experiments.get(3)).get(0);
		measurementValues.put(e4m1, new ArrayList<>());
		measurementValues.get(e4m1)
			.add(e4m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(2.3, 0.1, Units.METRE_PER_SECOND),
					initDateTime.minusDays(2).minusHours(10)));
		measurementValues.get(e4m1)
			.add(e4m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(2.6, 0.2, Units.METRE_PER_SECOND),
					initDateTime.minusDays(2)));
		
		measurements.put(experiments.get(4), new ArrayList<>());
		measurements.get(experiments.get(4)).add(
				experiments.get(4).addMeasurement("Voltage", ElectricPotential.class));
		
		Measurement e5m1 = measurements.get(experiments.get(4)).get(0);
		measurementValues.put(e5m1, new ArrayList<>());
		measurementValues.get(e5m1)
			.add(e5m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(0.8, 0.1, Units.VOLT),
					initDateTime.minusDays(3).minusHours(10)));
		measurementValues.get(e5m1)
			.add(e5m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(0.9, 0.2, Units.VOLT),
					initDateTime.minusDays(3)));
		
		reports.put(experiments.get(4), new ArrayList<>());
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(0), initDateTime.minusDays(1).minusHours(4)));
		reports.get(experiments.get(4)).get(0).setScore(new BigDecimal("39"));
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(0), initDateTime.minusHours(17)));
		reports.get(experiments.get(4)).get(1).setScore(new BigDecimal("51"));
		
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
		JpaService.clearTable(MeasurementValue.class, manager);
		JpaService.clearTable(Measurement.class, manager);
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
		
		assertEquals(5, results1.size());
		assertEquals(5, results2.size());
		assertTrue(results3.isEmpty());
		
		assertEquals("Test Experiment 3", results1.get(0).getName());
		assertEquals("Test Experiment 4", results1.get(1).getName());
		assertEquals("Test Experiment 2", results1.get(2).getName());
		
		assertEquals(initDateTime.plusDays(16), results1.get(0).getReportDueDate());
		assertEquals(initDateTime.plusDays(18), results1.get(1).getReportDueDate());
		assertEquals(initDateTime.plusDays(25), results1.get(2).getReportDueDate());
		
		assertEquals(0, BigDecimal.ZERO.compareTo(results1.get(0).getTotalReportScore()));
		assertEquals(0, BigDecimal.ZERO.compareTo(results1.get(1).getTotalReportScore()));
		assertEquals(0, BigDecimal.ZERO.compareTo(results1.get(2).getTotalReportScore()));
	}

}