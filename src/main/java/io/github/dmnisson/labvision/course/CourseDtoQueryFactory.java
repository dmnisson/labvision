package io.github.dmnisson.labvision.course;

import io.github.dmnisson.labvision.AbstractDtoQueriesFactory;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.repositories.CourseRepository;

public class CourseDtoQueryFactory extends AbstractDtoQueriesFactory<
	CourseRepository, Course, Integer,
	CourseDtoQuery<? extends Object, Integer>, Object, Integer
	> {

	private static CourseDtoQueryFactory instance = null;
	
	public static CourseDtoQueryFactory getInstance() {
		if (instance == null) {
			instance = new CourseDtoQueryFactory();
		}
		return instance;
	}
	
	private CourseDtoQueryFactory() {
	}

	public static <DTO> CourseDtoQuery<DTO, Integer> createDtoQueryForDtoType(
			CourseRepository courseRepository, Class<DTO> dtoClass) {
		return getInstance().createDtoQueriesForDtoTypeInternal(courseRepository, dtoClass);
	}

	@Override
	protected Class<Integer> getUserIdClass() {
		return Integer.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <DTO> CourseDtoQuery<DTO, Integer> createDtoQueriesForDtoTypeInternal(
			CourseRepository repository, Class<DTO> dtoClass) {
		
		if (dtoClass.equals(RecentCourseForStudentDashboard.class)) {
			assert getUserIdClass().equals(Integer.class);
			return (CourseDtoQuery<DTO, Integer>) new RecentCourseForStudentDashboardQuery(repository);
		} else {
			throw new IllegalArgumentException("Unrecognized DTO type: " + dtoClass);
		}
	}
	
}
