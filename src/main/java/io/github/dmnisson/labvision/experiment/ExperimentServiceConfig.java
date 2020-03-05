package io.github.dmnisson.labvision.experiment;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ExperimentServiceConfig {

	@Bean
	public ExperimentDtoQueriesFactory experimentDtoQueriesFactory() {
		return new ExperimentDtoQueriesFactory();
	}
	
}
