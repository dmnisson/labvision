package io.github.dmnisson.labvision;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.github.dmnisson.labvision.entities.StudentPreferences;
import io.github.dmnisson.labvision.repositories.StudentPreferencesRepository;
import io.github.dmnisson.labvision.student.StudentPreferencesConfig;
import io.github.dmnisson.labvision.student.StudentPreferencesService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

public class TestStudentPreferencesService extends LabvisionApplicationTests {

	@MockBean
	private StudentPreferencesConfig studentPreferencesConfig;
	
	@MockBean
	private StudentPreferencesRepository studentPreferencesRepository;
	
	@Autowired
	private StudentPreferencesService studentPreferencesService;
	
	@Test
	public void getMaxCurrentExperiments_ShouldReturnValueFromDao() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxCurrentExperiments())
			.thenReturn(7);
		
		when(studentPreferencesRepository.findMaxCurrentExperimentsByStudentId(eq(studentId)))
			.thenReturn(Optional.of(8));
		
		int maxCurrentExperiments = studentPreferencesService.getMaxCurrentExperiments(studentId);
		
		assertEquals(8, maxCurrentExperiments);
	}
	
	@Test
	public void getMaxCurrentExperiments_ShouldReturnDefaultValue() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxCurrentExperiments())
			.thenReturn(5);
		
		when(studentPreferencesRepository.findMaxCurrentExperimentsByStudentId(eq(studentId)))
			.thenReturn(Optional.empty());
		
		int maxCurrentExperiments = studentPreferencesService.getMaxCurrentExperiments(studentId);
		
		assertEquals(5, maxCurrentExperiments);
	}
	
	@Test
	public void getMaxRecentExperiments_ShouldReturnValueFromDao() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxRecentExperiments())
			.thenReturn(8);
		
		when(studentPreferencesRepository.findMaxRecentExperimentsByStudentId(eq(studentId)))
			.thenReturn(Optional.of(10));
		
		int maxRecentExperiments = studentPreferencesService.getMaxRecentExperiments(studentId);
		
		assertEquals(10, maxRecentExperiments);
	}
	
	@Test
	public void getMaxRecentExperiments_ShouldReturnDefaultValue() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxRecentExperiments())
			.thenReturn(9);
		
		when(studentPreferencesRepository.findMaxRecentExperimentsByStudentId(eq(studentId)))
			.thenReturn(Optional.empty());
		
		int maxRecentExperiments = studentPreferencesService.getMaxRecentExperiments(studentId);
		
		assertEquals(9, maxRecentExperiments);
	}
	
	@Test
	public void getMaxRecentCourses_ShouldReturnValueFromDao() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxRecentCourses())
			.thenReturn(9);
		
		when(studentPreferencesRepository.findMaxRecentCoursesByStudentId(eq(studentId)))
			.thenReturn(Optional.of(14));
		
		int maxRecentCourses = studentPreferencesService.getMaxRecentCourses(studentId);
		
		assertEquals(14, maxRecentCourses);
	}
	
	@Test
	public void getMaxRecentCourses_ShouldReturnDefaultValue() {
		final Integer studentId = 6;
		
		when(studentPreferencesConfig.getDefaultMaxRecentCourses())
			.thenReturn(17);
		
		when(studentPreferencesRepository.findMaxRecentCoursesByStudentId(eq(studentId)))
			.thenReturn(Optional.empty());
		
		int maxRecentCourses = studentPreferencesService.getMaxRecentCourses(studentId);
		
		assertEquals(17, maxRecentCourses);
	}
	
	@Test
	public void getStudentPreferences_ShouldReturnStudentPreferences() {
		final Integer studentId = 6;
		
		StudentPreferences studentPreferences = new StudentPreferences();
		
		when(studentPreferencesRepository.findByStudentId(studentId))
			.thenReturn(Optional.of(studentPreferences));
		
		StudentPreferences actualStudentPreferences 
			= studentPreferencesService.getStudentPreferences(studentId);
		
		assertEquals(studentPreferences, actualStudentPreferences);
	}
	
	@Test
	public void getStudentPreferences_ShouldReturnNull() {
		final Integer studentId = 6;
		
		when(studentPreferencesRepository.findByStudentId(studentId))
			.thenReturn(Optional.empty());
		
		StudentPreferences actualStudentPreferences 
			= studentPreferencesService.getStudentPreferences(studentId);
		
		assertNull(actualStudentPreferences);
	}
	
	@Test
	public void getDefaultStudentPreferences_ShouldReturnDefaults() {
		
		final int maxCurrentExperiments = 7;
		final int maxRecentExperiments = 8;
		final int maxRecentCourses = 12;
		
		when(studentPreferencesConfig.getDefaultMaxCurrentExperiments())
			.thenReturn(maxCurrentExperiments);
		when(studentPreferencesConfig.getDefaultMaxRecentExperiments())
			.thenReturn(maxRecentExperiments);
		when(studentPreferencesConfig.getDefaultMaxRecentCourses())
			.thenReturn(maxRecentCourses);
		
		StudentPreferences actualDefaultStudentPreferences 
			= studentPreferencesService.getDefaultStudentPreferences();
		
		assertNotNull(actualDefaultStudentPreferences);
		assertEquals(maxCurrentExperiments, actualDefaultStudentPreferences.getMaxCurrentExperiments());
		assertEquals(maxRecentExperiments, actualDefaultStudentPreferences.getMaxRecentExperiments());
		assertEquals(maxRecentCourses, actualDefaultStudentPreferences.getMaxRecentCourses());
	}
}
