package io.github.dmnisson.labvision.models.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayDeque;
import java.util.HashSet;

import io.github.dmnisson.labvision.models.NavLink;
import io.github.dmnisson.labvision.models.NavbarModel;

public class NavLinkSpecAssertions {

	public static void assertNavLinks(NavLinkSpec[] navLinkSpecs, NavbarModel navbarModel) {
		assertEquals(navLinkSpecs.length, navbarModel.getNavLinks().size());
		
		// depth-first search
		for (int i = 0; i < navLinkSpecs.length; i++) {
			HashSet<NavLinkSpec> discoveredSpecs = new HashSet<>();
			HashSet<NavLink> discoveredLinks = new HashSet<>();
			
			NavLinkSpec visitingSpec = navLinkSpecs[i];
			NavLink visitingLink = navbarModel.getNavLinks().get(i);
			
			ArrayDeque<NavLinkSpec> stack = new ArrayDeque<>();
			ArrayDeque<NavLink> linkStack = new ArrayDeque<>();
			
			stack.push(visitingSpec);
			linkStack.push(visitingLink);
			
			while (!stack.isEmpty()) {
				visitingSpec = stack.pop();
				visitingLink = linkStack.pop();
				if (!discoveredSpecs.contains(visitingSpec)) {
					assertEquals(visitingSpec.getPageName(), visitingLink.getPageName());
					discoveredSpecs.add(visitingSpec);
					discoveredLinks.add(visitingLink);
					
					if (visitingSpec.getChildren() == null) {
						assertNull(visitingLink.getDropdownMenu());
					}
					
					if (visitingSpec.getChildren() != null) {
						assertEquals(visitingSpec.getChildren().size(), visitingLink.getDropdownMenu().length);
						for (int j = 0; j < visitingSpec.getChildren().size(); j++) {
							stack.push(visitingSpec.getChildren().get(j));
							linkStack.push(visitingLink.getDropdownMenu()[j]);
						}
					}
				}
			}
		}
	}

}
