package com.eriklievaart.q.engine.parse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class ShellLexerU {

	public static void main(final String[] args) {
		Token echo = new Token("echo", TokenType.COMMAND);
		Token string = new Token("`hello world`", TokenType.STRING);
		checkTokens("echo -u \"err\" ", echo, string);

	}

	@Test
	public void tokenizeMultipleTrim() {
		checkTokens("echo `h w` ", new Token("echo", TokenType.COMMAND), new Token("h w", TokenType.STRING));
	}

	@Test
	public void tokenizeWhitespace() {
		checkTokens("   ");
	}

	@Test
	public void commandTokenValid() {
		checkTokens("command", new Token("command", TokenType.COMMAND));
	}

	@Test
	public void commandTokenInvalid() {
		checkTokens("/command", new Token("/command", TokenType.UNRECOGNIZED));
	}

	@Test
	public void variableTokenValid() {
		checkTokens("$file", new Token("file", TokenType.VARIABLE));
	}

	@Test
	public void variableTokenDefault() {
		checkTokens("$", new Token("", TokenType.VARIABLE));
	}

	@Test
	public void variableTokenNumeric() {
		checkTokens("$file1", new Token("file1", TokenType.VARIABLE));
	}

	@Test
	public void variableTokenUnderscore() {
		checkTokens("$12_", new Token("$12_", TokenType.UNRECOGNIZED));
	}

	@Test
	public void variableTokenTilde() {
		checkTokens("$12~", new Token("12~", TokenType.VARIABLE));
	}

	@Test
	public void variableTokenDot() {
		checkTokens("$book.media", new Token("$book.media", TokenType.UNRECOGNIZED));
	}

	@Test
	public void variableTokenUpperCase() {
		checkTokens("$FILE", new Token("file", TokenType.VARIABLE));
	}

	@Test
	public void variableTokenInvalid() {
		checkTokens("$fi'le", new Token("$fi'le", TokenType.UNRECOGNIZED));
	}

	@Test
	public void bookmarkInvalid() {
		checkTokens("@fi'le", new Token("@fi'le", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeDoubleQuotedString() {
		checkTokens("\"quotes\"", new Token("\"quotes\"", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeString() {
		checkTokens("`quotes`", new Token("quotes", TokenType.STRING));
	}

	@Test
	public void tokenizeStringEscape() {
		checkTokens("`need\\b\\qescapes`", new Token("need\\`escapes", TokenType.STRING));
	}

	@Test
	public void tokenizeInvalidEscapeSequence() {
		checkTokens("`\\,.`", new Token("`\\,.`", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeUnclosedString() {
		checkTokens("`missing quote", new Token("`missing quote", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeUnclosedQuote() {
		checkTokens("`", new Token("`", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeSwallowSomething() {
		checkTokens("|something", new Token("|", TokenType.SWALLOW), new Token("something", TokenType.RAW));
	}

	@Test
	public void tokenizeSwallowSpace() {
		checkTokens("| space ", new Token("|", TokenType.SWALLOW), new Token(" space ", TokenType.RAW));
	}

	@Test
	public void tokenizeSwallowNothing() {
		checkTokens("|", new Token("|", TokenType.SWALLOW), new Token("", TokenType.RAW));
	}

	@Test
	public void tokenizeFlagMissing() {
		checkTokens("-", new Token("-", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeFlagInvalid() {
		checkTokens("-^", new Token("^", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeFlagInvalidLong() {
		checkTokens("-a ^", new Token("a", TokenType.FLAG), new Token(" ^", TokenType.UNRECOGNIZED));
	}

	@Test
	public void tokenizeFlagValid() {
		checkTokens("-v", new Token("v", TokenType.FLAG));
	}

	@Test
	public void tokenizeFlagDouble() {
		checkTokens("-ab", new Token("a", TokenType.FLAG), new Token("b", TokenType.FLAG));
	}

	@Test
	public void tokenizeStringSwallow() {
		checkTokens("`string`|", new Token("string", TokenType.STRING), new Token("|", TokenType.SWALLOW),
				new Token("", TokenType.RAW));
	}

	@Test
	public void tokenizeCommandWithArgumentWhitespace() {
		checkTokens(" command -a ", new Token("command", TokenType.COMMAND), new Token("a", TokenType.FLAG));
	}

	@Test
	public void tokenizeCommandWithArgumentNoWhitespace() {
		checkTokens("command-a", new Token("command", TokenType.COMMAND), new Token("a", TokenType.FLAG));
	}

	@Test
	public void tokenizeSwallowAdvanced() {
		checkTokens("swallow| $@`|-\"a ", new Token("swallow", TokenType.COMMAND), new Token("|", TokenType.SWALLOW),
				new Token(" $@`|-\"a ", TokenType.RAW)); // note: no trimming
	}

	@Test
	public void tokenizeComplex() {
		String input = "command-ab-c`str`$var`@str`|raw$ -`\"";

		checkTokens(input, new Token("command", TokenType.COMMAND), new Token("a", TokenType.FLAG),
				new Token("b", TokenType.FLAG), new Token("c", TokenType.FLAG), new Token("str", TokenType.STRING),
				new Token("var", TokenType.VARIABLE), new Token("@str", TokenType.STRING),
				new Token("|", TokenType.SWALLOW), new Token("raw$ -`\"", TokenType.RAW));
	}

	@Test
	public void tokenizeComplexSpaces() {
		String input = " command -ab -c `str` $var `@str ` | raw$ -`\" ";

		checkTokens(input, new Token("command", TokenType.COMMAND), new Token("a", TokenType.FLAG),
				new Token("b", TokenType.FLAG), new Token("c", TokenType.FLAG), new Token("str", TokenType.STRING),
				new Token("var", TokenType.VARIABLE), new Token("@str ", TokenType.STRING),
				new Token("|", TokenType.SWALLOW), new Token(" raw$ -`\" ", TokenType.RAW));
	}

	private static void checkTokens(final String input, final Token... expected) {
		List<Token> tokens = ShellLexer.tokens(input);
		Check.isTrue(tokens.size() == expected.length, "expected: " + Arrays.toString(expected) + " got: " + tokens);
		Check.isEqual(tokens, Arrays.asList(expected));
	}
}
