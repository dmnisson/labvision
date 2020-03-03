package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

import javax.measure.quantity.Angle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.CourseClass;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.MeasurementValue;
import io.github.dmnisson.labvision.entities.ReportedResult;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.measure.Amount;
import io.github.dmnisson.labvision.measure.SI;
import io.github.dmnisson.labvision.repositories.CourseClassRepository;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.MeasurementValueRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

public class TestExperimentRepository extends LabvisionApplicationTests {

	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;

	@Autowired
	private CourseClassRepository courseClassRepository;

	@Autowired
	private MeasurementValueRepository measurementValueRepository;
	
	// Helper that tests that a given repository method returns only active experiments
	private void assertRepositoryMethodShouldOnlyGetActiveExperiments(
			final BiFunction<Integer, Pageable, List<CurrentExperimentForStudentDashboard>> repositoryMethodCaller, boolean addReportedResults) {
		Student testStudent1 = new Student();
		testStudent1.setUsername("testStudent1");
		testStudent1.setName("Test Student One");
		
		Student testStudent2 = new Student();
		testStudent2.setUsername("testStudent2");
		testStudent2.setName("Test Student Two");
		
		Course testCourse = new Course();
		testCourse.setName("Test Course 101");
		testCourse = courseRepository.save(testCourse);
		
		Experiment activeExperiment1 = 
				testCourse.addExperiment(
						"Test Experiment 1",
						"Test Student One's only active experiment.", 
						LocalDateTime.of(2050, 3, 4, 0, 0, 0)
						);
		testCourse = courseRepository.save(testCourse);
		
		testStudent1.addActiveExperiment(activeExperiment1);
		final Experiment savedActiveExperiment1 = experimentRepository.saveAndFlush(activeExperiment1);
		testStudent1 = studentRepository.save(testStudent1);
		
		if (addReportedResults) {
			ReportedResult reportedResult1 = savedActiveExperiment1.addReportedResult(testStudent1);
			reportedResult1.setName("Test Report 1");
			reportedResultRepository.saveAndFlush(reportedResult1);
		}
		
		testCourse = courseRepository.saveAndFlush(testCourse);
		
		Experiment activeExperiment2 = experimentRepository.saveAndFlush(
				testCourse.addExperiment(
						"Test Experiment 2",
						"The only experiment not active by Test Student One.", 
						LocalDateTime.of(2050, 3, 4, 0, 0, 0)
						)
				);
		
		testStudent2.addActiveExperiment(activeExperiment2);
		final Experiment savedActiveExperiment2 = experimentRepository.saveAndFlush(activeExperiment2);
		testStudent2 = studentRepository.save(testStudent2);
		
		if (addReportedResults) {
			ReportedResult reportedResult2 = savedActiveExperiment2.addReportedResult(testStudent1);
			reportedResult2.setName("Test Report 2");
			reportedResultRepository.saveAndFlush(reportedResult2);
		}
		
		testCourse = courseRepository.saveAndFlush(testCourse);
		
		List<CurrentExperimentForStudentDashboard> currentExperiments =
				repositoryMethodCaller.apply(testStudent1.getId(), PageRequest.of(0, Integer.MAX_VALUE));
		
		assertTrue(
				currentExperiments.stream()
					.anyMatch(e -> savedActiveExperiment1.getId().equals(e.getId()))
				);
		assertTrue(
				currentExperiments.stream()
					.noneMatch(e -> savedActiveExperiment2.getId().equals(e.getId()))
				);
	}
	
