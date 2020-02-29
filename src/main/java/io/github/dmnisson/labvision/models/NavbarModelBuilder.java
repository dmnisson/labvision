package io.github.dmnisson.labvision.models;

import java.util.ArrayList;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

public class NavbarModelBuilder implements NavBuilder<NavbarModelBuilder> {
	
	private final NavbarModel navbarModel;
	
	private final Class<?> controllerClass;
	
	protected NavbarModelBuilder(NavbarModel navbarModel, Class<?> controllerClass) {
		this.navbarModel = navbarModel;
		this.controllerClass = controllerClass;
	}
	
	public class DropdownBuilder<P extends NavBuilder<P>> implements NavBuilder<DropdownBuilder<P>> {
		
		private final String dropdownText;
		private final String dropdownPath;
		private ArrayList<NavLink> dropdownItems = new ArrayList<>();
		
		private P parentBuilder;
		
		DropdownBuilder(String dropdownText, String dropdownPath, P parentBuilder) {
			this.dropdownText = dropdownText;
			this.dropdownPath = dropdownPath;
			this.parentBuilder = parentBuilder;
		}
		
		public DropdownBuilder<P> link(String text, String path) {
			dropdownItems.add(navbarModel.createNavLink(text, path));
			return this;
		}
		
		public DropdownBuilder<P> link(String text, String methodName, Object... args) {
			dropdownItems.add(navbarModel.createNavLink(text, controllerClass, methodName, args));
			return this;
		}
		
		public DropdownBuilder<P> link(String text, Class<?> controllerClass, String methodName, Object... args) {
			dropdownItems.add(navbarModel.createNavLink(text, controllerClass, methodName, args));
			return this;
		}

		@Override
		public DropdownBuilder<DropdownBuilder<P>> dropdown(String text, String path) {
			return new DropdownBuilder<>(text, path, this);
		}

		@Override
		public DropdownBuilder<DropdownBuilder<P>> dropdown(String text, String methodName, Object... args) {
			return dropdown(text, controllerClass, methodName, args);
		}

		@Override
		public DropdownBuilder<DropdownBuilder<P>> dropdown(String text, Class<?> controllerClass, String methodName, Object... args) {
			return dropdown(text, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
					.toUriString()
					);
		}

		@Override
		public DropdownBuilder<P> link(NavLink link) {
			dropdownItems.add(link);
			return this;
		}
		
		public P buildDropdown() {
			return parentBuilder.link(new NavLink(
					navbarModel,
					dropdownText,
					dropdownPath,
					dropdownItems.toArray(new NavLink[] {})
					));
		}
	}
	
	public static NavbarModelBuilder withNavbarModel(NavbarModel navbarModel) {
		return new NavbarModelBuilder(navbarModel, null);
	}
	
	public static NavbarModelBuilder forController(Class<?> controllerClass) {
		return new NavbarModelBuilder(makeNavbarModel(), controllerClass);
	}

	protected static NavbarModel makeNavbarModel() {
		return new NavbarModelImpl();
	}
	
	@Override
	public NavbarModelBuilder link(String text, String path) {
		navbarModel.addNavLink(text, path);
		return this;
	}
	
	@Override
	public NavbarModelBuilder link(String text, String methodName, Object... args) {
		navbarModel.addNavLink(text, controllerClass, methodName, args);
		return this;
	}
	
	@Override
	public NavbarModelBuilder link(String text, Class<?> controllerClass, String methodName, Object... args) {
		navbarModel.addNavLink(text, controllerClass, methodName, args);
		return this;
	}
	
	public NavbarModelBuilder logoutLink(String path) {
		navbarModel.setLogoutLink(path);
		return this;
	}

	@Override
	public DropdownBuilder<NavbarModelBuilder> dropdown(String text, String path) {
		return new DropdownBuilder<>(text, path, this);
	}

	@Override
	public DropdownBuilder<NavbarModelBuilder> dropdown(String text, String methodName, Object... args) {
		return dropdown(text, controllerClass, methodName, args);
	}

	@Override
	public DropdownBuilder<NavbarModelBuilder> dropdown(String text, Class<?> controllerClass, String methodName, Object... args) {
		return dropdown(text, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString()
				);
	}

	@Override
	public NavbarModelBuilder link(NavLink link) {
		navbarModel.addNavLink(link);
		return this;
	}
	
	public NavbarModel build() {
		return navbarModel;
	}
}
