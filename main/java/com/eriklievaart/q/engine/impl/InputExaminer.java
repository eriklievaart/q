package com.eriklievaart.q.engine.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.engine.exception.ShellCommandMissingException;
import com.eriklievaart.q.engine.exception.ShellException;
import com.eriklievaart.q.engine.exception.ShellFlagMissingException;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.meta.FlagMetadata;
import com.eriklievaart.q.engine.meta.MetadataValidator;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.q.engine.parse.ShellParser;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.collection.OptionalOne;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class InputExaminer {

	private EngineSupplierFactory factory;
	private VariableExaminer variables = new VariableExaminer();

	public InputExaminer(EngineSupplierFactory factory) {
		this.factory = factory;
	}

	public EngineResult examineRaw(String raw) {
		PluginIndex metadata = factory.getPluginIndex();
		try {
			ShellCommand command = ShellParser.parseLine(raw);
			PluginContextImpl context = factory.getPluginContext(command);
			return examineParsed(command, context);

		} catch (ShellCommandMissingException scm) {
			String unrecognized = raw.trim().replaceFirst("[^a-zA-Z].++", "");
			return EngineResult.error(Str.sub("unrecognized command %: $", unrecognized, metadata.listCommands()));

		} catch (ShellFlagMissingException sfm) {
			String cmd = sfm.getShellCommand().getName();
			if (cmd == null || metadata.lookup(cmd).isEmpty()) {
				return EngineResult.error(Str.sub("Cannot lookup flag; missing command %", cmd));
			}
			return EngineResult.error(getFlagString(metadata.lookup(cmd).get()));

		} catch (ShellException shell) {
			return EngineResult.error(shell.getMessage());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public EngineResult examineParsed(ShellCommand parsed, PluginContextImpl context) throws Exception {
		try {
			OptionalOne<CommandMetadata> command = factory.getPluginIndex().lookup(parsed.getName());
			ShellCommandMissingException.on(command.isEmpty(), "unrecognized command %", parsed.getName());
			MetadataValidator.validate(parsed, command.get());
			variables.validate(parsed);
			factory.getPluginRunner().validate(parsed, context);
			return EngineResult.message(toString(parsed));

		} catch (PluginException e) {
			return EngineResult.error(toString(parsed) + " -> " + e.getMessage());

		}
	}

	private String getFlagString(final CommandMetadata command) {
		StringBuilder builder = new StringBuilder(command.getCommandName());
		builder.append(" -").append(Arrays.toString(command.getCharacterFlags())).append(" => ");

		List<String> flags = NewCollection.list();
		for (char f : command.getCharacterFlags()) {
			flags.add(command.getFlagMetadata(f).getName()); // does not swallow
		}
		return builder.append(flags).toString();
	}

	private String toString(final ShellCommand cmd) {
		CommandMetadata command = factory.getPluginIndex().lookup(cmd.getName()).get();
		ShellStringBuilder builder = new ShellStringBuilder(command.getCommandName());

		Iterator<ShellArgument> iter = cmd.getArguments();
		for (char f : command.addDefaultFlags(cmd.getFlags())) {

			FlagMetadata flag = command.getFlagMetadata(f);
			if (flag == null) {
				builder.appendFlagName("" + f);

			} else {
				builder.appendFlagName(flag.getName());
				for (ShellArgument argument : flag.getShellArguments(iter)) {
					builder.appendArgument(argument);
				}
			}
		}
		return builder.appendSwallowed(cmd.getSwallowed()).toString();
	}
}
