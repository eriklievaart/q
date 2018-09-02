package com.eriklievaart.q.engine.meta;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.engine.FileInvokable;
import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.PluginIndexFactory;
import com.eriklievaart.toolkit.lang.api.collection.MultiMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class CommandMetadataFactory {

	private final String name;
	private final CallPolicy policy;
	private final MultiMap<String, FlagMetadata> flags = NewCollection.multiMap();
	private final Map<String, FlagMetadata> primary = NewCollection.map();
	private Integer seed = 0;

	public CommandMetadataFactory(final String name) {
		this(name, CallPolicy.BOTH);
	}

	public CommandMetadataFactory(final String name, CallPolicy policy) {
		this.name = name;
		this.policy = policy;
	}

	public CommandMetadataFactory flag(final String named) {
		seed++;
		flags.add(seed.toString(), FlagMetadataFactory.named(named).addArgument("`default`").make());
		return this;
	}

	public CommandMetadataFactory mutexFlag(final String named) {
		flags.add("mutex", FlagMetadataFactory.named(named).addArgument("`default`").make());
		return this;
	}

	public CommandMetadataFactory mutexFlag(final String named, final String value) {
		flags.add("mutex", FlagMetadataFactory.named(named).addArgument(value).make());
		return this;
	}

	public CommandMetadataFactory mutexFlags(final List<FlagMetadata> value) {
		for (FlagMetadata flag : value) {
			this.flags.add("mutex", flag);
		}
		return this;
	}

	public CommandMetadataFactory primaryFlag(final String named) {
		seed++;
		FlagMetadata flag = FlagMetadataFactory.named(named).addArgument("`default`").make();
		primary.put(seed.toString(), flag);
		flags.add(seed.toString(), flag);
		return this;
	}

	public CommandMetadataFactory primaryFlag(final FlagMetadata flag) {
		seed++;
		primary.put(seed.toString(), flag);
		flags.add(seed.toString(), flag);
		return this;
	}

	public CommandMetadataFactory primaryMutexFlag(final String named) {
		FlagMetadata flag = FlagMetadataFactory.named(named).addArgument("`default`").make();
		primary.put("mutex", flag);
		flags.add("mutex", flag);
		return this;
	}

	private List<FlagGroupMetadata> makeGroupList() {
		List<FlagGroupMetadata> result = NewCollection.list();
		for (Entry<String, List<FlagMetadata>> group : flags.entrySet()) {
			result.add(new FlagGroupMetadata(group.getKey(), group.getValue(), primary.get(group.getKey())));
		}
		return result;
	}

	public CommandMetadata make() {
		QPlugin plugin = new QPlugin() {
			@Override
			public String getCommandName() {
				return name;
			}

			@Override
			public Invokable createInstance() {
				return new FileInvokable();
			}

			@Override
			public CallPolicy getCallPolicy() {
				return policy;
			}
		};
		return new CommandMetadata(plugin, makeGroupList());
	}

	public PluginIndex makeIndex() {
		return new PluginIndexFactory().command(make()).make();
	}

}
