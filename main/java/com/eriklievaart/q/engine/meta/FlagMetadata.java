package com.eriklievaart.q.engine.meta;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

/**
 * The meta data of a single flag of a plugin. This class is immutable.
 *
 * @author Erik Lievaart
 */
@Immutable
public class FlagMetadata {

	private final Character flag;
	private final String name;
	private final int arguments;
	private final List<ShellArgument> values;

	public FlagMetadata(final String name, final List<ShellArgument> values) {

		Check.notBlank(name);

		this.name = name;
		this.flag = name.charAt(0);
		this.arguments = values.size();
		this.values = ListTool.unmodifiableCopy(values);
	}

	/**
	 * Get the full name of the flag. This is only relevant for documentation/feedback purposes.
	 */
	public String getName() {
		return name;
	}

	Character getFlag() {
		return flag;
	}

	/**
	 * Return the required amount of default {@link ShellArgument}'s optionally overriding them.
	 *
	 * @param override
	 *            Explicit arguments.
	 * @return For every argument of this flag take one from the iterator. If the Iterator is depleted, use the default
	 *         argument instead.
	 */
	public ShellArgument[] getShellArguments(final Iterator<ShellArgument> override) {
		ShellArgument[] array = new ShellArgument[values.size()];
		for (int i = 0; i < arguments; i++) {
			ShellArgument next = override.hasNext() ? override.next() : ShellArgument.DEFAULT;
			array[i] = next.isDefault() ? values.get(i) : next;
		}
		return array;
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
		return ToString.simple(this, "$[$]", name);
	}
}
