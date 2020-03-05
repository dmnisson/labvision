package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	private ExperimentLists makeExperimentLists(final int limit, final int noReportsSize) {
		List<CurrentExperimentForStudentDashboard> experiments = IntStream.range(1, limit + 1)
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
				experiments.subList(0, noReportsSize), 
				(noReportsSize < limit) ? experiments.subList(noReportsSize, limit) : null
				);
		return experimentLists;
	}
	
	// --- Helpers to mock repository methods ---
	
	private void mockWithReportsRepositoryMethod(final Integer studentId, final int limit, final int noReportsSize,
			final ExperimentLists experimentLists) {
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				eq(studentId), 
				eq(PageRequest.of(0, limit - noReportsSize))
				))
			.thenReturn(experimentLists.getExperimentsWithReports());
	}

	private void mockNoReportsRepositoryMethod(final Integer studentId, final int limit,
			final ExperimentLists experimentLists) {
		when(experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(
				eq(studentId), 
				eq(PageRequest.of(0, limit))
				))
			.thenReturn(experimentLists.getExperimentsNoReports());
	}

	// Helper to call the experiment service method with the correct parameters
	private List<CurrentExperimentForStudentDashboard> getActualExperiments(final Integer studentId, final int limit)
			throws NoSuchMethodException {
		Integer userId = (Integer) new Integer[] { studentId }[0];
		
		return experimentService.findExperimentData(userId, limit, CurrentExperimentForStudentDashboard.class);
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

		public int getTotalSize() {
			return experimentsNoReports.size() + experimentsWithReports.size();
		}
	}
	
	// --- TESTS ---
	
	@Test
	public void findExperimentData_ShouldLimitNoRe() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		
		final ExperimentLists experimentLists = makeExperimentLists(limit, limit);
		
		mockNoReportsRepositoryMethod(studentId, limit, experimentLists);
		
		List<CurrentExperimentForStudentDashboard> actualExperiments 
				= getActualExperiments(studentId, limit);
		
		verify(experimentRepository, never()).findCurrentExperimentsForStudentDashboardWithSubmissions(any(), any());
		
		assertEquals(limit, actualExperiments.size());
	}
	
	@Test
	public void findExperimentData_ShouldLimitWithReportsResults() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		
		final ExperimentLists experimentLists = makeExperimentLists(limit, 0);
		
		when(experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(
				eq(studentId), 
				eq(PageRequest.of(0, limit))
				))
			.thenReturn(experimentLists.getExperimentsWithReports());
		
		List<CurrentExperimentForStudentDashboard> actualExperiments 
				= getActualExperiments(studentId, limit);
				
		assertEquals(limit, actualExperiments.size());
	}
	
	@Test
	public void findExperimentData_ShouldLimitTotalResultsSize() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int noReportsSize = 4;
		
		final ExperimentLists experimentLists = makeExperimentLists(limit, noReportsSize);
		
		mockNoReportsRepositoryMethod(studentId, limit, experimentLists);
		mockWithReportsRepositoryMethod(studentId, limit, noReportsSize, experimentLists);
		
		List<CurrentExperimentForStudentDashboard> actualExperiments
				= getActualExperiments(studentId, limit);
		
		assertEquals(limit, actualExperiments.size());
	}

	@Test
	public void findExperimentData_ShouldOrderNoReportsFirst() throws Exception {
		final Integer studentId = 6;
		final int limit = 8;
		final int noReportsSize = 4;
		
		final ExperimentLists experimentLists = makeExperimentLists(limit, noReportsSize);
		
		mockNoReportsRepositoryMethod(studentId, limit, experimentLists);
		mockWithReportsRepositoryMethod(studentId, limit, noReportsSize, experimentLists);
		
		List<CurrentExperimentForStudentDashboard> actualExperiments
			= getActualExperiments(studentId, limit);
		
		assertEquals(experimentLists.getTotalSize(), actualExperiments.size());
		
		for (int i = 0; i < experimentLists.getExperimentsNoReports().size(); i++) {
			assertEquals(experimentLists.getExperimentsNoReports().get(i).getId(),
					actualExperiments.get(i).getId());
		}
		
		for (int i = experimentLists.getExperimentsNoReports().size(); 
				i < experimentLists.getTotalSize(); 
				i++
				) {
			assertEquals(
					experimentLists.getExperimentsWithReports()
						.get(i - experimentLists.getExperimentsNoReports().size())
						.getId(),
					actualExperiments.get(i).getId()
					);
		}
	}
}
