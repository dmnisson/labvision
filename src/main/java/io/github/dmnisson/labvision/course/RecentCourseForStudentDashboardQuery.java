package io.github.dmnisson.labvision.course;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.repositories.CourseRepository;

class RecentCourseForStudentDashboardQuery implements CourseDtoQuery<RecentCourseForStudentDashboard, Integer> {

	private CourseRepository courseRepository;
	
	public RecentCourseForStudentDashboardQuery(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@Override
	public List<RecentCourseForStudentDashboard> findCourseData(Integer userId, Pageable pageable) {
		return courseRepository.findRecentCoursesForStudentDashboard(userId, pageable);
	}

}
