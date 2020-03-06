package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ExtendedModelMap;

import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.course.CourseService;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.StudentPreferences;
import io.github.dmnisson.labvision.experiment.ExperimentService;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.models.test.NavLinkSpec;
import io.github.dmnisson.labvision.models.test.NavLinkSpecAssertions;
import io.github.dmnisson.labvision.student.StudentController;
import io.github.dmnisson.labvision.student.StudentPreferencesService;
import io.github.dmnisson.labvision.utils.PaginationUtils;

public class TestStudentController extends LabvisionApplicationTests {
	
	@MockBean
	private LabVisionUserDetailsManager userDetailsManager;
	
	@MockBean
	private ExperimentService experimentService;
	
	@MockBean
	private CourseService courseService;
	
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
	
	abstract class DtoListTester<T, R extends Iterable<T>> {
		
		private Integer studentId;
		private String listAttributeName;
		private int maxListSize;
		private long itemCount;
		
		protected DtoListTester(Integer studentId, String listAttributeName, int maxListSize, long itemCount) {
			this.studentId = studentId;
			this.listAttributeName = listAttributeName;
			this.maxListSize = maxListSize;
			this.itemCount = itemCount;
		}
		
		protected abstract Class<T> getListItemClass();
		protected abstract void mockItemCountFunction();
		protected abstract T mapListItems(int itemNumber);
		protected abstract void assertListItemsHaveSameInfo(T expected, T actual);
		protected abstract void mockStudentPreferencesServiceMethodsForDtoClass(
				Integer studentId, int maxListSize);
		
		protected abstract Collector<T, ?, R> getResultCollector();
		protected abstract void mockFindDtoObjects(R expectedItems);
		
		protected Integer getStudentId() {
			return studentId;
		}
		
		protected String getListAttributeName() {
			return listAttributeName;
		}
		
		protected int getMaxListSize() {
			return maxListSize;
		}
		
		protected long getItemCount() {
			return itemCount;
		}
		
		// Creates a model spy and verifies that a list of a given type is added
		public ExtendedModelMap shouldAddList()
				throws Exception {
			LabVisionUser mockUser = mockLabVisionUser(studentId);
			
			R expectedIterable 
				= IntStream.range(1, (int) Math.min(maxListSize + 1, getItemCount() + 1))
					.mapToObj(i -> this.mapListItems(i))
					.collect(getResultCollector());
			
			mockFindDtoObjects(expectedIterable);
			mockItemCountFunction();
			mockStudentPreferencesServiceMethodsForDtoClass(studentId, maxListSize);
			
			ExtendedModelMap model = new ExtendedModelMap();
			ExtendedModelMap spyModel = spy(model);
			
			runControllerMethod(mockUser, spyModel);
			
			ArgumentCaptor<Iterable<?>> argumentCaptor 
				= ArgumentCaptor.forClass(Iterable.class);
			verify(spyModel, times(1)).addAttribute(eq(listAttributeName), argumentCaptor.capture());
			R actualIterable
				= StreamSupport.stream(argumentCaptor.getValue().spliterator(), false)
					.map(obj -> getListItemClass().cast(obj))
					.collect(getResultCollector());
			
			List<T> expectedList = StreamSupport.stream(expectedIterable.spliterator(), false)
					.collect(Collectors.toList());
			List<T> actualList = StreamSupport.stream(actualIterable.spliterator(), false)
					.collect(Collectors.toList());
			
			assertEquals(expectedList.size(), actualList.size());
		
			for (int i = 0; i < expectedList.size(); i++) {
				assertListItemsHaveSameInfo(expectedList.get(i), actualList.get(i));
			}
			
			return spyModel;
		}

		protected abstract void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception;
	}
	
