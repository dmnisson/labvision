package io.github.dmnisson.labvision.course;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CourseServiceConfig {

	@Bean
	public CourseDtoQueryFactory courseDtoQueryFactory() {
		return new CourseDtoQueryFactory();
	}
	
}
