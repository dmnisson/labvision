package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ExtendedModelMap;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.StudentPreferences;
import io.github.dmnisson.labvision.experiment.ExperimentService;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.models.test.NavLinkSpec;
import io.github.dmnisson.labvision.models.test.NavLinkSpecAssertions;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.student.StudentController;
import io.github.dmnisson.labvision.student.StudentPreferencesService;

public class TestStudentController extends LabvisionApplicationTests {
	
	@MockBean
	private LabVisionUserDetailsManager userDetailsManager;
	
	@MockBean
	private ExperimentService experimentService;
	
	@MockBean
	private CourseRepository courseRepository;
	
	@MockBean
	private StudentPreferencesService studentPreferencesService;
	
	@Autowired
	private StudentController studentController;
	
	// Helper to mock all of the StudentPreferencesService methods used by the
	// controller
	private void mockStudentPreferencesServiceMethods(
			final Integer studentId, 
			final int maxCurrentExperiments,
			final int maxRecentExperiments, 
			final int maxRecentCourses) {
		
		when(studentPreferencesService.getMaxCurrentExperiments(eq(studentId)))
			.thenReturn(maxCurrentExperiments);
		when(studentPreferencesService.getMaxRecentExperiments(eq(studentId)))
			.thenReturn(maxRecentExperiments);
		when(studentPreferencesService.getMaxRecentCourses(eq(studentId)))
			.thenReturn(maxRecentCourses);
	}
	
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
						NavLinkSpec.of("Settings", StudentController.class, "reviewSettings", null, null),
						NavLinkSpec.of("Courses", StudentController.class, "courses", null, null, null)
						))
		};
		
		NavLinkSpecAssertions.assertNavLinks(navLinkSpecs, navbarModel);
		assertEquals("/logout", navbarModel.getLogoutLink());
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperiments() throws Exception {
		final Integer studentId = 6;
		final int maxCurrentExperiments = 15;
		
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
		when(experimentService.findExperimentsForDashboard(
				eq(studentId),
				eq(maxCurrentExperiments),
				eq(CurrentExperimentForStudentDashboard.class)
				))
			.thenReturn(currentExperiments);
		mockStudentPreferencesServiceMethods(
				studentId, maxCurrentExperiments, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<CurrentExperimentForStudentDashboard> actualCurrentExperiments = 
				(List<CurrentExperimentForStudentDashboard>) model.getAttribute("currentExperiments");
		
		assertEquals(maxCurrentExperiments, actualCurrentExperiments.size());
		assertEquals(currentExperiments.size(), actualCurrentExperiments.size());
		for (int i = 0; i < currentExperiments.size(); i++) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(
					currentExperiments.get(i), 
					actualCurrentExperiments.get(i)
					);
		}
	}

	@Test
	public void dashboard_ShouldAddListOfRecentExperiments() throws Exception {
		final Integer studentId = 6;
		final int maxRecentExperiments = 15;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<RecentExperimentForStudentDashboard> recentExperiments = IntStream.range(1, 16)
				.mapToObj(i -> new RecentExperimentForStudentDashboard(
						i + 1, 
						"Test Experiment " + i, 
						LocalDateTime.of(1990, 1, 1, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(1989, 12, 15, 0, 0, 0)
							.plusDays(i),
						LocalDateTime.of(2050, 1, 1, 0, 0, 0)
							.plusDays(7 * i)
						))
				.collect(Collectors.toList());
		when(experimentService.findExperimentsForDashboard(
				eq(studentId),
				eq(maxRecentExperiments),
				eq(RecentExperimentForStudentDashboard.class)
				))
			.thenReturn(recentExperiments);
		mockStudentPreferencesServiceMethods(
				studentId, Integer.MAX_VALUE, maxRecentExperiments, Integer.MAX_VALUE);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<RecentExperimentForStudentDashboard> actualRecentExperiments = 
				(List<RecentExperimentForStudentDashboard>) model.getAttribute("recentExperiments");
		
		assertEquals(maxRecentExperiments, actualRecentExperiments.size());
		assertEquals(recentExperiments.size(), actualRecentExperiments.size());
		for (int i = 0; i < recentExperiments.size(); i++) {
			assertRecentExperimentForStudentDashboardHasSameInfo(
					recentExperiments.get(i), 
					actualRecentExperiments.get(i)
					);
		}
	}
	
	@Test
	public void dashboard_shouldAddListOfRecentCourses() throws Exception {
		final Integer studentId = 6;
		final int maxRecentCourses = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		List<RecentCourseForStudentDashboard> recentCourses = IntStream.range(1, 7)
				.mapToObj(i -> new RecentCourseForStudentDashboard(
						i, 
						"Test Course " + i, 
						LocalDateTime.now().minusDays(10 - i), 
						LocalDateTime.now().minusDays(9 - i)
						))
				.collect(Collectors.toList());
		
		when(courseRepository.findRecentCoursesForStudentDashboard(
				eq(studentId),
				eq(PageRequest.of(0, maxRecentCourses))
				))
			.thenReturn(recentCourses);
		mockStudentPreferencesServiceMethods(
				studentId, Integer.MAX_VALUE, Integer.MAX_VALUE, maxRecentCourses);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.dashboard(user, model);
		
		@SuppressWarnings("unchecked")
		List<RecentCourseForStudentDashboard> actualRecentCourses =
				(List<RecentCourseForStudentDashboard>) model.getAttribute("recentCourses");
		
		assertEquals(maxRecentCourses, actualRecentCourses.size());
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
	
	@Test
	public void reviewSettings_ShouldAddPreferencesToModel() {
		final Integer studentId = 6;
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		StudentPreferences studentPreferences = new StudentPreferences();
		StudentPreferences defaultStudentPreferences = new StudentPreferences();
		
		when(studentPreferencesService.getStudentPreferences(eq(studentId)))
			.thenReturn(studentPreferences);
		when(studentPreferencesService.getDefaultStudentPreferences())
			.thenReturn(defaultStudentPreferences);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		studentController.reviewSettings(user, model);
		
		assertEquals(studentPreferences, model.getAttribute("prefs"));
		assertEquals(defaultStudentPreferences, model.getAttribute("defaults"));
	}
	
	@Test
	public void updateSettings_shouldUpdatePreferencesAndAddNewPreferencesToModel() {
		final Integer studentId = 6;
		
		StudentPreferences defaultStudentPreferences = new StudentPreferences();
		
		StudentPreferences newStudentPreferences = new StudentPreferences();
		newStudentPreferences.setMaxCurrentExperiments(7);
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		ExtendedModelMap model = new ExtendedModelMap();
		
		when(studentPreferencesService.getDefaultStudentPreferences())
			.thenReturn(defaultStudentPreferences);
		
		studentController.updateSettings(newStudentPreferences, user, model);
		
		verify(studentPreferencesService, times(1)).updateStudentPreferences(studentId, newStudentPreferences);
		
		StudentPreferences modelStudentPreferences = (StudentPreferences) model.getAttribute("prefs");
		
		assertEquals(newStudentPreferences.getMaxCurrentExperiments(), 
				modelStudentPreferences.getMaxCurrentExperiments());
		assertNull(modelStudentPreferences.getMaxRecentExperiments());
		assertNull(modelStudentPreferences.getMaxRecentCourses());
		
		assertEquals(defaultStudentPreferences, model.getAttribute("defaults"));
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
