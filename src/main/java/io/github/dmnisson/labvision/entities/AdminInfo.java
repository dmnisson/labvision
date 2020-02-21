package io.github.dmnisson.labvision.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * Information for users with admin privileges
 * @author David Nisson
 *
 */
@Entity
@ValidAdminInfo
public class AdminInfo {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private Integer id;
	
	@OneToOne( mappedBy = "adminInfo", targetEntity = LabVisionUser.class )
	private LabVisionUser user;
	
	@Column
	private String firstName;
	
	@Column
	private String lastName;
	
	@Column
	@Email(message = "Email address must be valid.")
	private String email;
	
	@Column
	@Pattern(regexp="^\\+[1-9]\\d{1,14}$", message="Phone number must be in E.164 format.")
	private String phone;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LabVisionUser getUser() {
		return user;
	}

	public void setUser(LabVisionUser user) {
		this.user = user;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
