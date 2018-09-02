package com.eriklievaart.q.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.meta.PluginIntrospector;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.collection.OptionalOne;

public class PluginIndex {

	AtomicReference<List<CommandMetadata>> reference = new AtomicReference<>(new ArrayList<>());

	public OptionalOne<CommandMetadata> lookup(String command) {
		Check.notNull(command);

		Predicate<? super CommandMetadata> startsWith = c -> c.getCommandName().startsWith(command.trim());
		return new OptionalOne<>(reference.get().stream().filter(startsWith).collect(Collectors.toList()));
	}

	public List<String> listCommands() {
		List<String> matches = NewCollection.list();
		for (CommandMetadata registered : reference.get()) {
			matches.add(registered.getPlugin().getCommandName());
		}
		return matches;
	}

	public void init(List<QPlugin> plugins, EngineSupplierFactory factory) {
		List<CommandMetadata> clone = new ArrayList<>(reference.get());

		purgeUnused(clone, plugins);
		List<QPlugin> unprocessed = filterKeepUnprocessedPlugins(plugins, clone);
		clone.addAll(new PluginIntrospector(factory).generateMetadata(unprocessed));

		reference.set(Collections.unmodifiableList(clone));
	}

	private List<QPlugin> filterKeepUnprocessedPlugins(List<QPlugin> plugins, List<CommandMetadata> existing) {
		Set<String> names = existing.stream().map(p -> p.getCommandName()).collect(Collectors.toSet());
		List<QPlugin> clone = new ArrayList<>(plugins);

		Iterator<QPlugin> iterator = clone.iterator();
		while (iterator.hasNext()) {
			String command = iterator.next().getCommandName();
			if (names.contains(command)) {
				iterator.remove();
			}
		}
		return clone;
	}

	private void purgeUnused(List<CommandMetadata> list, List<QPlugin> plugins) {
		Set<String> names = plugins.stream().map(p -> p.getCommandName()).collect(Collectors.toSet());
		Iterator<CommandMetadata> iterator = list.iterator();
		while (iterator.hasNext()) {
			String command = iterator.next().getCommandName();
			if (!names.contains(command)) {
				iterator.remove();
			}
		}
	}
}