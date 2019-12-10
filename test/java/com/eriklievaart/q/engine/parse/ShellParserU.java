package com.eriklievaart.q.engine.parse;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import com.eriklievaart.q.engine.exception.ShellParseException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;

public class ShellParserU {

	@Test
	public void simpleCommand() {
		verify("cmd", new ShellCommandFactory("cmd"));
	}

	@Test
	public void emptyCommand() {
		verify("", ShellCommandFactory.parseException());
	}

	@Test
	public void singleFlag() {
		verify("cmd -f", new ShellCommandFactory("cmd").flags('f'));
	}

	@Test
	public void doubleFlag() {
		verify("cmd -ab", new ShellCommandFactory("cmd").flags('a', 'b'));
	}

	@Test
	public void doubleFlagCase() {
		verify("CMD -AB", new ShellCommandFactory("cmd").flags('a', 'b'));
	}

	@Test
	public void invalidFlag() {
		verify("cmd -a^", ShellCommandFactory.parseException());
	}

	@Test
	public void simpleSwallow() {
		verify("command|swallow", new ShellCommandFactory("command").swallow("swallow"));
	}

	@Test
	public void swallowCase() {
		verify("command|SWALLOW", new ShellCommandFactory("command").swallow("SWALLOW"));
	}

	@Test
	public void swallowNoCommand() {
		verify("|swallow", ShellCommandFactory.parseException());
	}

	@Test
	public void simpleString() {
		verify("echo `hello world` ", new ShellCommandFactory("echo").strings("hello world"));
	}

	@Test
	public void emptyString() {
		verify("echo ``", new ShellCommandFactory("echo").strings(""));
	}

	@Test
	public void simpleVariable() {
		verify("echo $var ", new ShellCommandFactory("echo").variables("var"));
	}

	@Test
	public void variableBannedChars() {
		verify("echo $12_", ShellCommandFactory.parseException());
	}

	@Test
	public void variableSpecialChars() {
		verify("echo $12~", new ShellCommandFactory("echo").variables("12~"));
	}

	@Test
	public void emptyVariable() {
		verify("echo $", new ShellCommandFactory("echo").variables(""));
	}

	@Test
	public void bookmarkNoCommand() {
		verify("@", ShellCommandFactory.parseException());
	}

	@Test
	public void complete() {
		verify("native -t `str` |ls -al",
				new ShellCommandFactory("native").flags('t').strings("str").swallow("ls -al"));
	}

	public static void verify(final String input, final ShellCommandFactory builder) {
		try {
			ShellCommand actual = ShellParser.parseLine(input);
			ShellCommand expected = builder.make();

			Check.isEqual(actual.getName(), expected.getName());
			Check.isEqual(Arrays.toString(actual.getFlags()), Arrays.toString(expected.getFlags()));
			CheckCollection.isEqual(actual.getArguments(), expected.getArguments());
			Check.isEqual(actual.getSwallowed(), expected.getSwallowed());

			Check.isTrue(EqualsBuilder.reflectionEquals(actual, expected));

			if (builder.expectError()) {
				throw new RuntimeException("Exception Expected");
			}

		} catch (ShellParseException spe) {
			if (!builder.expectError()) {
				throw new RuntimeException("Unexpected Parse Exception", spe);
			}
		}
	}
}