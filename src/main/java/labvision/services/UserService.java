package labvision.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import labvision.entities.User;

public class UserService {
	private final EntityManagerFactory entityManagerFactory;
	
	public UserService(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public User getUser(int id) {
		return getUser(id, false);
	}
	
	public User getUser(int id, boolean prefetchDevices) {
		// TODO implement this!
		return null;
	}
}
