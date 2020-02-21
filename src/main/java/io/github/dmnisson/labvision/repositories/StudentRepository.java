package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo;
import io.github.dmnisson.labvision.entities.Student;

public interface StudentRepository extends BaseLabVisionUserRepository<Student> {

	@EntityGraph( attributePaths = { "activeExperiments", "courseClasses" } )
	@Override
	public Optional<Student> findByUsername(String username);

	@Query(	"SELECT COUNT(DISTINCT s.id) FROM Instructor i "
			+ "JOIN i.courseClasses cc "
			+ "JOIN cc.students s "
			+ "WHERE i.id=:instructorid "
			+ "GROUP BY i")
	public Long countStudentsForInstructor(@Param("instructorid") Integer instructorId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	s.id,"
			+ "	s.username,"
			+ "	s.name"
			+ ") FROM Student s "
			+ "JOIN s.courseClasses cc "
			+ "WHERE cc.id=:courseclassid")
	public Page<LabVisionUserInfo> findForAdminByCourseClassId(@Param("courseclassid") Integer courseClassId, Pageable pageable);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	s.id,"
			+ "	s.username,"
			+ "	s.name"
			+ ") FROM Student s "
			+ "JOIN s.activeExperiments e "
			+ "WHERE e.id=:experimentid")
	public Page<LabVisionUserInfo> findForAdminByActiveExperimentId(@Param("experimentid") Integer experimentId, Pageable pageable);

	public boolean existsByUsernameAndCourseClassesId(String username, Integer courseClassId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	s.id,"
			+ "	s.username,"
			+ "	s.name"
			+ ") FROM Student s "
			+ "WHERE s.id=:studentid")
	public Optional<LabVisionUserInfo> findInfoById(@Param("studentid") Integer studentId);
	
}
