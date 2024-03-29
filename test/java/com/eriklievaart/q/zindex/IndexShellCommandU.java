package com.eriklievaart.q.zindex;

import org.junit.Test;

import com.eriklievaart.q.api.engine.DummyPluginContext;
import com.eriklievaart.q.vfs.impl.UrlResolverService;
import com.eriklievaart.q.zexecute.DummyQMainUi;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class IndexShellCommandU {

	@Test
	public void lookupExact() throws Exception {
		String path = "mem:///tmp/findme/";
		String query = "findme";

		DummyQMainUi ui = uiWithRecentlyVisited(path);
		runIndexShellCommand(query, ui);
		Check.isEqual(ui.getActivePath(), path);
	}

	@Test
	public void lookupFuzzy() throws Exception {
		String path = "mem:///tmp/findme/";
		String query = "find";

		DummyQMainUi ui = uiWithRecentlyVisited(path);
		runIndexShellCommand(query, ui);
		Check.isEqual(ui.getActivePath(), path);
	}

	@Test
	public void lookupPrioritizeExactOverStartsWith() throws Exception {
		String exact = "mem:///tmp/find";
		String fuzzy = "mem:///tmp/findme";
		String query = "find";

		DummyQMainUi ui = uiWithRecentlyVisited(fuzzy, exact);
		runIndexShellCommand(query, ui);
		Check.isEqual(ui.getActivePath(), exact);
	}

	@Test
	public void lookupPrioritizeShortestPath() throws Exception {
		String n3 = "mem:///tmp/find/deeply/nested/path";
		String n2 = "mem:///tmp/find/deeply/nested";
		String n1 = "mem:///tmp/find/deeply";
		String expect = "mem:///tmp/find";
		String query = "tmp find";

		DummyQMainUi ui = uiWithRecentlyVisited(n1, n2, expect, n3);
		runIndexShellCommand(query, ui);
		Check.isEqual(ui.getActivePath(), expect);
	}

	private void runIndexShellCommand(String query, DummyQMainUi ui) throws Exception {
		UrlResolverService resolver = new UrlResolverService();
		for (String directory : ui.getRecentlyVisitedDirectories()) {
			resolver.resolve(UrlTool.append(directory, "dummy.txt"));
		}
		IndexShellCommand testable = new IndexShellCommand(() -> ui, () -> resolver);
		testable.open(query);
		testable.invoke(new DummyPluginContext());
	}

	private DummyQMainUi uiWithRecentlyVisited(String... visited) {
		DummyQMainUi ui = new DummyQMainUi();
		for (String v : visited) {
			ui.addRecentlyVisitedDirectory(v);
		}
		return ui;
	}
}
