package com.eriklievaart.q.engine.meta;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

@Immutable
public class FlagGroupMetadata {

	private final List<FlagMetadata> flags;
	private final FlagMetadata primary;
	private final String group;

	public FlagGroupMetadata(final String group, final List<FlagMetadata> flags, final FlagMetadata primary) {
		this.group = group;
		this.flags = flags;
		this.primary = primary;

		checkAssertionExceptions();
	}

	private void checkAssertionExceptions() {
		Check.notNull(group);
		if (primary != null) {
			CheckCollection.isPresent(flags, primary, "Group % does not contain primary flag %", group, primary);
		}
	}

	List<FlagMetadata> getAllFlags() {
		return flags;
	}

	boolean containsFlag(final char c) {
		for (FlagMetadata item : flags) {
			if (item.getFlag() == c) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return group;
	}

	boolean hasPrimaryFlag() {
		return primary != null;
	}

	Character getPrimaryFlag() {
		return primary.getFlag();
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", group);
	}
}