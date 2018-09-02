package com.eriklievaart.q.engine.parse;

import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * This Exception signals that the parser was expecting a Q command, but encountered different input instead.
 *
 * @author Erik Lievaart
 */
public class ShellCommandMissingException extends ShellParseException {

	ShellCommandMissingException(final String message) {
		super(message);
	}

	public static void on(final boolean condition, final String format, final Object... args)
			throws ShellCommandMissingException {
		if (condition) {
			throw new ShellCommandMissingException(Str.sub(format, args));
		}
	}

	public static void unless(final boolean condition, final String format, final Object... args)
			throws ShellCommandMissingException {
		if (!condition) {
			throw new ShellCommandMissingException(Str.sub(format, args));
		}
	}
}
