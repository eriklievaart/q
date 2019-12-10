package com.eriklievaart.q.engine.parse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

/**
 * An parsed representation of a command to the {@link org.q.app.engine.impl.Engine}. The command is guaranteed to be
 * syntactically, but not necessarily semantically valid (although the swallowed content might not be syntactically
 * correct). The command and its flags might not exist. This Object is immutable.
 *
 * @author Erik Lievaart
 */
@Immutable
public class ShellCommand {

	private final String name;
	private final String swallowed;
	private final ShellArgument[] arguments;
	private final char[] flags;

	ShellCommand(String command, List<Character> flags, List<ShellArgument> arguments, String swallowed) {
		this.name = command;
		this.flags = ArrayUtils.toPrimitive(flags.toArray(new Character[] {}));
		this.arguments = arguments.toArray(new ShellArgument[] {});
		this.swallowed = swallowed;
	}

	/**
	 * Get the supplied command name. This might be a partial name.
	 *
	 * @return the command.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get an Iterator of the arguments passed to the command.
	 *
	 * @return all explicit arguments.
	 */
	public Iterator<ShellArgument> getArguments() {
		return Arrays.asList(arguments).iterator();
	}

	/**
	 * Get an array of flags set for the command.
	 *
	 * @return The flags are returned in specification order.
	 */
	public char[] getFlags() {
		return ArrayUtils.clone(flags);
	}

	/**
	 * Get any content swallowed by the command.
	 *
	 * @return the raw swallowed content.
	 */
	public String getSwallowed() {
		return swallowed;
	}

	/**
	 * Return a boolean whether or not this ShellCommand has specified any swallowed content.
	 *
	 * @return true or false.
	 */
	public boolean hasSwallowed() {
		return swallowed != null;
	}

	/**
	 * Return a boolean whether or not this ShellCommand was invoked without any swallowed content.
	 *
	 * @return true or false.
	 */
	public boolean isInvocable() {
		return !hasSwallowed();
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", name);
	}
}