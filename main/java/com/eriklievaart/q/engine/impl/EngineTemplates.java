package com.eriklievaart.q.engine.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.eriklievaart.toolkit.io.api.LineFilter;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class EngineTemplates {
	private final String templateResource = "/engine/templates.properties";

	private final Map<Character, String> templates = NewCollection.concurrentMap();
	private String fallback;

	{
		init(parseTemplates(StreamTool.toString(getClass().getResourceAsStream(templateResource))));
	}

	void init(final Map<String, String> ini) {
		fallback = ini.remove("");

		for (String key : ini.keySet()) {
			String format = "Fatal Error; Failed to load %. Expecting a zero or one non-letter key, but was: %.";
			Check.matches(key, "[^a-zA-Z]?", format, templateResource, key);
			templates.put(key.charAt(0), ini.get(key));
		}
	}

	static Map<String, String> parseTemplates(String raw) {
		List<String> l = new LineFilter(raw).dropBlank().dropHash().trim().list();
		return l.stream().collect(Collectors.toMap(e -> parseKey(e), e -> parseValue(e)));
	}

	private static String parseValue(String e) {
		Check.matches(e, "^.?=.+");
		return e.replaceFirst("^.?=", "");
	}

	private static String parseKey(String e) {
		String key = e.replaceFirst("=.*", "");
		Check.isTrue(key.length() <= 1, "key too long: %", key);
		return key;
	}

	public String apply(final String raw) {
		String content = isDefault(raw) ? raw : raw.substring(1);
		return lookup(raw).replace("${}", content);
	}

	private String lookup(final String raw) {
		return isDefault(raw) ? fallback : templates.get(raw.charAt(0));
	}

	private boolean isDefault(final String raw) {
		return Str.isEmpty(raw) || templates.get(raw.charAt(0)) == null;
	}
}
