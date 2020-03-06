package io.github.dmnisson.labvision.experiment;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class PastExperimentForStudentExperimentTableQueries
		implements ExperimentDtoQueries<PastExperimentForStudentExperimentTable, Integer> {

	private ExperimentRepository experimentRepository;
	
	public PastExperimentForStudentExperimentTableQueries(ExperimentRepository experimentRepository) {
		this.experimentRepository = experimentRepository;
	}

	@Override
	public Page<PastExperimentForStudentExperimentTable> findExperimentsNoSubmissions(Integer userId,
			Pageable pageable) {
		return new PageImpl<>(new ArrayList<>());
	}

	@Override
	public Page<PastExperimentForStudentExperimentTable> findExperimentsWithSubmissions(Integer userId,
			Pageable pageable) {
		return experimentRepository.findPastExperimentsForStudentExperimentTableWithSubmissions(userId, pageable);
	}

	@Override
	public long countExperimentsNoSubmissions(Integer userId) {
		return 0L;
	}

	@Override
	public long countExperiments(Integer userId) {
		return experimentRepository.countPastExperimentsByStudentId(userId);
	}

}
