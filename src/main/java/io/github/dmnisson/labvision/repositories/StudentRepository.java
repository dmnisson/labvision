package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

import io.github.dmnisson.labvision.entities.Student;

public interface StudentRepository extends BaseLabVisionUserRepository<Student> {

	@EntityGraph( attributePaths = { "activeExperiments", "courseClasses" } )
	@Override
	public Optional<Student> findByUsername(String username);
	
}
