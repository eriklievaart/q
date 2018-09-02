package com.eriklievaart.q.engine.parse;

import java.util.EnumSet;

import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

@Immutable
enum TokenType {
	RAW, SWALLOW, COMMAND, STRING, VARIABLE, UNRECOGNIZED, FLAG;

	private static final EnumSet<TokenType> ARGUMENT_TYPES = EnumSet.of(STRING, VARIABLE);

	boolean isArgument() {
		return ARGUMENT_TYPES.contains(this);
	}
}
