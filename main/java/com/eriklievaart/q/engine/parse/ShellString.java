package com.eriklievaart.q.engine.parse;

import java.util.Map;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.StringEscape;

/**
 * Utility class for (un)escaping strings (separated by `) on the Q command line.
 *
 * @author Erik Lievaart
 */
public class ShellString {

	private static final StringEscape SHELL_STRING = new StringEscape(escapeMapping());

	private ShellString() {
	}

	private static Map<Character, Character> escapeMapping() {
		Map<Character, Character> map = NewCollection.map();

		map.put('b', '\\');
		map.put('q', '`');

		return map;
	}

	/**
	 * Check if the string has any invalid escape sequences.
	 *
	 * @param raw
	 *            body of a string.
	 * @return true iff any escape sequences were found to be invalid.
	 */
	public static boolean hasInvalidEscapeSequence(final String raw) {
		return SHELL_STRING.hasInvalidEscapeSequence(raw);
	}

	/**
	 * Escapes all occurrences of command line escape characters in a String.
	 *
	 * @param input
	 *            String to escape.
	 * @return a String with all escape characters replaced with a backslash '\' and a control character.
	 */
	public static String escape(final String input) {
		return SHELL_STRING.escape(input);
	}

	static StringBuilder escape(final StringBuilder builder) {
		return SHELL_STRING.escape(builder);
	}

	/**
	 * Replaces all escape sequences in a String with the original character. All escape sequences in the String are
	 * assumed to be valid. For invalid escape sequences the result is unspecified, an Exception might be thrown.
	 *
	 * @param input
	 *            String that contains escape sequences.
	 * @return a String with all escape sequences replaced with the original character.
	 */
	public static String unescape(final String input) {
		return SHELL_STRING.unescape(input);
	}
}