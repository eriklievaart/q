package com.eriklievaart.q.wildcard.api;

import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;

public class WildcardChecker {

	private String[] includes;

	public WildcardChecker(String pattern) {
		includes = pattern.trim().split("\\s*,\\s*");
	}

	public boolean matches(String name) {
		for (String include : includes) {
			if (WildcardTool.match(include, name)) {
				return true;
			}
		}
		return false;
	}
}
