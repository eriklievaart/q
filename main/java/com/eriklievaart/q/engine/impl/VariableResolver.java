package com.eriklievaart.q.engine.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eriklievaart.q.ui.api.BrowserContext;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class VariableResolver {
	private Map<String, Function<BrowserContext, String>> variables = NewCollection.map();

	{
		variables.put("parent", browser -> browser.getDirectory().getParentFile().get().getUrl().getUrlEscaped());
		variables.put("dir", browser -> browser.getDirectory().getUrl().getUrlEscaped());
		variables.put("url", VariableResolver::getUrl);
		variables.put("urls", VariableResolver::getUrls);
		variables.put("name", VariableResolver::getName);
		variables.put("names", VariableResolver::getNames);
	}

	public String lookup(String variable, QContext context) {
		Check.notNull(variable, context);
		BrowserContext browser = getBrowserContext(context, variable);
		return resolve(variable, browser);
	}

	private String resolve(String variable, BrowserContext browser) {
		return variables.get(variable.replaceAll("[~12]", "")).apply(browser);
	}

	private BrowserContext getBrowserContext(QContext context, String orientation) {
		switch (orientation.replaceAll("[a-z]", "").trim()) {
		case "":
			return context.isLeftActive() ? context.getLeft() : context.getRight();

		case "~":
			return context.isLeftActive() ? context.getRight() : context.getLeft();

		case "1":
			return context.getLeft();

		case "2":
			return context.getRight();

		default:
			throw new AssertionException("Unknown orientation %", orientation);
		}
	}

	private static String getUrls(BrowserContext context) {
		return context.getUrls().stream().map(u -> u.getUrl().getUrlEscaped()).collect(Collectors.joining(" "));
	}

	private static String getUrl(BrowserContext context) {
		return context.getUrls().stream().map(u -> u.getUrl().getUrlEscaped()).findFirst().orElse("");
	}

	private static String getNames(BrowserContext context) {
		return context.getUrls().stream().map(u -> u.getUrl().getNameEscaped()).collect(Collectors.joining(" "));
	}

	private static String getName(BrowserContext context) {
		return context.getUrls().stream().map(u -> u.getUrl().getNameEscaped()).findFirst().orElse("");
	}

	public Map<String, String> createMap(QContext context) {
		Map<String, String> map = NewCollection.map();

		for (String orientation : Arrays.asList("", "~", "1", "2")) {
			variables.forEach((name, lambda) -> {
				map.put(name + orientation, lambda.apply(getBrowserContext(context, orientation)));
			});
		}
		return map;
	}
}
