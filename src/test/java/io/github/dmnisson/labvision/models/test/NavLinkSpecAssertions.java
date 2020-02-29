package io.github.dmnisson.labvision.models.test;

import java.util.ArrayDeque;
import java.util.HashSet;

import io.github.dmnisson.labvision.models.NavbarModel;

public class NavLinkSpecAssertions {

	public static void assertNavLinks(NavLinkSpec[] navLinkSpecs, NavbarModel navbarModel) {
		// depth-first search
		for (NavLinkSpec spec : navLinkSpecs) {
			HashSet<NavLinkSpec> discoveredSpecs = new HashSet<>();
			
			NavLinkSpec visitingSpec = spec;
			ArrayDeque<NavLinkSpec> stack = new ArrayDeque<>();
			stack.push(visitingSpec);
			
			while (!stack.isEmpty()) {
				visitingSpec = stack.pop();
				if (!discoveredSpecs.contains(visitingSpec)) {
					discoveredSpecs.add(visitingSpec);
					if (visitingSpec.getChildren() != null) {
						for (NavLinkSpec child : visitingSpec.getChildren()) {
							stack.push(child);
						}
					}
				}
			}
		}
	}

}
