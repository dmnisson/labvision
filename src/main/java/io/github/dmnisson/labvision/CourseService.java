package io.github.dmnisson.labvision;

import org.springframework.beans.factory.annotation.Autowired;
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
	
}
