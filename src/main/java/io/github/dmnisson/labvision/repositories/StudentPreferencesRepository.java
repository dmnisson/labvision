package io.github.dmnisson.labvision.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.dmnisson.labvision.entities.StudentPreferences;

public interface StudentPreferencesRepository extends JpaRepository<StudentPreferences, Integer> {
	StudentPreferences findByStudentId(Integer studentId);
}
