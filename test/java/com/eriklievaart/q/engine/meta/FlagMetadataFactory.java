package com.eriklievaart.q.engine.meta;

import java.util.List;

import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellParseException;
import com.eriklievaart.q.engine.parse.ShellParser;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class FlagMetadataFactory {
	private final String name;
	private final List<ShellArgument> values = NewCollection.list();

	private FlagMetadataFactory(final String name) {
		this.name = name;
	}

	public static FlagMetadataFactory named(final String name) {
		return new FlagMetadataFactory(name);
	}

	public FlagMetadataFactory addArgument(final String shellArgument) {
		try {
			return addArgument(ShellParser.parseArgument(shellArgument));
		} catch (ShellParseException e) {
			throw new RuntimeException("Invalid argument", e);
		}
	}

	public FlagMetadataFactory addArgument(final ShellArgument argument) {
		values.add(argument);
		return this;
	}

	public FlagMetadata make() {
		return new FlagMetadata(name, values);
	}
}
