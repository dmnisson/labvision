package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ExtendedModelMap;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.models.test.NavLinkSpec;
import io.github.dmnisson.labvision.models.test.NavLinkSpecAssertions;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.student.StudentController;

public class TestStudentController extends LabvisionApplicationTests {
	
	@MockBean
	private LabVisionUserDetailsManager userDetailsManager;
	
	@MockBean
	private ExperimentRepository experimentRepository;
	
	@MockBean
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentController studentController;
	
	@Test
	public void populateModel_ShouldAddNavbarModel() {
		ExtendedModelMap model = new ExtendedModelMap();
		
		LabVisionUserDetails labVisionUserDetails = mock(LabVisionUserDetails.class);
		
		when(userDetailsManager.isAdmin(eq(labVisionUserDetails)))
			.thenReturn(false);
		
		studentController.populateModel(model, labVisionUserDetails);
		
		final NavbarModel navbarModel = (NavbarModel) model.getAttribute("navbarModel");
		assertNotNull(navbarModel);

		NavLinkSpec[] navLinkSpecs = {
				NavLinkSpec.of("Dashboard", StudentController.class, "dashboard", null, null),
				NavLinkSpec.of("Experiments", StudentController.class, "experiments", null, null, null, null, null),
				NavLinkSpec.of("Reports", StudentController.class, "reports", null, null, null),
				NavLinkSpec.of("Errors", StudentController.class, "errors", null, null, null),
				NavLinkSpec.of("Account", "#", Arrays.asList(
						NavLinkSpec.of("Profile", StudentController.class, "profile", null, null),
						NavLinkSpec.of("Courses", StudentController.class, "courses", null, null, null)
						))
		};
		
		NavLinkSpecAssertions.assertNavLinks(navLinkSpecs, navbarModel);
		assertEquals("/logout", navbarModel.getLogoutLink());
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsWithNoReports() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<CurrentExperimentForStudentDashboard> currentExperiments = IntStream.range(1, 16)
				.mapToObj(i -> new CurrentExperimentForStudentDashboard(
						i + 1, 
						"Test Experiment " + i, 
						1, 
						"Test Course",
						LocalDateTime.of(1990, 1, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
							.plusDays(7 * i)
						))
				.collect(Collectors.toList());
		when(experimentRepository.findCurrentExperimentsForStudentDashboardNoReports(eq(studentId)))
			.thenReturn(currentExperiments);
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithReports(eq(studentId)))
			.thenReturn(new ArrayList<>());
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<CurrentExperimentForStudentDashboard> actualCurrentExperiments = 
				(List<CurrentExperimentForStudentDashboard>) model.getAttribute("currentExperiments");
		
		assertEquals(currentExperiments.size(), actualCurrentExperiments.size());
		for (int i = 0; i < currentExperiments.size(); i++) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(
					currentExperiments.get(i), 
					actualCurrentExperiments.get(i)
					);
		}
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsWithReports() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<CurrentExperimentForStudentDashboard> currentExperiments = IntStream.range(1, 16)
				.mapToObj(i -> new CurrentExperimentForStudentDashboard(
						i + 1, 
						"Test Experiment " + i, 
						1, 
						"Test Course",
						LocalDateTime.of(1990, 1, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
							.plusDays(7 * i)
						))
				.collect(Collectors.toList());
		when(experimentRepository.findCurrentExperimentsForStudentDashboardNoReports(eq(studentId)))
			.thenReturn(new ArrayList<>());
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithReports(eq(studentId)))
			.thenReturn(currentExperiments);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<CurrentExperimentForStudentDashboard> actualCurrentExperiments = 
				(List<CurrentExperimentForStudentDashboard>) model.getAttribute("currentExperiments");
		
