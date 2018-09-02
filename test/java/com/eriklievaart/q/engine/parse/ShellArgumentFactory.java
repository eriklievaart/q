package com.eriklievaart.q.engine.parse;

public class ShellArgumentFactory {

	public static ShellArgument string(final String value) {
		return ShellArgument.string(value);
	}

	public static ShellArgument variable(final String name) {
		return ShellArgument.variable(name);
	}
}
