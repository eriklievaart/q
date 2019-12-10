package com.eriklievaart.q.engine.parse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;
import com.eriklievaart.toolkit.lang.api.str.Str;

@Immutable
class Token {

	private final String value;
	private final TokenType type;
	private final String errorMessage;

	public Token(final String value, final TokenType type) {
		this.type = type;
		this.value = value;

		errorMessage = toString();
	}

	public String getValue() {
		return value;
	}

	public TokenType getType() {
		return type;
	}

	public String error() {
		return errorMessage;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return type + "{" + Str.quote(value) + "}";
	}
}