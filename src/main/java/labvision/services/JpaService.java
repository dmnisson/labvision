package labvision.services;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Base class for services that use the JPA
 * @author davidnisson
 *
 */
public class JpaService {
	/**
	 * JPQL expression used to determine date of last measurement value or report of an experiment
	 */
	protected static final String EXPERIMENT_LAST_UPDATED_FUNCTION = "	MAX(" +
				"		CASE WHEN (rr.added IS NULL OR mv.taken > rr.added)" +
				"			THEN mv.taken" +
				" 			ELSE rr.added" +
				"			END" +
				"		)";

	private final EntityManagerFactory entityManagerFactory;
	
	protected final String STUDENT_SERVLET_NAME = "labvision-student";
	protected final String FACULTY_SERVLET_NAME = "labvision-faculty";

	protected JpaService(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	/**
	 * Executes the consumer with a fresh EntityManager and closes it afterward
	 * @param consumer the consumer to execute
	 */
	protected void withEntityManager(Consumer<EntityManager> consumer) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		consumer.accept(entityManager);
		entityManager.close();
	}
	
	/**
	 * Executes the function with a fresh EntityManager and closes it afterward, then
	 * returns the result of the function
	 * @param function the function to execute
	 */
	protected <R> R withEntityManager(Function<EntityManager, R> function) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		R result = function.apply(entityManager);
		entityManager.close();
		return result;
	}
	
	/**
	 * Returns a path relative to the server root that will point to the resource
	 * @param servletName the name of the servlet
	 * @param pathInfo the information to pass to the servlet
	 * @param context the servlet context
	 * @return the relative path
	 * @throws ServletNotFoundException if a servlet with the given name is not found
	 * @throws ServletMappingNotFoundException if a mapping is not found for the specified servlet
	 */
	protected String getPathFor(String servletName, String pathInfo, ServletContext context) throws ServletNotFoundException, ServletMappingNotFoundException {
		ServletRegistration registration = context.getServletRegistration(servletName);
		if (Objects.isNull(registration)) {
			throw new ServletNotFoundException(servletName);
		}
		
		return context.getContextPath() +
				registration.getMappings().stream()
					.findFirst()
					.orElseThrow(() -> new ServletMappingNotFoundException(servletName))
					.replace("*", pathInfo.substring(1));
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
