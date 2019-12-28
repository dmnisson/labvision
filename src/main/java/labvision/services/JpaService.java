package labvision.services;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import labvision.utils.ThrowingConsumer;
import labvision.utils.ThrowingFunction;

/**
 * Base class for services that use the JPA
 * @author davidnisson
 *
 */
public class JpaService {
	/**
	 * JPQL expression used to determine date of last measurement value or report of an experiment
	 */
	public static final String EXPERIMENT_LAST_UPDATED_FUNCTION = "	MAX(" +
				"		CASE WHEN (rr.added IS NULL OR mv.taken > rr.added)" +
				"			THEN mv.taken" +
				" 			ELSE rr.added" +
				"			END" +
				"		)";

	private final EntityManagerFactory entityManagerFactory;
	
	protected final String FACULTY_SERVLET_NAME = "labvision-faculty";

	protected JpaService(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	/**
	 * Executes the consumer with a fresh EntityManager and closes it afterward
	 * @param consumer the consumer to execute
	 */
	protected <E extends Exception> void withEntityManager(ThrowingConsumer<EntityManager, E> consumer) throws E {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		consumer.accept(entityManager);
		entityManager.close();
	}
	
	/**
	 * Executes the function with a fresh EntityManager and closes it afterward, then
	 * returns the result of the function
	 * @param function the function to execute
	 */
	protected <R, E extends Exception> R withEntityManager(ThrowingFunction<EntityManager, R, E> function) throws E {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		R result = function.apply(entityManager);
		entityManager.close();
		return result;
	}
	
	public static <T> void clearTable(Class<T> entityClass, EntityManager manager) {
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaDelete<T> cd = cb.createCriteriaDelete(entityClass);
		cd.from(entityClass);
		
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		manager.createQuery(cd).executeUpdate();
		tx.commit();
	}
}
