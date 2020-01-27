package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.entities.CourseClass;

public interface CourseClassRepository extends JpaRepository<CourseClass, Integer> {
	
	@Query(	"SELECT cc FROM CourseClass cc "
			+ "JOIN cc.students s "
			+ "JOIN cc.course c "
			+ "JOIN c.experiments e "
			+ "WHERE s.id=:studentid AND e.id=:experimentid")
	List<CourseClass> findWithStudentIdAndExperimentId(
			@Param("studentid") Integer studentId, @Param("experimentid") Integer experimentId);

}
