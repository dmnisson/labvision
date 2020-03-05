package io.github.dmnisson.labvision.course;

import io.github.dmnisson.labvision.AbstractDtoQueriesFactory;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.repositories.CourseRepository;

public class CourseDtoQueryFactory extends AbstractDtoQueriesFactory<
	CourseRepository, Course, Integer,
	CourseDtoQuery<? extends Object, Integer>, Object, Integer
	> {
	
	CourseDtoQueryFactory() {
	}

	@Override
	protected Class<Integer> getUserIdClass() {
		return Integer.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <DTO> CourseDtoQuery<DTO, Integer> createDtoQueriesForDtoType(
			CourseRepository repository, Class<DTO> dtoClass) {
		
		if (dtoClass.equals(RecentCourseForStudentDashboard.class)) {
			assert getUserIdClass().equals(Integer.class);
			return (CourseDtoQuery<DTO, Integer>) new RecentCourseForStudentDashboardQuery(repository);
		} else {
			throw new IllegalArgumentException("Unrecognized DTO type: " + dtoClass);
		}
	}
	
}
