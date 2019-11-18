package labvision;

import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import labvision.services.ServletMappingNotFoundException;
import labvision.services.ServletNotFoundException;

public class PathConstructor implements IPathConstructor {
	private ServletContext context;
	
	public PathConstructor(ServletContext context) {
		this.context = context;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPathFor(String servletName, String pathInfo) throws ServletNotFoundException, ServletMappingNotFoundException {
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

}
