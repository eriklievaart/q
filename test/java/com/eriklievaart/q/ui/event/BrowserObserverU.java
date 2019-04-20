package com.eriklievaart.q.ui.event;

import org.junit.Test;

import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;

public class BrowserObserverU extends SandboxTest {

	@Test
	public void openPrevious() {
		MemoryFileSystem memory = new MemoryFileSystem();
		memory.resolve("/bla").mkdir();
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserObserver observer = new BrowserObserver(components);

		observer.update(memory.resolve("/"));
		observer.update(memory.resolve("/bla"));
		Check.isEqual(components.urlLabel.getText(), "/bla");

		observer.openPrevious();
		Check.isEqual(components.urlLabel.getText(), "/");

		observer.openPrevious();
		Check.isEqual(components.urlLabel.getText(), "/");
	}

	@Test
	public void openPreviousDeleted() {
		systemFile("/bla").mkdir();
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserObserver observer = new BrowserObserver(components);

		observer.update(systemFile("/"));
		observer.update(systemFile("/bla"));
		observer.update(systemFile("/"));
		Check.isEqual(components.urlLabel.getText(), systemFile("").getPath());

		systemFile("/bla").delete();
		observer.openPrevious();
		Check.isEqual(components.urlLabel.getText(), systemFile("").getPath());
	}

	@Test
	public void openNext() {
		MemoryFileSystem memory = new MemoryFileSystem();
		memory.resolve("/bla").mkdir();
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserObserver observer = new BrowserObserver(components);

		observer.update(memory.resolve("/"));
		observer.update(memory.resolve("/bla"));
		Check.isEqual(components.urlLabel.getText(), "/bla");

		observer.openPrevious();
		Check.isEqual(components.urlLabel.getText(), "/");

		observer.openRedoHistory();
		Check.isEqual(components.urlLabel.getText(), "/bla");
	}

	@Test
	public void openNextAfterNavigation() {
		MemoryFileSystem memory = new MemoryFileSystem();
		memory.resolve("/foo").mkdir();
		memory.resolve("/bar").mkdir();
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserObserver observer = new BrowserObserver(components);

		observer.update(memory.resolve("/"));
		observer.update(memory.resolve("/foo"));
		Check.isEqual(components.urlLabel.getText(), "/foo");

		observer.openPrevious();
		Check.isEqual(components.urlLabel.getText(), "/");

		observer.update(memory.resolve("/bar")); // foo should never be visited again
		Check.isEqual(components.urlLabel.getText(), "/bar");

		observer.openRedoHistory();
		Check.isEqual(components.urlLabel.getText(), "/bar");
	}
}