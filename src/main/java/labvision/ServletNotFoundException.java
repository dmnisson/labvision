package labvision.services;

public class ServletNotFoundException extends Exception {
	private static final long serialVersionUID = 6041945607028369953L;

	private final String servletName;

	public ServletNotFoundException(String servletName) {
		super("Could not find servlet: " + servletName);
		this.servletName = servletName;
	}

	public String getServletName() {
		return servletName;
	}
}
