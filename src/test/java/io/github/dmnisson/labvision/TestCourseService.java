package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import io.github.dmnisson.labvision.course.CourseService;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.repositories.CourseRepository;

public class TestCourseService extends LabvisionApplicationTests {

	@MockBean
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseService courseService;
	
	@Test
	public void findCourseData_ShouldLimitResultsForStudentDashboard() {
		final int userId = 6;
		final int limit = 8;
		
		List<RecentCourseForStudentDashboard> recentCourses = IntStream.range(101, 109)
				.mapToObj(n -> new RecentCourseForStudentDashboard(
						n - 100, 
						"Test Course " + n, 
						LocalDateTime.now().minusDays(3).minusHours(n - 100),
						LocalDateTime.now().minusDays(2).minusHours(n - 100)))
				.collect(Collectors.toList());
		
		final PageRequest pageable = PageRequest.of(0, limit);
		when(courseRepository.findRecentCoursesForStudentDashboard(
				eq(userId), 
				eq(pageable)
				))
			.thenReturn(recentCourses);
		
		List<RecentCourseForStudentDashboard> actualRecentCourses
			= courseService.findCourseData(userId, limit, RecentCourseForStudentDashboard.class);
	
		verify(courseRepository, times(1)).findRecentCoursesForStudentDashboard(userId, pageable);
		
		assertEquals(recentCourses.size(), actualRecentCourses.size());
	}
	
}
