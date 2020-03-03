package io.github.dmnisson.labvision.experiment;

import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;

public class ExperimentDashboardQueriesFactory {

	@SuppressWarnings("unchecked")
	public static <DTO, UserID> ExperimentDashboardQueries<DTO, UserID> createDashboardQueriesForDtoType(
			ExperimentRepository experimentRepository, Class<DTO> dtoClass, Class<UserID> userIdClass) {
		
		if (dtoClass.equals(CurrentExperimentForStudentDashboard.class)) {
			
			checkClassEqual(userIdClass, Integer.class);
			
			return (ExperimentDashboardQueries<DTO, UserID>) 
					new CurrentExperimentDashboardQueries(experimentRepository);
			
		} else if (dtoClass.equals(RecentExperimentForStudentDashboard.class)) {
			
			checkClassEqual(userIdClass, Integer.class);
			
			return (ExperimentDashboardQueries<DTO, UserID>) 
					new RecentExperimentDashboardQueries(experimentRepository);
			
		} else {
			throw new IllegalArgumentException("Unrecognized dashboard DTO type: " + dtoClass);
		}
		
	}
	
	private static <T> void checkClassEqual(Class<T> expectedClass, Class<?> actualClass) {
		if (!expectedClass.equals(actualClass)) {
			throw new IllegalArgumentException(actualClass + " class given where " + expectedClass + " expected");
		}
	}
	
}