package io.github.dmnisson.labvision.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dmnisson.labvision.entities.AdminOnly;

public interface AdminOnlyRepository extends JpaRepository<AdminOnly, Integer> {

}