	private RecentExperimentEntitySeeds seedEntitiesForRecentExperiments() {
		Student testStudent1 = new Student();
		testStudent1.setUsername("testStudent1");
		testStudent1.setName("Test Student One");
		
		Student testStudent2 = new Student();
		testStudent2.setUsername("testStudent2");
		testStudent2.setName("Test Student Two");
		
		Course testCourse = new Course();
		testCourse.setName("Test Course 101");
		testCourse = courseRepository.save(testCourse);
		
		CourseClass testCourseClass = testCourse.addCourseClass("Test Course 101 Morning");
		testCourseClass.addStudent(testStudent1);
		studentRepository.save(testStudent1);
		courseClassRepository.save(testCourseClass);
		
		Experiment experiment1 = testCourse.addExperiment(
				"Test Experiment 1", 
				"Test experiment number 1.",
				LocalDateTime.of(2050, 3, 4, 0, 0, 0)
				);
		testStudent1.addActiveExperiment(experiment1);
		Experiment savedExperiment1 = experimentRepository.saveAndFlush(experiment1);
		
		Experiment experiment2 = testCourse.addExperiment(
				"Test Experiment 2", 
				"Test experiment number 2.",
				LocalDateTime.of(2050, 3, 8, 0, 0, 0)
				);
		Measurement experiment2measurement1 = experiment2.addMeasurement("Test Measurement 1", Angle.class);
		
		MeasurementValue experiment2measurement1value1 
			= experiment2measurement1.addValue(
					testStudent1,
					testCourseClass,
					new Amount<>(1.6, 0.1, SI.getInstance().getUnit(Angle.class)),
					LocalDateTime.now()
					);
		measurementValueRepository.save(experiment2measurement1value1);
		
		Experiment savedExperiment2 = experimentRepository.saveAndFlush(experiment2);
		
		Experiment experiment3 = testCourse.addExperiment(
				"Test Experiment 3", 
				"Test experiment number 3.",
				LocalDateTime.of(2050, 3, 12, 0, 0, 0)
				);
		
		ReportedResult experiment3reportedResult1 = experiment3.addReportedResult(testStudent1);
		experiment3reportedResult1.setName("Test report 1 for Test Experiment 3");
		reportedResultRepository.save(experiment3reportedResult1);
		
		Experiment savedExperiment3 = experimentRepository.saveAndFlush(experiment3);
		
		Experiment experiment4 = testCourse.addExperiment(
				"Test Experiment 4", 
				"Test experiment number 4.",
				LocalDateTime.of(2050, 3, 16, 0, 0, 0)
				);
		
		Experiment savedExperiment4 = experimentRepository.saveAndFlush(experiment4);
		
		return new RecentExperimentEntitySeeds(
				testStudent1,
				savedExperiment1,
				savedExperiment2,
				savedExperiment3,
				savedExperiment4
				);
	}

	private CurrentExperimentEntitySeeds seedEntitiesForCurrentExperiments() {
		Student testStudent1 = new Student();
		testStudent1.setUsername("testStudent1");
		testStudent1.setName("Test Student One");
		
		Student testStudent2 = new Student();
		testStudent2.setUsername("testStudent2");
		testStudent2.setName("Test Student Two");
		
		Course testCourse = new Course();
		testCourse.setName("Test Course 101");
		testCourse = courseRepository.save(testCourse);
		
		Experiment activeExperiment1 = 
				testCourse.addExperiment(
						"Test Experiment 1",
						"Test experiment number 1.", 
						LocalDateTime.of(2050, 3, 4, 0, 0, 0)
						);
		
		testStudent1 = studentRepository.save(testStudent1);
		ReportedResult reportedResult1 = activeExperiment1.addReportedResult(testStudent1);
		reportedResult1.setName("Test Student One's Report For Experiment 1");
		reportedResult1 = reportedResultRepository.save(reportedResult1);
		
		testStudent1.addActiveExperiment(activeExperiment1);
		final Experiment savedActiveExperiment1 = experimentRepository.saveAndFlush(activeExperiment1);
		testStudent1 = studentRepository.save(testStudent1);
		
		testCourse = courseRepository.saveAndFlush(testCourse);
		
		Experiment activeExperiment2 = experimentRepository.saveAndFlush(
				testCourse.addExperiment(
						"Test Experiment 2",
						"Test experiment number 2.", 
						LocalDateTime.of(2050, 3, 4, 0, 0, 0)
						)
				);
		
		ReportedResult reportedResult2 = activeExperiment2.addReportedResult(testStudent2);
		reportedResult2.setName("Test Student Two's Report For Experiment 2");
		reportedResult2 = reportedResultRepository.save(reportedResult2);
		
		testStudent1.addActiveExperiment(activeExperiment2);
		testStudent1 = studentRepository.save(testStudent1);
		testStudent2 = studentRepository.save(testStudent2);
		final Experiment savedActiveExperiment2 = experimentRepository.saveAndFlush(activeExperiment2);
		
		testCourse = courseRepository.saveAndFlush(testCourse);
		
		final CurrentExperimentEntitySeeds parameterObject = new CurrentExperimentEntitySeeds(testStudent1, savedActiveExperiment1, savedActiveExperiment2);
		return parameterObject;
	}
	
	static class CurrentExperimentEntitySeeds {
		private Student testStudent1;
		private Experiment savedActiveExperiment1;
		private Experiment savedActiveExperiment2;

		public CurrentExperimentEntitySeeds(Student testStudent1, Experiment savedActiveExperiment1,
				Experiment savedActiveExperiment2) {
			this.testStudent1 = testStudent1;
			this.savedActiveExperiment1 = savedActiveExperiment1;
			this.savedActiveExperiment2 = savedActiveExperiment2;
		}

		public Student getTestStudent1() {
			return testStudent1;
		}

		public Experiment getSavedActiveExperiment1() {
			return savedActiveExperiment1;
		}

		public Experiment getSavedActiveExperiment2() {
			return savedActiveExperiment2;
		}
	}

	static class RecentExperimentEntitySeeds {
		private Student testStudent1;
		private Experiment savedExperiment1;
		private Experiment savedExperiment2;
		private Experiment savedExperiment3;
		private Experiment savedExperiment4;

