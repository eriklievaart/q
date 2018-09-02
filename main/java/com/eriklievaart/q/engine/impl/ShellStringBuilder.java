package com.eriklievaart.q.engine.impl;

import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.toolkit.lang.api.concurrent.Prototype;
import com.eriklievaart.toolkit.lang.api.str.Str;

@Prototype
public class ShellStringBuilder {

	private final StringBuilder builder;

	public ShellStringBuilder() {
		builder = new StringBuilder();
	}

	public ShellStringBuilder(final String name) {
		builder = new StringBuilder(name);
	}

	public ShellStringBuilder appendFlagName(final String name) {
		builder.append(" -").append(name);
		return this;
	}

	public ShellStringBuilder appendArgument(final ShellArgument argument) {
		builder.append(" ");

		switch (argument.getType()) {

		case VARIABLE:
			builder.append('$').append(argument.getValue());
			break;

		case STRING:
			builder.append("`").append(argument.getValue()).append("`");
			break;

		default:
			throw new IllegalArgumentException(Str.sub("unknown type: %", argument.getType()));
		}
		return this;
	}

	public ShellStringBuilder appendSwallowed(final String swallowed) {
		if (swallowed != null) {
			builder.append("|").append(swallowed);
		}
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
