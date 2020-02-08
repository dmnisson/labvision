package io.github.dmnisson.labvision.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Device {
	@Id
	@GeneratedValue( generator = "uuid" )
	@GenericGenerator( name = "uuid", strategy = "uuid2" )
	private String id;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn( name="User_id" )
	private LabVisionUser user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LabVisionUser getUser() {
		return user;
	}

	public void setUser(LabVisionUser user) {
		this.user = user;
	}
}
