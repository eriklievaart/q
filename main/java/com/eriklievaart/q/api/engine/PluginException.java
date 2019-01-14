package com.eriklievaart.q.api.engine;

import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Exception used to signal that a ShellCommand's configuration is not valid.
 *
 * @author Erik Lievaart
 */
public class PluginException extends Exception {

	/**
	 * Constructor with message.
	 *
	 * @param message
	 *            Message to the user why the command is not valid.
	 */
	public PluginException(final String message) {
		super(message);
	}

	/**
	 * Constructor with message and cause.
	 *
	 * @param message
	 *            Message to the user why the command is not valid.
	 * @param cause
	 *            Cause of the ValidationException.
	 */
	public PluginException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Shorthand for checking a boolean flag and throwing a ValidationException if the flag is true.
	 *
	 * @param condition
	 *            throw a ValidationException if condition evaluates to true.
	 * @param format
	 *            Template for the message with '%' as place holders.
	 * @param args
	 *            substitution arguments for the '%' symbols in the format.
	 * @throws PluginException
	 *             if condition evaluates to true.
	 */
	public static void on(final boolean condition, final String format, final Object... args)
			throws PluginException {

		if (condition) {
			throw new PluginException(Str.sub(format, args));
		}
	}

	/**
	 * Shorthand for checking a boolean flag and throwing a ValidationException if the flag is false.
	 *
	 * @param condition
	 *            throw a ValidationException if condition evaluates to false.
	 * @param format
	 *            Template for the message with '%' as place holders.
	 * @param args
	 *            substitution arguments for the '%' symbols in the format.
	 * @throws PluginException
	 *             if condition evaluates to false.
	 */
	public static void unless(final boolean condition, final String format, final Object... args)
			throws PluginException {

		if (!condition) {
			throw new PluginException(Str.sub(format, args));
		}
	}
}