		assertEquals(currentExperiments.size(), actualCurrentExperiments.size());
		for (int i = 0; i < currentExperiments.size(); i++) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(
					currentExperiments.get(i), 
					actualCurrentExperiments.get(i)
					);
		}
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsWithNoReportsFirst() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<CurrentExperimentForStudentDashboard> currentExperimentsNoReports = IntStream.range(1, 16)
				.mapToObj(i -> new CurrentExperimentForStudentDashboard(
						i + 1, 
						"Test Experiment " + i, 
						1, 
						"Test Course",
						LocalDateTime.of(1990, 1, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
							.plusDays(7 * i)
						))
				.collect(Collectors.toList());
		
		List<CurrentExperimentForStudentDashboard> currentExperimentsWithReports = IntStream.range(16, 26)
				.mapToObj(i -> new CurrentExperimentForStudentDashboard(
						i + 1, 
						"Test Experiment " + i, 
						1, 
						"Test Course",
						LocalDateTime.of(1990, 1, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
							.plusDays(7 * i)
						))
				.collect(Collectors.toList());
		
		when(experimentRepository.findCurrentExperimentsForStudentDashboardNoReports(eq(studentId)))
			.thenReturn(currentExperimentsNoReports);
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithReports(eq(studentId)))
			.thenReturn(currentExperimentsWithReports);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<CurrentExperimentForStudentDashboard> actualCurrentExperiments = 
				(List<CurrentExperimentForStudentDashboard>) model.getAttribute("currentExperiments");
		
		assertEquals(currentExperimentsNoReports.size() + currentExperimentsWithReports.size(), 
				actualCurrentExperiments.size());
		
		for (int i = 0; i < currentExperimentsNoReports.size(); i++) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(
					currentExperimentsNoReports.get(i), 
					actualCurrentExperiments.get(i)
					);
		}
		
		for (int i = 0; i < currentExperimentsWithReports.size(); i++) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(
					currentExperimentsWithReports.get(i), 
					actualCurrentExperiments.get(i + currentExperimentsNoReports.size())
					);
		}
	}
	
	@Test
	public void dashboard_ShouldAddListOfRecentExperimentsWithNoReportsFirst() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<RecentExperimentForStudentDashboard> recentExperimentsNoReports = IntStream.range(1, 13)
				.mapToObj(i -> new RecentExperimentForStudentDashboard(
						i,
						"Test Experiment " + i,
						LocalDateTime.of(1989, 3, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(1991, 3, 1, 0, 0, 0)
							.plusDays(i),
						null
						))
				.collect(Collectors.toList());
		
		List<RecentExperimentForStudentDashboard> recentExperimentsWithReports = IntStream.range(13, 27)
				.mapToObj(i -> new RecentExperimentForStudentDashboard(
						i,
						"Test Experiment " + i,
						LocalDateTime.of(1989, 3, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(1991, 3, 1, 0, 0, 0)
							.plusDays(i + 1).plusHours(6 * i),
						null
						))
				.collect(Collectors.toList());
		
		when(experimentRepository.findRecentExperimentsForStudentDashboardNoReports(studentId))
			.thenReturn(recentExperimentsNoReports);
		when(experimentRepository.findRecentExperimentsForStudentDashboardWithReports(studentId))
			.thenReturn(recentExperimentsWithReports);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<RecentExperimentForStudentDashboard> actualRecentExperiments = 
				(List<RecentExperimentForStudentDashboard>) model.getAttribute("recentExperiments");
		
		assertEquals(recentExperimentsNoReports.size() + recentExperimentsWithReports.size(),
				actualRecentExperiments.size());
		
		for (int i = 0; i < recentExperimentsNoReports.size(); i++) {
			assertRecentExperimentForStudentDashboardHasSameInfo(
					recentExperimentsNoReports.get(i),
					actualRecentExperiments.get(i)
					);
		}
		
		for (int i = 0; i < recentExperimentsWithReports.size(); i++) {
			assertRecentExperimentForStudentDashboardHasSameInfo(
					recentExperimentsWithReports.get(i),
					actualRecentExperiments.get(i + recentExperimentsNoReports.size())
					);
		}
	}

	@Test
	public void dashboard_shouldAddListOfRecentCourses() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<RecentCourseForStudentDashboard> recentCourses = IntStream.range(1, 7)
				.mapToObj(i -> new RecentCourseForStudentDashboard(
						i, 
						"Test Course " + i, 
						LocalDateTime.now().minusDays(10 - i), 
						LocalDateTime.now().minusDays(9 - i)
						))
				.collect(Collectors.toList());
		
		when(courseRepository.findRecentCoursesForStudentDashboard(studentId))
			.thenReturn(recentCourses);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<RecentCourseForStudentDashboard> actualRecentCourses =
				(List<RecentCourseForStudentDashboard>) model.getAttribute("recentCourses");
		
		assertEquals(recentCourses.size(), actualRecentCourses.size());
		
		for (int i = 0; i < recentCourses.size(); i++) {
			assertEquals(recentCourses.get(i).getId(), 
					actualRecentCourses.get(i).getId());
			assertEquals(recentCourses.get(i).getName(), 
					actualRecentCourses.get(i).getName());
			assertEquals(recentCourses.get(i).getMostRecentValueTaken(), 
					actualRecentCourses.get(i).getMostRecentValueTaken());
			assertEquals(recentCourses.get(i).getLastUpdated(),
					actualRecentCourses.get(i).getLastUpdated());
		}
	}
	
	// Assertion helpers
	private static void assertCurrentExperimentForStudentDashboardHasSameInfo(
			final CurrentExperimentForStudentDashboard currentExperiment,
			final Object actualCurrentExperimentObj) {
		
		assertTrue(actualCurrentExperimentObj instanceof CurrentExperimentForStudentDashboard);
		CurrentExperimentForStudentDashboard actualCurrentExperiment = 
				(CurrentExperimentForStudentDashboard) actualCurrentExperimentObj;
		
		assertEquals(currentExperiment.getId(), 
				actualCurrentExperiment.getId());
		assertEquals(currentExperiment.getName(), 
				actualCurrentExperiment.getName());
		assertEquals(currentExperiment.getCourseId(),
				actualCurrentExperiment.getCourseId());
		assertEquals(currentExperiment.getCourseName(),
				actualCurrentExperiment.getCourseName());
		assertEquals(currentExperiment.getLastUpdated(),
				actualCurrentExperiment.getLastUpdated());
		assertEquals(currentExperiment.getReportDueDate(),
				actualCurrentExperiment.getReportDueDate());
	}
	
	private void assertRecentExperimentForStudentDashboardHasSameInfo(
			RecentExperimentForStudentDashboard recentExperiment,
			Object actualRecentExperimentObj) {
		
		assertTrue(actualRecentExperimentObj instanceof RecentExperimentForStudentDashboard);
		RecentExperimentForStudentDashboard actualRecentExperiment = 
				(RecentExperimentForStudentDashboard) actualRecentExperimentObj;
		
		assertEquals(recentExperiment.getId(),
				actualRecentExperiment.getId());
		assertEquals(recentExperiment.getName(),
				actualRecentExperiment.getName());
		assertEquals(recentExperiment.getLastUpdated(),
				actualRecentExperiment.getLastUpdated());
		assertEquals(recentExperiment.getMostRecentValueTaken(),
				actualRecentExperiment.getMostRecentValueTaken());
		assertEquals(recentExperiment.getReportDueDate(),
				actualRecentExperiment.getReportDueDate());
	}
	
	// Mock helpers
	private static LabVisionUser mockLabVisionUser(final Integer studentId) {
		LabVisionUser user = mock(LabVisionUser.class);
		when(user.getId()).thenReturn(studentId);
		when(user.getDisplayName()).thenReturn("Test User");
		return user;
	}
}
