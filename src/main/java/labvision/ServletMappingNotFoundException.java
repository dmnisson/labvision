package labvision.services;

public class ServletMappingNotFoundException extends Exception {
	private static final long serialVersionUID = 4424877607495816062L;

	private final String servletName;

	public ServletMappingNotFoundException(String servletName) {
		super();
		this.servletName = servletName;
	}

	public String getServletName() {
		return servletName;
	}
}
