package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.github.dmnisson.labvision.entities.LabVisionUser;

@NoRepositoryBean
public interface BaseLabVisionUserRepository<U extends LabVisionUser> extends CrudRepository<U, Integer> {
	Optional<U> findByUsername(String username);
}