	@Test
	public void populateModel_ShouldAddNavbarModel() {
		ExtendedModelMap model = new ExtendedModelMap();
		ExtendedModelMap spyModel = spy(model);
		
		LabVisionUserDetails labVisionUserDetails = mock(LabVisionUserDetails.class);
		
		when(userDetailsManager.isAdmin(eq(labVisionUserDetails)))
			.thenReturn(false);
		
		studentController.populateModel(spyModel, labVisionUserDetails);
		
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
		
		ArgumentCaptor<NavbarModel> navbarModelCaptor = ArgumentCaptor.forClass(NavbarModel.class);
		verify(spyModel, times(1)).addAttribute(eq("navbarModel"), navbarModelCaptor.capture());
		final NavbarModel navbarModel = navbarModelCaptor.getValue();
		
		assertNotNull(navbarModel);
		NavLinkSpecAssertions.assertNavLinks(navLinkSpecs, navbarModel);
		assertEquals("/logout", navbarModel.getLogoutLink());
	}
	
	class CurrentExperimentForStudentDashboardListTester
		extends DtoListTester<
			CurrentExperimentForStudentDashboard,
			List<CurrentExperimentForStudentDashboard>
			> {

		public CurrentExperimentForStudentDashboardListTester(Integer studentId, String listAttributeName,
				int maxListSize, long itemCount) {
			super(studentId, listAttributeName, maxListSize, itemCount);
		}

		@Override
		protected Class<CurrentExperimentForStudentDashboard> getListItemClass() {
			return CurrentExperimentForStudentDashboard.class;
		}

		@Override
		protected void mockItemCountFunction() {
			when(experimentService.countCurrentExperimentsByStudentId(eq(getStudentId())))
				.thenReturn(getItemCount());
		}

		@Override
		protected CurrentExperimentForStudentDashboard mapListItems(int itemNumber) {
			return new CurrentExperimentForStudentDashboard(
					itemNumber + 1, 
					"Test Experiment " + itemNumber, 
					1, 
					"Test Course",
					LocalDateTime.of(1990, 1, 1, 0, 0, 0)
						.plusDays(itemNumber),
					LocalDateTime.of(2050, 1, 1, 0, 0, 0)
						.plusDays(7 * itemNumber)
					);
		}

		@Override
		protected void assertListItemsHaveSameInfo(CurrentExperimentForStudentDashboard expected,
				CurrentExperimentForStudentDashboard actual) {
			assertCurrentExperimentForStudentDashboardHasSameInfo(expected, actual);
		}

		@Override
		protected void mockStudentPreferencesServiceMethodsForDtoClass(Integer studentId, int maxListSize) {
			mockStudentPreferencesServiceMethods(studentId, maxListSize, Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		@Override
		protected void mockFindDtoObjects(List<CurrentExperimentForStudentDashboard> expectedList) {
			when(experimentService.findExperimentData(
					eq(getStudentId()),
					eq(0),
					eq(getMaxListSize()), eq(getListItemClass()), 
					any()
					))
				.thenReturn(expectedList);
		}

		@Override
		protected void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception {
			studentController.dashboard(user, spyModel);
		}

		@Override
		protected Collector<CurrentExperimentForStudentDashboard, ?, List<CurrentExperimentForStudentDashboard>> 
		getResultCollector() {
			return Collectors.toList();
		}
		
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsLargerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "currentExperiments";
		final int maxCurrentExperiments = 15;
		final long numOfCurrentExperiments = 17;
		
		ExtendedModelMap spyModel = new CurrentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxCurrentExperiments, numOfCurrentExperiments)
				.shouldAddList();
		
		verify(spyModel).addAttribute("numMoreCurrentExperiments",
				numOfCurrentExperiments - maxCurrentExperiments);
	}

	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsMaxSize() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "currentExperiments";
		final int maxCurrentExperiments = 15;
		final long numOfCurrentExperiments = 15;
		
		ExtendedModelMap spyModel = new CurrentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxCurrentExperiments, numOfCurrentExperiments)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreCurrentExperiments"), any());
	}
	
	@Test
	public void dashboard_ShouldAddListOfCurrentExperimentsSmallerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "currentExperiments";
		final int maxCurrentExperiments = 15;
		final long numOfCurrentExperiments = 12;
		
		ExtendedModelMap spyModel = new CurrentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxCurrentExperiments, numOfCurrentExperiments)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreCurrentExperiments"), any());
	}
	
	class RecentExperimentForStudentDashboardListTester
		extends DtoListTester<
			RecentExperimentForStudentDashboard,
			List<RecentExperimentForStudentDashboard>
		> {

		protected RecentExperimentForStudentDashboardListTester(Integer studentId, String listAttributeName,
				int maxListSize, long itemCount) {
			super(studentId, listAttributeName, maxListSize, itemCount);
		}

		@Override
		protected Class<RecentExperimentForStudentDashboard> getListItemClass() {
			return RecentExperimentForStudentDashboard.class;
		}

		@Override
		protected void mockItemCountFunction() {
			when(experimentService.countRecentExperimentsByStudentId(getStudentId()))
				.thenReturn(getItemCount());
		}

		@Override
		protected RecentExperimentForStudentDashboard mapListItems(int itemNumber) {
			return new RecentExperimentForStudentDashboard(
					itemNumber + 1, 
					"Test Experiment " + itemNumber, 
					LocalDateTime.of(1990, 1, 1, 0, 0, 0)
						.plusDays(itemNumber),
					LocalDateTime.of(1989, 12, 15, 0, 0, 0)
						.plusDays(itemNumber),
					LocalDateTime.of(2050, 1, 1, 0, 0, 0)
						.plusDays(7 * itemNumber)
					);
		}

		@Override
		protected void assertListItemsHaveSameInfo(RecentExperimentForStudentDashboard expected,
				RecentExperimentForStudentDashboard actual) {
			assertRecentExperimentForStudentDashboardHasSameInfo(expected, actual);
		}

		@Override
		protected void mockStudentPreferencesServiceMethodsForDtoClass(Integer studentId, int maxListSize) {
			mockStudentPreferencesServiceMethods(studentId, Integer.MAX_VALUE, maxListSize, Integer.MAX_VALUE);
		}

		@Override
		protected void mockFindDtoObjects(List<RecentExperimentForStudentDashboard> expectedList) {
			when(experimentService.findExperimentData(
					eq(getStudentId()),
					eq(0),
					eq(getMaxListSize()), eq(getListItemClass()), 
					any()
					))
				.thenReturn(expectedList);
		}

		@Override
		protected void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception {
			studentController.dashboard(user, spyModel);
		}

		@Override
		protected Collector<RecentExperimentForStudentDashboard, ?, List<RecentExperimentForStudentDashboard>> getResultCollector() {
			return Collectors.toList();
		}
		
	}
	
	@Test
	public void dashboard_ShouldAddListOfRecentExperimentsLargerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentExperiments";
		final int maxRecentExperiments = 15;
		final long numOfRecentExperiments = 18;
		
		
		ExtendedModelMap spyModel = new RecentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentExperiments, numOfRecentExperiments)
				.shouldAddList();
		
		verify(spyModel).addAttribute("numMoreRecentExperiments", 
				numOfRecentExperiments - maxRecentExperiments);
	}
	
	@Test
	public void dashboard_ShouldAddListOfRecentExperimentsMaxSize() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentExperiments";
		final int maxRecentExperiments = 15;
		final long numOfRecentExperiments = 15;
		
		ExtendedModelMap spyModel = new RecentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentExperiments, numOfRecentExperiments)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreRecentExperiments"), any());
	}
	
	@Test
	public void dashboard_ShouldAddListOfRecentExperimentsSmallerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentExperiments";
		final int maxRecentExperiments = 15;
		final long numOfRecentExperiments = 12;
		
		ExtendedModelMap spyModel = new RecentExperimentForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentExperiments, numOfRecentExperiments)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreRecentExperiments"), any());
	}
	
	class RecentCourseForStudentDashboardListTester
		extends DtoListTester<
			RecentCourseForStudentDashboard,
			List<RecentCourseForStudentDashboard>
		> {
		
		public RecentCourseForStudentDashboardListTester(Integer studentId, String listAttributeName, 
				int maxListSize, long itemCount) {
			super(studentId, listAttributeName, maxListSize, itemCount);
		}

		@Override
		protected Class<RecentCourseForStudentDashboard> getListItemClass() {
			return RecentCourseForStudentDashboard.class;
		}

		@Override
		protected void mockItemCountFunction() {
			when(courseService.countRecentCoursesByStudentId(getStudentId()))
				.thenReturn(getItemCount());
		}

		@Override
		protected RecentCourseForStudentDashboard mapListItems(int i) {
			return new RecentCourseForStudentDashboard(
					i, 
					"Test Course " + i, 
					LocalDateTime.now().minusDays(10 - i), 
					LocalDateTime.now().minusDays(9 - i)
					);
		}

		@Override
		protected void assertListItemsHaveSameInfo(RecentCourseForStudentDashboard expected,
				RecentCourseForStudentDashboard actual) {
			assertEquals(expected.getId(), actual.getId());
			assertEquals(expected.getName(), actual.getName());
			assertEquals(expected.getMostRecentValueTaken(), actual.getMostRecentValueTaken());
			assertEquals(expected.getLastUpdated(), actual.getLastUpdated());
		}

		@Override
		protected void mockStudentPreferencesServiceMethodsForDtoClass(Integer studentId, int maxListSize) {
			mockStudentPreferencesServiceMethods(
					studentId, Integer.MAX_VALUE, Integer.MAX_VALUE, maxListSize);
		}

		@Override
		protected void mockFindDtoObjects(List<RecentCourseForStudentDashboard> expectedList) {
			when(courseService.findCourseData(
					eq(getStudentId()), 
					eq(getMaxListSize()), 
					eq(RecentCourseForStudentDashboard.class)
					))
				.thenReturn(expectedList);
		}

		@Override
		protected void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception {
			studentController.dashboard(user, spyModel);
		}

		@Override
		protected Collector<RecentCourseForStudentDashboard, ?, List<RecentCourseForStudentDashboard>> getResultCollector() {
			return Collectors.toList();
		}
		
		
	}
	
	@Test
	public void dashboard_shouldAddListOfRecentCoursesLargerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentCourses";
		final int maxRecentCourses = 6;
		final long numOfRecentCourses = 8;
		
		ExtendedModelMap spyModel = new RecentCourseForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentCourses, numOfRecentCourses)
				.shouldAddList();
		
		verify(spyModel).addAttribute("numMoreRecentCourses", 
				numOfRecentCourses - maxRecentCourses);
	}
	
	@Test
	public void dashboard_shouldAddListOfRecentCoursesMaxSize() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentCourses";
		final int maxRecentCourses = 6;
		final long numOfRecentCourses = 6;
		
		ExtendedModelMap spyModel = new RecentCourseForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentCourses, numOfRecentCourses)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreRecentCourses"), any());
	}
	
	@Test
	public void dashboard_shouldAddListOfRecentCoursesSmallerThanMax() throws Exception {
		final Integer studentId = 6;
		final String listAttributeName = "recentCourses";
		final int maxRecentCourses = 6;
		final long numOfRecentCourses = 4;
		
		ExtendedModelMap spyModel = new RecentCourseForStudentDashboardListTester(
				studentId, listAttributeName, maxRecentCourses, numOfRecentCourses)
				.shouldAddList();
		
		verify(spyModel, never()).addAttribute(eq("numMoreRecentCourses"), any());
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
		ExtendedModelMap spyModel = spy(model);
		
		studentController.reviewSettings(user, spyModel);
		
		ArgumentCaptor<StudentPreferences> prefsArgumentCaptor 
			= ArgumentCaptor.forClass(StudentPreferences.class);
		verify(spyModel, times(1)).addAttribute(eq("prefs"), prefsArgumentCaptor.capture());
		
		ArgumentCaptor<StudentPreferences> defaultsArgumentCaptor 
			= ArgumentCaptor.forClass(StudentPreferences.class);
		verify(spyModel, times(1)).addAttribute(eq("defaults"), defaultsArgumentCaptor.capture());
		
		assertEquals(studentPreferences, prefsArgumentCaptor.getValue());
		assertEquals(defaultStudentPreferences, defaultsArgumentCaptor.getValue());
	}
	
	@Test
	public void updateSettings_shouldUpdatePreferencesAndAddNewPreferencesToModel() {
		final Integer studentId = 6;
		
		StudentPreferences defaultStudentPreferences = new StudentPreferences();
		
		StudentPreferences newStudentPreferences = new StudentPreferences();
		newStudentPreferences.setMaxCurrentExperiments(7);
		
		LabVisionUser user = mockLabVisionUser(studentId);
		
		ExtendedModelMap model = new ExtendedModelMap();
		ExtendedModelMap spyModel = spy(model);
		
		when(studentPreferencesService.getDefaultStudentPreferences())
			.thenReturn(defaultStudentPreferences);
		
		HashMap<String, String> requestParams = new HashMap<>();
		requestParams.put("maxCurrentExperiments",
				newStudentPreferences.getMaxCurrentExperiments().toString());
		requestParams.put("maxRecentExperiments", "");
		requestParams.put("maxRecentCourses", "");
		
		studentController.updateSettings(requestParams, user, spyModel);
		
		ArgumentCaptor<StudentPreferences> studentPreferencesCaptor 
			= ArgumentCaptor.forClass(StudentPreferences.class);
		verify(studentPreferencesService, times(1))
			.updateStudentPreferences(eq(studentId), studentPreferencesCaptor.capture());
		StudentPreferences argStudentPreferences = studentPreferencesCaptor.getValue();
		assertEquals(newStudentPreferences.getMaxCurrentExperiments(), 
				argStudentPreferences.getMaxCurrentExperiments());
		assertNull(argStudentPreferences.getMaxRecentExperiments());
		assertNull(argStudentPreferences.getMaxRecentCourses());
		
		verify(spyModel, times(1)).addAttribute("prefs", argStudentPreferences);
		verify(spyModel, times(1)).addAttribute("defaults", defaultStudentPreferences);
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
	
	private static void assertRecentExperimentForStudentDashboardHasSameInfo(
			RecentExperimentForStudentDashboard recentExperiment,
			RecentExperimentForStudentDashboard actualRecentExperimentObj) {
		
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
	
	// --- Experiments table tests ---
	
	class CurrentExperimentForStudentExperimentTableListTester
		extends DtoListTester<
			CurrentExperimentForStudentExperimentTable,
			Page<CurrentExperimentForStudentExperimentTable>
			> {

		protected CurrentExperimentForStudentExperimentTableListTester(
				Integer studentId, String listAttributeName,
				int maxListSize, long itemCount) {
			super(studentId, listAttributeName, maxListSize, itemCount);
		}

		@Override
		protected Class<CurrentExperimentForStudentExperimentTable> getListItemClass() {
			return CurrentExperimentForStudentExperimentTable.class;
		}

		@Override
		protected void mockItemCountFunction() {
			when(experimentService.countCurrentExperimentsByStudentId(eq(getStudentId())))
				.thenReturn(getItemCount());
		}

		@Override
		protected CurrentExperimentForStudentExperimentTable mapListItems(int itemNumber) {
			return new CurrentExperimentForStudentExperimentTable(
					itemNumber, 
					"Test Experiment " + itemNumber, 
					LocalDateTime.of(1995, 1, 1, 0, 0, 0).plusDays(itemNumber),
					LocalDateTime.of(2050, 3, 4, 0, 0, 0).plusDays(2 * itemNumber), 
					LocalDateTime.of(1995, 1, 1, 0, 0, 0).plusDays(itemNumber - 1),
					BigDecimal.ZERO);
		}

		@Override
		protected void assertListItemsHaveSameInfo(CurrentExperimentForStudentExperimentTable expected,
				CurrentExperimentForStudentExperimentTable actual) {
			assertEquals(expected.getId(), actual.getId());
			assertEquals(expected.getName(), actual.getName());
			assertEquals(expected.getLastUpdated(), actual.getLastUpdated());
			assertEquals(expected.getReportDueDate(), actual.getReportDueDate());
			assertEquals(expected.getLastReportUpdated(), actual.getLastReportUpdated());
			assertEquals(expected.getTotalReportScore(), actual.getTotalReportScore());
		}

		@Override
		protected void mockStudentPreferencesServiceMethodsForDtoClass(Integer studentId, int maxListSize) {
			// TODO consider adding the option for students to change their default page size 
		}

		@Override
		protected void mockFindDtoObjects(Page<CurrentExperimentForStudentExperimentTable> expectedList) {
			when(experimentService.findExperimentData(
					eq(getStudentId()), 
					eq(PageRequest.of(0, getMaxListSize())), 
					eq(getListItemClass()),
					any()
					))
			.thenReturn(expectedList);
			
			// ensure we return an empty Page object on other calls to avoid ClassCastExceptions
			when(experimentService.findExperimentData(
					eq(getStudentId()), 
					any(),
					not(eq(getListItemClass())),
					any()
					))
			.thenReturn(new PageImpl<>(new ArrayList<>()));
		}

		@Override
		protected void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception {
			studentController.experiments(
					"currentExperiments", 
					user, 
					spyModel, 
					PageRequest.of(0, getMaxListSize()), 
					PageRequest.of(0, getMaxListSize())
					);
		}

		@Override
		protected Collector<
			CurrentExperimentForStudentExperimentTable, 
			?, Page<CurrentExperimentForStudentExperimentTable>
		> getResultCollector() {
			
			return PaginationUtils.pageCollector(PageRequest.of(0, getMaxListSize()), 
					(int) getItemCount());
		}
	}
	
	class PastExperimentForStudentTableListTester
		extends DtoListTester<
			PastExperimentForStudentExperimentTable,
			Page<PastExperimentForStudentExperimentTable>
	> {

		protected PastExperimentForStudentTableListTester(
				Integer studentId, 
				String listAttributeName, 
				int maxListSize,
				long itemCount) {
			super(studentId, listAttributeName, maxListSize, itemCount);
		}

		@Override
		protected Class<PastExperimentForStudentExperimentTable> getListItemClass() {
			return PastExperimentForStudentExperimentTable.class;
		}

		@Override
		protected void mockItemCountFunction() {
			when(experimentService.countPastExperimentsByStudentId(eq(getStudentId())))
				.thenReturn(getItemCount());
		}

		@Override
		protected PastExperimentForStudentExperimentTable mapListItems(int itemNumber) {
			return new PastExperimentForStudentExperimentTable(
					itemNumber, 
					"Test Experiment " + itemNumber, 
					LocalDateTime.of(1997, 2, 1, 0, 0, 0).plusDays(itemNumber), 
					itemNumber % 3, 
					LocalDateTime.of(1996, 3, 5, 0, 0, 0).plusDays(3 * itemNumber),
					BigDecimal.ZERO);
		}

		@Override
		protected void assertListItemsHaveSameInfo(PastExperimentForStudentExperimentTable expected,
				PastExperimentForStudentExperimentTable actual) {
			assertEquals(expected.getId(), actual.getId());
			assertEquals(expected.getName(), actual.getName());
			assertEquals(expected.getLastUpdated(), actual.getLastUpdated());
			assertEquals(expected.getReportCount(), actual.getReportCount());
			assertEquals(expected.getLastReportUpdated(), actual.getLastReportUpdated());
		}

		@Override
		protected void mockStudentPreferencesServiceMethodsForDtoClass(Integer studentId, int maxListSize) {
			// TODO consider adding the option for students to change their default page size 
		}

		@Override
		protected void mockFindDtoObjects(Page<PastExperimentForStudentExperimentTable> expectedItems) {
			when(experimentService.findExperimentData(
					eq(getStudentId()), 
					eq(PageRequest.of(0, getMaxListSize())),
					eq(getListItemClass()), 
					any()
					))
				.thenReturn(expectedItems);
			
			// ensure we return an empty Page object on other calls to avoid ClassCastExceptions
			when(experimentService.findExperimentData(
					eq(getStudentId()), 
					any(), 
					not(eq(getListItemClass())),
					any()
					))
			.thenReturn(new PageImpl<>(new ArrayList<>()));
		}

		@Override
		protected void runControllerMethod(LabVisionUser user, ExtendedModelMap spyModel) throws Exception {
			studentController.experiments(
					"pastExperiments", 
					user, 
					spyModel, 
					PageRequest.of(0, getMaxListSize()), 
					PageRequest.of(0, getMaxListSize())
					);
		}

		@Override
		protected Collector<PastExperimentForStudentExperimentTable, 
		?, Page<PastExperimentForStudentExperimentTable>> getResultCollector() {
			
			return PaginationUtils.pageCollector(
					PageRequest.of(0, getMaxListSize()), 
					(int) getItemCount()
					);
		}
		
	}
	
	// Helper function to verify that PaginationUtils.addPageModelAttributes() has
	// added the correct attributes
	private static void verifyPaginationAttributesAdded(ExtendedModelMap modelSpy, final String listAttributeName,
			final int pageSize, final long itemCount) {
		verify(modelSpy, times(1)).addAttribute(
				listAttributeName + "_pages", 
				IntStream.range(
						1, 
						(int)(itemCount / pageSize) + 2
						)
					.mapToObj(Integer::valueOf)
					.collect(Collectors.toList())
				);
		verify(modelSpy, times(1)).addAttribute(
				listAttributeName + "_currentPage",
				1
				);
		verify(modelSpy, never()).addAttribute(
				eq(listAttributeName + "_prevPageUrl"),
				any()
				);
		verify(modelSpy, times(1)).addAttribute(
				eq(listAttributeName + "_nextPageUrl"),
				anyString()
				);
		verify(modelSpy, times(1)).addAttribute(
				eq(listAttributeName + "_pageUrls"),
				argThat((Map<Integer, String> map) -> 
						map.size() == (int)(itemCount / pageSize) + 1
						)
				);
	}
	
	@Test
	public void experiments_ShouldAddListOfCurrentExperiments() throws Exception {
		final Integer studentId = 6;
		
		final String listAttributeName = "currentExperiments";
		final int pageSize = 15;
		final long itemCount = 17;
		
		ExtendedModelMap modelSpy = new CurrentExperimentForStudentExperimentTableListTester(
				studentId, 
				listAttributeName,
				pageSize, 
				itemCount
				).shouldAddList();
		
		verify(modelSpy, times(1)).addAttribute("activePane", "currentExperiments");
		
		verifyPaginationAttributesAdded(modelSpy, listAttributeName, pageSize, itemCount);
		
	}
	
	@Test
	public void experiments_ShouldAddListOfPastExperiments() throws Exception {
		final Integer studentId = 6;
		
		final String listAttributeName = "pastExperiments";
		final int pageSize = 18;
		final long itemCount = 27;
		
		ExtendedModelMap modelSpy = new PastExperimentForStudentTableListTester(
				studentId, 
				listAttributeName,
				pageSize, 
				itemCount
				).shouldAddList();
		
		verify(modelSpy, times(1)).addAttribute("activePane", "pastExperiments");
		
		verifyPaginationAttributesAdded(modelSpy, listAttributeName, pageSize, itemCount);
	}
}
