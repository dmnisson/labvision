package io.github.dmnisson.labvision.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class LabVisionUser implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column( name = "id", updatable = false, nullable = false )
	private Integer id;
	
	/**
	 * The username
	 */
	@Column( name = "username", columnDefinition = "VARCHAR_IGNORECASE(128) NOT NULL UNIQUE" )
	protected String username;

	public LabVisionUser() {
		super();
	}
	
	public LabVisionUser(String username) {
		super();
		setUsername(username);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public abstract UserRole getRole();

	public String getDisplayName() {
		return username;
	}
	
	@Override
	public int hashCode() {
		return 17;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabVisionUser other = (LabVisionUser) obj;
		if (id == null)
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}
}