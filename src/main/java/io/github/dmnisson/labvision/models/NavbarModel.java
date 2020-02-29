package io.github.dmnisson.labvision.models;

import java.util.List;

import io.github.dmnisson.labvision.models.NavbarModelImpl.NavLink;

public interface NavbarModel {

	/**
	 * Gets a list view of the links in this navbar
	 * @return the links
	 */
	List<NavLink> getNavLinks();

	/**
	 * Add a new Navlink to the navigation bar.
	 * @param link the link to add
	 */
	void addNavLink(NavLink link);

	/**
	 * Add a new Navlink to the navigation bar.
	 * @param pageName the page name
	 * @param url new URL
	 */
	void addNavLink(String pageName, String url);

	/**
	 * Create a new NavLink for a given URL
	 * @param pageName the page name
	 * @param url new URL
	 */
	NavLink createNavLink(String pageName, String url);

	/**
	 * Removes the navigation link at the given index.
	 * @param index the index at which to remove the link
	 */
	void removeNavLink(int index);

	/**
	 * Removes all navigation links matching the name and URL
	 * @param link the link whose copies to remove
	 */
	void removeNavLinkClones(NavLink link);

	/**
	 * Removes all navigation links with the given name
	 * @param name the name of all the links to remove
	 */
	void removeNavLinksByName(String name);

	String getLogoutLink();

	void setLogoutLink(String logoutLink);

	<C> void addNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args);

	<C> NavLink createNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args);

}