package com.eriklievaart.q.engine.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class VariableExaminer {
	private static final List<String> BASE_NAMES = listBaseNames();
	private static final Set<String> DEFAULT_VARIABLES = listVariableNames();

	private static List<String> listBaseNames() {
		return ListTool.of("", "parent", "dir", "dirname", "url", "urls", "urlname", "urlnames");
	}

	private static Set<String> listVariableNames() {
		List<String> suffixes = ListTool.of("", "~", "1", "2");

		Set<String> result = NewCollection.set();
		for (String prefix : BASE_NAMES) {
			for (String suffix : suffixes) {
				result.add(prefix + suffix);
			}
		}
		return result;
	}

	public void validate(final ShellCommand parsed) throws PluginException {
		Iterator<ShellArgument> iter = parsed.getArguments();
		while (iter.hasNext()) {
			checkArgument(iter.next());
		}
	}

	private void checkArgument(final ShellArgument argument) throws PluginException {
		switch (argument.getType()) {

		case VARIABLE:
			checkVariable(argument.getValue());
			break;
		}
	}

	private void checkVariable(final String name) throws PluginException {
		if (!DEFAULT_VARIABLES.contains(name)) {
			throw new PluginException("Missing Variable $" + name + " => " + getKeysStartingWith(name, BASE_NAMES));
		}
	}

	List<String> getKeysStartingWith(final String prefix, final Collection<String> keys) {
		List<String> result = NewCollection.list();

		for (String key : keys) {
			if (key.startsWith(prefix)) {
				result.add(key);
			}
		}
		return result;
	}
}
