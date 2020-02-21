package io.github.dmnisson.labvision.auth;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.TransactionSystemException;

import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.entities.AdminInfo;
import io.github.dmnisson.labvision.entities.AdminOnly;
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.repositories.AdminOnlyRepository;
import io.github.dmnisson.labvision.repositories.InstructorRepository;
import io.github.dmnisson.labvision.repositories.LabVisionUserRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;

public class LabVisionUserDetailsManager extends JdbcUserDetailsManager {

	public static final String DEF_PAGE_ALL_USERS_SQL = "select username from users order by username "
			+ "offset ? fetch ? rows only";
	public static final String DEF_COUNT_ALL_USERS_SQL = "select count(username) from users";
	public static final String DEF_COUNT_ADMINS_SQL = "select COUNT(users.username) from users "
			+ "inner join authorities on users.username=authorities.username "
			+ "where authorities.authority='ROLE_ADMIN'";
	
	private String pageAllUsersSql = DEF_PAGE_ALL_USERS_SQL;
	private String countAllUsersSql = DEF_COUNT_ALL_USERS_SQL;
	private String countAdminsSql = DEF_COUNT_ADMINS_SQL;
	
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
	
	@Autowired
	private AdminOnlyRepository adminOnlyRepository;
	
	@Autowired
	private SecureRandom secureRandom;

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
		
