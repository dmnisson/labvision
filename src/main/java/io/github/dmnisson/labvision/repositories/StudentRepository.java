package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
}
