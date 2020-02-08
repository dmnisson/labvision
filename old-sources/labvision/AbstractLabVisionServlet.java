package labvision;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

public abstract class AbstractLabVisionServlet extends HttpServlet {

	/**
	 * Generated version UID
	 */
	private static final long serialVersionUID = 2504784965726800656L;
	
	protected static String URL_COMPUTATION_ERROR_MESSAGE = "Could not compute URLs. "
				+ "This is likely a problem with the app configuration. "
				+ "Please contact your institution for assistance.";

	public AbstractLabVisionServlet() {
		super();
	}

	protected IPathConstructor getPathConstructor() {
		return (IPathConstructor) getServletContext()
				.getAttribute(LabVisionServletContextListener.PATH_CONSTRUCTOR_ATTR);
	}

	/**
	 * Send and log errors in URL computations
	 * @param exception the exception
	 */
	protected void handleURLComputationError(HttpServletResponse response, Exception exception) throws IOException {
		Logger.getLogger(this.getClass())
			.error(URL_COMPUTATION_ERROR_MESSAGE, exception);
		response.sendError(500, URL_COMPUTATION_ERROR_MESSAGE);
	}

}