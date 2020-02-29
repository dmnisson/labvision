package io.github.dmnisson.labvision.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A navigation link.
 * @author davidnisson
 *
 */
public class NavLink implements Serializable {
	private final NavbarModel navbarModel;

	/**
	 * Version UID for serialization of individual NavLinks
	 */
	private static final long serialVersionUID = -3479018459606633102L;
	
	private final String pageName;
	private final String url;
	private final NavLink[] dropdownMenu;
	
	public NavLink(NavbarModel navbarModel, String pageName, String url) {
		this(navbarModel, pageName, url, null);
	}
	
	public NavLink(NavbarModel navbarModel, String pageName, String url, NavLink[] dropdownMenu) {
		this.navbarModel = navbarModel;
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
		result = prime * result + getNavbarModel().hashCode();
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
		if (!getNavbarModel().equals(other.getNavbarModel()))
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

	private NavbarModel getNavbarModel() {
		return navbarModel;
	}
}