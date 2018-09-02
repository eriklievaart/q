package com.eriklievaart.q.ui.render.browser;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;

public class VirtualFileWrapperU {

	@Test
	public void equals() {
		MemoryFileSystem memory = new MemoryFileSystem();
		VirtualFileWrapper duplicate1 = new VirtualFileWrapper(memory.resolve("duplicate"));
		VirtualFileWrapper duplicate2 = new VirtualFileWrapper(memory.resolve("duplicate"));

		Check.isFalse(duplicate1 == duplicate2);
		Check.isTrue(duplicate1.equals(duplicate2));
	}
}
