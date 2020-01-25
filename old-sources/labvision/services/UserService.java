package labvision.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import labvision.entities.Device;
import labvision.entities.User;
import labvision.entities.User_;

public class UserService extends JpaService {	
	public UserService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}
	
	public User getUser(int id) {
		return getUser(id, false);
	}
	
	public User getUser(String username) {
		return getUser(username, false);
	}
	
	public User getUser(int id, boolean prefetchDevices) {
		return withEntityManager(manager -> {
			final User user;
			if (!prefetchDevices) {
				user = manager.find(User.class, id);
			} else {
				CriteriaBuilder cb = manager.getCriteriaBuilder();
				CriteriaQuery<User> cq = cb.createQuery(User.class);
				Root<User> u = cq.from(User.class);
				u.fetch(User_.devices, JoinType.LEFT);
				cq.select(u).where(cb.equal(u.get(User_.id), id));
				
				TypedQuery<User> query = manager.createQuery(cq);
				user = query.getResultStream().findAny().orElse(null);
			}
			return user;
		});
	}
	
	public User getUser(String username, boolean prefetchDevices) {
		return withEntityManager(manager -> {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> u = cq.from(User.class);
			if (prefetchDevices) {
				u.fetch(User_.devices, JoinType.LEFT);
			}
			cq.select(u).where(cb.equal(u.get(User_.username), username));
			
			TypedQuery<User> query = manager.createQuery(cq);
			return query.getResultStream().findAny().orElse(null);
		});
	}
	
	public void addUser(User user) {
		withEntityManager(entityManager -> {
			EntityTransaction tx = entityManager.getTransaction();
			tx.begin();
			entityManager.persist(user);
			tx.commit();
		});
	}
	
	public Device addDevice(User user, Device device) {
		return withEntityManager(entityManager -> {
			EntityTransaction tx = entityManager.getTransaction();
			tx.begin();
			Device managedDevice = entityManager.merge(device);
			user.addDevice(managedDevice);
			tx.commit();
			
			return managedDevice;
		});
	}

	public Device getDevice(String deviceId) {
		return withEntityManager(manager -> {
			return manager.find(Device.class, deviceId);
		});
	}
}
