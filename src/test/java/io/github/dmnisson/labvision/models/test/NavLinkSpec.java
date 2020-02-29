package io.github.dmnisson.labvision.models.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * Stores information needed to create a navigation link
 * @author David Nisson
 *
 */
public abstract class NavLinkSpec {
	
	private final String pageName;
	
	public NavLinkSpec(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return this.pageName;
	}
	
	public abstract String getUriString();
	
	public abstract List<NavLinkSpec> getChildren();
	
	public static NavLinkSpec of(String pageName, String uriString) {
		return new UriNavLinkSpec(pageName, uriString);
	}
	
	public static NavLinkSpec of(String pageName, Class<?> controllerType, String methodName, Object... args) {
		return new ControllerMethodNameNavLinkSpec(pageName, controllerType, methodName, args);
	}
	
	public static NavLinkSpec of(String pageName, String uriString, Collection<? extends NavLinkSpec> dropdownItems) {
		return new DropdownNavLinkSpec(pageName, uriString, dropdownItems);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NavLinkSpec other = (NavLinkSpec) obj;
		if (pageName == null) {
			if (other.pageName != null)
				return false;
		} else if (!pageName.equals(other.pageName))
			return false;
		return true;
	}
	
}

class UriNavLinkSpec extends NavLinkSpec {
	
	public UriNavLinkSpec(String pageName, String uriString) {
		super(pageName);
		this.uriString = uriString;
	}

	private final String uriString;

	@Override
	public String getUriString() {
		return uriString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uriString == null) ? 0 : uriString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriNavLinkSpec other = (UriNavLinkSpec) obj;
		if (uriString == null) {
			if (other.uriString != null)
				return false;
		} else if (!uriString.equals(other.uriString))
			return false;
		return true;
	}

	@Override
	public List<NavLinkSpec> getChildren() {
		return null;
	}
	
	
}

class ControllerMethodNameNavLinkSpec extends NavLinkSpec {
	
	private final Class<?> controllerClass;
	private final String methodName;
	private final Object[] args;
	
	public ControllerMethodNameNavLinkSpec(String pageName, Class<?> controllerClass, String methodName,
			Object... args) {
		super(pageName);
		this.controllerClass = controllerClass;
		this.methodName = methodName;
		this.args = args;
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Object[] getArgs() {
		return args;
	}

	@Override
	public String getUriString() {
		return MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.deepHashCode(args);
		result = prime * result + ((controllerClass == null) ? 0 : controllerClass.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControllerMethodNameNavLinkSpec other = (ControllerMethodNameNavLinkSpec) obj;
		if (!Arrays.deepEquals(args, other.args))
			return false;
		if (controllerClass == null) {
			if (other.controllerClass != null)
				return false;
		} else if (!controllerClass.equals(other.controllerClass))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

	@Override
	public List<NavLinkSpec> getChildren() {
		return null;
	}
	
	
}

class DropdownNavLinkSpec extends NavLinkSpec {
	
	private final String uriString;
	
	private final ArrayList<NavLinkSpec> children = new ArrayList<>();
	
	public DropdownNavLinkSpec(String pageName, String uriString, Collection<? extends NavLinkSpec> dropdownItemSpecs) {
		super(pageName);
		this.uriString = uriString;
		this.children.addAll(dropdownItemSpecs);
	}

	@Override
	public String getUriString() {
		return uriString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((uriString == null) ? 0 : uriString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DropdownNavLinkSpec other = (DropdownNavLinkSpec) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (uriString == null) {
			if (other.uriString != null)
				return false;
		} else if (!uriString.equals(other.uriString))
			return false;
		return true;
	}

	@Override
	public List<NavLinkSpec> getChildren() {
		return children;
	}

}