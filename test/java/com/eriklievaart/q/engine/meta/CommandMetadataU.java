package com.eriklievaart.q.engine.meta;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class CommandMetadataU {

	@Test
	public void addDefaultFlagsExplicit() {
		CommandMetadata meta = new CommandMetadataFactory("new").mutexFlag("flag").mutexFlag("test").make();
		char[] flags = meta.addDefaultFlags(new char[] { 'f' });
		Check.isTrue(flags[0] == 'f');
		Check.isTrue(flags.length == 1);
	}

	@Test
	public void addDefaultFlagsPrimary() {
		CommandMetadata meta = new CommandMetadataFactory("new").primaryMutexFlag("test").make();
		char[] flags = meta.addDefaultFlags(new char[] {});
		Check.isTrue(flags[0] == 't');
		Check.isTrue(flags.length == 1);
	}

	@Test
	public void addDefaultFlagsAddPrimaryToOtherFlag() {
		CommandMetadata meta = new CommandMetadataFactory("ant").primaryFlag("copy").flag("includes").make();
		char[] flags = meta.addDefaultFlags(new char[] { 'i' });

		Check.isTrue(flags[0] == 'i');
		Check.isTrue(flags[1] == 'c');
		Check.isTrue(flags.length == 2);
	}
}