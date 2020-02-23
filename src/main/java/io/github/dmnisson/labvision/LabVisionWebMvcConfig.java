package io.github.dmnisson.labvision;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.dmnisson.labvision.pagination.MapPageableHandlerMethodArgumentResolver;

@Configuration
@EnableWebMvc
public class LabVisionWebMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(mapPageableResolver());
	}
	
	@Bean
	public MapPageableHandlerMethodArgumentResolver mapPageableResolver() {
		return new MapPageableHandlerMethodArgumentResolver();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/")
			.setCachePeriod(31558150);
	}

}
