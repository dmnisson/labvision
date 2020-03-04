package io.github.dmnisson.labvision.experiment;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class RecentExperimentDashboardQueries
		implements ExperimentDtoQueries<RecentExperimentForStudentDashboard, Integer> {

	private final ExperimentRepository experimentRepository;
	
	public RecentExperimentDashboardQueries(ExperimentRepository experimentRepository) {
		this.experimentRepository = experimentRepository;
	}

	@Override
	public List<RecentExperimentForStudentDashboard> findExperimentsNoReports(Integer userId, Pageable pageable) {
		return experimentRepository.findRecentExperimentsForStudentDashboardNoSubmissions(userId, pageable);
	}

	@Override
	public List<RecentExperimentForStudentDashboard> findExperimentsWithReports(Integer userId, Pageable pageable) {
		return experimentRepository.findRecentExperimentsForStudentDashboardWithSubmissions(userId, pageable);
	}

}
