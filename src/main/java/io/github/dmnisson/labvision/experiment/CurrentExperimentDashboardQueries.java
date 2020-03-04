package io.github.dmnisson.labvision.experiment;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class CurrentExperimentDashboardQueries
		implements ExperimentDtoQueries<CurrentExperimentForStudentDashboard, Integer> {

	private final ExperimentRepository experimentRepository;
	
	public CurrentExperimentDashboardQueries(ExperimentRepository experimentRepository) {
		this.experimentRepository = experimentRepository;
	}

	@Override
	public List<CurrentExperimentForStudentDashboard> findExperimentsNoReports(Integer userId, Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(userId, pageable);
	}

	@Override
	public List<CurrentExperimentForStudentDashboard> findExperimentsWithReports(Integer userId, Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(userId, pageable);
	}

}
