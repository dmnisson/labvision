package labvision.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Speed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import labvision.LabVisionConfig;
import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.ParameterForExperimentView;
import labvision.dto.experiment.ParameterValueForExperimentView;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.entities.Course;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.FileType;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
import labvision.entities.ReportDocument;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.entities.Variable;
import labvision.measure.Amount;
import labvision.utils.Pair;
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
		
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 1",
				"Description of Test Experiment 1",
				initDateTime.plusDays(23)));
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 2",
				"Description of Test Experiment 2",
				initDateTime.plusDays(25)));
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 3",
				"Description of Test Experiment 3", 
				initDateTime.plusDays(16)));
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 4",
				"Description of Test Experiment 4",
				initDateTime.plusDays(18)));
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 5",
				"Description of Test Experiment 5",
				initDateTime.plusDays(19)));
		experiments.add(courses.get(0).addExperiment(
				"Test Experiment 6",
				"Description of Test Experiment 6",
				initDateTime.plusDays(20)));
		
		IntStream.range(0, experiments.size()).forEach(i -> 
			students.get(0).addActiveExperiment(experiments.get(i)));
		IntStream.range(0, experiments.size()).forEach(i -> 
			students.get(1).addActiveExperiment(experiments.get(i)));
		
		reports.put(experiments.get(0), new ArrayList<>());
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusDays(1)));
		reports.get(experiments.get(0)).get(0)
				.makeReportDocument("Test_Report_1_exp_1.pdf", FileType.PDF, "Test_Report_1_exp_1.pdf");
		reports.get(experiments.get(0)).get(0).setScore(new BigDecimal("45"));
		reports.get(experiments.get(0)).add(experiments.get(0)
				.addReportedResult(students.get(0), initDateTime.minusHours(14)));
		reports.get(experiments.get(0)).get(1)
				.makeReportDocument("Test_Report_2_exp_1.pdf", FileType.PDF, "Test_Report_2_exp_1.pdf");
		reports.get(experiments.get(0)).get(1).setScore(new BigDecimal("50"));
		
		measurements.put(experiments.get(1), new ArrayList<>());
		measurements.get(experiments.get(1)).add(
				experiments.get(1).addMeasurement("Acceleration", Acceleration.class));
		
		Measurement e2m1 = measurements.get(experiments.get(1)).get(0);
		
		// parameters for Acceleration measurement in experiment 2
		parameters.put(e2m1, new ArrayList<>());
		parameters.get(e2m1).add(e2m1.addParameter("Mass", Mass.class));
		parameters.get(e2m1).add(e2m1.addParameter("angle", Angle.class));
		parameters.get(e2m1).add(e2m1.addParameter("Power", Power.class));
		
		// values for Acceleration measurement in experiment 2
		measurementValues.put(e2m1, new ArrayList<>());
		MeasurementValue e2m1v1 = e2m1.addValue(
				students.get(1), 
				courseClasses.get(0),
				new Amount<>(7.1, 0.3, Units.METRE_PER_SQUARE_SECOND),
				initDateTime.minusHours(8));
		measurementValues.get(e2m1).add(e2m1v1);
		
		MeasurementValue e2m1v2 = e2m1.addValue(
				students.get(2),
				courseClasses.get(0),
				new Amount<>(7.2, 0.1, Units.METRE_PER_SQUARE_SECOND),
				initDateTime.minusHours(7));
		measurementValues.get(e2m1).add(e2m1v2);
		
		// parameter values
		parameterValues.put(e2m1v1, new ArrayList<>());
		parameterValues.get(e2m1v1).add(e2m1v1.addParameterValue(
				parameters.get(e2m1).get(0),
				new Amount<>(140.1, 1.3, Units.GRAM)));
		parameterValues.get(e2m1v1).add(e2m1v1.addParameterValue(
				parameters.get(e2m1).get(1),
				new Amount<>(97.403, 1.1, Units.RADIAN)));
		parameterValues.get(e2m1v1).add(e2m1v1.addParameterValue(
				parameters.get(e2m1).get(2),
				new Amount<>(25.1, 0.4, Units.WATT)));
		
		parameterValues.put(e2m1v2, new ArrayList<>());
		parameterValues.get(e2m1v2).add(e2m1v2.addParameterValue(
				parameters.get(e2m1).get(0),
				new Amount<>(171.1, 1.3, Units.GRAM)));
		parameterValues.get(e2m1v2).add(e2m1v2.addParameterValue(
				parameters.get(e2m1).get(1),
				new Amount<>(99.181, 1.1, Units.RADIAN)));
		parameterValues.get(e2m1v2).add(e2m1v2.addParameterValue(
				parameters.get(e2m1).get(2),
				new Amount<>(27.1, 0.4, Units.WATT)));
		
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
		
		reports.put(experiments.get(2), new ArrayList<>());
		reports.get(experiments.get(2)).add(
				experiments.get(2).addReportedResult(
						students.get(2), 
						initDateTime.minusHours(8)));
		reports.get(experiments.get(2)).get(0).setScore(new BigDecimal("79"));
		
		measurements.put(experiments.get(3), new ArrayList<>());
		measurements.get(experiments.get(3)).add(
				experiments.get(3).addMeasurement("Speed", Speed.class));
		
		Measurement e4m1 = measurements.get(experiments.get(3)).get(0);
		measurementValues.put(e4m1, new ArrayList<>());
		measurementValues.get(e4m1)
			.add(e4m1.addValue(
					students.get(0), 
					courseClasses.get(0), 
					new Amount<>(2.6, 0.2, Units.METRE_PER_SECOND),
					initDateTime.minusDays(2)));
		measurementValues.get(e4m1)
		.add(e4m1.addValue(
				students.get(0), 
				courseClasses.get(0), 
				new Amount<>(2.3, 0.1, Units.METRE_PER_SECOND),
				initDateTime.minusDays(2).minusHours(10)));
		
		measurementValues.get(e4m1)
			.add(e4m1.addValue(
					students.get(2), 
					courseClasses.get(0), 
					new Amount<>(2.7, 0.2, Units.METRE_PER_SECOND), 
					initDateTime.minusHours(10)));
		
		reports.put(experiments.get(3), new ArrayList<>());
		reports.get(experiments.get(3)).add(
				experiments.get(3).addReportedResult(
						students.get(2),
						initDateTime.minusHours(9)));
		reports.get(experiments.get(3)).get(0)
				.makeReportDocument("Test_Report_1_exp_4.pdf", FileType.PDF, "Test_Report_1_exp_4.pdf");
		reports.get(experiments.get(3)).get(0).setScore(new BigDecimal("67"));
		
		
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
		measurementValues.get(e5m1)
			.add(e5m1.addValue(
					students.get(2), 
					courseClasses.get(0), 
					new Amount<>(0.7, 0.2, Units.VOLT), 
					initDateTime.minusHours(11)));
		
		reports.put(experiments.get(4), new ArrayList<>());
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(0), initDateTime.minusDays(1).minusHours(4)));
		reports.get(experiments.get(4)).get(0)
				.makeReportDocument("Test_Report_1_exp_5.pdf", FileType.PDF, "Test_Report_1_exp_5.pdf");
		reports.get(experiments.get(4)).get(0).setScore(new BigDecimal("39"));
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(0), initDateTime.minusHours(17)));
		reports.get(experiments.get(4)).get(1)
				.makeReportDocument("Test_Report_2_exp_5.pdf", FileType.PDF, "Test_Report_2_exp_5.pdf");
		reports.get(experiments.get(4)).get(1).setScore(new BigDecimal("51"));
		
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(2), initDateTime.minusHours(12)));
		reports.get(experiments.get(4)).get(2)
				.makeReportDocument("Test_Report_3_exp_5.pdf", FileType.PDF, "Test_Report_3_exp_5.pdf");
		reports.get(experiments.get(4)).get(2).setScore(new BigDecimal("14"));
		reports.get(experiments.get(4)).add(experiments.get(4)
				.addReportedResult(students.get(2), initDateTime.minusHours(13)));
		reports.get(experiments.get(4)).get(3)
				.makeReportDocument("Test_Report_4_exp_5.pdf", FileType.PDF, "Test_Report_4_exp_5.pdf");
		reports.get(experiments.get(4)).get(3).setScore(new BigDecimal("47"));
		
		measurements.put(experiments.get(5), new ArrayList<>());
		measurements.get(experiments.get(5)).add(
				experiments.get(5).addMeasurement("Voltage", ElectricPotential.class));
		measurements.get(experiments.get(5)).add(
				experiments.get(5).addMeasurement("magnetic Field", MagneticFluxDensity.class));
		
		reports.put(experiments.get(5), new ArrayList<>());
		reports.get(experiments.get(5)).add(experiments.get(5)
				.addReportedResult(students.get(1), initDateTime.minusHours(15)));
		reports.get(experiments.get(5)).get(0).setScore(new BigDecimal("85"));
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		
		courses.forEach(c -> manager.persist(c));
		students.forEach(s -> manager.persist(s));
		courseClasses.forEach(cc -> manager.persist(cc));
		experiments.forEach(e -> manager.persist(e));
		reports.forEach((e, l) -> l.forEach(rr -> manager.persist(rr)));
		measurements.forEach((e, l) -> l.forEach(m -> manager.persist(m)));
		measurementValues.forEach((m, l) -> l.forEach(mv -> manager.persist(mv)));
		parameters.forEach((m, l) -> l.forEach(p -> manager.persist(p)));
		parameterValues.forEach((m, l) -> l.forEach(pv -> manager.persist(pv)));
		
		tx.commit();
		
		service = new StudentExperimentService(entityManagerFactory);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		JpaService.clearTable(ParameterValue.class, manager);
		JpaService.clearTable(Parameter.class, manager);
		JpaService.clearTable(MeasurementValue.class, manager);
		JpaService.clearTable(Measurement.class, manager);
		JpaService.clearTable(ReportedResult.class, manager);
		JpaService.clearTable(ReportDocument.class, manager);
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
		
		assertEquals(6, results1.size());
		assertEquals(6, results2.size());
		assertTrue(results3.isEmpty());
		
		String[] expectedExperimentNames1 = {
				"Test Experiment 6", // report due t+20
				"Test Experiment 2", // report due t+25
				"Test Experiment 1", // report submitted t-14
				"Test Experiment 5", // report submitted t-17
				"Test Experiment 3", // measurement taken t-24
				"Test Experiment 4"  // measurement taken t-48
		};
		String[] expectedExperimentNames2 = {
				"Test Experiment 3", // report due t+16
				"Test Experiment 4", // report due t+18
				"Test Experiment 5", // report due t+19
				"Test Experiment 1", // report due t+23
				"Test Experiment 2", // measurement taken t-8
				"Test Experiment 6"  // report submitted t-15
		};
		
		assertArrayEquals(expectedExperimentNames1, results1.stream()
				.map(CurrentExperimentForStudentExperimentTable::getName)
				.toArray(String[]::new));
		assertArrayEquals(expectedExperimentNames2, results2.stream()
				.map(CurrentExperimentForStudentExperimentTable::getName)
				.toArray(String[]::new));
		
		LocalDateTime[] expectedReportDueDates1 = {
				initDateTime.plusDays(20), // Test Experiment 6
				initDateTime.plusDays(25), // Test Experiment 2
				initDateTime.plusDays(23), // Test Experiment 1
				initDateTime.plusDays(19), // Test Experiment 5
				initDateTime.plusDays(16), // Test Experiment 3
				initDateTime.plusDays(18), // Test Experiment 4
		};
		LocalDateTime[] expectedReportDueDates2 = {
				initDateTime.plusDays(16), // Test Experiment 3
				initDateTime.plusDays(18), // Test Experiment 4
				initDateTime.plusDays(19), // Test Experiment 5
				initDateTime.plusDays(23), // Test Experiment 1
				initDateTime.plusDays(25), // Test Experiment 2
				initDateTime.plusDays(20), // Test Experiment 6
		};
		
		assertArrayEquals(expectedReportDueDates1, results1.stream()
				.map(CurrentExperimentForStudentExperimentTable::getReportDueDate)
				.toArray(LocalDateTime[]::new));
		assertArrayEquals(expectedReportDueDates2, results2.stream()
				.map(CurrentExperimentForStudentExperimentTable::getReportDueDate)
				.toArray(LocalDateTime[]::new));
		

		BigDecimal[] expectedTotalReportScores1 = {
				BigDecimal.ZERO,	    // Test Experiment 6
				BigDecimal.ZERO,        // Test Experiment 2
				BigDecimal.valueOf(95), // Test Experiment 1
				BigDecimal.valueOf(90), // Test Experiment 5
				BigDecimal.ZERO,        // Test Experiment 3
				BigDecimal.ZERO         // Test Experiment 4
		};
		BigDecimal[] expectedTotalReportScores2 = {
				BigDecimal.ZERO,        // Test Experiment 3
				BigDecimal.ZERO,        // Test Experiment 4
				BigDecimal.ZERO,        // Test Experiment 5
				BigDecimal.ZERO,        // Test Experiment 1
				BigDecimal.ZERO,        // Test Experiment 2
				BigDecimal.valueOf(85)  // Test Experiment 6
		};
		
		assertArrayEquals(
				new int[expectedTotalReportScores1.length],
				IntStream.range(0, expectedTotalReportScores1.length)
					.map(i -> expectedTotalReportScores1[i]
							.compareTo(results1.get(i).getTotalReportScore()))
					.toArray());
		assertArrayEquals(
				new int[expectedTotalReportScores2.length],
				IntStream.range(0, expectedTotalReportScores2.length)
					.map(i -> expectedTotalReportScores2[i]
							.compareTo(results2.get(i).getTotalReportScore()))
					.toArray());
	}
	
	@Test
	void testGetPastExperiments() {
		List<PastExperimentForStudentExperimentTable> results1 = 
				service.getPastExperiments(students.get(0).getId());
		List<PastExperimentForStudentExperimentTable> results2 = 
				service.getPastExperiments(students.get(1).getId());
		List<PastExperimentForStudentExperimentTable> results3 =
				service.getPastExperiments(students.get(2).getId());

		assertTrue(results1.isEmpty());
		assertTrue(results2.isEmpty());
		assertEquals(4, results3.size());
		
		String[] expectedExperimentNames = {
				"Test Experiment 2",
				"Test Experiment 3",
				"Test Experiment 4",
				"Test Experiment 5"
		};
		
		assertArrayEquals(expectedExperimentNames, results3.stream()
				.map(PastExperimentForStudentExperimentTable::getName)
				.toArray(String[]::new));
		
		LocalDateTime[] expectedLastUpdatedDates = {
				initDateTime.minusHours(7),
				initDateTime.minusHours(8),
				initDateTime.minusHours(9),
				initDateTime.minusHours(11)
		};
		
		assertArrayEquals(expectedLastUpdatedDates, results3.stream()
				.map(PastExperimentForStudentExperimentTable::getLastUpdated)
				.toArray(LocalDateTime[]::new));
		
		long[] expectedReportCounts = { 0, 1, 1, 2 };
		
		assertArrayEquals(expectedReportCounts, results3.stream()
				.mapToLong(PastExperimentForStudentExperimentTable::getReportCount)
				.toArray());
		
		LocalDateTime[] expectedLastReportUpdatedDates = {
				null,
				initDateTime.minusHours(8),
				initDateTime.minusHours(9),
				initDateTime.minusHours(12)
		};
		
		assertArrayEquals(expectedLastReportUpdatedDates, results3.stream()
				.map(PastExperimentForStudentExperimentTable::getLastReportUpdated)
				.toArray(LocalDateTime[]::new));
		
		BigDecimal[] expectedTotalReportScores = {
				BigDecimal.ZERO,
				new BigDecimal("79"),
				new BigDecimal("67"),
				new BigDecimal("61")
		};
		
		assertArrayEquals(new int[expectedTotalReportScores.length],
				IntStream.range(0, expectedTotalReportScores.length)
					.map(i -> expectedTotalReportScores[i]
							.compareTo(results3.get(i).getTotalReportScore()))
					.toArray());
	}
	
	@Test
	void testGetMeasurementValues() {
		// mapping of student and experiment to expected results
		Map<Student, Map<Experiment, Map<Integer, List<MeasurementValueForExperimentView>>>> results
			= students.stream().collect(Collectors.toMap(Function.identity(),
					(student) -> experiments.stream().collect(Collectors.toMap(Function.identity(),
							(experiment) -> service.getMeasurementValues(experiment.getId(), student.getId())
							))
					));
		
		// experiment 1 should have no measurements
		students.forEach(student -> 
			assertTrue(results.get(student).get(experiments.get(0)).isEmpty()));
		
		// experiment 2 has 1 measurement
		students.forEach(student -> 
			assertEquals(1, results.get(student).get(experiments.get(1)).size()));
		
		// measurement values of experiment 2
		int e2m1Id = measurements.get(experiments.get(1)).get(0).getId();
		assertTrue(results.get(students.get(0)).get(experiments.get(1)).get(e2m1Id).isEmpty());
		assertEquals(1, results.get(students.get(1)).get(experiments.get(1)).get(e2m1Id).size());
		assertEquals(1, results.get(students.get(2)).get(experiments.get(1)).get(e2m1Id).size());
		MeasurementValueForExperimentView mv22 = 
				results.get(students.get(1)).get(experiments.get(1)).get(e2m1Id).get(0);
		MeasurementValueForExperimentView mv32 = 
				results.get(students.get(2)).get(experiments.get(1)).get(e2m1Id).get(0);
		
		assertEquals("Acceleration", mv22.getMeasurementName());
		assertEquals("Acceleration", mv32.getMeasurementName());
		
		assertEquals(Units.METRE_PER_SQUARE_SECOND.getDimension(),
				Variable.dimensionObjectFor(mv22.getDimension()));
		assertEquals(Units.METRE_PER_SQUARE_SECOND.getDimension(),
				Variable.dimensionObjectFor(mv32.getDimension()));
		
		assertEquals(Units.METRE_PER_SQUARE_SECOND.toString(), mv22.getUnitString());
		assertEquals(Units.METRE_PER_SQUARE_SECOND.toString(), mv32.getUnitString());
		
		assertEquals(7.1, mv22.getValue());
		assertEquals(7.2, mv32.getValue());
		
		assertEquals(0.3, mv22.getUncertainty());
		assertEquals(0.1, mv32.getUncertainty());
		
		assertEquals(initDateTime.minusHours(8), mv22.getTaken());
		assertEquals(initDateTime.minusHours(7), mv32.getTaken());
		
		// measurement values of experiment 3
		int e3m1Id = measurements.get(experiments.get(2)).get(0).getId();
		assertEquals(2, results.get(students.get(0)).get(experiments.get(2)).get(e3m1Id).size());
		assertTrue(results.get(students.get(1)).get(experiments.get(2)).get(e3m1Id).isEmpty());
		assertTrue(results.get(students.get(2)).get(experiments.get(2)).get(e3m1Id).isEmpty());
		
		List<MeasurementValueForExperimentView> mv3 = 
				results.get(students.get(0)).get(experiments.get(2)).get(e3m1Id);
		
		// check measurement name
		mv3.stream()
			.map(MeasurementValueForExperimentView::getMeasurementName)
			.forEach(name -> assertEquals("Angle", name));
		
		// check dimensions
		mv3.stream()
			.map(MeasurementValueForExperimentView::getDimension)
			.map(Variable::dimensionObjectFor)
			.forEach(dimension -> assertEquals(Units.RADIAN.getDimension(), dimension));
		
		// check unit symbols
		mv3.stream()
			.map(MeasurementValueForExperimentView::getUnitString)
			.forEach(str -> assertEquals(Units.RADIAN.toString(), str));
		
		// check ordering of values and uncertainties
		assertArrayEquals(
				new double[] {1.3, 1.5},
				mv3.stream()
					.mapToDouble(MeasurementValueForExperimentView::getValue)
					.toArray());
		assertArrayEquals(
				new double[] {0.1, 0.5},
				mv3.stream()
					.mapToDouble(MeasurementValueForExperimentView::getUncertainty)
					.toArray());
		
		// check timestamps
		assertArrayEquals(
				new LocalDateTime[] {
						initDateTime.minusDays(1).minusHours(10),
						initDateTime.minusDays(1)
				},
				mv3.stream()
					.map(MeasurementValueForExperimentView::getTaken)
					.toArray(LocalDateTime[]::new));
		
		// measurement values of experiment 4
		int e4m1Id = measurements.get(experiments.get(3)).get(0).getId();
		List<MeasurementValueForExperimentView> mv14 =
				results.get(students.get(0)).get(experiments.get(3)).get(e4m1Id);
		List<MeasurementValueForExperimentView> mv24 =
				results.get(students.get(1)).get(experiments.get(3)).get(e4m1Id);
		List<MeasurementValueForExperimentView> mv34 =
				results.get(students.get(2)).get(experiments.get(3)).get(e4m1Id);
		
		assertEquals(2, mv14.size());
		assertTrue(mv24.isEmpty());
		assertEquals(1, mv34.size());
		
		// check measurement name
		Stream.concat(mv14.stream(), mv34.stream())
			.map(MeasurementValueForExperimentView::getMeasurementName)
			.forEach(name -> assertEquals("Speed", name));
		
		// check dimensions
		Stream.concat(mv14.stream(), mv34.stream())
			.map(MeasurementValueForExperimentView::getDimension)
			.map(Variable::dimensionObjectFor)
			.forEach(dimension -> assertEquals(Units.METRE_PER_SECOND.getDimension(), dimension));
		
		// check unit symbols
		Stream.concat(mv14.stream(), mv34.stream())
			.map(MeasurementValueForExperimentView::getUnitString)
			.forEach(str -> assertEquals(Units.METRE_PER_SECOND.toString(), str));
		
		// check ordering of values and uncertainties
		assertArrayEquals(
				new double[] {2.3, 2.6, 2.7},
				Stream.concat(mv14.stream(), mv34.stream())
					.mapToDouble(MeasurementValueForExperimentView::getValue)
					.toArray());
		assertArrayEquals(
				new double[] {0.1, 0.2, 0.2},
				Stream.concat(mv14.stream(), mv34.stream())
					.mapToDouble(MeasurementValueForExperimentView::getUncertainty)
					.toArray());
		
		// check timestamps
		assertArrayEquals(
				new LocalDateTime[] {
						initDateTime.minusDays(2).minusHours(10),
						initDateTime.minusDays(2),
						initDateTime.minusHours(10)
				},
				Stream.concat(mv14.stream(), mv34.stream())
					.map(MeasurementValueForExperimentView::getTaken)
					.toArray(LocalDateTime[]::new));
	}
	
	@Test
	void testGetMeasurements() {
		// experiment IDs
		int[] experimentIds = experiments.stream()
				.mapToInt(Experiment::getId)
				.toArray();
		
		// measurement names
		String[][] expectedMeasurementNames = {
				{ },
				{ "Acceleration" },
				{ "Angle" },
				{ "Speed" },
				{ "Voltage" },
				{ "magnetic Field", "Voltage" }
		};
		
		IntStream.range(0, expectedMeasurementNames.length)
			.forEach(i -> {
				assertArrayEquals(expectedMeasurementNames[i], 
						service.getMeasurements(experimentIds[i]).stream()
							.map(MeasurementForExperimentView::getName)
							.toArray(String[]::new));
			});
		
		// unit symbols
		String[][] expectedUnitStrings = {
				{ },
				{ Units.METRE_PER_SQUARE_SECOND.toString() },
				{ Units.RADIAN.toString() },
				{ Units.METRE_PER_SECOND.toString() },
				{ Units.VOLT.toString() },
				{ Units.TESLA.toString(), Units.VOLT.toString() }
		};
		
		IntStream.range(0, expectedUnitStrings.length)
		.forEach(i -> {
			assertArrayEquals(expectedUnitStrings[i], 
					service.getMeasurements(experimentIds[i]).stream()
						.map(MeasurementForExperimentView::getUnitString)
						.toArray(String[]::new));
		});
	}
	
	@Test
	void testGetParameters() {
		// parameters of the Acceleration measurement in Experiment 2
		List<ParameterForExperimentView> e2m1p = service.getParameters(
				measurements.get(experiments.get(1)).get(0).getId());
		
		assertEquals(3, e2m1p.size());
		
		String[] expectedNames = { "angle", "Mass", "Power" };
		assertArrayEquals(expectedNames, e2m1p.stream()
				.map(ParameterForExperimentView::getName)
				.toArray(String[]::new));
		
		String[] expectedUnitStrings = {
				Units.RADIAN.toString(),
				Units.KILOGRAM.toString(),
				Units.WATT.toString()
		};
		assertArrayEquals(expectedUnitStrings, e2m1p.stream()
				.map(ParameterForExperimentView::getUnitString)
				.toArray(String[]::new));
		
		// no other measurements have parameters
		experiments.stream()
			.filter(e -> e != experiments.get(1) && measurements.get(e) != null)
			.flatMapToInt(e -> measurements.get(e).stream().mapToInt(Measurement::getId))
			.forEach(measurementId -> {
				assertTrue(service.getParameters(measurementId).isEmpty());
			});
	}
	
	@Test
	void testGetParameterValues() {
		// parameter values for the Acceleration measurement in experiment 2
		int e2m1v1Id = measurementValues.get(measurements.get(experiments.get(1)).get(0)).get(0).getId();
		int e2m1v2Id = measurementValues.get(measurements.get(experiments.get(1)).get(0)).get(1).getId();
		
		Map<Integer, ParameterValueForExperimentView> e2m1v1pv = 
				service.getParameterValues(e2m1v1Id);
		Map<Integer, ParameterValueForExperimentView> e2m1v2pv =
				service.getParameterValues(e2m1v2Id);
		
		assertEquals(3, e2m1v1pv.size());
		assertEquals(3, e2m1v2pv.size());
		
		// maps should contain ids of all parameters
		Map<String, Integer> expectedParameterIds = 
				parameters.get(measurements.get(experiments.get(1)).get(0)).stream()
				.collect(Collectors.toMap(Parameter::getName, Parameter::getId));
		assertTrue(e2m1v1pv.keySet().containsAll(expectedParameterIds.values()));
		assertTrue(e2m1v2pv.keySet().containsAll(expectedParameterIds.values()));

		// expected parameter values
		Map<String, Double> expectedValues1 = Stream.of(
			new Pair<>("Mass", 0.1401),
			new Pair<>("angle", 97.403),
			new Pair<>("Power", 25.1)
		)
			.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		Map<String, Double> expectedValues2 = Stream.of(
			new Pair<>("Mass", 0.1711),
			new Pair<>("angle", 99.181),
			new Pair<>("Power", 27.1)
		)
			.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		
		expectedValues1.entrySet().stream()
			.forEach(e -> assertEquals(
					e.getValue(),
					e2m1v1pv.get(expectedParameterIds.get(e.getKey())).getValue(),
					0.0001));
		expectedValues2.entrySet().stream()
			.forEach(e -> assertEquals(
					e.getValue(),
					e2m1v2pv.get(expectedParameterIds.get(e.getKey())).getValue(),
					0.0001));
		
		// expected parameter uncertainties
		Map<String, Double> expectedUncertainties1 = Stream.of(
			new Pair<>("Mass", 0.0013),
			new Pair<>("angle", 1.1),
			new Pair<>("Power", 0.4)
		)
			.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		Map<String, Double> expectedUncertainties2 = Stream.of(
			new Pair<>("Mass", 0.0013),
			new Pair<>("angle", 1.1),
			new Pair<>("Power", 0.4)
		)
			.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		
		expectedUncertainties1.entrySet().stream()
			.forEach(e -> assertEquals(
					e.getValue(),
					e2m1v1pv.get(expectedParameterIds.get(e.getKey())).getUncertainty(),
					0.0001));
		expectedUncertainties2.entrySet().stream()
			.forEach(e -> assertEquals(
					e.getValue(),
					e2m1v2pv.get(expectedParameterIds.get(e.getKey())).getUncertainty(),
					0.0001));
		
		// no parameters for the other measurement values
		measurementValues.entrySet().stream()
			.flatMapToInt(e -> e.getValue().stream().mapToInt(MeasurementValue::getId))
			.filter(id -> id != e2m1v1Id && id != e2m1v2Id)
			.forEach(id -> {
				assertTrue(service.getParameterValues(id).isEmpty());
			});
		
	}
	
	@Test
	void testGetReportedResults() {
		String[][][] expectedFilenames = {
			// student 1
			{
				// experiment 1
				{ "Test_Report_2_exp_1.pdf", "Test_Report_1_exp_1.pdf" },
				// experiment 2
				{ },
				// experiment 3
				{ },
				// experiment 4
				{ },
				// experiment 5
				{ "Test_Report_2_exp_5.pdf", "Test_Report_1_exp_5.pdf" },
				// experiment 6
				{ }
			},
			// student 2
			{
				// experiment 1
				{ },
				// experiment 2
				{ },
				// experiment 3
				{ },
				// experiment 4
				{ },
				// experiment 5
				{ },
				// experiment 6 has a report but no documents
				{ null }
			},
			// student 3
			{
				// experiment 1
				{ },
				// experiment 2
				{ },
				// experiment 3
				{ null },
				// experiment 4
				{ "Test_Report_1_exp_4.pdf" },
				// experiment 5
				{ "Test_Report_3_exp_5.pdf", "Test_Report_4_exp_5.pdf" },
				// experiment 6
				{ }
			}
		};
		
		LocalDateTime[][][] expectedSubmissionDates = {
			{
				{ initDateTime.minusHours(14), initDateTime.minusDays(1) },
				{ },
				{ },
				{ },
				{ initDateTime.minusHours(17), initDateTime.minusDays(1).minusHours(4) },
				{ }
			},
			{
				{ },
				{ },
				{ },
				{ },
				{ },
				{ initDateTime.minusHours(15) }
			},
			{
				{ },
				{ },
				{ initDateTime.minusHours(8) },
				{ initDateTime.minusHours(9) },
				{ initDateTime.minusHours(12), initDateTime.minusHours(13) },
				{ }
			}
		};
		
		BigDecimal[][][] expectedScores = {
			{
				{ new BigDecimal("50"), new BigDecimal("45") },
				{ },
				{ },
				{ },
				{ new BigDecimal("51"), new BigDecimal("39") },
				{ }
			},
			{
				{ },
				{ },
				{ },
				{ },
				{ },
				{ new BigDecimal("85") }
			},
			{
				{ },
				{ },
				{ new BigDecimal("79") },
				{ new BigDecimal("67") },
				{ new BigDecimal("14"), new BigDecimal("47") },
				{ }
			}
		};
		
		IntStream.range(0, students.size())
			.mapToObj(Integer::valueOf)
			.flatMap(is -> IntStream.range(0, experiments.size())
					.mapToObj(ie -> new Pair<>(is, ie)))
			.forEach(pair -> {
				int is = pair.getKey();
				int ie = pair.getValue();
				
				List<ReportedResultForStudentExperimentView> results = service
						.getReportedResults(experiments.get(ie).getId(), students.get(is).getId());
				
				String failMessage = String.format("failure for student %d, experiment %d", is+1, ie+1);
				
				assertArrayEquals(expectedFilenames[is][ie], results.stream()
						.map(ReportedResultForStudentExperimentView::getReportDocumentFilename)
						.toArray(String[]::new),
						failMessage);
				assertArrayEquals(expectedSubmissionDates[is][ie], results.stream()
						.map(ReportedResultForStudentExperimentView::getAdded)
						.toArray(LocalDateTime[]::new),
						failMessage);
				
				assertArrayEquals(new int[expectedScores[is][ie].length],
						IntStream.range(0, results.size())
							.map(ir -> expectedScores[is][ie][ir].compareTo(results.get(ir).getScore()))
							.toArray(),
							failMessage);
			});
	}
}
