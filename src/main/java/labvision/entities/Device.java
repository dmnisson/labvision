package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Device {
	@Id
	@GeneratedValue( generator = "uuid" )
	@GenericGenerator( name = "uuid", strategy = "uuid2" )
	private String id;
	
	@ManyToOne
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
