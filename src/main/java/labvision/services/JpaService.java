package labvision.services;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Base class for services that use the JPA
 * @author davidnisson
 *
 */
public class JpaService {
	private final EntityManagerFactory entityManagerFactory;

	protected JpaService(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	protected void withEntityManager(Consumer<EntityManager> consumer) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		consumer.accept(entityManager);
		entityManager.close();
	}
	
	protected <R> R withEntityManager(Function<EntityManager, R> function) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		R result = function.apply(entityManager);
		entityManager.close();
		return result;
	}
}
