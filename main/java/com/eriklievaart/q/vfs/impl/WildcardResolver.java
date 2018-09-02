package com.eriklievaart.q.vfs.impl;

import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;

class WildcardResolver {

	private final TreeSet<String> files;
	private final String query;

	WildcardResolver(final String query, final TreeSet<String> fileNames) {
		Check.notNull(query, fileNames);
		this.files = fileNames;
		this.query = StringUtils.endsWith(query, "*") || StringUtils.endsWith(query, "?") ? query : query + "*";
	}

	String resolve() {
		String match = wildcardMatch();
		return match != null ? match : wildcardInsensitiveMatch();
	}

	String wildcardMatch() {
		for (String path : files) {
			if (WildcardTool.match(query, path)) {
				return path;
			}
		}
		return null;
	}

	String wildcardInsensitiveMatch() {
		for (String path : files) {
			if (WildcardTool.match(query.toLowerCase(), path.toLowerCase())) {
				return path;
			}
		}
		return null;
	}

}
