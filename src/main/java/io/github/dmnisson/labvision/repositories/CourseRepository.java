package io.github.dmnisson.labvision.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseTable;
import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseView;
import io.github.dmnisson.labvision.dto.course.CourseInfo;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Student;

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

	@Query( "SELECT new io.github.dmnisson.labvision.dto.course.CourseInfo("
			+ "	e.course.id,"
			+ "	e.course.name"
			+ ") FROM Experiment e "
			+ "WHERE e.id=:experimentid")
	public Optional<CourseInfo> findCourseInfoForExperiment(@Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseForStudentCourseTable("
			+ "	c.id,"
			+ "	c.name,"
			+ "	cc.name,"
			+ "	COUNT(c.id),"
			+ "	MIN(e.reportDueDate) AS nrd"
			+ ") FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "JOIN cc.students s "
			+ "LEFT JOIN c.experiments e "
			+ "WHERE s.id=:studentid "
			+ "GROUP BY c.id, c.name, cc.name "
			+ "ORDER BY nrd ASC NULLS LAST")
	public List<CourseForStudentCourseTable> findForStudentCourseTable(@Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseForStudentCourseView("
			+ "	c.id,"
			+ "	c.name,"
			+ "	cc.name"
			+ ") FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "JOIN cc.students s "
			+ "WHERE s.id=:studentid AND c.id=:courseid")
	public Optional<CourseForStudentCourseView> findForStudentCourseView(@Param("studentid") Integer studentId, @Param("courseid") Integer courseId);

	@Query(	"SELECT s FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "JOIN cc.students s "
			+ "WHERE s.id=:studentid AND c.id=:courseid")
	public Optional<Student> findStudentEnrolled(@Param("studentid") Integer studentId, @Param("courseid") Integer courseId);
	
	
}
