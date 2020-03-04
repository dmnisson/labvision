package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.measure.quantity.Length;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
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

public class TestCourseRepository extends LabvisionApplicationTests {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseClassRepository courseClassRepository;
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private MeasurementValueRepository measurementValueRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	// Helper function to generate seeds for recent courses tests
	private SeedsForRecentCourses makeSeedsForRecentCourses(String studentUsername, String studentName, int courseNumbersFrom, int courseNumbersTo) {
		
		Student testStudent = new Student();
		testStudent.setUsername(studentUsername);
		testStudent.setName(studentName);
		testStudent = studentRepository.save(testStudent);
		
		List<Course> testCourses = IntStream.range(courseNumbersFrom, courseNumbersTo)
				.mapToObj(i -> {
					Course testCourse = new Course("Test Course " + i);
					return courseRepository.save(testCourse);
				}).collect(Collectors.toList());
		
		// First test course has student enrolled
		CourseClass testCourse1classOnline = testCourses.get(0)
				.addCourseClass("Test Course 101 Online");
		testCourse1classOnline.addStudent(testStudent);
		testCourse1classOnline = courseClassRepository.save(testCourse1classOnline);
		
		// Second test course has measurement value submission from student
		CourseClass testCourse2classOnline = testCourses.get(1)
				.addCourseClass("Test Course 102 Online");
		testCourse2classOnline.addStudent(testStudent);
		testCourse2classOnline = courseClassRepository.save(testCourse2classOnline);
		Experiment testExperiment1 = testCourses.get(1)
				.addExperiment(
						"Test Experiment 1", 
						"Test experiment number 1.", 
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
						);
		Measurement testExperiment1measurement1
			= testExperiment1.addMeasurement("Test Measurement", Length.class);
		testExperiment1 = experimentRepository.save(testExperiment1);
		testExperiment1measurement1 = testExperiment1.getMeasurements().stream().findAny().get();
		MeasurementValue testExperiment1measurement1value1
			= testExperiment1measurement1.addValue(
					testStudent, 
					testCourse2classOnline, 
					new Amount<>(1.1, 0.1, SI.getInstance().getUnit(Length.class)), 
					LocalDateTime.now().minusHours(2)
					);
		measurementValueRepository.save(testExperiment1measurement1value1);
		
		// Third test course has report submission from student
		CourseClass testCourse3classOnline = testCourses.get(2)
				.addCourseClass("Test Course 102 Online");
		testCourse3classOnline.addStudent(testStudent);
		testCourse3classOnline = courseClassRepository.save(testCourse3classOnline);
		Experiment testExperiment2 = testCourses.get(2)
				.addExperiment(
						"Test Experiment 2", 
						"Test experiment number 2.", 
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
						);
		testExperiment2 = experimentRepository.save(testExperiment2);
		ReportedResult reportedResult = testExperiment2.addReportedResult(testStudent);
		reportedResult.setName("Test Report for Test Experiment 2");
		reportedResult.setAdded(LocalDateTime.now().minusHours(3));
		reportedResult = reportedResultRepository.saveAndFlush(reportedResult);
		
		// Fourth test course has experiment active for student
		Experiment testExperiment3 = testCourses.get(3)
				.addExperiment(
						"Test Experiment 3",
						"Test experiment number 3.",
						LocalDateTime.of(2050, 1, 2, 0, 0, 0)
						);
		testExperiment3 = experimentRepository.save(testExperiment3);
		testStudent.addActiveExperiment(testExperiment3);
		studentRepository.save(testStudent);
		
		return new SeedsForRecentCourses(testStudent, testCourses);
	}
	
	static class SeedsForRecentCourses {
		
		private Student testStudent;
		
		private List<Course> testCourses;
		
		public SeedsForRecentCourses(Student testStudent, List<Course> testCourses) {
			this.testStudent = testStudent;
			this.testCourses = testCourses;
		}

		public Student getTestStudent() {
			return testStudent;
		}

		public void setTestStudent(Student testStudent) {
			this.testStudent = testStudent;
		}

		public List<Course> getTestCourses() {
			return testCourses;
		}

		public void setTestCourses(List<Course> testCourses) {
			this.testCourses = testCourses;
		}
		
	}
	
	@Transactional
	@Test
	public void findRecentCoursesForStudentDashboard_ShouldGetCoursesWhereStudentIsEnrolledOrHasSubmissions() {
		
		SeedsForRecentCourses seeds1 
			= makeSeedsForRecentCourses("testStudent1", "Test Student One", 101, 105);
		SeedsForRecentCourses seeds2
			= makeSeedsForRecentCourses("testStudent2", "Test Student Two", 105, 109);
		
		List<RecentCourseForStudentDashboard> recentCourses
			= courseRepository.findRecentCoursesForStudentDashboard(
					seeds1.getTestStudent().getId(),
					PageRequest.of(0, Integer.MAX_VALUE)
					);
		
		for (Course course : seeds1.getTestCourses()) {
			assertTrue(
					recentCourses.stream().anyMatch(c -> course.getId().equals(c.getId())),
					String.format("Expected to find course %s but did not", course.getName())
					);
		}
		
		for (Course course : seeds2.getTestCourses()) {
			assertTrue(
					recentCourses.stream().noneMatch(c -> course.getId().equals(c.getId())),
					String.format("Expected not to find course %s but did", course.getName())
					);
		}
		
	}
	
}
