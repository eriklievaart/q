package com.eriklievaart.q.engine.parse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class ShellCommandFactory {
	private final String cmd;
	private List<Character> flags = Collections.emptyList();
	private List<ShellArgument> arguments = Collections.emptyList();
	private String swallow = null;
	private final boolean error;

	public ShellCommandFactory(final String command) {
		this(command, false);
	}

	public ShellCommandFactory(final String command, final boolean error) {
		cmd = command;
		this.error = error;
	}

	public static ShellCommandFactory parseException() {
		return new ShellCommandFactory("parse error", true);
	}

	public ShellCommandFactory variables(final String var) {
		Check.isFalse(var.startsWith("$"), "Variable names cannot contain $ %", var);
		arguments = Arrays.asList(ShellArgument.variable(var));
		return this;
	}

	public ShellCommandFactory strings(final String... args) {
		List<ShellArgument> list = NewCollection.list();
		for (String arg : args) {
			list.add(ShellArgument.string(arg));
		}
		arguments = list;
		return this;
	}

	public ShellCommandFactory flags(final char... cs) {
		flags = Arrays.asList(ArrayUtils.toObject(cs));
		return this;
	}

	public ShellCommandFactory swallow(final String raw) {
		swallow = raw;
		return this;
	}

	public ShellCommand make() {
		return new ShellCommand(cmd, flags, arguments, swallow);
	}

	public boolean expectError() {
		return error;
	}
}
