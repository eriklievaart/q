package com.eriklievaart.q.engine.parse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * A parsed representation of a single argument passed to a ShellCommand. The argument is guaranteed to be
 * syntactically, but not necessarily semantically valid. For instance, the variable reference might not exist. This
 * Object is immutable.
 *
 * @author Erik Lievaart
 */
@Immutable
public class ShellArgument {
	/** Instance to use when the default value should be used for the argument. */
	public static final ShellArgument DEFAULT = variable("");

	private final String value;
	private final ShellArgumentType type;

	private ShellArgument(final String value, final ShellArgumentType type) {
		this.value = value;
		this.type = type;
	}

	static ShellArgument variable(final String name) {
		return new ShellArgument(name, ShellArgumentType.VARIABLE);
	}

	static ShellArgument string(final String name) {
		return new ShellArgument(name, ShellArgumentType.STRING);
	}

	/**
	 * Get the value of the argument.
	 *
	 * @return The String contents, the variable name or the bookmark name depending on the type.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get the {@link ShellArgumentType}.
	 *
	 * @return the type of data contained in this argument.
	 */
	public ShellArgumentType getType() {
		return type;
	}

	/**
	 * Get a flag indicating if this is a default variable, or if the value of the ShellArgument should be used.
	 *
	 * @return true iff the default variable should be used instead of the value.
	 */
	public boolean isDefault() {
		return type == ShellArgumentType.VARIABLE && Str.isEmpty(value);
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
		return ToString.simple(this, "$[$:$]", type, value);
	}
}
