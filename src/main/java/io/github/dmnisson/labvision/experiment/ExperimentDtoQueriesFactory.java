package io.github.dmnisson.labvision.experiment;

import io.github.dmnisson.labvision.AbstractDtoQueriesFactory;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class ExperimentDtoQueriesFactory extends AbstractDtoQueriesFactory<
	ExperimentRepository, Experiment, Integer,
	ExperimentDtoQueries<? extends Object, Integer>, Object, Integer
	> {
	
	ExperimentDtoQueriesFactory() {
		super();
	}
	
	@Override
	protected Class<Integer> getUserIdClass() {
		return Integer.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <DTO> ExperimentDtoQueries<DTO, Integer> createDtoQueriesForDtoType(
			ExperimentRepository repository, Class<DTO> dtoClass) {
		
		if (dtoClass.equals(CurrentExperimentForStudentDashboard.class)) {
			
			return (ExperimentDtoQueries<DTO, Integer>) 
					new CurrentExperimentForStudentDashboardQueries(repository);
			
		} else if (dtoClass.equals(RecentExperimentForStudentDashboard.class)) {
			
			return (ExperimentDtoQueries<DTO, Integer>) 
					new RecentExperimentForStudentDashboardQueries(repository);
		
		} else if (dtoClass.equals(CurrentExperimentForStudentExperimentTable.class)) {
			
			return (ExperimentDtoQueries<DTO, Integer>) 
					new CurrentExperimentForStudentExperimentTableQueries(repository);
			
		} else if (dtoClass.equals(PastExperimentForStudentExperimentTable.class)) {
			
			return (ExperimentDtoQueries<DTO, Integer>)
					new PastExperimentForStudentExperimentTableQueries(repository);
			
		} else {
			throw new IllegalArgumentException("Unrecognized DTO type: " + dtoClass);
		}
	}
}
