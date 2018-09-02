package com.eriklievaart.q.engine.parse;

/**
 * Enumeration of the possible types of arguments a Q ShellCommand will accept.
 *
 * @author Erik Lievaart
 */
public enum ShellArgumentType {
	/** A String literal. */
	STRING,
	/** A Q variable. */
	VARIABLE;
}
