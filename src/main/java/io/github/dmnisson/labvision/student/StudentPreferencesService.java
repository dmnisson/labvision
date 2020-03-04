package io.github.dmnisson.labvision.student;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.entities.StudentPreferences;
import io.github.dmnisson.labvision.repositories.StudentPreferencesRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

@Service
public class StudentPreferencesService {

	@Autowired
	private StudentPreferencesConfig studentPreferencesConfig;

	@Autowired
	private StudentPreferencesRepository studentPreferencesRepository;

	@Autowired
	private StudentRepository studentRepository;
	
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
	
	public StudentPreferences getStudentPreferences(Integer studentId) {
		return studentPreferencesRepository.findByStudentId(studentId).orElse(null);
	}
	
	public StudentPreferences getDefaultStudentPreferences() {
		
		StudentPreferences preferences = new StudentPreferences();
		preferences.setMaxCurrentExperiments(
				studentPreferencesConfig.getDefaultMaxCurrentExperiments()
				);
		preferences.setMaxRecentExperiments(
				studentPreferencesConfig.getDefaultMaxRecentExperiments()
				);
		preferences.setMaxRecentCourses(
				studentPreferencesConfig.getDefaultMaxRecentCourses()
				);
		
		return preferences;
	}
	
	public void updateStudentPreferences(Integer studentId, StudentPreferences newStudentPreferences) {
		
		Student student = studentRepository.findById(studentId).get();
		
		Optional<StudentPreferences> oldStudentPreferences = studentPreferencesRepository.findByStudentId(studentId);
		
		if (!oldStudentPreferences.isPresent()) {
			student.setStudentPreferences(newStudentPreferences);
		} else {
			StudentPreferences studentPreferences = oldStudentPreferences.get();
			studentPreferences.setMaxCurrentExperiments(
					newStudentPreferences.getMaxCurrentExperiments()
					);
			studentPreferences.setMaxRecentExperiments(
					newStudentPreferences.getMaxRecentExperiments()
					);
			studentPreferences.setMaxRecentCourses(
					newStudentPreferences.getMaxRecentCourses()
					);
		}
		
		studentRepository.save(student);
	}
	
}
