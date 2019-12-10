package com.eriklievaart.q.engine.parse;

import org.junit.Test;

import com.eriklievaart.q.engine.exception.ShellParseException;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ShellArgumentU {

	@Test
	public void ofRawVariable() throws ShellParseException {
		ShellArgument argument = ShellParser.parseArgument("$var");
		Check.isTrue(argument.getType() == ShellArgumentType.VARIABLE, "not parsed as variable: %", argument);
		Check.isEqual(argument.getValue(), "var");
	}

	@Test
	public void ofRawLiteral() throws ShellParseException {
		ShellArgument argument = ShellParser.parseArgument("`test`");
		Check.isTrue(argument.getType() == ShellArgumentType.STRING, "not parsed as string: %", argument);
		Check.isEqual(argument.getValue(), "test");
	}

	@Test
	public void ofRawLiteralEscaped() throws ShellParseException {
		ShellArgument argument = ShellParser.parseArgument("`test\\b`");
		Check.isTrue(argument.getType() == ShellArgumentType.STRING, "Not parsed as string: %", argument);
		Check.isEqual(argument.getValue(), "test\\");
	}
}