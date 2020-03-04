package io.github.dmnisson.labvision.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.entities.StudentPreferences;

public interface StudentPreferencesRepository extends JpaRepository<StudentPreferences, Integer> {
	
	Optional<StudentPreferences> findByStudentId(Integer studentId);
	
	@Query("SELECT sp.maxCurrentExperiments FROM StudentPreferences sp WHERE sp.student.id=:studentid")
	Optional<Integer> findMaxCurrentExperimentsByStudentId(@Param("studentid") Integer id);

	@Query("SELECT sp.maxRecentExperiments FROM StudentPreferences sp WHERE sp.student.id=:studentid")
	Optional<Integer> findMaxRecentExperimentsByStudentId(@Param("studentid") Integer id);

	@Query("SELECT sp.maxRecentCourses FROM StudentPreferences sp WHERE sp.student.id=:studentid")
	Optional<Integer> findMaxRecentCoursesByStudentId(@Param("studentid") Integer id);
}