		try {
			instructorRepository.save(instructor);
		} catch (TransactionSystemException e) {
			if (e.contains(ConstraintViolationException.class)) {
				throw (ConstraintViolationException) e.getRootCause();
			} else {
				throw e;
			}
		}
	}
	
	public void createAdminOnlyUser(UserDetails userDetails, String firstName, String lastName, String email, String phone) {
		createUser(userDetails);
		
		AdminOnly adminOnly = new AdminOnly();
		adminOnly.setUsername(userDetails.getUsername());
		
		AdminInfo adminInfo = new AdminInfo();
		adminInfo.setFirstName(firstName);
		adminInfo.setLastName(lastName);
		adminInfo.setEmail(email);
		adminInfo.setPhone(phone);
		
		adminOnly.setAdminInfo(adminInfo);
		
		try {
			adminOnlyRepository.save(adminOnly);
		} catch (TransactionSystemException e) {
			if (e.contains(ConstraintViolationException.class)) {
				throw (ConstraintViolationException) e.getRootCause();
			} else {
				throw e;
			}
		}
	}
	
	private LabVisionUserDetails makeLabVisionUserDetails(String username, UserDetails userDetails,
			LabVisionUser labVisionUser) {
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
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDetails = super.loadUserByUsername(username);
		LabVisionUser labVisionUser = labVisionUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found by that name"));
		
		LabVisionUserDetails details = makeLabVisionUserDetails(username, userDetails, labVisionUser);
		return details;
	}

	public Long countStudents() {
		return studentRepository.count();
	}

	public Long countInstructors() {
		return instructorRepository.count();
	}
	
	public Long countAdmins() {
		return getJdbcTemplate().query(countAdminsSql, rs -> {
			if (rs.next()) {
				return Long.valueOf(rs.getLong(1));
			} else {
				return Long.valueOf(0);
			}
		});
	}

	public void setPageAllUsersSql(String pageAllUsersSql) {
		this.pageAllUsersSql = pageAllUsersSql;
	}

	public void setCountAdminsSql(String countAdminsSql) {
		this.countAdminsSql = countAdminsSql;
	}

	public Page<LabVisionUserDetails> findAllUsers(Pageable pageable) {
		List<String> usernames = getJdbcTemplate()
				.queryForList(
						pageAllUsersSql, 
						new Object[] {pageable.getOffset(), pageable.getPageSize()}, 
						String.class
						);
		List<LabVisionUserDetails> content = usernames.stream()
				.map(username -> (LabVisionUserDetails) loadUserByUsername(username))
				.collect(Collectors.toList());
		
		return new PageImpl<LabVisionUserDetails>(
				content,
				pageable,
				usernames.size()
				);
	}

	public Long countUsers() {
		return getJdbcTemplate().query(countAllUsersSql, rs -> {
			if (rs.next()) {
				return Long.valueOf(rs.getLong(1));
			} else {
				return Long.valueOf(0);
			}
		});
	}

	public LabVisionUserDetails loadUserById(Integer id) {
		LabVisionUser labVisionUser = labVisionUserRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(LabVisionUser.class, id));
		
		UserDetails userDetails = super.loadUserByUsername(labVisionUser.getUsername());
		
		return makeLabVisionUserDetails(labVisionUser.getUsername(), userDetails, labVisionUser);
	}

	public String makePasswordResetToken(Integer id) {
		LabVisionUser labVisionUser = labVisionUserRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(LabVisionUser.class, id));
		
		byte[] tokenBytes = new byte[48];
		secureRandom.nextBytes(tokenBytes);
		
		StringBuilder tokenBuilder = new StringBuilder();
		for (int i = 0; i < 48; i++) {
			tokenBuilder.append(byteToHex(tokenBytes[i]));
		}
		
		String token = tokenBuilder.toString();
		
		labVisionUser.setPasswordResetToken(token);
		
		labVisionUserRepository.save(labVisionUser);
		
		return token;
	}
	
	private static String byteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit(num & 0xF, 16);
		return new String(hexDigits);
	}

	public UserDetails loadUserByPasswordResetToken(String token) {
		LabVisionUser labVisionUser = labVisionUserRepository.findByPasswordResetToken(token)
				.orElseThrow(() -> new ResourceNotFoundException(LabVisionUser.class, token));
		
		UserDetails userDetails = super.loadUserByUsername(labVisionUser.getUsername());
		
		return makeLabVisionUserDetails(labVisionUser.getUsername(), userDetails, labVisionUser);
	}

	public void clearPasswordResetToken(String username) {
		LabVisionUser labVisionUser = labVisionUserRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException(LabVisionUser.class, username));
		
		labVisionUser.setPasswordResetToken(null);
		
		labVisionUserRepository.save(labVisionUser);
	}
	
	// Update the current authentication when the current user profile is updated
	private void updateAuthentication(LabVisionUserDetails labVisionUserDetails, LabVisionUser labVisionUser) {
		labVisionUserDetails = makeLabVisionUserDetails(labVisionUser.getUsername(), labVisionUserDetails, labVisionUser);
		
		Authentication authentication = new PreAuthenticatedAuthenticationToken(
				labVisionUserDetails,
				labVisionUserDetails.getPassword(),
				labVisionUserDetails.getAuthorities()
				);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void updateAdminInfo(Integer id, String firstName, String lastName, String adminEmail, String adminPhone, boolean adminInitiated) {
		LabVisionUserDetails labVisionUserDetails = loadUserById(id);
		LabVisionUser labVisionUser = labVisionUserDetails.getLabVisionUser();
		
		AdminInfo adminInfo = labVisionUser.getAdminInfo();
		if (Objects.isNull(adminInfo)) {
			adminInfo = new AdminInfo();
		}
		adminInfo.setFirstName(firstName);
		adminInfo.setLastName(lastName);
		adminInfo.setEmail(adminEmail);
		adminInfo.setPhone(adminPhone);
		
		labVisionUser.setAdminInfo(adminInfo);
		
		try {
			labVisionUser = labVisionUserRepository.save(labVisionUser);
			
			if (!adminInitiated) updateAuthentication(labVisionUserDetails, labVisionUser);
		} catch (TransactionSystemException e) {
			if (e.contains(ConstraintViolationException.class)) {
				throw (ConstraintViolationException) e.getRootCause();
			} else {
				throw e;
			}
		}
	}
	
	public void updateStudent(Integer id, String name, boolean adminInitiated) {
		LabVisionUserDetails labVisionUserDetails = loadUserById(id);
		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Student.class, id));
		student.setName(name);
		
		student = studentRepository.save(student);
		
		if (!adminInitiated) updateAuthentication(labVisionUserDetails, student);
	}

	public void updateInstructor(Integer id, String name, String email, boolean adminInitiated) {
		LabVisionUserDetails labVisionUserDetails = loadUserById(id);
		Instructor instructor = instructorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Instructor.class, id));
		
		instructor.setName(name);
		instructor.setEmail(email);
		
		try {
			instructor = instructorRepository.save(instructor);
			
			if (!adminInitiated) updateAuthentication(labVisionUserDetails, instructor);
		} catch (TransactionSystemException e) {
			if (e.contains(ConstraintViolationException.class)) {
				throw (ConstraintViolationException) e.getRootCause();
			} else {
				throw e;
			}
		}
	}

	public void changePasswordWithToken(String token, String newPassword) {
		Objects.requireNonNull(token);
		
		UserDetails userDetails = User.withUserDetails(loadUserByPasswordResetToken(token))
				.password(newPassword)
				.build();
		
		updateUser(userDetails);
	}

	public boolean isAdmin(LabVisionUserDetails userDetails) {
		return userDetails.getAuthorities().stream()
			.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	public void grantAdminAuthority(String username) {
		UserDetails userDetails = loadUserByUsername(username);
		
		updateUser(User.withUserDetails(userDetails)
				.authorities(Stream.concat(
							userDetails.getAuthorities().stream(),
							Stream.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
						).collect(Collectors.toList())
						)
				.build()
				);
	}

	public void revokeAdminAuthority(String username) {
		UserDetails userDetails = loadUserByUsername(username);
		
		updateUser(User.withUserDetails(userDetails)
				.authorities(userDetails.getAuthorities().stream()
						.filter(auth -> !auth.getAuthority().equals("ROLE_ADMIN"))
						.collect(Collectors.toList())
						)
				.build()
				);
	}

	@Override
	public void deleteUser(String username) {
		labVisionUserRepository.deleteByUsername(username);
		
		super.deleteUser(username);
	}
	
	public void deleteUserById(Integer id) {
		UserDetails userDetails = loadUserById(id);
		
		deleteUser(userDetails.getUsername());
	}
	
}
