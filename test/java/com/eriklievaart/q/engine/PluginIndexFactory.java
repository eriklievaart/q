package com.eriklievaart.q.engine;

import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class PluginIndexFactory {

	private List<CommandMetadata> commands = NewCollection.list();

	public PluginIndexFactory command(CommandMetadata metadata) {
		commands.add(metadata);
		return this;
	}

	public PluginIndex make() {
		PluginIndex index = new PluginIndex();
		index.reference.set(new ArrayList<>(commands));
		return index;
	}
}