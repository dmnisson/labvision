package labvision;

import javax.servlet.ServletContext;

/**
 * Interface for computing the correct paths for a given endpoint
 * @author davidnisson
 *
 */
public interface IPathConstructor {

	/**
	 * Returns a path relative to the server root that will point to the resource
	 * @param servletName the name of the servlet
	 * @param pathInfo the information to pass to the servlet
	 * @param context the servlet context
	 * @return the relative path
	 * @throws ServletNotFoundException if a servlet with the given name is not found
	 * @throws ServletMappingNotFoundException if a mapping is not found for the specified servlet
	 */
	String getPathFor(String servletName, String pathInfo)
			throws ServletNotFoundException, ServletMappingNotFoundException;

}