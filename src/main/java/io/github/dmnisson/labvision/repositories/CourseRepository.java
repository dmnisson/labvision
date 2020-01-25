package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard(" +
		    "	c.id," +
		    "	c.name," +
		    "	MAX(mv.taken)," +
		    ExperimentRepository.EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu" +
			") " +
		    "FROM Course c " +
			"JOIN c.experiments e " +
		    "LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
		    "LEFT JOIN mv.student s " +
		    "LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN rr.student s2 " +
			"WHERE (s.id IS NULL OR s.id=:studentid) " +
			"AND (s2.id IS NULL OR s2.id=:studentid) " +
			"AND (s.id IS NOT NULL OR s2.id IS NOT NULL)" +
			"GROUP BY c " +
			"ORDER BY lu DESC")
	public List<RecentCourseForStudentDashboard> findRecentCoursesForStudentDashboard(@Param("studentid") Integer studentid);
}
