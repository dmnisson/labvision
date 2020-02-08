package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

import io.github.dmnisson.labvision.entities.Instructor;

public interface InstructorRepository extends BaseLabVisionUserRepository<Instructor> {
	@EntityGraph( attributePaths = { "courseClasses", "experiments" } )
	@Override
	Optional<Instructor> findByUsername(String username);
}
