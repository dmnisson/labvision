package io.github.dmnisson.labvision.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * A model of the links in a navbar
 * @author davidnisson
 *
 */
public class NavbarModelImpl implements Serializable, NavbarModel {
	/**
	 * Version UID for serialization of this model
	 */
	private static final long serialVersionUID = -3042518130150103927L;
	
	private final List<NavLink> navLinks = new ArrayList<>();
	
	private String logoutLink;
	
	/**
	 * Gets a list view of the links in this navbar
	 * @return the links
	 */
	@Override
	public List<NavLink> getNavLinks() {
		return navLinks;
	}
	
	/**
	 * Add a new Navlink to the navigation bar.
	 * @param link the link to add
	 */
	@Override
	public void addNavLink(NavLink link) {
		navLinks.add(link);
	}
	
	/**
	 * Add a new Navlink to the navigation bar.
	 * @param pageName the page name
	 * @param url new URL
	 */
	@Override
	public void addNavLink(String pageName, String url) {
		addNavLink(createNavLink(pageName, url));
	}

	/**
	 * Create a new NavLink for a given URL
	 * @param pageName the page name
	 * @param url new URL
	 */
	@Override
	public NavLink createNavLink(String pageName, String url) {
		return new NavLink(this, pageName, url);
	}
	
	/**
	 * Removes the navigation link at the given index.
	 * @param index the index at which to remove the link
	 */
	@Override
	public void removeNavLink(int index) {
		navLinks.remove(index);
	}
	
	/**
	 * Removes all navigation links matching the name and URL
	 * @param link the link whose copies to remove
	 */
	@Override
	public void removeNavLinkClones(NavLink link) {
		navLinks.removeIf((l) -> link.equals(l));
	}
	
	/**
	 * Removes all navigation links with the given name
	 * @param name the name of all the links to remove
	 */
	@Override
	public void removeNavLinksByName(String name) {
		navLinks.removeIf((l) -> l.getPageName().equals(name));
	}
	
	@Override
	public String getLogoutLink() {
		return logoutLink;
	}

	@Override
	public void setLogoutLink(String logoutLink) {
		this.logoutLink = logoutLink;
	}

	@Override
	public <C> void addNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args) {
		addNavLink(createNavLink(pageName, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString()));
	}
	
	@Override
	public <C> NavLink createNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args) {
		return createNavLink(pageName, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString());
	}
}
