package com.eriklievaart.q.engine.exception;

/**
 * An Exception used to indicate that a line on the command line shell could not be parsed.
 *
 * @author Erik Lievaart
 */
public class ShellParseException extends ShellException {

	public ShellParseException(final String message) {
		super(message);
	}

	public ShellParseException(final String format, final Object... args) {
		super(format, args);
	}

	public static void on(final boolean b, final String format, final Object... args) throws ShellParseException {
		if (b) {
			throw new ShellParseException(format, args);
		}
	}

	public static void unless(final boolean b, final String format, final Object... args) throws ShellParseException {
		if (!b) {
			throw new ShellParseException(format, args);
		}
	}
}
