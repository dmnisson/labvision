package io.github.dmnisson.labvision.experiment;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface ExperimentDtoQueries<DTO, UserID> {

	public List<DTO> findExperimentsNoReports(UserID userId, Pageable pageable);
	
	public List<DTO> findExperimentsWithReports(UserID userId, Pageable pageable);
	
}
