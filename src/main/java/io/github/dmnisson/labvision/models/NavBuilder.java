package io.github.dmnisson.labvision.models;

public interface NavBuilder<B extends NavBuilder<B>> {

	B link(NavLink link);
	
	B link(String text, String path);

	B link(String text, String methodName, Object... args);

	B link(String text, Class<?> controllerClass, String methodName, Object... args);
	
	NavbarModelBuilder.DropdownBuilder<B> dropdown(String text, String path);

	NavbarModelBuilder.DropdownBuilder<B> dropdown(String text, String methodName, Object... args);
	
	NavbarModelBuilder.DropdownBuilder<B> dropdown(String text, Class<?> controllerClass, String methodName, Object... args);

}