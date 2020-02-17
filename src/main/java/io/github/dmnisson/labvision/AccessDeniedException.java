package io.github.dmnisson.labvision;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Class<?> resourceClass;
	
	private final Object resourceId;

	public AccessDeniedException(Class<?> resourceClass, Object resourceId) {
		super();
		this.resourceClass = resourceClass;
		this.resourceId = resourceId;
	}

	public Class<?> getResourceClass() {
		return resourceClass;
	}

	public Object getResourceId() {
		return resourceId;
	}
	
}
