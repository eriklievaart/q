package com.eriklievaart.q.engine.meta;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class CommandMetadata {

	private final QPlugin plugin;
	private final List<FlagGroupMetadata> groups;
	private final Map<Character, FlagMetadata> flags;

	public CommandMetadata(QPlugin plugin, List<FlagGroupMetadata> groups) {
		Check.notNull(plugin, groups);
		Check.notNull(plugin.getThreadPolicy());
		Check.notBlank(plugin.getCommandName(), "Command must have a name: $", plugin.getClass());

		this.plugin = plugin;
		this.groups = groups;
		this.flags = createFlagMap(groups);
	}

	private Map<Character, FlagMetadata> createFlagMap(final List<FlagGroupMetadata> metadata) {
		Map<Character, FlagMetadata> map = new Hashtable<>();
		for (FlagGroupMetadata group : metadata) {
			for (FlagMetadata item : group.getAllFlags()) {
				map.put(item.getFlag(), item);
			}
		}
		return MapTool.unmodifiableCopy(map);
	}

	public QPlugin getPlugin() {
		return plugin;
	}

	public String getGroup(final char flag) {
		for (FlagGroupMetadata group : groups) {
			if (group.containsFlag(flag)) {
				return group.getName();
			}
		}
		return null;
	}

	/**
	 * Return the meta data of one of this command's flags.
	 *
	 * @param flag
	 *            Character identifying the flag.
	 * @return The FlagMetadata.
	 */
	public FlagMetadata getFlagMetadata(final char flag) {
		return flags.get(flag);
	}

	/**
	 * List the flags available for this plugin.
	 *
	 * @return the letter of the flags only.
	 */
	public char[] getCharacterFlags() {
		char[] chars = ArrayUtils.toPrimitive(this.flags.keySet().toArray(new Character[] {}));
		Arrays.sort(chars);
		return chars;
	}

	/**
	 * Returns the flags specified in the {@link ShellCommand} complemented with any flags that are on by default.
	 *
	 * @return the letter of the flags only.
	 */
	public char[] addDefaultFlags(char[] specified) {
		List<Character> result = NewCollection.list();
		Set<String> groupsSpecified = NewCollection.set();

		for (char c : specified) {
			result.add(c);
			String group = getGroup(c);
			if (Str.notBlank(group)) {
				groupsSpecified.add(group);
			}
		}
		for (FlagGroupMetadata group : groups) {
			if (!groupsSpecified.contains(group.getName()) && group.hasPrimaryFlag()) {
				result.add(group.getPrimaryFlag());
			}
		}
		return ArrayUtils.toPrimitive(result.toArray(new Character[] {}));
	}

	/**
	 * Returns whether or not a flag is primary.
	 */
	public boolean isPrimary(final char flag) {
		for (FlagGroupMetadata group : this.groups) {
			if (group.hasPrimaryFlag() && group.getPrimaryFlag() == flag) {
				return true;
			}
		}
		return false;
	}

	public String getCommandName() {
		return plugin.getCommandName();
	}

	public String getFlagString() {
		return flags.values().stream().map(m -> m.getName()).collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", plugin.getCommandName());
	}
}
