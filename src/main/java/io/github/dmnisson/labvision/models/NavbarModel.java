package io.github.dmnisson.labvision.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * A model of the links in a navbar
 * @author davidnisson
 *
 */
public class NavbarModel implements Serializable {
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
	public List<NavLink> getNavLinks() {
		return navLinks;
	}
	
	/**
	 * Add a new Navlink to the navigation bar.
	 * @param link the link to add
	 */
	public void addNavLink(NavLink link) {
		navLinks.add(link);
	}
	
	/**
	 * Add a new Navlink to the navigation bar.
	 * @param pageName the page name
	 * @param url new URL
	 */
	public void addNavLink(String pageName, String url) {
		addNavLink(createNavLink(pageName, url));
	}

	/**
	 * Create a new NavLink for a given URL
	 * @param pageName the page name
	 * @param url new URL
	 */
	public NavLink createNavLink(String pageName, String url) {
		return new NavLink(pageName, url);
	}
	
	/**
	 * Removes the navigation link at the given index.
	 * @param index the index at which to remove the link
	 */
	public void removeNavLink(int index) {
		navLinks.remove(index);
	}
	
	/**
	 * Removes all navigation links matching the name and URL
	 * @param link the link whose copies to remove
	 */
	public void removeNavLinkClones(NavLink link) {
		navLinks.removeIf((l) -> link.equals(l));
	}
	
	/**
	 * Removes all navigation links with the given name
	 * @param name the name of all the links to remove
	 */
	public void removeNavLinksByName(String name) {
		navLinks.removeIf((l) -> l.getPageName().equals(name));
	}
	
	public String getLogoutLink() {
		return logoutLink;
	}

	public void setLogoutLink(String logoutLink) {
		this.logoutLink = logoutLink;
	}

	/**
	 * A navigation link.
	 * @author davidnisson
	 *
	 */
	public class NavLink implements Serializable {
		/**
		 * Version UID for serialization of individual NavLinks
		 */
		private static final long serialVersionUID = -3479018459606633102L;
		
		private final String pageName;
		private final String url;
		private final NavLink[] dropdownMenu;
		
		public NavLink(String pageName, String url) {
			this(pageName, url, null);
		}
		
		public NavLink(String pageName, String url, NavLink[] dropdownMenu) {
			this.pageName = pageName;
			this.url = url;
			if (dropdownMenu == null) {
				this.dropdownMenu = null;
			} else {
				this.dropdownMenu = Arrays.copyOf(dropdownMenu, dropdownMenu.length);
			}
		}
		
		public String getPageName() {
			return pageName;
		}
		
		public String getUrl() {
			return url;
		}

		public boolean isDropdown() {
			return dropdownMenu != null;
		}
		
		public NavLink[] getDropdownMenu() {
			return dropdownMenu;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
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
			NavLink other = (NavLink) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (pageName == null) {
				if (other.pageName != null)
					return false;
			} else if (!pageName.equals(other.pageName))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "NavLink [pageName=" + pageName + ", url=" + url + ", dropdownMenu=" + Arrays.toString(dropdownMenu)
					+ "]";
		}

		private NavbarModel getOuterType() {
			return NavbarModel.this;
		}
	}

	public <C> void addNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args) {
		addNavLink(createNavLink(pageName, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString()));
	}
	
	public <C> NavLink createNavLink(String pageName, Class<C> controllerClass, String methodName, Object... args) {
		return createNavLink(pageName, MvcUriComponentsBuilder.fromMethodName(controllerClass, methodName, args)
				.toUriString());
	}
}