		public RecentExperimentEntitySeeds(
				Student testStudent1,
				Experiment savedExperiment1,
				Experiment savedExperiment2,
				Experiment savedExperiment3,
				Experiment savedExperiment4) {
			this.testStudent1 = testStudent1;
			this.savedExperiment1 = savedExperiment1;
			this.savedExperiment2 = savedExperiment2;
			this.savedExperiment3 = savedExperiment3;
			this.savedExperiment4 = savedExperiment4;
		}

		public Student getTestStudent1() {
			return testStudent1;
		}

		public Experiment getSavedExperiment1() {
			return savedExperiment1;
		}

		public Experiment getSavedExperiment2() {
			return savedExperiment2;
		}

		public Experiment getSavedExperiment3() {
			return savedExperiment3;
		}

		public Experiment getSavedExperiment4() {
			return savedExperiment4;
		}
		
	}
	
	@Transactional
	@Test
	public void findCurrentExperimentsForStudentDashboardNoReports_ShouldOnlyGetActiveExperiments() {
		
		assertRepositoryMethodShouldOnlyGetActiveExperiments((id, pageable) -> 
			experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(id, pageable), false);
	}

	@Transactional
	@Test
	public void findCurrentExperimentsForStudentDashboardWithReports_ShouldOnlyGetActiveExperiments() {
		
		assertRepositoryMethodShouldOnlyGetActiveExperiments((id, pageable) -> 
			experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(id, pageable), true);
	}
	
	@Transactional
	@Test
	public void findCurrentExperimentsForStudentDashboardNoReports_ShouldGetExperimentsWithNoReports() {
		
		final CurrentExperimentEntitySeeds seeds = seedEntitiesForCurrentExperiments();
		
		List<CurrentExperimentForStudentDashboard> currentExperiments =
		experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(
				seeds.getTestStudent1().getId(), 
				PageRequest.of(0, Integer.MAX_VALUE)
				);
				
		assertTrue(
				currentExperiments.stream()
					.noneMatch(e -> seeds.getSavedActiveExperiment1().getId().equals(e.getId()))
				);
		assertTrue(
				currentExperiments.stream()
					.anyMatch(e -> seeds.getSavedActiveExperiment2().getId().equals(e.getId()))
				);
	}

	@Transactional
	@Test
	public void findCurrentExperimentsForStudentDashboardWithReports_ShouldGetExperimentsWithReports() {
		
		final CurrentExperimentEntitySeeds seeds = seedEntitiesForCurrentExperiments();
		
		List<CurrentExperimentForStudentDashboard> currentExperiments =
		experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				seeds.getTestStudent1().getId(), 
				PageRequest.of(0, Integer.MAX_VALUE)
				);
				
		assertTrue(
				currentExperiments.stream()
					.anyMatch(e -> seeds.getSavedActiveExperiment1().getId().equals(e.getId()))
				);
		assertTrue(
				currentExperiments.stream()
					.noneMatch(e -> seeds.getSavedActiveExperiment2().getId().equals(e.getId()))
				);
	}
	
	@Transactional
	@Test
	public void findRecentExperimentsForStudentDashboardNoReports_ShouldGetExperimentsWithNoReports() {
		
		final RecentExperimentEntitySeeds seeds = seedEntitiesForRecentExperiments();
		
		List<RecentExperimentForStudentDashboard> recentExperiments
				= experimentRepository.findRecentExperimentsForStudentDashboardNoSubmissions(
						seeds.getTestStudent1().getId(), 
						PageRequest.of(0, Integer.MAX_VALUE));
		
		assertTrue(
				recentExperiments.stream()
					.anyMatch(e -> seeds.getSavedExperiment1().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.noneMatch(e -> seeds.getSavedExperiment2().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.noneMatch(e -> seeds.getSavedExperiment3().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.anyMatch(e -> seeds.getSavedExperiment4().getId().equals(e.getId()))
				);
		
	}
	
	@Transactional
	@Test
	public void findRecentExperimentsForStudentDashboardWithReports_ShouldGetExperimentsWithReports() {
		
		final RecentExperimentEntitySeeds seeds = seedEntitiesForRecentExperiments();
		
		List<RecentExperimentForStudentDashboard> recentExperiments
				= experimentRepository.findRecentExperimentsForStudentDashboardWithSubmissions(
						seeds.getTestStudent1().getId(), 
						PageRequest.of(0, Integer.MAX_VALUE));
		
		assertTrue(
				recentExperiments.stream()
					.noneMatch(e -> seeds.getSavedExperiment1().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.anyMatch(e -> seeds.getSavedExperiment2().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.anyMatch(e -> seeds.getSavedExperiment3().getId().equals(e.getId()))
				);
		assertTrue(
				recentExperiments.stream()
					.noneMatch(e -> seeds.getSavedExperiment4().getId().equals(e.getId()))
				);
		
	}
	
}