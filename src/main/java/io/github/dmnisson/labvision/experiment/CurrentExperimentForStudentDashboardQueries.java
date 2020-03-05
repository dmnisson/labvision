package io.github.dmnisson.labvision.experiment;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class CurrentExperimentForStudentDashboardQueries
		implements ExperimentDtoQueries<CurrentExperimentForStudentDashboard, Integer> {

	private final ExperimentRepository experimentRepository;
	
	public CurrentExperimentForStudentDashboardQueries(ExperimentRepository experimentRepository) {
		this.experimentRepository = experimentRepository;
	}

	@Override
	public List<CurrentExperimentForStudentDashboard> findExperimentsNoSubmissions(Integer userId, Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentDashboardNoSubmissions(userId, pageable);
	}

	@Override
	public List<CurrentExperimentForStudentDashboard> findExperimentsWithSubmissions(Integer userId, Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentDashboardWithSubmissions(userId, pageable);
	}

	@Override
	public long countExperimentsNoSubmissions(Integer userId) {
		return experimentRepository.countCurrentExperimentsByStudentIdNoSubmissions(userId);
	}

	@Override
	public long countExperiments(Integer userId) {
		return experimentRepository.countCurrentExperimentsByStudentId(userId);
	}

}
