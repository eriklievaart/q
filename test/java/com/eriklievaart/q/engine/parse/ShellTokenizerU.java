package com.eriklievaart.q.engine.parse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class ShellTokenizerU {

	@Test
	public void tokenizeSingle() {
		checkTokens("command", "command");
	}

	@Test
	public void tokenizeArgumentWhitespace() {
		checkTokens(" command -argument ", "command", "-argument");
	}

	@Test
	public void tokenizeArgumentNoWhitespace() {
		checkTokens("command-argument", "command", "-argument");
	}

	@Test
	public void tokenizeString() {
		checkTokens("`quotes`", "`quotes`");
	}

	@Test
	public void tokenizeUnclosedString() {
		checkTokens("`missing quote", "`missing quote");
	}

	@Test
	public void tokenizeUnclosedString2() {
		checkTokens("`", "`");
	}

	@Test
	public void tokenizeTrailingSpace() {
		checkTokens("echo `hello world` ", "echo", "`hello world`");
	}

	@Test
	public void tokenizeSwallowNoTrimming() {
		checkTokens("swallow| $@`|:-\"a ", "swallow", "|", " $@`|:-\"a "); // note: no trimming
	}

	@Test
	public void tokenizeComplex() {
		String input = "command-arg`@str`$var@book|raw$ -`\"";
		checkTokens(input, "command", "-arg", "`@str`", "$var", "@book", "|", "raw$ -`\"");
	}

	@Test
	public void tokenizeComplexSpaces() {
		String input = "command -arg `str` $var @book `@str ` | raw$ -`\" ";
		checkTokens(input, "command", "-arg", "`str`", "$var", "@book", "`@str `", "|", " raw$ -`\" ");
	}

	private static void checkTokens(final String input, final String... expected) {
		List<String> tokens = ShellTokenizer.rawTokens(input);
		Check.isTrue(tokens.size() == expected.length, "expected: " + Arrays.toString(expected) + " got: " + tokens);
		Check.isEqual(tokens, Arrays.asList(expected));
	}

}
