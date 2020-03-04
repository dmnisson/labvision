package io.github.dmnisson.labvision.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.dmnisson.labvision.repositories.StudentPreferencesRepository;

@Service
public class StudentPreferencesService {

	@Autowired
	private StudentPreferencesConfig studentPreferencesConfig;

	@Autowired
	private StudentPreferencesRepository studentPreferencesRepository;
	
	public int getMaxCurrentExperiments(Integer studentId) {
		return studentPreferencesRepository.findMaxCurrentExperimentsByStudentId(studentId)
				.orElse(studentPreferencesConfig.getDefaultMaxCurrentExperiments());
	}
	
	public int getMaxRecentExperiments(Integer studentId) {
		return studentPreferencesRepository.findMaxRecentExperimentsByStudentId(studentId)
				.orElse(studentPreferencesConfig.getDefaultMaxRecentExperiments());
	}
	
	public int getMaxRecentCourses(Integer studentId) {
		return studentPreferencesRepository.findMaxRecentCoursesByStudentId(studentId)
				.orElse(studentPreferencesConfig.getDefaultMaxRecentCourses());
	}
	
}
