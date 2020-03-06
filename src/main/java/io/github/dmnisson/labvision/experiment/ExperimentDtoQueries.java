package io.github.dmnisson.labvision.experiment;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.DtoQueries;

public interface ExperimentDtoQueries<DTO, UserID> extends DtoQueries<DTO, UserID> {

	public Iterable<DTO> findExperimentsNoSubmissions(UserID userId, Pageable pageable);
	
	public Iterable<DTO> findExperimentsWithSubmissions(UserID userId, Pageable pageable);

	public long countExperimentsNoSubmissions(Integer userId);

	public long countExperiments(Integer userId);
	
}
