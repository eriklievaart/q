package com.eriklievaart.q.ui.main;

import java.util.List;

import org.junit.Test;

import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;

public class UiControllerU {

	@Test
	public void createUrlString() throws Exception {
		MemoryFileSystem memory = new MemoryFileSystem();

		List<VirtualFileWrapper> wrappers = NewCollection.list();
		wrappers.add(new VirtualFileWrapper(memory.resolve("/tmp/a")));
		wrappers.add(new VirtualFileWrapper(memory.resolve("/tmp/b")));

		String urls = BrowserActions.createUrlString(wrappers);
		Check.isEqual(urls, "mem:///tmp/a mem:///tmp/b");
	}
}
