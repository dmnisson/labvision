package io.github.dmnisson.labvision.auth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.repositories.InstructorRepository;
import io.github.dmnisson.labvision.repositories.LabVisionUserRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

public class LabVisionUserDetailsManager extends JdbcUserDetailsManager {

	public LabVisionUserDetailsManager() {
		super();
	}
	
	public LabVisionUserDetailsManager(DataSource dataSource) {
		super(dataSource);
	}
	
	@Autowired
	private LabVisionUserRepository labVisionUserRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private InstructorRepository instructorRepository;

	public void createStudent(UserDetails userDetails, String name) {
		createUser(userDetails);
		
		Student student = new Student(name, userDetails.getUsername());
		studentRepository.save(student);
	}
	
	public void createInstructor(UserDetails userDetails, String name) {
		createUser(userDetails);
		
		Instructor instructor = new Instructor();
		instructor.setUsername(userDetails.getUsername());
		instructor.setName(name);
		
		instructorRepository.save(instructor);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDetails = super.loadUserByUsername(username);
		LabVisionUser labVisionUser = labVisionUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found by that name"));
		
		LabVisionUserDetails details = new LabVisionUserDetails(
				username,
				userDetails.getPassword(),
				userDetails.isEnabled(),
				userDetails.isAccountNonExpired(),
				userDetails.isCredentialsNonExpired(),
				userDetails.isAccountNonLocked(),
				userDetails.getAuthorities(), labVisionUser);
		return details;
	}
}
