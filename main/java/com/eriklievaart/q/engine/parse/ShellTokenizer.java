package com.eriklievaart.q.engine.parse;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Stateless;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Splits an input string into raw unprocessed token Strings.
 *
 * @author Erik Lievaart
 */
@Stateless
class ShellTokenizer {

	private static final String SEPARATORS = "$@`|-"; // note: not a separator: '\'

	private final List<String> tokens = NewCollection.list();
	private final String input;
	private final int lastIndex;
	private int tokenStart = 0;
	private int tokenEnd = 0;

	/**
	 * Constructor. Calling the constructor parses the content and will result in an IllegalArgumentException when
	 * invoked with content that does not fulfill very basic syntax requirements.
	 *
	 * @param input
	 *            line of text to parse.
	 */
	private ShellTokenizer(final String input) {
		assert Str.notEmpty(input);
		this.input = input;
		lastIndex = input.length() - 1;
		createTokens();
	}

	static List<String> rawTokens(final String input) {
		return new ShellTokenizer(input).tokens;
	}

	private boolean tokenStartsWith(final char c) {
		return input.charAt(tokenStart) == c;
	}

	private boolean tokenEndsWith(final char c) {
		return input.charAt(tokenEnd) == c;
	}

	private List<String> createTokens() {

		while (getNextTokenStart() < input.length()) {
			if (tokenStartsWith('|')) {
				extractSwallowedContent();
				return tokens;
			}
			if (tokenStartsWith('`')) {
				extractQuotedString();
				continue;
			}
			extractSimpleContent();
		}
		return tokens;
	}

	private int getNextTokenStart() {
		while (tokenStart < input.length() && tokenStartsWith(' ')) {
			tokenEnd = ++tokenStart;
		}
		return tokenStart;
	}

	private void extractSimpleContent() {
		do {
			tokenEnd++;
		} while (tokenEnd <= lastIndex && !SEPARATORS.contains("" + input.charAt(tokenEnd)));

		tokens.add(input.substring(tokenStart, tokenEnd).trim());
		tokenStart = tokenEnd;
	}

	private void extractSwallowedContent() {
		tokens.add("|");
		tokens.add(input.substring(tokenEnd + 1));
	}

	private void extractQuotedString() {
		do {
			tokenEnd++;
		} while (tokenEnd < lastIndex && !tokenEndsWith('`'));

		tokens.add(input.substring(tokenStart, Math.min(lastIndex + 1, tokenEnd + 1)));
		tokenStart = ++tokenEnd;
	}
}