package com.eriklievaart.q.engine.meta;

import java.util.Arrays;
import java.util.Map;

import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Stateless;
import com.eriklievaart.toolkit.lang.api.str.Str;

@Stateless
public class MetadataValidator {

	private MetadataValidator() {
	}

	public static void validate(final ShellCommand command, final CommandMetadata meta) throws PluginException {
		checkName(command.getName(), meta);
		checkIsRunnable(command.isInvocable(), meta);
		checkSwallows(command.hasSwallowed(), meta);
		checkFlags(command.getFlags(), meta);
	}

	private static void checkName(final String name, final CommandMetadata meta) throws PluginException {
		boolean check = meta == null || !meta.getCommandName().startsWith(name);
		PluginException.on(check, "Unrecognized command: %", name);
	}

	private static void checkIsRunnable(final boolean runnable, final CommandMetadata meta) throws PluginException {
		CallPolicy policy = meta.getPlugin().getCallPolicy();
		boolean isRunnable = policy == CallPolicy.FLAGS_ONLY || policy == CallPolicy.BOTH;
		PluginException.on(runnable && !isRunnable, "Swallowed expected for %!", meta.getCommandName());
	}

	private static void checkSwallows(final boolean swallowed, final CommandMetadata meta) throws PluginException {
		CallPolicy policy = meta.getPlugin().getCallPolicy();
		boolean swallows = policy == CallPolicy.PIPED || policy == CallPolicy.BOTH;
		PluginException.on(swallowed && !swallows, "% does not accept piped input!", meta.getCommandName());
	}

	private static void checkFlags(final char[] flags, final CommandMetadata meta) throws PluginException {
		checkFlagsExist(flags, meta);
		checkMutexFlags(flags, meta);
	}

	private static void checkFlagsExist(final char[] flags, final CommandMetadata meta) throws PluginException {
		char[] metaFlags = meta.getCharacterFlags();

		for (char f : flags) {
			PluginException.on(Arrays.binarySearch(metaFlags, f) < 0, "Unknown flag %", f);
		}
	}

	private static void checkMutexFlags(final char[] flags, final CommandMetadata meta) throws PluginException {
		Map<String, Character> groups = NewCollection.map();

		for (char f : flags) {
			String group = meta.getGroup(f);
			boolean duplicate = Str.notBlank(group) && groups.containsKey(group);
			PluginException.on(duplicate, "Mutually exclusive flags: % %", groups.get(group), f);
			groups.put(group, f);
		}
	}
}