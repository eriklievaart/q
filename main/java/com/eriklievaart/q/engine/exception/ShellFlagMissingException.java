package com.eriklievaart.q.engine.exception;

import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Exception raised when a dash '-' is encountered on the command line without a trailing letter. Flags should always
 * have their letters suffixed to be considered complete.
 *
 * @author Erik Lievaart
 */
public class ShellFlagMissingException extends ShellParseException {

	private final ShellCommand command;

	public ShellFlagMissingException(final ShellCommand command, final String message) {
		super(message);
		this.command = command;
	}

	/**
	 * Get the ShellCommand for which the flag is missing.
	 *
	 * @return the ShellCommand.
	 */
	public ShellCommand getShellCommand() {
		return command;
	}

	public static void on(boolean condition, ShellCommand command, String format, Object... args)
			throws ShellFlagMissingException {
		if (condition) {
			throw new ShellFlagMissingException(command, Str.sub(format, args));
		}
	}
}