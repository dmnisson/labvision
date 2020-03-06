package io.github.dmnisson.labvision.experiment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class CurrentExperimentForStudentExperimentTableQueries
		implements ExperimentDtoQueries<CurrentExperimentForStudentExperimentTable, Integer> {

	private ExperimentRepository experimentRepository;
	
	public CurrentExperimentForStudentExperimentTableQueries(ExperimentRepository experimentRepository) {
		this.experimentRepository = experimentRepository;
	}

	@Override
	public Page<CurrentExperimentForStudentExperimentTable> findExperimentsNoSubmissions(Integer userId,
			Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentExperimentTableNoSubmissions(userId, pageable);
	}

	@Override
	public Page<CurrentExperimentForStudentExperimentTable> findExperimentsWithSubmissions(Integer userId,
			Pageable pageable) {
		return experimentRepository.findCurrentExperimentsForStudentExperimentTableWithSubmissions(userId, pageable);
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
