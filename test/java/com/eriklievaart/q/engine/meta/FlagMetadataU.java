package com.eriklievaart.q.engine.meta;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellParseException;
import com.eriklievaart.q.engine.parse.ShellParser;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;

public class FlagMetadataU {
	private static final List<ShellArgument> EMPTY_LIST = Collections.emptyList();

	@Test
	public void getShellArgumentsEmpty() {
		Check.isEqual(new FlagMetadata("flag", EMPTY_LIST).getShellArguments(null), new ShellArgument[] {});
	}

	@Test
	public void getShellArgumentsOptional() throws ShellParseException {
		ShellArgument argument = ShellParser.parseArgument("$var");
		Iterator<ShellArgument> empty = FromCollection.toIterator();

		FlagMetadata metadata = FlagMetadataFactory.named("flag").addArgument(argument).make();
		Check.isEqual(metadata.getShellArguments(empty), new ShellArgument[] { argument });
	}

	@Test
	public void getShellArgumentsOverride() throws ShellParseException {
		ShellArgument preset = ShellParser.parseArgument("$preset");
		ShellArgument override = ShellParser.parseArgument("$override");
		Iterator<ShellArgument> iterator = FromCollection.toIterator(override);

		FlagMetadata metadata = FlagMetadataFactory.named("flag").addArgument(preset).make();
		Check.isEqual(metadata.getShellArguments(iterator), new ShellArgument[] { override });
	}

	@Test
	public void getShellArgumentsExplicitDefault() throws ShellParseException {
		ShellArgument argument = ShellParser.parseArgument("$var");
		Iterator<ShellArgument> iterator = FromCollection.toIterator(ShellArgument.DEFAULT);

		FlagMetadata metadata = FlagMetadataFactory.named("flag").addArgument(argument).make();
		Check.isEqual(metadata.getShellArguments(iterator), new ShellArgument[] { argument });
	}
}
