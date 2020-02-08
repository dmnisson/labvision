package io.github.dmnisson.labvision;

import java.time.LocalDateTime;

import javax.measure.quantity.Length;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.CourseClass;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.InstructorRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Component
@Profile("dev")
public class DevInitializingBean implements InitializingBean {

	@Autowired
	private LabVisionUserDetailsManager userDetailsManager;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// seed users
		String[][] seedUsers = {
				{ "student1", "Password123", "STUDENT", "Student One" },
				{ "instructor1", "Password1234", "FACULTY", "Instructor One" }
		};
		
		for (String[] seed : seedUsers) {
			String username = seed[0], password = seed[1], authority = seed[2];
			
			// first check if user has been created already
			if (userDetailsManager.userExists(username)) {
				continue;
			}
			
			// then seed the user in the database
			UserDetails user = User.withDefaultPasswordEncoder()
					.username(username)
					.password(password)
					.roles(authority)
					.build();
			
			switch (authority) {
			case "STUDENT":
				userDetailsManager.createStudent(user, seed[3]);
				break;
			case "FACULTY":
				userDetailsManager.createInstructor(user, seed[3]);
			}
		}
		
		if (databaseIsEmpty()) {
			// seed course, classes and experiment
			Course seedCourse = new Course("Course 101");
			Experiment seedExperiment = seedCourse.addExperiment(
					"How long is the rod?",
					"Measure the rod length as best as you can.",
					LocalDateTime.of(2100, 1, 1, 0, 0, 0));
			CourseClass seedCourseClass = seedCourse.addCourseClass("Course 101 Online");
			Student student1 = studentRepository.findByUsername("student1").get();
			Instructor instructor1 = instructorRepository.findByUsername("instructor1").get();
			
			seedCourse = courseRepository.save(seedCourse);
			seedCourseClass = seedCourse.getCourseClasses().stream()
					.filter(cc -> cc.getName().equals("Course 101 Online"))
					.findAny().get();
			
			student1.addCourseClass(seedCourseClass);
			student1.addActiveExperiment(seedExperiment);
			
			instructor1.addCourseClass(seedCourseClass);
			instructor1.addExperiment(seedExperiment);
			
			seedExperiment.addMeasurement("Length", Length.class);
			
			courseRepository.save(seedCourse);
			experimentRepository.save(seedExperiment);
			studentRepository.save(student1);
			instructorRepository.save(instructor1);
		}
	}

	private boolean databaseIsEmpty() {
		return courseRepository.count() == 0 &&
				experimentRepository.count() == 0;
	}
	
}
