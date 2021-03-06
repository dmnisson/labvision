package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo;
import io.github.dmnisson.labvision.entities.Instructor;

public interface InstructorRepository extends BaseLabVisionUserRepository<Instructor> {
	@EntityGraph( attributePaths = { "courseClasses", "experiments" } )
	@Override
	Optional<Instructor> findByUsername(String username);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	i.id,"
			+ "	i.username,"
			+ "	i.name"
			+ ") FROM Instructor i "
			+ "JOIN i.courseClasses cc "
			+ "WHERE cc.id=:courseclassid")
	Page<LabVisionUserInfo> findForAdminByCourseClassId(@Param("courseclassid") Integer courseClassId, Pageable pageable);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	i.id,"
			+ "	i.username,"
			+ "	i.name"
			+ ") FROM Instructor i "
			+ "JOIN i.experiments e "
			+ "WHERE e.id=:experimentid")
	Page<LabVisionUserInfo> findForAdminByExperimentId(@Param("experimentid") Integer experimentId, Pageable pageable);

	boolean existsByUsernameAndCourseClassesId(String username, Integer courseClassId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo("
			+ "	i.id,"
			+ "	i.username,"
			+ "	i.name"
			+ ") FROM Instructor i "
			+ "WHERE i.id=:instructorid")
	Optional<LabVisionUserInfo> findInfoById(@Param("instructorid") Integer instructorId);

	boolean existsByUsernameAndExperimentsId(String username, Integer experimentId);
}
