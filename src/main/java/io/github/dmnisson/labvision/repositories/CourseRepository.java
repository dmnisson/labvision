package io.github.dmnisson.labvision.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.course.CourseForAdminTable;
import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseTable;
import io.github.dmnisson.labvision.dto.course.CourseForStudentCourseView;
import io.github.dmnisson.labvision.dto.course.CourseInfo;
import io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.Student;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.course.RecentCourseForStudentDashboard(" +
		    "	c.id," +
		    "	c.name," +
		    "	MAX(mv.taken)," +
		    ExperimentRepository.EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu" +
			") " +
		    "FROM Course c " +
			"LEFT JOIN c.courseClasses cc " +
		    "LEFT JOIN cc.students s1 " +
			"LEFT JOIN c.experiments e " +
		    "LEFT JOIN e.activeStudents s2 " +
		    "LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
		    "LEFT JOIN mv.student s3 " +
		    "LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN rr.student s4 " +
			"WHERE (s1.id IS NULL OR s1.id=:studentid) " +
			"AND (s2.id IS NULL OR s2.id=:studentid) " +
			"AND (s3.id IS NULL OR s3.id=:studentid) " +
			"AND (s4.id IS NULL OR s4.id=:studentid) " +
			"AND (s1.id IS NOT NULL OR s2.id IS NOT NULL OR s3.id IS NOT NULL OR s4.id IS NOT NULL)" +
			"GROUP BY c " +
			"ORDER BY lu DESC NULLS LAST")
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
	public Page<CourseForStudentCourseTable> findForStudentCourseTable(@Param("studentid") Integer studentId, Pageable pageable);

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

	@Query(	"SELECT c FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "JOIN cc.instructors i "
			+ "WHERE c.id=:courseid AND i.id=:instructorid")
	public Optional<Course> findByIdForInstructor(@Param("courseid") Integer courseId, @Param("instructorid") Integer instructorId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseForAdminTable("
			+ "	c.id,"
			+ "	c.name,"
			+ "	COUNT(DISTINCT cc.id),"
			+ "	COUNT(DISTINCT e.id)"
			+ ") FROM Course c "
			+ "LEFT JOIN c.courseClasses cc "
			+ "LEFT JOIN c.experiments e "
			+ "WHERE c.id=:courseid "
			+ "GROUP BY c.id, c.name")
	public Optional<CourseForAdminTable> findForAdminTable(@Param("courseid") Integer courseId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseInfo("
			+ "	c.id,"
			+ "	c.name"
			+ ") FROM Course c "
			+ "WHERE c.id=:courseid")
	public Optional<CourseInfo> findCourseInfo(@Param("courseid") Integer courseId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseInfo("
			+ "	c.id,"
			+ "	c.name"
			+ ") FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "WHERE cc.id=:courseclassid")
	public Optional<CourseInfo> findCourseInfoByCourseClassId(@Param("courseclassid") Integer courseClassId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseInfo("
			+ "	c.id,"
			+ "	c.name"
			+ ") FROM Course c "
			+ "JOIN c.experiments e "
			+ "WHERE e.id=:experimentid")
	public Optional<CourseInfo> findCourseInfoByExperimentId(@Param("experimentid") Integer experimentId);
}
