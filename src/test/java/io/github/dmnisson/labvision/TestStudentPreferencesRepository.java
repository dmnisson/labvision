package io.github.dmnisson.labvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.entities.StudentPreferences;
import io.github.dmnisson.labvision.repositories.StudentPreferencesRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

public class TestStudentPreferencesRepository extends LabvisionApplicationTests {

	@Autowired
	private StudentPreferencesRepository studentPreferencesRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	private Student seedStudentWithPrefs() {
		Student testStudent = new Student("Test Student One", "testStudent1");
		StudentPreferences prefs = new StudentPreferences();
		prefs.setMaxCurrentExperiments(7);
		prefs.setMaxRecentExperiments(8);
		prefs.setMaxRecentCourses(9);
		testStudent.setStudentPreferences(prefs);
		studentRepository.save(testStudent);
		
		return testStudent;
	}
	
	private Student seedStudentWithoutPrefs() {
		Student testStudent = new Student("Test Student One", "testStudent1");
		studentRepository.save(testStudent);
		
		return testStudent;
	}
	
	@Transactional
	@Test
	public void findMaxCurrentExperimentsByStudentId_ShouldGetMaxCurrentExperiments() {
		Student testStudent = seedStudentWithPrefs();
		
		Optional<Integer> maxCurrentExperiments 
			= studentPreferencesRepository.findMaxCurrentExperimentsByStudentId(testStudent.getId());
		
		assertTrue(maxCurrentExperiments.isPresent());
		assertEquals(7, maxCurrentExperiments.get());
	}
	
	@Transactional
	@Test
	public void findMaxCurrentExperimentsByStudentId_ShouldBeEmptyForNoPreferences() {
		Student testStudent = seedStudentWithoutPrefs();
		
		Optional<Integer> maxCurrentExperiments 
			= studentPreferencesRepository.findMaxCurrentExperimentsByStudentId(testStudent.getId());
	
		assertFalse(maxCurrentExperiments.isPresent());
	}
	
	@Transactional
	@Test
	public void findMaxRecentExperimentsByStudentId_ShouldGetMaxRecentExperiments() {
		Student testStudent = seedStudentWithPrefs();
		
		Optional<Integer> maxRecentExperiments 
			= studentPreferencesRepository.findMaxRecentExperimentsByStudentId(testStudent.getId());
		
		assertTrue(maxRecentExperiments.isPresent());
		assertEquals(8, maxRecentExperiments.get());
	}
	
	@Transactional
	@Test
	public void findMaxRecentExperimentsByStudentId_ShouldBeEmptyForNoPreferences() {
		Student testStudent = seedStudentWithoutPrefs();
		
		Optional<Integer> maxCurrentExperiments 
			= studentPreferencesRepository.findMaxRecentExperimentsByStudentId(testStudent.getId());
	
		assertFalse(maxCurrentExperiments.isPresent());
	}
	
	@Transactional
	@Test
	public void findMaxRecentCoursesByStudentId_ShouldGetMaxRecentCourses() {
		Student testStudent = seedStudentWithPrefs();
		
		Optional<Integer> maxRecentCourses 
			= studentPreferencesRepository.findMaxRecentCoursesByStudentId(testStudent.getId());
		
		assertTrue(maxRecentCourses.isPresent());
		assertEquals(9, maxRecentCourses.get());
	}
	
	@Transactional
	@Test
	public void findMaxRecentCoursesByStudentId_ShouldBeEmptyForNoPreferences() {
		Student testStudent = seedStudentWithoutPrefs();
		
		Optional<Integer> maxRecentCourses 
			= studentPreferencesRepository.findMaxRecentCoursesByStudentId(testStudent.getId());
	
		assertFalse(maxRecentCourses.isPresent());
	}
	
}
