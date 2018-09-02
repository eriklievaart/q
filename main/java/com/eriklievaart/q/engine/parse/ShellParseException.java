package com.eriklievaart.q.engine.parse;

import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * An Exception used to indicate that a line on the command line shell could not be parsed.
 * 
 * @author Erik Lievaart
 */
public class ShellParseException extends Exception {

	ShellParseException(final String message) {
		super(message);
	}

	ShellParseException(final String format, final Object... args) {
		super(Str.sub(format, args));
	}

	static void on(final boolean b, final String format, final Object... args) throws ShellParseException {
		if (b) {
			throw new ShellParseException(format, args);
		}
	}

	static void unless(final boolean b, final String format, final Object... args) throws ShellParseException {
		if (!b) {
			throw new ShellParseException(format, args);
		}
	}

}
