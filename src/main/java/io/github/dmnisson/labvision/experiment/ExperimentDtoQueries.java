package io.github.dmnisson.labvision.experiment;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.DtoQueries;

public interface ExperimentDtoQueries<DTO, UserID> extends DtoQueries<DTO, UserID> {

	public List<DTO> findExperimentsNoSubmissions(UserID userId, Pageable pageable);
	
	public List<DTO> findExperimentsWithSubmissions(UserID userId, Pageable pageable);

	public long countExperimentsNoSubmissions(Integer userId);

	public long countExperiments(Integer userId);
	
}
