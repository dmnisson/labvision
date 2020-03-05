package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.experiment.ExperimentService;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class TestExperimentService extends LabvisionApplicationTests {

	@MockBean
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private ExperimentService experimentService;
	
	// Helper function to create NoReports and WithReports lists
	private ExperimentLists makeExperimentLists(int page, final int limit, final int noReportsSize, final int totalSize) {
		List<CurrentExperimentForStudentDashboard> experiments = IntStream.range(
					page * limit + 1, 
					Math.min((page + 1) * limit + 1, totalSize + 1)
					)
				.mapToObj(i -> new CurrentExperimentForStudentDashboard(
						i,
						"Test Experiment " + i,
						1,
						"Test Course",
						LocalDateTime.now().minusDays(25 - i),
						LocalDateTime.of(2050, 3, 1, 0, 0, 0)
						))
				.collect(Collectors.toList());
		
		final ExperimentLists experimentLists = new ExperimentLists(
				experiments.subList(0, Math.max(0, Math.min(limit, noReportsSize - page * limit))),
				(noReportsSize - page * limit < limit) ?
						experiments.subList(
								Math.max(0, noReportsSize - page * limit), 
								Math.max(0, Math.min(limit, totalSize - page * limit))) : 
						new ArrayList<>()
				);
		return experimentLists;
	}
	
	// --- Helpers to mock repository methods ---
	
	private void mockWithSubmissionsRepositoryMethod(final Integer studentId, final int page, final int limit,
			final int noReportsSize, final ExperimentLists experimentLists) {
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				eq(studentId), 
				eq(PageRequest.of(page - noReportsSize / limit, limit))
				))
			.thenReturn(experimentLists.getExperimentsWithReports());
	}

	private void mockNoSubmissionsRepositoryMethod(final Integer studentId, final int page,
			final int limit, final ExperimentLists experimentLists) {
		when(experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(
				eq(studentId), 
				eq(PageRequest.of(page, limit))
				))
			.thenReturn(experimentLists.getExperimentsNoReports());
	}
	
	private void mockCountRepositoryMethod(final Integer studentId, final long totalSize) {
		when(experimentRepository.countCurrentExperimentsByStudentId(
				eq(studentId)
				))
			.thenReturn(totalSize);
	}
	
	private void mockNoSubmissionsCountRepositoryMethod(final Integer studentId, final long noReportsSize) {
		when(experimentRepository.countCurrentExperimentsByStudentIdNoSubmissions(
				eq(studentId)
				))
			.thenReturn(noReportsSize);
	}

	// Helper to call the experiment service method with the correct parameters
	private Iterable<CurrentExperimentForStudentDashboard> getActualExperiments(final Integer studentId, int page, final int limit)
			throws NoSuchMethodException {
		Integer userId = (Integer) new Integer[] { studentId }[0];
		
		return experimentService.findExperimentData(userId, page, limit, CurrentExperimentForStudentDashboard.class);
	}
	
	// Class that stores experiments with no reports and experiments with reports
	static class ExperimentLists {
		private List<CurrentExperimentForStudentDashboard> experimentsNoReports;
		private List<CurrentExperimentForStudentDashboard> experimentsWithReports;

		public ExperimentLists(List<CurrentExperimentForStudentDashboard> experimentsNoReports,
				List<CurrentExperimentForStudentDashboard> experimentsWithReports) {
			this.experimentsNoReports = experimentsNoReports;
			this.experimentsWithReports = experimentsWithReports;
		}

		public List<CurrentExperimentForStudentDashboard> getExperimentsNoReports() {
			return experimentsNoReports;
		}

		public List<CurrentExperimentForStudentDashboard> getExperimentsWithReports() {
			return experimentsWithReports;
		}

		public int getTotalPageSize() {
			return experimentsNoReports.size() + experimentsWithReports.size();
		}
	}
	
	// --- TESTS ---
	
	@Test
	public void findExperimentData_ShouldLimitNoSubmissionsSize() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int noReportsSize = 11;
		final int totalSize = 11;
		
		final ExperimentLists experimentLists1 = makeExperimentLists(0, limit, noReportsSize, totalSize);
		final ExperimentLists experimentLists2 = makeExperimentLists(1, limit, noReportsSize, totalSize);
		
		mockCountRepositoryMethod(studentId, totalSize);
		mockNoSubmissionsCountRepositoryMethod(studentId, noReportsSize);
		mockNoSubmissionsRepositoryMethod(studentId, 0, limit, experimentLists1);
		mockNoSubmissionsRepositoryMethod(studentId, 1, limit, experimentLists2);
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments1
				= getActualExperiments(studentId, 0, limit);
		
		verify(experimentRepository, never()).findCurrentExperimentsForStudentDashboardWithSubmissions(any(), any());
		
		assertEquals(limit, StreamSupport.stream(actualExperiments1.spliterator(), false).count());
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments2
				= getActualExperiments(studentId, 1, limit);

		verify(experimentRepository, never()).findCurrentExperimentsForStudentDashboardWithSubmissions(any(), any());
		
		assertEquals(totalSize - limit, StreamSupport.stream(actualExperiments2.spliterator(), false).count());
	}
	
	@Test
	public void findExperimentData_ShouldLimitWithSubmissionsResults() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int totalSize = 12;
		
		final ExperimentLists experimentLists1 = makeExperimentLists(0, limit, 0, totalSize);
		final ExperimentLists experimentLists2 = makeExperimentLists(1, limit, 0, totalSize);
		
		mockCountRepositoryMethod(studentId, totalSize);
		mockNoSubmissionsCountRepositoryMethod(studentId, 0);
		
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				eq(studentId), 
				eq(PageRequest.of(0, limit))
				))
			.thenReturn(experimentLists1.getExperimentsWithReports());
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				eq(studentId), 
				eq(PageRequest.of(1, limit))
				))
			.thenReturn(experimentLists2.getExperimentsWithReports());
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments1 
				= getActualExperiments(studentId, 0, limit);
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments2 
				= getActualExperiments(studentId, 1, limit);
				
		assertEquals(limit, StreamSupport.stream(actualExperiments1.spliterator(), false).count());
		assertEquals(totalSize - limit, StreamSupport.stream(actualExperiments2.spliterator(), false).count());
	}
	
	@Test
	public void findExperimentData_ShouldLimitTotalResultsSize() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int noReportsSize = 12;
		final int totalSize = 15;
		
		final ExperimentLists experimentLists1 = makeExperimentLists(0, limit, noReportsSize, totalSize);
		final ExperimentLists experimentLists2 = makeExperimentLists(1, limit, noReportsSize, totalSize);
		
		mockCountRepositoryMethod(studentId, totalSize);
		mockNoSubmissionsCountRepositoryMethod(studentId, noReportsSize);
		mockNoSubmissionsRepositoryMethod(studentId, 0, limit, experimentLists1);
		mockNoSubmissionsRepositoryMethod(studentId, 1, limit, experimentLists2);
		mockWithSubmissionsRepositoryMethod(studentId, 1, limit, noReportsSize, experimentLists2);
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments1
				= getActualExperiments(studentId, 0, limit);
		
		verify(experimentRepository, never()).findCurrentExperimentsForStudentDashboardWithSubmissions(any(), any());
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments2
				= getActualExperiments(studentId, 1, limit);
		
		final long count1 = StreamSupport.stream(actualExperiments1.spliterator(), false).count();
		assertEquals(limit, count1);
		final long count2 = StreamSupport.stream(actualExperiments2.spliterator(), false).count();
		assertEquals(totalSize - limit, count2);
		
		assertEquals(totalSize, count1 + count2);
	}

	@Test
	public void findExperimentData_ShouldOrderNoReportsFirst() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int noReportsSize = 12;
		final int totalSize = 16;
		
		final ExperimentLists experimentLists1 = makeExperimentLists(0, limit, noReportsSize, totalSize);
		final ExperimentLists experimentLists2 = makeExperimentLists(1, limit, noReportsSize, totalSize);
		
		mockCountRepositoryMethod(studentId, totalSize);
		mockNoSubmissionsCountRepositoryMethod(studentId, noReportsSize);
		
		mockNoSubmissionsRepositoryMethod(studentId, 0, limit, experimentLists1);
		mockNoSubmissionsRepositoryMethod(studentId, 1, limit, experimentLists2);
		mockWithSubmissionsRepositoryMethod(studentId, 1, limit, noReportsSize, experimentLists2);
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments1
			= getActualExperiments(studentId, 0, limit);
		
		verify(experimentRepository, never()).findCurrentExperimentsForStudentDashboardWithSubmissions(any(), any());
		
		Iterable<CurrentExperimentForStudentDashboard> actualExperiments2
			= getActualExperiments(studentId, 1, limit);
		
		assertEquals(experimentLists1.getTotalPageSize(), 
				StreamSupport.stream(actualExperiments1.spliterator(), false).count());
		assertEquals(experimentLists2.getTotalPageSize(), 
				StreamSupport.stream(actualExperiments2.spliterator(), false).count());
		
		List<CurrentExperimentForStudentDashboard> actualExperimentsList1
			= StreamSupport.stream(actualExperiments1.spliterator(), false).collect(Collectors.toList());
		List<CurrentExperimentForStudentDashboard> actualExperimentsList2
			= StreamSupport.stream(actualExperiments2.spliterator(), false).collect(Collectors.toList());
		
		for (int i = 0; i < experimentLists1.getExperimentsNoReports().size(); i++) {
			final int expectedId = experimentLists1.getExperimentsNoReports().get(i).getId();
			final int actualId = actualExperimentsList1.get(i).getId();
			assertEquals(expectedId, actualId);
		}
		
		assertTrue(experimentLists1.getExperimentsWithReports().isEmpty());
		
		for (int i = 0; i < experimentLists2.getExperimentsNoReports().size(); i++) {
			final int expectedId = experimentLists2.getExperimentsNoReports().get(i).getId();
			final int actualId = actualExperimentsList2.get(i).getId();
			assertEquals(expectedId,
					actualId,
					String.format("Expected id=%d but got id=%d at index %d", expectedId, actualId, i)
					);
		}
		
		for (int i = experimentLists2.getExperimentsNoReports().size(); 
				i < experimentLists2.getTotalPageSize(); 
				i++
				) {
			assertEquals(
					experimentLists2.getExperimentsWithReports()
						.get(i - experimentLists2.getExperimentsNoReports().size())
						.getId(),
					actualExperimentsList2.get(i).getId()
					);
		}
	}
}
