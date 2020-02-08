package io.github.dmnisson.labvision;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Class<?> resourceClass;
	
	private final Object resourceId;
	
	public ResourceNotFoundException(Class<?> resourceClass, Object resourceId) {
		super("Could not find resource of type " + resourceClass + "with key: " + resourceId);
		this.resourceClass = null;
		this.resourceId = new Object();
	}

	public Class<?> getResourceClass() {
		return resourceClass;
	}

	public Object getResourceId() {
		return resourceId;
	}
	
}
