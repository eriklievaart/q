package com.eriklievaart.q.engine.parse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Stateless;
import com.eriklievaart.toolkit.lang.api.pattern.PatternTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Verifies that raw tokens are syntactically correct, replaces escape characters, shorthands and Classifies tokens.
 * Running the ShellLexer results in a stream of {@link Token} instances. This class is not supposed to throw
 * Exceptions, but instead is expected to return tokens of TokenType UNRECOGNIZED, when lexical analysis fails.
 * Reasoning: unlike the parser, the lexer is unaware of the context in which a token occurs. The parser is more capable
 * of providing meaningful messages.
 *
 * @author Erik Lievaart
 */
@Stateless
class ShellLexer {

	private ShellLexer() {
	}

	public static List<Token> tokens(final String input) throws IllegalArgumentException {
		if (Str.isEmpty(input)) {
			return Arrays.asList(new Token(input, TokenType.UNRECOGNIZED));
		}
		List<Token> result = NewCollection.list();
		Iterator<String> iter = ShellTokenizer.rawTokens(input).iterator();

		convertRawTokens(iter, result);
		return result;
	}

	private static void convertRawTokens(final Iterator<String> iter, final List<Token> result) {
		while (iter.hasNext()) {
			String raw = iter.next();
			char head = raw.charAt(0);

			switch (head) {
			case '|':
				result.addAll(swallowTokens(raw, iter));
				return;
			case '-':
				result.addAll(flagTokens(raw));
				break;
			default:
				result.add(convertSingleToken(head, raw));
			}
		}
	}

	private static Token convertSingleToken(final char head, final String raw) {
		switch (head) {

		case '$':
			return variableToken(raw);
		case '`':
			return convertString(raw);
		}
		return commandToken(raw);
	}

	private static List<? extends Token> flagTokens(final String raw) {
		if (raw.length() == 1) {
			return Arrays.asList(new Token(raw, TokenType.UNRECOGNIZED));
		}
		List<Token> flags = NewCollection.list();
		for (int i = 1; i < raw.length(); i++) {
			if (!Character.isLetter(raw.charAt(i))) {
				flags.add(new Token(raw.substring(i), TokenType.UNRECOGNIZED));
				break;
			}
			flags.add(new Token(raw.substring(i, i + 1), TokenType.FLAG));
		}
		return flags;
	}

	private static Token convertString(final String raw) {
		if (!raw.endsWith("`") || raw.length() == 1 || ShellString.hasInvalidEscapeSequence(raw)) {
			return new Token(raw, TokenType.UNRECOGNIZED);
		}
		return convertString(new StringBuilder(raw).deleteCharAt(0).deleteCharAt(raw.length() - 2));
	}

	private static Token convertString(final StringBuilder builder) {
		return new Token(ShellString.unescape(builder.toString()), TokenType.STRING);
	}

	private static List<Token> swallowTokens(final String raw, final Iterator<String> iter) {
		assert raw.length() == 1 : "Swallow token should be exactly 1 character: " + Str.quote(raw);
		List<Token> tokens = FromCollection.toList(new Token(raw, TokenType.SWALLOW));

		String swallow = iter.hasNext() ? iter.next() : "";
		tokens.add(new Token(swallow, TokenType.RAW));
		Check.isFalse(iter.hasNext(), "There cannot be anything trailing a command that swallows the whole line");
		return tokens;
	}

	private static Token variableToken(final String raw) {
		if (PatternTool.matches("[$][~a-zA-Z0-9]*+", raw)) {
			return new Token(raw.substring(1).toLowerCase(), TokenType.VARIABLE);
		}
		return new Token(raw, TokenType.UNRECOGNIZED);
	}

	private static Token commandToken(final String raw) {
		TokenType type = PatternTool.matches("[a-zA-Z]++", raw) ? TokenType.COMMAND : TokenType.UNRECOGNIZED;
		return new Token(raw, type);
	}

}
