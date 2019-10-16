package labvision.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import labvision.LabVisionConfig;
import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.measure.Amount;
import tec.units.ri.unit.Units;

@TestInstance(Lifecycle.PER_CLASS)
class TestStudentDashboardService {
	private EntityManagerFactory emf;
	private LabVisionConfig config;
	private StudentDashboardService service;
	
	private List<Student> students;
	private List<Experiment> experiments;
	private List<Course> courses;
	private List<CourseClass> courseClasses;
	private List<Measurement> measurements;
	private List<MeasurementValue> measurementValues;
	private List<ReportedResult> reportedResults;
	
	private LocalDateTime initDateTime;
	
	@BeforeAll
	void setUpBeforeClass() throws Exception {
		emf = Persistence.createEntityManagerFactory(LabVisionConfig.TESTING_PERSISTENCE_UNIT_NAME);
		config = new LabVisionConfig("~/.labvision/test.properties");
		service = new StudentDashboardService(emf, config);
		initDateTime = LocalDateTime.now();
		
		// set up test database
		EntityManager manager = emf.createEntityManager();
		SecureRandom random = new SecureRandom();
		
		students = new ArrayList<>();
		students.add(new Student("Student Tester One", "studenttester1", "password1", config, random));
		students.add(new Student("Student Tester Two", "studenttester2", "password2", config, random));
		
		courses = new ArrayList<>();
		courses.add(new Course("Test 101"));
		courses.add(new Course("Test 102"));
		courses.add(new Course("Test 103"));
		
		courseClasses = new ArrayList<>();
		courseClasses.add(courses.get(0).addCourseClass("Test 101 Morning Class"));
		courseClasses.add(courses.get(0).addCourseClass("Test 101 Afternoon Class"));
		courseClasses.add(courses.get(1).addCourseClass("Test 102 Morning Class"));
		courseClasses.add(courses.get(1).addCourseClass("Test 102 Afternoon Class"));
		
		courseClasses.get(0).addStudent(students.get(0));
		courseClasses.get(1).addStudent(students.get(0));
		courseClasses.get(3).addStudent(students.get(0));
		courseClasses.get(1).addStudent(students.get(1));
		courseClasses.get(2).addStudent(students.get(1));
		
		experiments = new ArrayList<Experiment>();
		measurements = new ArrayList<>();
		measurementValues = new ArrayList<>();
		reportedResults = new ArrayList<>();
		
		experiments.add(courses.get(2).addExperiment(
				"Test Experiment 1", 
				"Description for Test Experiment 1",
				initDateTime.plusDays(7)));
		
		experiments.add(courses.get(1).addExperiment(
				"Test Experiment 2",
				"Description for Test Experiment 2",
				initDateTime.plusDays(9)));
		measurements.add(experiments.get(1).addMeasurement("Length", Length.class));
		measurementValues.add(measurements.get(0).addValue(
				students.get(0),
				courseClasses.get(0),
				new Amount<>(1.2, 0.1, Units.METRE),
				initDateTime.minusHours(10)));
		measurementValues.add(measurements.get(0).addValue(
				students.get(0),
				courseClasses.get(2),
				new Amount<>(1.2, 0.1, Units.METRE),
				initDateTime.minusHours(8)));
		measurementValues.add(measurements.get(0).addValue(
				students.get(1),
				courseClasses.get(1),
				new Amount<>(1.2, 0.1, Units.METRE),
				initDateTime.minusHours(7)));
		
		students.get(0).addActiveExperiment(experiments.get(1));
		
		experiments.add(courses.get(1).addExperiment(
				"Test Experiment 3",
				"Description for Test Experiment 3",
				initDateTime.plusDays(11)
				));
		
		reportedResults.add(experiments.get(2).addReportedResult(students.get(0), initDateTime.minusHours(7)));
		reportedResults.add(experiments.get(2).addReportedResult(students.get(0), initDateTime.minusHours(9)));
		reportedResults.add(experiments.get(2).addReportedResult(students.get(1), initDateTime.minusHours(5)));
		
		students.get(0).addActiveExperiment(experiments.get(2));
		
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 4",
				"Description for Test Experiment 4",
				initDateTime.plusDays(8)));
		
		measurements.add(experiments.get(3).addMeasurement("Duration", Time.class));
		measurementValues.add(measurements.get(1).addValue(
				students.get(0),
				courseClasses.get(1),
				new Amount<>(1.6, 0.1, Units.SECOND),
				initDateTime.minusHours(11)));
		measurementValues.add(measurements.get(1).addValue(
				students.get(0),
				courseClasses.get(3),
				new Amount<>(1.5, 0.1, Units.SECOND),
				initDateTime.minusHours(9)));
		measurementValues.add(measurements.get(1).addValue(
				students.get(1),
				courseClasses.get(2),
				new Amount<>(1.7, 0.1, Units.SECOND),
				initDateTime.minusHours(7)));
		
		reportedResults.add(experiments.get(3).addReportedResult(students.get(0), initDateTime.minusHours(10)));
		reportedResults.add(experiments.get(3).addReportedResult(students.get(0), initDateTime.minusHours(6)));
		reportedResults.add(experiments.get(3).addReportedResult(students.get(1), initDateTime.minusHours(4)));
		
		students.get(0).addActiveExperiment(experiments.get(3));
		students.get(1).addActiveExperiment(experiments.get(3));
		
		experiments.add(courses.get(1).addExperiment("Test Experiment 5", "Description for Test Experiment 5", initDateTime.plusDays(5)));
		measurements.add(experiments.get(4).addMeasurement("Mass", Mass.class));
		measurementValues.add(measurements.get(2).addValue(
				students.get(1),
				courseClasses.get(1),
				new Amount<>(12.5, 0.3, Units.GRAM),
				initDateTime.minusHours(2)));
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
	
		students.forEach(s -> manager.persist(s));
		courses.forEach(c -> manager.persist(c));
		courseClasses.forEach(cc -> manager.persist(cc));
		measurements.forEach(m -> manager.persist(m));
		experiments.forEach(e -> manager.persist(e));
		measurementValues.forEach(mv -> manager.persist(mv));
		reportedResults.forEach(rr -> manager.persist(rr));
		
		tx.commit();
		manager.close();
	}
	
	@AfterAll
	void tearDownAfterClass() throws Exception {
		EntityManager manager = emf.createEntityManager();
		
		JpaService.clearTable(MeasurementValue.class, manager);
		JpaService.clearTable(ReportedResult.class, manager);
		JpaService.clearTable(CourseClass.class, manager);
		JpaService.clearTable(Measurement.class, manager);
		JpaService.clearTable(Student.class, manager);
		JpaService.clearTable(Experiment.class, manager);
		JpaService.clearTable(Course.class, manager);
		
		manager.close();
		emf.close();
	}

	@Test
	void testGetCurrentExperiments() {
		int student1Id = students.get(0).getId();
		int student2Id = students.get(1).getId();
		List<CurrentExperimentForStudentDashboard> experiments1 = service.getCurrentExperiments(student1Id);
		List<CurrentExperimentForStudentDashboard> experiments2 = service.getCurrentExperiments(student2Id);
		
		assertEquals(3, experiments1.size());
		assertEquals(1, experiments2.size());
		
		assertEquals("Test Experiment 2", experiments1.get(0).getName());
		assertEquals("Test Experiment 4", experiments1.get(1).getName());
		assertEquals("Test Experiment 3", experiments1.get(2).getName());
		assertEquals("Test Experiment 4", experiments2.get(0).getName());
		
		assertEquals(initDateTime.minusHours(8), experiments1.get(0).getLastUpdated());
		assertEquals(initDateTime.minusHours(6), experiments1.get(1).getLastUpdated());
		assertEquals(initDateTime.minusHours(7), experiments1.get(2).getLastUpdated());
		assertEquals(initDateTime.minusHours(4), experiments2.get(0).getLastUpdated());
	}
	
	@Test
	void testGetRecentExperiments() {
		int student1Id = students.get(0).getId();
		int student2Id = students.get(1).getId();
		List<RecentExperimentForStudentDashboard> experiments1 = service.getRecentExperiments(student1Id, 5);
		List<RecentExperimentForStudentDashboard> experiments2 = service.getRecentExperiments(student2Id, 5);
		List<RecentExperimentForStudentDashboard> experiments2_limit3 = service.getRecentExperiments(student2Id, 3);
		
		assertEquals(3, experiments1.size());
		assertEquals(4, experiments2.size());
		assertEquals(3, experiments2_limit3.size());
		
		assertEquals("Test Experiment 2", experiments1.get(0).getName());
		assertEquals("Test Experiment 4", experiments1.get(1).getName());
		assertEquals("Test Experiment 3", experiments1.get(2).getName());
		assertEquals("Test Experiment 5", experiments2.get(0).getName());
		assertEquals("Test Experiment 2", experiments2.get(1).getName());
		assertEquals("Test Experiment 4", experiments2.get(2).getName());
		assertEquals("Test Experiment 3", experiments2.get(3).getName());
		assertEquals("Test Experiment 5", experiments2_limit3.get(0).getName());
		assertEquals("Test Experiment 2", experiments2_limit3.get(1).getName());
		assertEquals("Test Experiment 4", experiments2_limit3.get(2).getName());
		
		assertEquals(initDateTime.minusHours(8), experiments1.get(0).getLastUpdated());
		assertEquals(initDateTime.minusHours(6), experiments1.get(1).getLastUpdated());
		assertEquals(initDateTime.minusHours(7), experiments1.get(2).getLastUpdated());
		assertEquals(initDateTime.minusHours(2), experiments2.get(0).getLastUpdated());
		assertEquals(initDateTime.minusHours(7), experiments2.get(1).getLastUpdated());
		assertEquals(initDateTime.minusHours(4), experiments2.get(2).getLastUpdated());
		assertEquals(initDateTime.minusHours(5), experiments2.get(3).getLastUpdated());
	}
	
	@Test
	void testGetRecentCourses() {
		int student1Id = students.get(0).getId();
		int student2Id = students.get(1).getId();
		List<RecentCourseForStudentDashboard> courses1 = service.getRecentCourses(student1Id);
		List<RecentCourseForStudentDashboard> courses2 = service.getRecentCourses(student2Id);
		
		assertEquals(2, courses1.size());
		assertEquals(2, courses2.size());
		
		assertEquals("Test 101", courses1.get(0).getName());
		assertEquals("Test 102", courses1.get(1).getName());
		assertEquals("Test 102", courses2.get(0).getName());
		assertEquals("Test 101", courses2.get(1).getName());
		
		assertEquals(initDateTime.minusHours(6), courses1.get(0).getLastUpdated());
		assertEquals(initDateTime.minusHours(7), courses1.get(1).getLastUpdated());
		assertEquals(initDateTime.minusHours(2), courses2.get(0).getLastUpdated());
		assertEquals(initDateTime.minusHours(4), courses2.get(1).getLastUpdated());
	}
}
