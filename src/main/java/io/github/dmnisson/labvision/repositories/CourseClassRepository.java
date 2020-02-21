package io.github.dmnisson.labvision.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.course.CourseClassForAdminTable;
import io.github.dmnisson.labvision.dto.course.CourseForAdminTable;
import io.github.dmnisson.labvision.entities.CourseClass;

public interface CourseClassRepository extends JpaRepository<CourseClass, Integer> {
	
	@Query(	"SELECT cc FROM CourseClass cc "
			+ "JOIN cc.students s "
			+ "JOIN cc.course c "
			+ "JOIN c.experiments e "
			+ "WHERE s.id=:studentid AND e.id=:experimentid")
	List<CourseClass> findWithStudentIdAndExperimentId(
			@Param("studentid") Integer studentId, @Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseClassForAdminTable("
			+ "	cc.id,"
			+ "	cc.name,"
			+ "	COUNT(DISTINCT s.id),"
			+ "	COUNT(DISTINCT i.id)"
			+ ") FROM Course c "
			+ "JOIN c.courseClasses cc "
			+ "LEFT JOIN cc.students s "
			+ "LEFT JOIN cc.instructors i "
			+ "WHERE c.id=:courseid "
			+ "GROUP BY cc.id, cc.name")
	Page<CourseClassForAdminTable> findForAdminByCourseId(@Param("courseid") Integer courseId, Pageable pageable);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.course.CourseClassForAdminTable("
			+ "	cc.id,"
			+ "	cc.name,"
			+ "	COUNT(DISTINCT s.id),"
			+ "	COUNT(DISTINCT i.id)"
			+ ") FROM CourseClass cc "
			+ "LEFT JOIN cc.students s "
			+ "LEFT JOIN cc.instructors i "
			+ "WHERE cc.id=:courseclassid "
			+ "GROUP BY cc.id, cc.name")
	Optional<CourseClassForAdminTable> findForAdminById(@Param("courseclassid") Integer courseClassId);

}
