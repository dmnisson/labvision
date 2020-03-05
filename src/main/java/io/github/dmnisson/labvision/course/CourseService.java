package io.github.dmnisson.labvision.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dmnisson.labvision.repositories.CourseRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	public boolean checkStudentEnrolled(Integer studentId, Integer courseId) {
		return courseRepository.findStudentEnrolled(studentId, courseId).isPresent();
	}
	
	public long countRecentCoursesByStudentId(Integer studentId) {
		return courseRepository.countRecentCoursesByStudentId(studentId);
	}
	
	public <DTO> List<DTO> findCourseData(Integer userId, int limit, Class<DTO> dtoClass) {
		Pageable pageable = PageRequest.of(0, limit);
		
		CourseDtoQuery<DTO, Integer> dtoQuery =
				CourseDtoQueryFactory.createDtoQueryForDtoType(courseRepository, dtoClass);
		
		return dtoQuery.findCourseData(userId, pageable);
	}
	
}
